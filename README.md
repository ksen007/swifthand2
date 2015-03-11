~/Library/Android/sdk/platform-tools/adb forward tcp:9090 tcp:9090
~/gradle-2.3/bin/gradle build
~/Library/Android/sdk/platform-tools/adb push dist/swifthand2.jar /data/local/tmp/
~/Library/Android/sdk/platform-tools/adb shell uiautomator runtest swifthand2.jar -c swifthand.SwiftHand &
javac src/main/java/swifthand/Client.java src/main/java/swifthand/Constants.java 
java -cp src/main/java/ swifthand.Client

