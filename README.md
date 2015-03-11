make sure a device is connected via USB and adb can communicate with it.
    
    /Users/ksen/Library/Android/sdk/tools/emulator -netdelay none -netspeed full -avd Nexus_4_API_21_2

    ~/Library/Android/sdk/platform-tools/adb forward tcp:9090 tcp:9090
    ~/gradle-2.3/bin/gradle build
    ~/Library/Android/sdk/platform-tools/adb push dist/swifthand2.jar /data/local/tmp/
    ~/Library/Android/sdk/platform-tools/adb shell uiautomator runtest swifthand2.jar -c swifthand.SwiftHand &
    javac src/main/java/swifthand/Client.java src/main/java/swifthand/Constants.java 
    java -cp src/main/java/ swifthand.Client

