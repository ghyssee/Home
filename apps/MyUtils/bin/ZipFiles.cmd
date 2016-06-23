@echo off
cd..
java -Xmx1024m -cp MyUtils-1.0.jar be.home.main.ZipFiles %1 %2 %3 %4
