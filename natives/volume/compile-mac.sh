#!/bin/bash

WD="$PWD"

cd ${BASH_SOURCE%/*}

g++ -O3 -shared -fPIC -I"${JAVA_HOME}/include/darwin" -I"${JAVA_HOME}/include" -I"volume-effect" -std=c++17 -o "$WD/libvolume.dylib" *.cpp volume-effect/*.cpp
