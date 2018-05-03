#!/bin/bash

deploy=0

while [ "$1" != "" ]; do
    if [ "$1" == "deploy" ]; then
        deploy=1
    fi
    shift
done

declare -a arr=("distortion" "timescale" "tremolo" "vibrato")

for i in "${arr[@]}"
do
   $("${i}/compile-mac.sh" > "${i}.log")
done

if [ deploy == 1 ]; then
    "cp *.dylib ../src/main/resources/natives/darwin/"
fi