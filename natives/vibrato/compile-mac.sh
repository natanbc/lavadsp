#!/bin/bash

g++ -O3 -shared -fPIC -I"${JAVA_HOME}/include/darwin" -I"${JAVA_HOME}/include" -I"${BASH_SOURCE%/*}/Vibrato-effect/BerVibrato" -std=c++17 -o libvibrato.dylib "${BASH_SOURCE%/*}/*.cpp" "${BASH_SOURCE%/*}/Vibrato-effect/BerVibrato/*.cpp"