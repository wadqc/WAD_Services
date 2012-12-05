@echo off
REM WAD QC software
REM Script WAD_UninstallServicesCSP.bat
REM
REM Uninstalls WAD_Collector, WAD_Selector and WAD_Processor as a Windows service
@echo on
sc delete WAD_Collector
sc delete WAD_Selector
sc delete WAD_Processor
@pause
