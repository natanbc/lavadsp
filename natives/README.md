# Compiling

## Windows (MSVC)

```
cl /Ox /LD /EHsc /Fe:timescale.dll /I "%JAVA_HOME%\include\win32" /I "%JAVA_HOME%\include" *.cpp soundtouch\*.cpp
```

## Linux (g++)

```
g++ -O3 -shared -fPIC -I"${JAVA_HOME}/include/linux" -I"${JAVA_HOME}/include" -std=c++17 -o libtimescale.so *.cpp soundtouch/*.cpp 
```

# Library

The [SoundTouch](https://www.surina.net/soundtouch) library is used to process audio.