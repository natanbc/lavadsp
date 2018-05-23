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