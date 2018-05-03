@echo off

setlocal

set clean=0
set deploy=0
set cleanlogs=0

for %%x in (%*) do (
   if "%%x"=="clean" set clean=1
   if "%%x"=="deploy" set deploy=1
   if "%%x"=="cleanlogs" set cleanlogs=1
)

setlocal enabledelayedexpansion
for %%l in ("distortion", "timescale", "tremolo", "vibrato") do (
    echo Compiling %%l
    call %%l\compile > %%l.log
)

if "%clean%"=="1" (echo Cleaning... && del *.obj && del *.lib && del *.exp)

if "%cleanlogs%"=="1" (echo Cleaning logs... && del *.log)

if "%deploy%"=="1" (echo Deploying... && copy *.dll ..\src\main\resources\natives\win-x86-64\)