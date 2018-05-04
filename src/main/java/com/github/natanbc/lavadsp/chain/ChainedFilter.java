package com.github.natanbc.lavadsp.chain;

import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChainedFilter implements FloatPcmAudioFilter {
    private final List<FloatPcmAudioFilter> filters;
    private final FloatPcmAudioFilter last;
    private final Map<Class<? extends AudioFilter>, List<AudioFilter>> filtersByClass;

    ChainedFilter(List<FloatPcmAudioFilter> filters, FloatPcmAudioFilter last) {
        this.filters = filters;
        this.last = last;
        this.filtersByClass = new HashMap<>();
        for(AudioFilter f : filters) {
            filtersByClass.computeIfAbsent(f.getClass(), unused->new ArrayList<>()).add(f);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends AudioFilter> List<T> getAllFiltersByClass(Class<T> clazz) {
        return (List<T>)filtersByClass.getOrDefault(clazz, Collections.emptyList());
    }

    public <T extends AudioFilter> T getFirstFilterByClass(Class<T> clazz) {
        List<T> list = getAllFiltersByClass(clazz);
        if(list.isEmpty()) return null;
        return list.get(0);
    }

    public List<FloatPcmAudioFilter> getAllFilters() {
        return filters;
    }

    @Override
    public void process(float[][] input, int offset, int length) throws InterruptedException {
        last.process(input, offset, length);
    }

    @Override
    public void seekPerformed(long requestedTime, long providedTime) {
        for(FloatPcmAudioFilter filter : filters) {
            filter.seekPerformed(requestedTime, providedTime);
        }
    }

    @Override
    public void flush() throws InterruptedException {
        for(FloatPcmAudioFilter filter : filters) {
            filter.flush();
        }
    }

    @Override
    public void close() {
        for(FloatPcmAudioFilter filter : filters) {
            filter.close();
        }
    }
}
