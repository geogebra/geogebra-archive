@echo off
del *.java
..\..\..\..\javacc-4.0/bin/javacc.bat -NOSTATIC Parser.jj
pause