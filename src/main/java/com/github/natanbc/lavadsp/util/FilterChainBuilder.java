package com.github.natanbc.lavadsp.util;

import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilterChain;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.ShortPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.SplitShortPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;

import java.util.*;

/**
 * Builder for audio filter chains.
 *
 * <br>This class is equivalent to the lavaplayer class with the same name,
 * with the exception that, when possible, this class uses vector instructions
 * for needed conversions.
 *
 * @see com.sedmelluq.discord.lavaplayer.filter.FilterChainBuilder
 */
public class FilterChainBuilder {
    private final List<AudioFilter> filters = new ArrayList<>();
    
    /**
     * @param filter The filter to add as the first one in the chain.
     */
    public void addFirst(AudioFilter filter) {
        filters.add(filter);
    }
    
    /**
     * @return The first chain in the filter.
     */
    public AudioFilter first() {
        return filters.get(filters.size() - 1);
    }
    
    /**
     * @param channelCount Number of input channels expected by the current head of the chain.
     * @return The first chain in the filter as a float PCM filter, or if it is not, then adds an adapter filter to the
     *         beginning and returns that.
     */
    public FloatPcmAudioFilter makeFirstFloat(int channelCount) {
        AudioFilter first = first();
        
        if (first instanceof FloatPcmAudioFilter) {
            return (FloatPcmAudioFilter) first;
        } else {
            return prependUniversalFilter(first, channelCount);
        }
    }
    
    /**
     * @param channelCount Number of input channels expected by the current head of the chain.
     * @return The first chain in the filter as an universal PCM filter, or if it is not, then adds an adapter filter to
     *         the beginning and returns that.
     */
    public UniversalPcmAudioFilter makeFirstUniversal(int channelCount) {
        AudioFilter first = first();
        
        if (first instanceof UniversalPcmAudioFilter) {
            return (UniversalPcmAudioFilter) first;
        } else {
            return prependUniversalFilter(first, channelCount);
        }
    }
    
    /**
     * @param context See {@link com.sedmelluq.discord.lavaplayer.filter.AudioFilterChain#context}.
     * @param channelCount Number of input channels expected by the current head of the chain.
     * @return The built filter chain. Adds an adapter to the beginning of the chain if the first filter is not universal.
     */
    public AudioFilterChain build(Object context, int channelCount) {
        UniversalPcmAudioFilter firstFilter = makeFirstUniversal(channelCount);
        return new AudioFilterChain(firstFilter, filters, context);
    }
    
    private UniversalPcmAudioFilter prependUniversalFilter(AudioFilter first, int channelCount) {
        UniversalPcmAudioFilter universalInput;
        
        if (first instanceof SplitShortPcmAudioFilter) {
            universalInput = VectorSupport.makeUniversal((SplitShortPcmAudioFilter) first, channelCount);
        } else if (first instanceof FloatPcmAudioFilter) {
            universalInput = VectorSupport.makeUniversal((FloatPcmAudioFilter) first, channelCount);
        } else if (first instanceof ShortPcmAudioFilter) {
            universalInput = VectorSupport.makeUniversal((ShortPcmAudioFilter) first, channelCount);
        } else {
            throw new RuntimeException("Filter must implement at least one data type.");
        }
        
        addFirst(universalInput);
        return universalInput;
    }
}
