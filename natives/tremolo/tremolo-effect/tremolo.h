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

#pragma once

#include "jni.h"

#include <inttypes.h>

class Tremolo {
    public:
        Tremolo(jint sampleRate);
        ~Tremolo();
        void process(jfloat* input, jint inputOffset, jfloat* output, jint outputOffset, jint samples);
        void setDepth(jdouble depth);
        void setFrequency(jdouble frequency);
    private:
        inline jfloat processOneSample(jfloat sample);

        jint sampleRate;
        jdouble frequency;
        jdouble depth;
        jdouble phase;
};