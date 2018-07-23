#!/bin/bash

WD="$PWD"

cd ${BASH_SOURCE%/*}

g++ -msse3 -mavx2 -O3 -shared -fPIC -I"${JAVA_HOME}/include/linux" -I"${JAVA_HOME}/include" -I"karaoke-effect" -std=c++17 -o "$WD/libkaraoke.so" *.cpp karaoke-effect/*.cpp
