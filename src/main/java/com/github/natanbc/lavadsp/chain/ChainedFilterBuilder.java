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

public class ChainedFilterBuilder implements PcmFilterFactory {
    private final List<AudioDataFormatConstructor> filters = new ArrayList<>();
    private final Map<Class<? extends AudioFilter>, List<Consumer<? extends AudioFilter>>> filterConfigurators = new HashMap<>();

    public synchronized <T extends AudioFilter> void addConfigurator(Class<T> clazz, Consumer<T> configurator) {
        Objects.requireNonNull(clazz, "Class may not be null");
        Objects.requireNonNull(configurator, "Configurator may not be null");
        filterConfigurators.computeIfAbsent(clazz, unused->new ArrayList<>()).add(configurator);
    }

    public synchronized ChainedFilterBuilder add(AudioDataFormatConstructor factory) {
        Objects.requireNonNull(factory, "Factory may not be null");
        filters.add(factory);
        return this;
    }

    public ChainedFilterBuilder add(ChannelCountSampleRateConstructor factory) {
        Objects.requireNonNull(factory, "Factory may not be null");
        return add((FloatPcmAudioFilter output, AudioDataFormat format)->factory.create(output, format.channelCount, format.sampleRate));
    }

    public ChainedFilterBuilder add(ChannelCountConstructor factory) {
        Objects.requireNonNull(factory, "Factory may not be null");
        return add((downstream, channelCount, sampleRate)->factory.create(downstream, channelCount));
    }

    @SuppressWarnings("unchecked")
    public synchronized ChainedFilter build(AudioDataFormat format, UniversalPcmAudioFilter output) {
        if(filters.isEmpty()) {
            throw new IllegalStateException("No factories provided");
        }
        List<FloatPcmAudioFilter> list = new ArrayList<>(filters.size());
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
                list.add(f = f2);
            } catch(Throwable t) {
                for(FloatPcmAudioFilter filter : list) {
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
        return new ChainedFilter(list, f);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AudioFilter> buildChain(AudioTrack track, AudioDataFormat format, UniversalPcmAudioFilter output) {
        return Collections.singletonList(build(format, output));
    }

    @FunctionalInterface
    public interface ChannelCountConstructor {
        FloatPcmAudioFilter create(FloatPcmAudioFilter downstream, int channelCount);
    }

    @FunctionalInterface
    public interface ChannelCountSampleRateConstructor {
        FloatPcmAudioFilter create(FloatPcmAudioFilter downstream, int channelCount, int sampleRate);
    }

    @FunctionalInterface
    public interface AudioDataFormatConstructor {
        FloatPcmAudioFilter create(FloatPcmAudioFilter downstream, AudioDataFormat format);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void uncheckedThrow(Throwable throwable) throws T {
        throw (T)throwable;
    }
}
