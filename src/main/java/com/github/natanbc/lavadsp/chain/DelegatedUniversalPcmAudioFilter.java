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

import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;

import java.nio.ShortBuffer;

/**
 * Utility class that delegates all calls to a separate filter, used by {@link UnlinkedChainedFilter}.
 */
@SuppressWarnings("WeakerAccess")
public class DelegatedUniversalPcmAudioFilter implements UniversalPcmAudioFilter {
    protected volatile UniversalPcmAudioFilter delegate = new NoopUniversalPcmAudioFilter();

    protected void setDelegate(UniversalPcmAudioFilter newDelegate) {
        this.delegate = newDelegate;
    }

    @Override
    public void process(float[][] input, int offset, int length) throws InterruptedException {
        delegate.process(input, offset, length);
    }

    @Override
    public void process(short[] input, int offset, int length) throws InterruptedException {
        delegate.process(input, offset, length);
    }

    @Override
    public void process(ShortBuffer buffer) throws InterruptedException {
        delegate.process(buffer);
    }

    @Override
    public void process(short[][] input, int offset, int length) throws InterruptedException {
        delegate.process(input, offset, length);
    }

    @Override
    public void seekPerformed(long requestedTime, long providedTime) {
        delegate.seekPerformed(requestedTime, providedTime);
    }

    @Override
    public void flush() throws InterruptedException {
        delegate.flush();
    }

    @Override
    public void close() {
        delegate.close();
    }

    /**
     * Noop filter, which discards all data received.
     */
    protected static class NoopUniversalPcmAudioFilter implements UniversalPcmAudioFilter {
        @Override
        public void process(float[][] input, int offset, int length) {
            //noop
        }

        @Override
        public void process(short[] input, int offset, int length) {
            //noop
        }

        @Override
        public void process(ShortBuffer buffer) {
            //noop
        }

        @Override
        public void process(short[][] input, int offset, int length) {
            //noop
        }

        @Override
        public void seekPerformed(long requestedTime, long providedTime) {
            //noop
        }

        @Override
        public void flush() {
            //noop
        }

        @Override
        public void close() {
            //noop
        }
    }
}
