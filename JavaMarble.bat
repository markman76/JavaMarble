@echo off
::
::  $HeaderURL$
::
::!/bin/bash
:: java
set JAVA_BIN="C:\JAVA\jdk1.7.0_40\bin"

:: command
@echo Run Java Marble...
%JAVA_BIN%\java -cp ./bin Marble.MarbleMain
::java -cp JavaMarble.jar Marble.MarbleMain

::
::  $Log$
::
::  Revision 1.0  2007.10.23 15:00:00  MK
::  initialize
::
::