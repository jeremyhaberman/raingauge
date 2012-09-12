# RainGauge

RainGauge is an Android application that monitors rainfall and manual watering to help homeowners provide their lawn the water it needs.  It was born at the [Gluecon 2012](http://gluecon.com/2012/) hackathon.

The app serves two purposes:  

1. It helps the developer keep his lawn from dying.  
2. It hopes to provide an example for how to write tests for Android applications.

## Contents

* [Setting up a dev environment](#devsetup)
* [Building](#building)
* [Testing](#testing)

<a name="devsetup"></a>
## Setting up a dev environment

1. Install the [Android SDK](http://developer.android.com/sdk/index.html)
2. Install the [SDK Tools](http://developer.android.com/sdk/tools-notes.html)
3. Clone the repo (if you want to contribute, fork it first)

        git clone git@github.com:jeremyhaberman/raingauge.git

<a name="building"></a>
## Building

The default ant targets are available for building RainGauge at the command line.

For example, to clean the project, create a debug build and install the package:

        ant clean debug install

<a name="testing"></a>
## Testing

Tests are located in `tests`.  There are three subdirectories, each for different testing tools:

* `junit` JUnit tests
* `monkey` A shell script for running tests with the monkey tool
* `monkeyrunner` Tests using monkeyrunner

### JUnit tests

To run all the JUnit tests:

1. Change directories to `tests/junit`

        cd tests/junit

2. Run the tests:

        ant clean debug install test
        
    To run the EMMA code coverage report, run *:
    
        ant clean emma debug install test
        
    \* This must be run on a rooted device or emulator.
    
### monkey test

The monkey test sends 500 pseudo-random events (e.g. clicks, touches) to the application.

To run the test on Mac OS X or Linux:

    cd tests/monkey
    ./run-monkey.sh
        
To run the test on other Windows, copy the **adb** command from the shell script and run it manually in a Command Prompt.  (If you're feeling generous, fork the project, create a batch file and submit a pull request.)

### monkeyrunner test

To run the monkeyrunner test:  

    cd tests/monkeyrunner
    monkeyrunner test-notification.py
    
The test should generate a `notification.png` file containing a screen shot of the test notification in the status bar.