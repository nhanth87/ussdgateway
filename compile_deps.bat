@echo off
cd /d "%~dp0"
echo Compiling jdiameter...
cd jdiameter
call mvn clean install -DskipTests -Dcheckstyle.skip=true -Dmaven.javadoc.skip=true
if %ERRORLEVEL% NEQ 0 (
    echo jdiameter failed!
    exit /b %ERRORLEVEL%
)
echo jdiameter SUCCESS!
cd ..
echo Compiling jain-slee.diameter...
cd jain-slee.diameter
call mvn clean install -DskipTests -Dcheckstyle.skip=true -Dmaven.javadoc.skip=true
if %ERRORLEVEL% NEQ 0 (
    echo jain-slee.diameter failed!
    exit /b %ERRORLEVEL%
)
echo jain-slee.diameter SUCCESS!
echo All dependencies compiled successfully!
