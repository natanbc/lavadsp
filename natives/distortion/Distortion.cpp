#include "jni.h"
#include "distortion.h"

extern "C" {
#define METHOD(_RETURN, _NAME) JNIEXPORT _RETURN JNICALL Java_com_github_natanbc_lavadsp_natives_DistortionLibrary_##_NAME

#ifndef NO_CRITICALS
    #define CRITICAL_AVAILABLE true
    #define CRITICALNAME(_NAME) JavaCritical_com_github_natanbc_lavadsp_natives_DistortionLibrary_##_NAME
#else
    #define CRITICAL_AVAILABLE false
    #define CRITICALNAME(_NAME) FakeCritical_com_github_natanbc_lavadsp_natives_DistortionLibrary_##_NAME
#endif
//see https://stackoverflow.com/questions/36298111/is-it-possible-to-use-sun-misc-unsafe-to-call-c-functions-without-jni/36309652#36309652
//only really useful for methods manipulating arrays (https://github.com/jnr/jnr-ffi/issues/86#issuecomment-250325189)
#define CRITICALMETHOD(_RETURN, _NAME) JNIEXPORT _RETURN JNICALL CRITICALNAME(_NAME)

#define SETTER(_NAME) \
    METHOD(void, _NAME)(JNIEnv* env, jobject thiz, jlong instance, jdouble value) {\
        ((Distortion*)instance)->##_NAME##(value);\
    }

    METHOD(jboolean, criticalMethodsAvailable)(JNIEnv* env, jobject thiz) {
        return CRITICAL_AVAILABLE;
    }

    METHOD(jlong, create)(JNIEnv* env, jobject thiz) {
        return (jlong)new Distortion();
    }

    SETTER(setSinScale)
    SETTER(setCosScale)
    SETTER(setTanScale)
    SETTER(setScale)
    SETTER(setOffset)

    METHOD(void, enable)(JNIEnv* env, jobject thiz, jlong instance, jint functions) {
        ((Distortion*)instance)->enable(functions);
    }

    METHOD(void, disable)(JNIEnv* env, jobject thiz, jlong instance, jint functions) {
        ((Distortion*)instance)->disable(functions);
    }

    CRITICALMETHOD(void, process)(jlong instance, jint unused1, jfloat* input, jint inputOffset, jint unused2, jfloat* output, jint outputOffset, jint size) {
        ((Distortion*)instance)->process(input, inputOffset, output, outputOffset, size);
    }

    METHOD(void, process)(JNIEnv* env, jobject thiz, jlong instance, jfloatArray input, jint inputOffset, jfloatArray output, jint outputOffset, jint size) {
        auto in = (jfloat*)env->GetPrimitiveArrayCritical(input, nullptr);
        auto out = (jfloat*)env->GetPrimitiveArrayCritical(output, nullptr);
        CRITICALNAME(process)(instance, 0, in, inputOffset, 0, out, outputOffset, size);
        env->ReleasePrimitiveArrayCritical(input, in, JNI_COMMIT);
        env->ReleasePrimitiveArrayCritical(output, out, JNI_COMMIT);
    }

    METHOD(void, destroy)(JNIEnv* env, jobject thiz, jlong instance) {
        delete (Distortion*)instance;
    }
}