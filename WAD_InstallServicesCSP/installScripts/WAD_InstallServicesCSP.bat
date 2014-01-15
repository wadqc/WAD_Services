@echo off
REM WAD QC software
REM Script WAD_InstallServicesCSP.bat
REM
REM Installs WAD_Collector, WAD_Selector and WAD_Processor as a Windows service
REM
REM DEPENDENCIES
REM - Windows 2003 Resource kit, distributed at no cost by Microsoft.
REM   In fact only the executable SrvAny from that package is needed.
REM - WAD_*_params.reg files
REM - WAD services depend on the mysql service, which may be installed from the XAMPP
REM   control panel.
REM
REM NOTES
REM - Modify the path to SrvAny in the lines below.
REM - Modify the path to your WAD installation in the WAD_*_params.reg files
REM
REM ##########################################################################
REM # Use of this script is your own responsibility, sofware comes without
REM # warranty.
REM ##########################################################################
REM Version 1.0 / 20121115 / Joost Kuijer / VUmc

setlocal
echo Starting WAD_InstallServicesCSP.bat

set SrvAnyExe="C:\WAD-software\WAD_InstallServicesCSP\installScripts\srvany.exe"
echo Path to SrvAny = %SrvAnyExe%
if not exist %SrvAnyExe% goto ERROR_WARNING

echo.
echo Install WAD_Collector service
sc create WAD_Collector start= auto binPath= %SrvAnyExe%
regedit WAD_Collector_params.reg
sc config WAD_Collector depend= mysql

echo.
echo Install WAD_Selector service
sc create WAD_Selector start= auto binPath= %SrvAnyExe%
regedit WAD_Selector_params.reg
sc config WAD_Selector depend= mysql

echo.
echo Install WAD_Processor service
sc create WAD_Processor start= auto binPath= %SrvAnyExe%
regedit WAD_Processor_params.reg
sc config WAD_Processor depend= mysql

pause
goto EXIT

rem # error
:ERROR_WARNING
echo.
echo ERROR
echo Path to SrvAny.exe is not correct, please edit it in this script.
echo.
echo Also check path to WAD_Collector.jar, WAD_Selector.jar and WAD_Processor.jar
echo in the WAD_*_params.reg files!
echo.
pause
goto EXIT

:EXIT

endlocal
