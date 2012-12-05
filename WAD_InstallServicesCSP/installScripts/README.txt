WAD install services scripts

Installs WAD_Collector, WAD_Selector and WAD_Processor as a Windows service

DEPENDENCIES
- Windows 2003 Resource kit, distributed at no cost by Microsoft.
  In fact only the executable SrvAny from that package is needed.
- WAD_*_params.reg files
- WAD services depend on the mysql service, which may be installed from the XAMPP
  control panel.

NOTES
- Modify the path to SrvAny in the install script.
- Modify the path to your WAD installation in the WAD_*_params.reg files

##########################################################################
# Use of this software is your own responsibility, sofware comes without
# warranty.
##########################################################################
README Version 1.0 / 20121115 / Joost Kuijer / VUmc
