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

#include "tremolo.h"

#define _USE_MATH_DEFINES
#include <math.h>

Tremolo::Tremolo(jint sampleRate): sampleRate(sampleRate) {
    setDepth(0.5);
    setFrequency(2);
}

Tremolo::~Tremolo() {}

void Tremolo::setDepth(jdouble depth) {
    //we want [0, 0.5]
    this->depth = depth/2;
}

void Tremolo::setFrequency(jdouble frequency) {
    this->frequency = frequency;
}

void Tremolo::process(jfloat* input, jint inputOffset, jfloat* output, jint outputOffset, jint samples) {
    jfloat* actualIn = input + inputOffset;
    jfloat* actualOut = output + outputOffset;
    for(jint i = 0; i < samples; i++) {
        actualOut[i] = processOneSample(actualIn[i]);
    }
}

jfloat Tremolo::processOneSample(jfloat sample) {
    double offset = 1.0 - depth;
    double modSignal = offset + depth * sin((double)phase);
    this->phase += 2 * M_PI / sampleRate * frequency;
    return (float)(modSignal * sample);
}