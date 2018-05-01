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