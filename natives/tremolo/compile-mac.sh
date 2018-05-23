#!/bin/bash

WD="$PWD"

cd ${BASH_SOURCE%/*}

g++ -O3 -shared -fPIC -I"${JAVA_HOME}/include/darwin" -I"${JAVA_HOME}/include" -I"tremolo-effect" -std=c++17 -o "$WD/libtremolo.dylib" *.cpp tremolo-effect/*.cpp