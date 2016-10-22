@echo off
set SCRIPT_DRIVE=%~d0
set SCRIPT_PATH=%~p0
%SCRIPT_DRIVE%
cd %SCRIPT_PATH%
cd..
java -Xmx1024m -cp MyUtils-1.0.jar be.home.main.tools.ZipFiles %1 %2 %3 %4
