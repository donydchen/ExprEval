@echo off
cd src
javadoc -private -author -version -d ..\doc -classpath ..\lib parser\*.java lexer\*.java
cd ..
pause
@echo on
