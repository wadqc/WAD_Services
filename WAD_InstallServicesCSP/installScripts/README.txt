WAD install services scripts

Installs WAD_Collector, WAD_Selector and WAD_Processor as a Windows service

Note: recommended to create a new user account for WAD services and run the services
as this user. User needs to have full (read/write) access to XML input/output folders.

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
README Version 1.1 / 20130115 / Joost Kuijer / VUmc
