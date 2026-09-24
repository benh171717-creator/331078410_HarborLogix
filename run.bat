@echo off
if "%1"=="test"  java -cp out harborlogix.tests.AcceptanceTest
if "%1"=="app"   java -cp out harborlogix.app.TerminalApp
if "%1"=="check" java -cp out harborlogix.tools.OpenClosedCheck src\harborlogix\ops\Yard.java
if "%1"==""      echo usage: run.bat [test^|app^|check]
