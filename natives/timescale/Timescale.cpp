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

#include "soundtouch/SoundTouch.h"

#include "jni.h"

using namespace soundtouch;

extern "C" {

#define RELEASE_ARRAY(_ENV,_ARRAY,_CARRAY)\
    _ENV -> ReleasePrimitiveArrayCritical(_ARRAY, _CARRAY, 0)

#define METHOD(_RETURN, _NAME) JNIEXPORT _RETURN JNICALL Java_com_github_natanbc_lavadsp_natives_TimescaleLibrary_##_NAME

#ifndef NO_CRITICALS
    #define CRITICAL_AVAILABLE true
    #define CRITICALNAME(_NAME) JavaCritical_com_github_natanbc_timescale_lavadsp_TimescaleLibrary_##_NAME
#else
    #define CRITICAL_AVAILABLE false
    #define CRITICALNAME(_NAME) FakeCritical_com_github_natanbc_timescale_lavadsp_TimescaleLibrary_##_NAME
#endif
//see https://stackoverflow.com/questions/36298111/is-it-possible-to-use-sun-misc-unsafe-to-call-c-functions-without-jni/36309652#36309652
//only really useful for methods manipulating arrays (https://github.com/jnr/jnr-ffi/issues/86#issuecomment-250325189)
#define CRITICALMETHOD(_RETURN, _NAME) JNIEXPORT _RETURN JNICALL CRITICALNAME(_NAME)

    METHOD(jstring, soundTouchVersion)(JNIEnv* env, jobject thiz) {
        return env->NewStringUTF(SoundTouch::getVersionString());
    }

    METHOD(jint, soundTouchVersionID)(JNIEnv* env, jobject thiz) {
        return (jint)SoundTouch::getVersionId();
    }

    METHOD(jboolean, criticalMethodsAvailable)(JNIEnv* env, jobject thiz) {
        return CRITICAL_AVAILABLE;
    }

    METHOD(jlong, create)(JNIEnv* env, jobject thiz, jint channels, jint sampleRate) {
        auto st = new SoundTouch();
        st->setChannels((uint32_t)channels);
        st->setSampleRate((uint32_t)sampleRate);
        st->clear();
        return (jlong)st;
    }

    METHOD(void, destroy)(JNIEnv* env, jobject thiz, jlong instance) {
        delete (SoundTouch*)instance;
    }

    CRITICALMETHOD(jint, process)(jlong instance, jint unused1, jfloat* src, jint inputOffset, jint inputLength, jint unused2, jfloat* dest, jint outputOffset, jint outputLength, jint unused3, jint* written) {
        auto st = (SoundTouch*)instance;
        st->putSamples(src + inputOffset, (uint32_t)inputLength);
        uint32_t r = st->receiveSamples(dest + outputOffset, (uint32_t)outputLength);
        written[0] = (int32_t)r;
        return 0;
    }

    METHOD(jint, process)(JNIEnv* env, jobject thiz, jlong instance, jfloatArray input, jint inputOffset, jint inputLength,
        jfloatArray output, jint outputOffset, jint outputLength, jintArray written) {
        auto src = (jfloat*)env->GetPrimitiveArrayCritical(input, nullptr);
        auto dest = (jfloat*)env->GetPrimitiveArrayCritical(output, nullptr);
        auto w = (jint*)env->GetPrimitiveArrayCritical(written, nullptr);
        CRITICALNAME(process)(instance, 0, src, inputOffset, inputLength, 0, dest, outputOffset, outputLength, 0, w);
        RELEASE_ARRAY(env, input, src);
        RELEASE_ARRAY(env, output, dest);
        RELEASE_ARRAY(env, written, w);
        return 0;
    }

    CRITICALMETHOD(jint, read)(jlong instance, jint unused, jfloat* dest, jint outputOffset, jint outputLength) {
        auto st = (SoundTouch*)instance;
        return (jint)st->receiveSamples(dest + outputOffset, (uint32_t)outputLength);
    }

    METHOD(jint, read)(JNIEnv* env, jobject thiz, jlong instance, jfloatArray output, jint outputOffset, jint outputLength) {
        auto dest = (jfloat*)env->GetPrimitiveArrayCritical(output, nullptr);
        auto j = CRITICALNAME(read)(instance, 0, dest, outputOffset, outputLength);
        RELEASE_ARRAY(env, output, dest);
        return j;
    }

    METHOD(void, reset)(JNIEnv* env, jobject thiz, jlong instance) {
        ((SoundTouch*)instance)->clear();
    }

    METHOD(void, setSpeed)(JNIEnv* env, jobject thiz, jlong instance, jdouble speed) {
        ((SoundTouch*)instance)->setTempo(speed);
    }

    METHOD(void, setPitch)(JNIEnv* env, jobject thiz, jlong instance, jdouble pitch) {
        ((SoundTouch*)instance)->setPitch(pitch);
    }

    METHOD(void, setRate)(JNIEnv* env, jobject thiz, jlong instance, jdouble rate) {
        ((SoundTouch*)instance)->setRate(rate);
    }

    METHOD(jint, getSetting)(JNIEnv* env, jobject thiz, jlong instance, jint setting) {
        return ((SoundTouch*)instance)->getSetting(setting);
    }

    METHOD(jint, setSetting)(JNIEnv* env, jobject thiz, jlong instance, jint setting, jint value) {
        return ((SoundTouch*)instance)->setSetting(setting, value);
    }

    METHOD(jdouble, getInputOutputSampleRatio)(JNIEnv* env, jobject thiz, jlong instance) {
        return ((SoundTouch*)instance)->getInputOutputSampleRatio();
    }

    METHOD(void, flush)(JNIEnv* env, jobject thiz, jlong instance) {
        ((SoundTouch*)instance)->flush();
    }

    METHOD(jint, numUnprocessedSamples)(JNIEnv* env, jobject thiz, jlong instance) {
        return (jint)((SoundTouch*)instance)->numUnprocessedSamples();
    }

    METHOD(jint, numSamples)(JNIEnv* env, jobject thiz, jlong instance) {
        return (jint)((SoundTouch*)instance)->numSamples();
    }

    METHOD(jboolean, isEmpty)(JNIEnv* env, jobject thiz, jlong instance) {
        return (jboolean)((SoundTouch*)instance)->isEmpty();
    }
}