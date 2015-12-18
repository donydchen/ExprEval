@echo off
cd bin
java test.ExprEvalTest ..\testcases\mytest.xml  > ..\testcases\report3.txt
cd ..
type testcases\report3.txt
pause
del testcases\report3.txt
@echo on