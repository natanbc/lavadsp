#!/bin/bash

deploy=0
cleanlogs=0

while [ "$1" != "" ]; do
    if [ "$1" == "deploy" ]; then
        deploy=1
    fi
    if [ "$1" == "cleanlogs" ]; then
        cleanlogs=1
    fi
    shift
done

declare -a arr=("distortion" "timescale" "tremolo" "vibrato")

for i in "${arr[@]}"
do
   $("${i}/compile-linux.sh" > "${i}.log")
done

if [ $deploy == 1 ]; then
    cp *.so ../src/main/resources/natives/linux-x86-64/
fi

if [ $cleanlogs == 1 ]; then
    rm *.log
fi
