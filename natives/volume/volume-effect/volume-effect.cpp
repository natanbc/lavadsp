/*
 * Copyright 2018 gabixdev
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

#include "volume.h"

#include <math.h>

Volume::Volume() {}
Volume::~Volume() {}

#define SETTER(_NAME,_FIELD) \
    void Volume::_NAME(jdouble v) {\
        this->_FIELD = v;\
    }

SETTER(setVolume, volume)

void Volume::process(jfloat* input, jint inputOffset, jfloat* output, jint outputOffset, jint size) {    
    jfloat* actualIn = input + inputOffset;
    jfloat* actualOut = output + outputOffset;
    
    if (this->volume == 1.0) {
        for(jint i = 0; i < size; i++) {
            actualOut[i] = actualIn[i];
        }
    } else if (this->volume == 0.0) {
        for(jint i = 0; i < size; i++) {
            actualOut[i] = 0.0;
        }
    } else {
        jfloat result = 0.0;
        for(jint i = 0; i < size; i++) {
            result = actualIn[i] * this->volume;
            actualOut[i] = fmax(-1.0, fmin(1.0, result));
        }
    }
}
