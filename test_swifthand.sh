#!/bin/sh

swifthandpath=$HOME/swifthand2/swifthand2

if [ -z $1 ]; then
	echo "usage: ./test_swifthand.sh <path_to_apk>"
	exit
fi

if [ ! -f $1 ]; then
	echo "[*] error: apk file $1 does not exist!"
	exit
fi

package=`aapt dump badging $1 | grep package | awk '{print $2}' | sed s/name=//g | sed s/\'//g`
activity=`aapt dump badging $1 | grep launchable-activity | awk '{print $2}' | sed s/name=//g | sed s/\'//g`
arg="${package}/${activity}"

adb install -r -d $1

#$swifthandpath/build_client.sh $arg
java -cp src/main/java/ swifthand.Client $arg
