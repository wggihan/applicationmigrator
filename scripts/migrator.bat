@echo off


rem if not "%JAVA_HOME%" == "" goto gotJavaHome
rem echo The JAVA_HOME environment variable is not defined
rem echo This environment variable is needed to run this program
rem goto end

rem :gotJavaHome
rem if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
rem goto okJavaHome

rem :noJavaHome
rem echo The JAVA_HOME environment variable is not defined correctly
rem echo This environment variable is needed to run this program
rem echo NB: JAVA_HOME should point to a JDK/JRE
rem got o end

rem :okJavaHome
rem check the INSTALATION_HOME environment variable
set CURRENT_DIR=%cd%
set INSTALATION_HOME=%CURRENT_DIR%
set MIGRATOR_HOME=%userprofile%\.apimigrator


rem ----- Execute The Requested Command ---------------------------------------
echo Using INSTALATION_HOME:   %INSTALATION_HOME%
echo Using JAVA_HOME:    %JAVA_HOME%
echo Using MIGRATOR_HOME: %MIGRATOR_HOME%
rem set _RUNJAVA="%JAVA_HOME%\bin\java"

set _RUNJAVA="%JAVA_HOME%\bin\java"

%_RUNJAVA% %JAVA_OPTS% -Dmigrator.home="%MIGRATOR_HOME%" -jar apimigrator.jar %*

endlocal
:end
