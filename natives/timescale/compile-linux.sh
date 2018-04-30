#!/bin/bash

g++ -O3 -shared -fPIC -I"${JAVA_HOME}/include/linux" -I"${JAVA_HOME}/include" -std=c++17 -o libtimescale.so "${BASH_SOURCE%/*}/*.cpp" "${BASH_SOURCE%/*}/soundtouch/*.cpp"