#include "jni.h"

#define FUNCTION_SIN 1
#define FUNCTION_COS 2
#define FUNCTION_TAN 4

#define ALL_FUNCTIONS (FUNCTION_SIN | FUNCTION_COS | FUNCTION_TAN)

class Distortion {
    public:
        Distortion();
        ~Distortion();
        void process(jfloat* input, jint inputOffset, jfloat* output, jint outputOffset, jint size);
        void setSinOffset(jdouble offset);
        void setSinScale(jdouble scale);
        void setCosOffset(jdouble offset);
        void setCosScale(jdouble scale);
        void setTanOffset(jdouble offset);
        void setTanScale(jdouble scale);
        void setOffset(jdouble offset);
        void setScale(jdouble scale);

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
        jint enabled = ALL_FUNCTIONS;
        jdouble sinOffset = 0;
        jdouble sinScale = 1;
        jdouble cosOffset = 0;
        jdouble cosScale = 1;
        jdouble tanOffset = 0;
        jdouble tanScale = 1;
        jdouble offset = 0;
        jdouble scale = 1;
};