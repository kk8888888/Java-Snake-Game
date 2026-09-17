@echo off
setlocal

set "JAVA_HOME=C:\Users\student\.p2\pool\plugins\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_21.0.11.v20260515-1531\jre"
set "PROJECT_ROOT=%~dp0.."

"%JAVA_HOME%\bin\javac.exe" -d "%PROJECT_ROOT%\work" "%~dp0SnakeGame.java"
if errorlevel 1 (
    echo Compilation failed.
    pause
    exit /b 1
)

start "Snake" "%JAVA_HOME%\bin\javaw.exe" -cp "%PROJECT_ROOT%\work" SnakeGame
