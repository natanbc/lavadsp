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

 #include "karaoke.h"

#define _USE_MATH_DEFINES
#include <math.h>

Karaoke::Karaoke(jint sampleRate) {
    this->sampleRate = sampleRate;
    this->level = 1.0;
    this->monoLevel = 1.0;
    this->filterBand = 220.0;
    this->filterWidth = 100.0;
    this->updateFilters();
}

Karaoke::~Karaoke() {}

void Karaoke::updateFilters() {
    jint rate = this->sampleRate;
    jfloat C = exp(-2 * M_PI * this->filterWidth / rate);
    jfloat B = -4 * C / (1 + C) * cos(2 * M_PI * this->filterBand / rate);
    jfloat A = sqrt(1 - B * B / (4 * C)) * (1 - C);

    this->A = A;
    this->B = B;
    this->C = C;
    this->y1 = 0.0;
    this->y2 = 0.0;
}

void Karaoke::setLevel(jfloat level) {
    this->level = level;
}

void Karaoke::setMonoLevel(jfloat level) {
    this->monoLevel = level;
}

void Karaoke::setFilterBand(jfloat band) {
    this->filterBand = band;
    this->updateFilters();
}

void Karaoke::setFilterWidth(jfloat width) {
    this->filterWidth = width;
    this->updateFilters();
}

void Karaoke::process(jfloat* leftIn, jfloat* rightIn, jint inputOffset, jfloat* leftOut, jfloat* rightOut, jint outputOffset, jint samples) {
    jint i;
    jdouble l, r, o;
    jdouble y;

    for (i = 0; i < samples; i++) {
        /* get left and right inputs */
        l = leftIn[i];
        r = rightIn[i];
        /* do filtering */
        y = (this->A * ((l + r) / 2.0) - this->B * this->y1) -
            this->C * this->y2;
        this->y2 = this->y1;
        this->y1 = y;
        /* filter mono signal */
        o = y * this->monoLevel * this->level;
        /* now cut the center */
        leftOut[i] = l - (r * this->level) + o;
        rightOut[i] = r - (l * this->level) + o;
    }
}
