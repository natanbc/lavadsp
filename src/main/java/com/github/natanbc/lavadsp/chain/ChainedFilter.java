package com.github.natanbc.lavadsp.chain;

import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents a filter chain, allowing runtime reconfiguration of filters.
 */
@SuppressWarnings("WeakerAccess")
public class ChainedFilter implements FloatPcmAudioFilter {
    /**
     * All filters registered in the builder.
     */
    protected final List<AudioFilter> filters;

    /**
     * Last filter in the chain, which gets fed the data provided by lavaplayer.
     */
    protected final FloatPcmAudioFilter last;

    /**
     * Maps the filters by type, used to speed up class based lookups.
     */
    protected final Map<Class<? extends AudioFilter>, List<AudioFilter>> filtersByClass;

    protected ChainedFilter(List<AudioFilter> filters, FloatPcmAudioFilter last) {
        this.filters = filters;
        this.last = last;
        this.filtersByClass = new HashMap<>();
        for(AudioFilter f : filters) {
            filtersByClass.computeIfAbsent(f.getClass(), unused->new ArrayList<>()).add(f);
        }
    }

    /**
     * Configures the first filter of a given type, if multiple are available.
     *
     * <br>Equivalent to
     * <pre>
     * {@code
     * T filter = getFirstFilterByClass(clazz);
     * if(filter != null) {
     *      configurator.accept(filter);
     * }
     * }
     * </pre>
     *
     * @param clazz Filter class, used for internal lookup.
     * @param configurator Configurator for the filter. Will be called one time at most.
     * @param <T> Filter type.
     */
    public synchronized <T extends AudioFilter> void configureFirst(Class<T> clazz, Consumer<T> configurator) {
        List<T> filters = getByType(clazz);
        if(filters != null) {
            //guaranteed to have at least one element
            configurator.accept(filters.get(0));
        }
    }

    /**
     * Configures all filters of a given type.
     *
     * <br>Equivalent to {@code getAllFiltersByClass(clazz).forEach(configurator);}
     *
     * @param clazz Filter class, used for internal lookup.
     * @param configurator Configurator for the filter. May be called multiple times.
     * @param <T> Filter type.
     */
    public synchronized <T extends AudioFilter> void configure(Class<T> clazz, Consumer<T> configurator) {
        List<T> filters = getByType(clazz);
        if(filters != null) {
            filters.forEach(configurator);
        }
    }

    /**
     * Returns an unmodifiable list of all filters of a given type.
     *
     * @param clazz Filter class, used for internal lookup.
     * @param <T> Filter type.
     *
     * @return List of filters of the provided class.
     */
    public <T extends AudioFilter> List<T> getAllFiltersByClass(Class<T> clazz) {
        List<T> list = getByType(clazz);
        if(list == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * Returns the first filter of a given class, if multiple are available.
     *
     * @param clazz Filter class, used for internal lookup.
     * @param <T> Filter type.
     *
     * @return Possibly null filter of the given class.
     */
    public synchronized <T extends AudioFilter> T getFirstFilterByClass(Class<T> clazz) {
        List<T> list = getByType(clazz);
        if(list == null) return null;
        return list.get(0);
    }

    /**
     * Returns an unmodifiable list containing all filters in the chain.
     *
     * @return A list containing all filters in the chain.
     */
    public synchronized List<AudioFilter> getAllFilters() {
        return Collections.unmodifiableList(filters);
    }

    @Override
    public void process(float[][] input, int offset, int length) throws InterruptedException {
        last.process(input, offset, length);
    }

    @Override
    public void seekPerformed(long requestedTime, long providedTime) {
        for(AudioFilter filter : filters) {
            filter.seekPerformed(requestedTime, providedTime);
        }
    }

    @Override
    public void flush() throws InterruptedException {
        for(AudioFilter filter : filters) {
            filter.flush();
        }
    }

    @Override
    public void close() {
        for(AudioFilter filter : filters) {
            filter.close();
        }
    }

    @SuppressWarnings("unchecked")
    private synchronized <T extends AudioFilter> List<T> getByType(Class<T> clazz) {
        return (List<T>)filtersByClass.get(clazz);
    }
}
