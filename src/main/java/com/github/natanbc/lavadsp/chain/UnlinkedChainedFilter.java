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
import com.sedmelluq.discord.lavaplayer.tools.DaemonThreadFactory;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * Chained filter whose output can be dynamically changed.
 */
@SuppressWarnings("WeakerAccess")
public class UnlinkedChainedFilter extends ChainedFilter implements PcmFilterFactory {
    /**
     * Default cleanup delay, in milliseconds.
     */
    public static final long DEFAULT_CLEANUP_DELAY_MS = 15000;

    private static final Logger LOGGER = LoggerFactory.getLogger(UnlinkedChainedFilter.class);
    private static final AtomicReferenceFieldUpdater<UnlinkedChainedFilter, ScheduledFuture<?>> UPDATER;

    static {
        //the compiler is stupid enough to require this
        @SuppressWarnings("unchecked")
        AtomicReferenceFieldUpdater<UnlinkedChainedFilter, ScheduledFuture<?>> updater =
                (AtomicReferenceFieldUpdater<UnlinkedChainedFilter, ScheduledFuture<?>>)
                        (AtomicReferenceFieldUpdater)
                                AtomicReferenceFieldUpdater.newUpdater(UnlinkedChainedFilter.class, ScheduledFuture.class, "cleanupTask");
        UPDATER = updater;
    }

    /**
     * Format used when building the chain, used to verify compatibility when provided as a PcmFilterFactory
     * to an AudioPlayer.
     */
    protected final AudioDataFormat format;

    /**
     * Delegated filter, used to transparently change the actual output.
     */
    protected final DelegatedUniversalPcmAudioFilter output;

    /**
     * Executor used to close the filters.
     *
     * <br>When {@link #close() close()} is called, a task is queued in this executor to actually close the filters.
     * When called, {@link #buildChain(AudioTrack, AudioDataFormat, UniversalPcmAudioFilter) buildChain()}
     * cancels and clears that task, otherwise it throws.
     *
     * <br>An internal, global executor is used if this field is null.
     */
    protected volatile ScheduledExecutorService cleanupExecutor;

    /**
     * Delay, in milliseconds, of the cleanup task. Defaults to 15 seconds.
     */
    protected volatile long cleanupDelayMs = DEFAULT_CLEANUP_DELAY_MS;

    private volatile ScheduledFuture<?> cleanupTask;

    protected UnlinkedChainedFilter(List<AudioFilter> filters, FloatPcmAudioFilter last, AudioDataFormat format, DelegatedUniversalPcmAudioFilter output) {
        super(filters, last);
        this.format = format;
        this.output = output;
    }

    /**
     * Sets the executor used to clean up filters. An internal, global one is used if null.
     *
     * @param cleanupExecutor Executor to use.
     *
     * @return {@code this}, for chaining calls.
     *
     * @see #cleanupExecutor
     */
    public UnlinkedChainedFilter setCleanupExecutor(ScheduledExecutorService cleanupExecutor) {
        this.cleanupExecutor = cleanupExecutor;
        return this;
    }

    /**
     * Sets the cleanup delay, in milliseconds. Small values (less than a few seconds) are not recommended
     * and can lead to undefined behaviour.
     *
     * @param cleanupDelayMs Delay before running cleanup task.
     *
     * @return {@code this}, for chaining calls.
     *
     * @see #cleanupDelayMs
     */
    public UnlinkedChainedFilter setCleanupDelay(long cleanupDelayMs) {
        this.cleanupDelayMs = cleanupDelayMs;
        return this;
    }

    /**
     * Links this filter to a new output, discarding the previous one.
     *
     * @param filter New filter to set as output.
     *
     * @return {@code this}, for chaining calls.
     */
    public UnlinkedChainedFilter link(UniversalPcmAudioFilter filter) {
        output.setDelegate(filter);
        return this;
    }

    @Override
    public void process(float[][] input, int offset, int length) throws InterruptedException {
        cancelCleanup();
        super.process(input, offset, length);
    }

    @Override
    public void close() {
        LOGGER.debug("Scheduling cleanup with delay of {} ms", cleanupDelayMs);
        ScheduledFuture<?> future = cleanupExecutor().schedule(super::close, cleanupDelayMs, TimeUnit.MILLISECONDS);
        if(!UPDATER.compareAndSet(this, null, future)) {
            LOGGER.debug("Cleanup already scheduled. Cancelling new task");
            future.cancel(true);
        }
    }

    @Override
    public List<AudioFilter> buildChain(AudioTrack track, AudioDataFormat format, UniversalPcmAudioFilter output) {
        if(!format.equals(this.format)) {
            String expected = prettyPrint(this.format);
            String actual   = prettyPrint(format);
            LOGGER.error("Format mismatch: {} expected, got {}", expected, actual);
            throw new IllegalArgumentException("Mismatched format: " + expected + " expected, got " + actual);
        }
        cancelCleanup();
        link(output);
        return Collections.singletonList(this);
    }

    private void cancelCleanup() {
        ScheduledFuture<?> task = cleanupTask;
        if(task != null) {
            LOGGER.debug("Cancelling cleanup task");
            if(task.isDone() && !task.isCancelled()) {
                LOGGER.error("Filters have already been released");
                throw new IllegalStateException("Filters have already been released");
            }
            task.cancel(true);
            if(!UPDATER.compareAndSet(this, task, null)) {
                LOGGER.warn("Cleanup task changed while cancelling");
            }
        }
    }

    private ScheduledExecutorService cleanupExecutor() {
        ScheduledExecutorService s = cleanupExecutor;
        if(s == null) return CleanupExecutor.INSTANCE;
        return s;
    }

    private static String prettyPrint(AudioDataFormat format) {
        return "(" + format.codecName() + ", " + format.channelCount + " channels, " +
                format.sampleRate + " Hz, " + format.chunkSampleCount + " samples/chunk)";
    }

    private static class CleanupExecutor {
        private static final ScheduledExecutorService INSTANCE = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory("lavadsp-unlinked-chained-filter-cleanup"));
    }
}
