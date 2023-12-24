@echo off

if not defined JAVAFX_HOME echo JAVAFX_HOME environment variable must be set!

java --module-path %JAVAFX_HOME%\lib --add-modules javafx.controls --class-path "lib/*" j3.GUI