#!/bin/bash

WD="$PWD"

cd ${BASH_SOURCE%/*}

g++ -msse3 -mavx2 -O3 -shared -fPIC -I"${JAVA_HOME}/include/linux" -I"${JAVA_HOME}/include" -std=c++17 -o "$WD/libtimescale.so" *.cpp soundtouch/*.cpp
