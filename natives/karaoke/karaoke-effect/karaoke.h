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

class Karaoke {
    public:
        Karaoke(jint sampleRate);
        ~Karaoke();
        void process(jfloat* leftIn, jfloat* rightIn, jint inputOffset, jfloat* leftOut, jfloat* rightOut, jint outputOffset, jint samples);
        void setLevel(jfloat level);
        void setMonoLevel(jfloat level);
        void setFilterBand(jfloat band);
        void setFilterWidth(jfloat width);
    private:
        jint sampleRate;
        float level;
        float monoLevel;
        float filterBand;
        float filterWidth;
        float A;
        float B;
        float C;
        float y1;
        float y2;

        void updateFilters();
};