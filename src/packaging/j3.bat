@echo off

set JAVA_HOME=

pushd runtime
set JAVA_HOME=%cd%
popd

set PATH=%JAVA_HOME%\bin;%PATH%

java --class-path "lib/*" j3.GUI
