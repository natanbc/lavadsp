# Compiling

## Windows (MSVC)

```
cl /Ox /LD /EHsc /Fe:vibrato.dll /I "%JAVA_HOME%\include\win32" /I "%JAVA_HOME%\include" /I Vibrato-effect\BerVibrato *.cpp Vibrato-effect\BerVibrato\*.cpp
```

## Linux (g++)

```
g++ -O3 -shared -fPIC -I"${JAVA_HOME}/include/linux" -I"${JAVA_HOME}/include" -I"Vibrato-effect/BerVibrato" -std=c++17 -o libvibrato.so *.cpp Vibrato-effect/BerVibrato/*.cpp 
```

## Mac (g++)

```
g++ -O3 -shared -fPIC -I"${JAVA_HOME}/include/darwin" -I"${JAVA_HOME}/include" -I"Vibrato-effect/BerVibrato" -std=c++17 -o libvibrato.dylib *.cpp Vibrato-effect/BerVibrato/*.cpp
```

# Library

The [Vibrato-effect](https://github.com/Bershov/Vibrato-effect) library is used to process audio.