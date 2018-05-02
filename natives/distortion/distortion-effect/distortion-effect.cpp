#include "distortion.h"

#include <math.h>

Distortion::Distortion() {}
Distortion::~Distortion() {}

void Distortion::setSinScale(jdouble scale) {
    this->sinScale = scale;
}

void Distortion::setCosScale(jdouble scale) {
    this->cosScale = scale;
}

void Distortion::setTanScale(jdouble scale) {
    this->tanScale = scale;
}

void Distortion::setScale(jdouble scale) {
    this->scale = scale;
}

void Distortion::setOffset(jdouble offset) {
    this->offset = offset;
}

void Distortion::process(jfloat* input, jint inputOffset, jfloat* output, jint outputOffset, jint size) {
    jfloat* actualIn = input + inputOffset;
    jfloat* actualOut = output + outputOffset;
    jdouble _sin = sinScale;
    jdouble _cos = cosScale;
    jdouble _tan = tanScale;
    jdouble _s = scale;
    jdouble _o = offset;
    bool useSin = isEnabled(FUNCTION_SIN);
    bool useCos = isEnabled(FUNCTION_COS);
    bool useTan = isEnabled(FUNCTION_TAN);
    for(jint i = 0; i < size; i++) {
        jfloat sample = actualIn[i];
        actualOut[i] = fmax(-1.0, fmin(1.0, _o + _s * (useSin ? sin(sample * _sin) : 1) * (useCos ? cos(sample * _cos) : 1) * (useTan ? tan(sample * _tan) : 1)));
    }
}