# Drayton Wiser Smartthings Device Type

This is a basic device type and connect smart app for Drayton Wiser radiator valves.

## Find your secret

Connection to the Drayton hub is done through a local LAN connection rather than via the cloud. This requires an app "secret" that the hub provides. To access this secret you need to jump through a couple of hoops:

1. Log out of the app. Make sure you’re at the login screen
1. Tap Setup / Create Account (even though your system has already been set up).
1. Select the HubR type you have
1. Press the Setup button on the hub
1. This will start the WiserHeatXXX access point.
1. Connect to WiserHeatXXX with you device or a real computer. You should get an IP in the 192.168.8.0/24 range from the hub's DHCP server.
1. Go to http://192.168.8.1/secret/ in a web browser. You'll set a long string of seemingly random numbers. This is your secret! 
1. Now finish the setup…
1. Follow the on-screen instructions to connect your smartphone to WiserHeatXXX
1. Tap Skip when prompted to set up your heating system.
1. Follow the on-screen instructions to connect your Heat HubR to the
1. Internet by selecting your new Wi-Fi network.
1. Tap Skip when prompted to register an account.
1. You have now changed to a new Wi-Fi network. You will see the home screen and can proceed to control your heating as normal.

## Find your Wiser hub IP Address

1. Use and app like Fing (https://www.fing.io/) to scan your network for your hub. 
1. It will be called something like WiserHeat012A34.
1. Remember the IP address


## Add the repo, smart app and device types in the SmartThings IDE

1. Under "My Device Handlers" click "Settings" to add Github Repo to your IDE with the following settings:
  * Owner: chrisduffer
  * Name:drayton-wiser
  * Branch: master
1. Under "My Device Handlers" click "Update From Repo" 
1. Select "drayton-wiser (master)" from the drop-down menu.
1. Select "ALL of the device types in the "New" tab.
1. Click the "Publish" check-box in the bottom right.
1. Click on "Execute Update"
1. Go to the “My SmartApps” tab in the IDE and click 
1. Select "drayton-wiser (master)" from the drop-down menu.
1. Select ALL the SmartApps in the "New" tab.
1. Click the "Publish" check-box in the bottom right.
1. Click on "Execute Update"

## Install the SmartApp 

1. Open the SmartThings app
1. Go to Marketplace, SmartApps
1. Scroll to the bottom and select "MyApps"
1. Select "Drayton Wiser (Connect)"
1. Enter the IP address of your hub
1. Paste in your secret
1. Fill in all the other fields
1. Tap "Save"
1. The app will add a "Drayton Room X" thermostat device for each room and a "Drayton Away Mode" switch to turn on/off away mode

## Configure your rooms

1. Go into the new "Drayton Away Mode" device
1. Tap the refresh icon
1. Rename it and give it a new icon if you like
1. Go into each room.
1. Tap refresh.
1. Wait for it to update.
1. The room name from Wiser will be displayed at the bottom. If this displays "0" check the "Recently" tab.
1. Set the name in the device settings to whatever you want.
1. There's a bug in the Android app where you need to set each field to something other than whats displayed. Set the polling interval to something other than 1, then save, then go back and set it to 1 (or whatever you want).

## Room Device Type Instructions

The room device type works much like a normal thermostat device, but without any cooling.

### Top section
In the center is the current temperature. 
On the right is the set point. If the heating is off in this room this will be blank.
The arrows override the scheduled temperate up or down by 0.5

### Icons

Current heating state (on or off)
Current mode
Demand %
Refresh - tap to refresh
Override Type - If the scheduled temperature is overridden, tapping this will cancel that
Mode - Tap to switch THE WHOLE SYSTEM between home and away mode

## Away Mode Type Instructions

The device is to allow you to trigger away mode from other automations.
For example:
Add "Turn ON Drayton Away Mode" to the "Goodbye" routine. 
Add "Turn OFF Drayton Away Mode" to the "I'm Back" routine. 