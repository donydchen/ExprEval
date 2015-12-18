@echo off
cd src
javac -d ..\bin -classpath ..\bin parser\*.java lexer\*.java
cd ..
pause
@echo on
