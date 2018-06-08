/*
 * Copyright 2018 natanbc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.natanbc.lavadsp.chain;

import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.PcmFilterFactory;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Utility class for building {@link ChainedFilter ChainedFilter} objects, which chains multiple
 * filters and allows reconfiguring filters by type as needed.
 *
 * <br>This class may also be directly provided to {@link com.sedmelluq.discord.lavaplayer.player.AudioPlayer#setFilterFactory(PcmFilterFactory)},
 * but the resulting filters may not be reconfigured.
 */
@SuppressWarnings("WeakerAccess")
public class ChainedFilterBuilder implements PcmFilterFactory {
    /**
     * List of all filters factories registered in this builder.
     */
    protected final List<AudioDataFormatConstructor> filters = new ArrayList<>();

    /**
     * Map of the configurators to be called for created filters. Keys are the filter class.
     */
    protected final Map<Class<? extends AudioFilter>, List<Consumer<? extends AudioFilter>>> filterConfigurators = new HashMap<>();

    /**
     * Adds a configurator that gets called when a filter of the specified type is created.
     *
     * @param clazz Class of the filter that should be configured.
     * @param configurator Configures the filter when it's created. May be called multiple times.
     * @param <T> Filter type.
     */
    public synchronized <T extends AudioFilter> void addConfigurator(Class<T> clazz, Consumer<T> configurator) {
        Objects.requireNonNull(clazz, "Class may not be null");
        Objects.requireNonNull(configurator, "Configurator may not be null");
        filterConfigurators.computeIfAbsent(clazz, unused->new ArrayList<>()).add(configurator);
    }

    /**
     * Adds a new filter factory, used to build a filter chain when {@link #build(AudioDataFormat, UniversalPcmAudioFilter)} is called.
     *
     * @param factory Factory to add.
     *
     * @return {@code this}, for chaining calls
     *
     * @see #build(AudioDataFormat, UniversalPcmAudioFilter)
     */
    public synchronized ChainedFilterBuilder add(AudioDataFormatConstructor factory) {
        Objects.requireNonNull(factory, "Factory may not be null");
        filters.add(factory);
        return this;
    }

    /**
     * Adds a new filter factory, used to build a filter chain when {@link #build(AudioDataFormat, UniversalPcmAudioFilter)} is called.
     *
     * @param factory Factory to add.
     *
     * @return {@code this}, for chaining calls
     *
     * @see #build(AudioDataFormat, UniversalPcmAudioFilter)
     */
    public ChainedFilterBuilder add(ChannelCountSampleRateConstructor factory) {
        Objects.requireNonNull(factory, "Factory may not be null");
        return add((FloatPcmAudioFilter output, AudioDataFormat format)->factory.create(output, format.channelCount, format.sampleRate));
    }

    /**
     * Adds a new filter factory, used to build a filter chain when {@link #build(AudioDataFormat, UniversalPcmAudioFilter)} is called.
     *
     * @param factory Factory to add.
     *
     * @return {@code this}, for chaining calls
     *
     * @see #build(AudioDataFormat, UniversalPcmAudioFilter)
     */
    public ChainedFilterBuilder add(ChannelCountConstructor factory) {
        Objects.requireNonNull(factory, "Factory may not be null");
        return add((downstream, channelCount, sampleRate)->factory.create(downstream, channelCount));
    }

    /**
     * Builds an unlinked chained filter, used for convenience in cases such as
     * <pre>
     * {@code
     * ChainedFilterBuilder builder = new ChainedFilterBuilder();
     *
     * builder.add(TimescalePcmAudioFilter::new);
     * builder.add(TremoloPcmAudioFilter::new);
     *
     * builder.addConfigurator(TremoloPcmAudioFilter.class, filter->filter.setDepth(0.8));
     * builder.addConfigurator(TimescalePcmAudioFilter.class, filter->filter.setSpeed(2));
     *
     * UnlinkedChainedFilter filter = builder.buildUnlinked(StandardAudioDataFormats.DISCORD_OPUS);
     *
     * audioPlayer.setFilterFactory(filter);
     *
     * //some time later
     *
     * filter.configure(X.class, x->x.setY(y));
     * }
     * </pre>
     *
     * This filter may be provided directly to {@link com.sedmelluq.discord.lavaplayer.player.AudioPlayer#setFilterFactory(PcmFilterFactory)}.
     *
     * @param format Audio format to use when building the chain.
     *
     * @return The newly created filter.
     *
     * @see #build(AudioDataFormat, UniversalPcmAudioFilter)
     */
    @SuppressWarnings("unchecked")
    public synchronized UnlinkedChainedFilter buildUnlinked(AudioDataFormat format) {
        if(filters.isEmpty()) {
            throw new IllegalStateException("No factories provided");
        }
        List<AudioFilter> list = new ArrayList<>(filters.size());
        DelegatedUniversalPcmAudioFilter output = new DelegatedUniversalPcmAudioFilter();
        FloatPcmAudioFilter f = output;
        for(AudioDataFormatConstructor c : filters) {
            try {
                FloatPcmAudioFilter f2 = c.create(f, format);
                if(f2 == null) {
                    //catch block will close all already created filters and rethrow
                    throw new IllegalArgumentException("Factory " + c + " returned a null filter");
                }
                List<Consumer<? extends AudioFilter>> configurators = filterConfigurators.get(f2.getClass());
                if(configurators != null) {
                    for(Consumer<? extends AudioFilter> configurator : configurators) {
                        ((Consumer<FloatPcmAudioFilter>)configurator).accept(f2);
                    }
                }
                list.add(f2);
                f = f2;
            } catch(Throwable t) {
                for(AudioFilter filter : list) {
                    try {
                        filter.close();
                    } catch(Throwable t2) {
                        t.addSuppressed(t2);
                    }
                }
                //this would be unchecked anyway, so this won't be a problem
                ChainedFilterBuilder.<Error>uncheckedThrow(t);
                throw new AssertionError("should not be reached", t);
            }
        }
        return new UnlinkedChainedFilter(list, f, format, output);
    }

    /**
     * Builds and links a new filter. This is equivalent to calling {@code buildUnlinked(format).link(output)}.
     *
     * <br>The returned filter is actually an instance of {@link UnlinkedChainedFilter}.
     *
     * @param format Audio format to use when building the chain.
     * @param output Output to write generated audio to.
     *
     * @return The newly created filter.
     */
    public synchronized ChainedFilter build(AudioDataFormat format, UniversalPcmAudioFilter output) {
        return buildUnlinked(format).link(output);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AudioFilter> buildChain(AudioTrack track, AudioDataFormat format, UniversalPcmAudioFilter output) {
        return Collections.singletonList(build(format, output));
    }

    /**
     * Factory type for creating filters.
     */
    @FunctionalInterface
    public interface ChannelCountConstructor {
        FloatPcmAudioFilter create(FloatPcmAudioFilter downstream, int channelCount);
    }

    /**
     * Factory type for creating filters.
     */
    @FunctionalInterface
    public interface ChannelCountSampleRateConstructor {
        FloatPcmAudioFilter create(FloatPcmAudioFilter downstream, int channelCount, int sampleRate);
    }

    /**
     * Factory type for creating filters.
     */
    @FunctionalInterface
    public interface AudioDataFormatConstructor {
        FloatPcmAudioFilter create(FloatPcmAudioFilter downstream, AudioDataFormat format);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void uncheckedThrow(Throwable throwable) throws T {
        throw (T)throwable;
    }
}
