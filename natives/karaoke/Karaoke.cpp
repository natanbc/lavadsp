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
#include "karaoke-effect/karaoke.h"

extern "C" {

#define RELEASE_ARRAY(_ENV,_ARRAY,_CARRAY)\
    _ENV -> ReleasePrimitiveArrayCritical(_ARRAY, _CARRAY, 0)

#define METHOD(_RETURN, _NAME) JNIEXPORT _RETURN JNICALL Java_com_github_natanbc_lavadsp_natives_KaraokeLibrary_##_NAME

#ifndef NO_CRITICALS
    #define CRITICAL_AVAILABLE true
    #define CRITICALNAME(_NAME) JavaCritical_com_github_natanbc_lavadsp_natives_KaraokeLibrary_##_NAME
#else
    #define CRITICAL_AVAILABLE false
    #define CRITICALNAME(_NAME) FakeCritical_com_github_natanbc_lavadsp_natives_KaraokeLibrary_##_NAME
#endif
//see https://stackoverflow.com/questions/36298111/is-it-possible-to-use-sun-misc-unsafe-to-call-c-functions-without-jni/36309652#36309652
//only really useful for methods manipulating arrays (https://github.com/jnr/jnr-ffi/issues/86#issuecomment-250325189)
#define CRITICALMETHOD(_RETURN, _NAME) JNIEXPORT _RETURN JNICALL CRITICALNAME(_NAME)

    METHOD(jboolean, criticalMethodsAvailable)(JNIEnv* env, jobject thiz) {
        return CRITICAL_AVAILABLE;
    }

    METHOD(jlong, create)(JNIEnv* env, jobject thiz, jint sampleRate) {
        return (jlong)new Karaoke(sampleRate);
    }

    METHOD(void, setLevel)(JNIEnv* env, jobject thiz, jlong instance, jfloat level) {
        ((Karaoke*)instance)->setLevel(level);
    }

    METHOD(void, setMonoLevel)(JNIEnv* env, jobject thiz, jlong instance, jfloat level) {
        ((Karaoke*)instance)->setMonoLevel(level);
    }

    METHOD(void, setFilterBand)(JNIEnv* env, jobject thiz, jlong instance, jfloat band) {
        ((Karaoke*)instance)->setFilterBand(band);
    }

    METHOD(void, setFilterWidth)(JNIEnv* env, jobject thiz, jlong instance, jfloat width) {
        ((Karaoke*)instance)->setFilterWidth(width);
    }

    CRITICALMETHOD(void, process)(jlong instance, jint unused1, jfloat* leftIn, jint unused2, jfloat* rightIn, jint inputOffset, jint unused3, jfloat* leftOut, jint unused4, jfloat* rightOut, jint outputOffset, jint size) {
        ((Karaoke*)instance)->process(leftIn, rightIn, inputOffset, leftOut, rightOut, outputOffset, size);
    }

    METHOD(void, process)(JNIEnv* env, jobject thiz, jlong instance, jfloatArray inputLeft, jfloatArray inputRight, jint inputOffset, jfloatArray outputLeft, jfloatArray outputRight, jint outputOffset, jint size) {
        auto leftIn = (jfloat*)env->GetPrimitiveArrayCritical(inputLeft, nullptr);
        auto rightIn = (jfloat*)env->GetPrimitiveArrayCritical(inputRight, nullptr);
        auto leftOut = (jfloat*)env->GetPrimitiveArrayCritical(outputLeft, nullptr);
        auto rightOut = (jfloat*)env->GetPrimitiveArrayCritical(outputRight, nullptr);
        CRITICALNAME(process)(instance, 0, leftIn, 0, rightIn, inputOffset, 0, leftOut, 0, rightOut, outputOffset, size);
        RELEASE_ARRAY(env, inputLeft, leftIn);
        RELEASE_ARRAY(env, inputRight, rightIn);
        RELEASE_ARRAY(env, outputLeft, leftOut);
        RELEASE_ARRAY(env, outputRight, rightOut);
    }

    METHOD(void, destroy)(JNIEnv* env, jobject thiz, jlong instance) {
        delete (Karaoke*)instance;
    }
}