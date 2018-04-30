#!/bin/bash

g++ -O3 -shared -fPIC -I"${JAVA_HOME}/include/darwin" -I"${JAVA_HOME}/include" -std=c++17 -o libtimescale.dylib "${BASH_SOURCE%/*}/*.cpp" "${BASH_SOURCE%/*}/soundtouch/*.cpp"