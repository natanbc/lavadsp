#include "BerVibrato.h"

#include "jni.h"

extern "C" {

#define METHOD(_RETURN, _NAME) JNIEXPORT _RETURN JNICALL Java_com_github_natanbc_lavadsp_natives_VibratoLibrary_##_NAME

#ifndef NO_CRITICALS
    #define CRITICAL_AVAILABLE true
    #define CRITICALNAME(_NAME) JavaCritical_com_github_natanbc_lavadsp_natives_VibratoLibrary_##_NAME
#else
    #define CRITICAL_AVAILABLE false
    #define CRITICALNAME(_NAME) FakeCritical_com_github_natanbc_lavadsp_natives_VibratoLibrary_##_NAME
#endif
//see https://stackoverflow.com/questions/36298111/is-it-possible-to-use-sun-misc-unsafe-to-call-c-functions-without-jni/36309652#36309652
//only really useful for methods manipulating arrays (https://github.com/jnr/jnr-ffi/issues/86#issuecomment-250325189)
#define CRITICALMETHOD(_RETURN, _NAME) JNIEXPORT _RETURN JNICALL CRITICALNAME(_NAME)

    METHOD(jboolean, criticalMethodsAvailable)(JNIEnv* env, jobject thiz) {
        return CRITICAL_AVAILABLE;
    }

    METHOD(jlong, initialize)(JNIEnv* env, jobject thiz, jint sampleRate) {
        auto v = new BerVibrato();
        v->initialize(sampleRate);
        return (jlong)v;
    }

    METHOD(jfloat, maxFrequency)(JNIEnv* env, jobject thiz) {
        return VIBRATO_FREQUENCY_MAX_HZ;
    }

    METHOD(void, setFrequency)(JNIEnv* env, jobject thiz, jlong instance, jfloat frequency) {
        ((BerVibrato*)instance)->setFrequency(frequency);
    }

    METHOD(void, setDepth)(JNIEnv* env, jobject thiz, jlong instance, jfloat depth) {
        ((BerVibrato*)instance)->setDepth(depth);
    }

    CRITICALMETHOD(void, process)(jlong instance, jint unused1, jfloat* in, jint inputOffset, jint unused2, jfloat* out, jint outputOffset, jint size) {
        auto v = (BerVibrato*)instance;
        auto actualIn = in + inputOffset;
        auto actualOut = out + outputOffset;
        for(jint i = 0; i < size; i++) {
            actualOut[i] = v->processOneSample(actualIn[i]);
        }
    }

    METHOD(void, process)(JNIEnv* env, jobject thiz, jlong instance, jfloatArray input, jint inputOffset, jfloatArray output, jint outputOffset, jint size) {
        auto in = (jfloat*)env->GetPrimitiveArrayCritical(input, nullptr);
        auto out = (jfloat*)env->GetPrimitiveArrayCritical(output, nullptr);
        CRITICALNAME(process)(instance, 0, in, inputOffset, 0, out, outputOffset, size);
        env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
        env->ReleasePrimitiveArrayCritical(output, out, JNI_COMMIT);
    }

    METHOD(void, destroy)(JNIEnv* env, jobject thiz, jlong instance) {
        delete (BerVibrato*)instance;
    }
}