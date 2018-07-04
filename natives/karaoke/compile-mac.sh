#!/bin/bash

WD="$PWD"

cd ${BASH_SOURCE%/*}

g++ -O3 -shared -fPIC -I"${JAVA_HOME}/include/darwin" -I"${JAVA_HOME}/include" -I"karaoke-effect" -std=c++17 -o "$WD/libkaraoke.dylib" *.cpp karaoke-effect/*.cpp