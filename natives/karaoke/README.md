# Compiling

## Windows (MSVC)

```
cl /Ox /LD /EHsc /Fe:karaoke.dll /I "%JAVA_HOME%\include\win32" /I "%JAVA_HOME%\include" /I karaoke-effect *.cpp karaoke-effect\*.cpp
```

## Linux (g++)

```
g++ -O3 -shared -fPIC -I"${JAVA_HOME}/include/linux" -I"${JAVA_HOME}/include" -I"karaoke-effect" -std=c++17 -o libkaraoke.so *.cpp karaoke-effect/*.cpp 
```

## Mac (g++)

```
g++ -O3 -shared -fPIC -I"${JAVA_HOME}/include/darwin" -I"${JAVA_HOME}/include" -I"karaoke-effect" -std=c++17 -o libkaraoke.dylib *.cpp karaoke-effect/*.cpp
```

# Library

Code based on [this](https://github.com/Kurento/gst-plugins-good/blob/master/gst/audiofx/audiokaraoke.h)
and [this](https://github.com/Kurento/gst-plugins-good/blob/master/gst/audiofx/audiokaraoke.c)
