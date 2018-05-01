#!/bin/bash

WD="$PWD"

cd ${BASH_SOURCE%/*}

g++ -O3 -shared -fPIC -I"${JAVA_HOME}/include/darwin" -I"${JAVA_HOME}/include" -std=c++17 -o "$WD/libtimescale.dylib" *.cpp soundtouch/*.cpp
