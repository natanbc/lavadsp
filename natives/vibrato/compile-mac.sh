#!/bin/bash

WD="$PWD"

cd ${BASH_SOURCE%/*}

g++ -O3 -shared -fPIC -I"${JAVA_HOME}/include/darwin" -I"${JAVA_HOME}/include" -I"Vibrato-effect/BerVibrato" -std=c++17 -o "$WD/libvibrato.dylib" *.cpp Vibrato-effect/BerVibrato/*.cpp