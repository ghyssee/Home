@echo off
FOR /F "tokens=2-4 delims=/-/ " %%A IN ("%date%") DO (
SET DAY=%%A
SET MONTH=%%B
SET YEAR=%%C
)
For /f "tokens=1-2 delims=/:" %%a in ("%TIME%") do (set MYTIME=%%a:%%b)

java -Xmx1024m -cp MyUtils-1.0.jar be.home.main.ZipFiles %1 %2 %3 %4.%date%
pause