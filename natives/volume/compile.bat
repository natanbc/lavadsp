cl /Ox /LD /EHsc /Fe:volume.dll /I "%JAVA_HOME%\include\win32" /I "%JAVA_HOME%\include" /I"%~dp0\volume-effect" %~dp0\*.cpp %~dp0\volume-effect\*.cpp
