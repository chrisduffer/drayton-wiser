# Drayton Wiser Smartthings Device Type

This is a basic device type and connect smart app for Drayton Wiser radiator valves.

Connection to the Drayton hub is done through a local LAN connection rather than via the cloud. This requires an app "secret" that the hub provides. To access this secret you need to jump through a couple of hoops:

1. Log out of the app. Make sure you’re at the login screen
2. Tap Setup / Create Account (even though your system has already been set up).
3. Select the HubR type you have
4. Press the Setup button on the hub
5. This will start the WiserHeatXXX access point.
6. Connect to WiserHeatXXX with a real computer. You should get an IP in the 192.168.8.0/24 range from the hub's DHCP server.
7. Go to http://192.168.8.1/secret/ in a web browser. You'll set a long string on seemingly random numbers. This is your secret! 
8. Now finish the setup…
9. Follow the on-screen instructions to connect your smartphone to WiserHeatXXX
10. Tap Skip when prompted to set up your heating system.
11. Follow the on-screen instructions to connect your Heat HubR to the
12. Internet by selecting your new Wi-Fi network.
13. Tap Skip when prompted to register an account.
14. You have now changed to a new Wi-Fi network. You will see the home
15. screen and can proceed to control your heating as normal
