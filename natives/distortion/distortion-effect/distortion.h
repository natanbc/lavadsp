#include "jni.h"

#define FUNCTION_SIN 1
#define FUNCTION_COS 2
#define FUNCTION_TAN 4

class Distortion {
    public:
        Distortion();
        ~Distortion();
        void process(jfloat* input, jint inputOffset, jfloat* output, jint outputOffset, jint size);
        void setSinScale(jdouble scale);
        void setCosScale(jdouble scale);
        void setTanScale(jdouble scale);
        void setScale(jdouble scale);
        void setOffset(jdouble offset);

        void enable(jint functions) {
            enabled |= functions;
        }

        void disable(jint functions) {
            enabled &= ~functions;
        }

        bool isEnabled(jint function) {
            return (enabled & function) != 0;
        }
    private:
        jint enabled = FUNCTION_SIN + FUNCTION_COS + FUNCTION_TAN;
        jdouble offset = 0;
        jdouble sinScale = 1;
        jdouble cosScale = 1;
        jdouble tanScale = 1;
        jdouble scale = 1;
};