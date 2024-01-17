::Close the driver station
taskkill /IM "DriverStation.exe"

::Force close shuffleboard so there is no save dialog, which prevents it from restarting
taskkill /IM "javaw.exe" /F

taskkill /IM "electron.exe"

::Disable Firewalls
netsh advfirewall set allprofiles state off 

::Disable Adapters (competition only)
netsh interface set interface "Wi-Fi" DISABLED
netsh interface set interface "Wi-Fi 2" DISABLED

:: 2 seconds is the minimum, 1.9 doesn't allow time to close and reopen
timeout /t 2

::Start driver station
Start /max "" "C:\Program Files (x86)\FRC Driver Station\DriverStation.exe"

::Start twilightdash
::cd "C:\Users\User\Documents\GitHub\2023-robot\TwighlightDash"
::npm run start

"C:\Users\Blue Twilight\Desktop\chooseauto.txt"

timeout /t 30
taskkill "notepad.exe"