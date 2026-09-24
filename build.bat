@echo off
REM HarborLogix build script (Windows)
if exist out rmdir /s /q out
mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
del sources.txt
echo Build OK.
echo.
echo   run.bat test    - acceptance test
echo   run.bat app     - your demo program
echo   run.bat check   - open/closed check on Yard.java
