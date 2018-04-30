#!/bin/bash

g++ -O3 -shared -fPIC -I"${JAVA_HOME}/include/linux" -I"${JAVA_HOME}/include" -I"${BASH_SOURCE%/*}/Vibrato-effect/BerVibrato" -std=c++17 -o libvibrato.so "${BASH_SOURCE%/*}/*.cpp" "${BASH_SOURCE%/*}/Vibrato-effect/BerVibrato/*.cpp"