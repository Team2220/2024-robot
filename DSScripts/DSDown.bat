::Close the driver station
taskkill /IM "DriverStation.exe"

::Force close shuffleboard so there is no save dialog, which prevents it from restarting
taskkill /IM "javaw.exe" /F

taskkill /IM "electron.exe"

taskkill /IM "notepad.exe"

::Enable Firewalls
netsh advfirewall set allprofiles state on 

::Enable Adapters
netsh interface set interface "Wi-Fi" ENABLED
netsh interface set interface "Wi-Fi 2" ENABLED