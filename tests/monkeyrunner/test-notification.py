# 
# Filename: test-notification.py
# Author: jeremyhaberman
# 
# Tests DefaultNotificationManager
#
# Usage: monkeyrunner test-notification.py
#

import subprocess;

# Imports the monkeyrunner modules used by this program
from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice

# Connects to the current device, returning a MonkeyDevice object
device = MonkeyRunner.waitForConnection()

subprocess.call("adb shell am instrument -w -e class com.jeremyhaberman.raingauge.android.DefaultNotificationManagerTest com.jeremyhaberman.raingauge.tests/android.test.InstrumentationTestRunner", shell=True)

device.drag((50,5), (50,800), 1.0, 10)

image = device.takeSnapshot()
image.writeToFile("notification.png", "png")
