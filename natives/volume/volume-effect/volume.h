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

#include "jni.h"

class Volume {
    public:
        Volume();
        ~Volume();
        void process(jfloat* input, jint inputOffset, jfloat* output, jint outputOffset, jint size);
        void setVolume(jdouble volume);
        jdouble getVolume() {
            return volume;
        }
    private:
        jdouble volume = 1.0;
};
