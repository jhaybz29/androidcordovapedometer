# Android Cordova Pedometer
This is a simple Android Pedometer plugin mainly developed to bridge the pedometer feature to Outsystems through Cordova.

**Features:**
 - Lets you ask permission for "Activity Recognition" needed for the pedometer app.
 - Lets you capture the steps being detected by android while the application is active. (steps starts at 0)

## Usage

For Cordova:

 - First the application needs to check for permission
 - If the application does not have permission it will need to request the permission
 - When the permission is already granted. You can now call the "Start Pedometer" function
 - You will then need a listener that executes when ever a step event is happening
 - And finally if you are going to exit the app call the "Stop Pedometer" function

To check if the application has permission: 

    function onSuccess (data) 
    {
        var hasPermission = data.hasPermission;
        var success = true;
    }
    
    function onFail (error) 
    {
        var errorMsg = error;
        var success = true;
    }
    
    var androidpedometer = cordova.plugins.AndroidCordovaPedometer;
    androidpedometer.checkPermissionActivityRecognition(onSuccess, onFail);

To request permission for the application

    function onSuccess (data) 
    {
        var hasPermission = data.hasPermission;
        var success = true;
    }
    
    function onFail (error) 
    {
        var errorMsg = error;
        var success = true;
    }
    
    var androidpedometer = cordova.plugins.AndroidCordovaPedometer;
    androidpedometer.requestPermissionActivityRecognition(onSuccess, onFail);

To Start the Pedometer

    function onSuccess (data) 
    {
        var success = true;
    }
    
    function onFail (error)
    {
        var errorMsg = error;
        var success = false;
    }
    
    var androidpedometer = cordova.plugins.AndroidCordovaPedometer;
    androidpedometer.startPedometerUpdates(onSuccess, onFail);

To listen to the Pedometer Steps

    cordova.plugins.AndroidCordovaPedometer.onStepping
    (
        function(steps)
        {
            //function you want to do
        }
    );

To Stop the Pedometer

    function onSuccess (data) 
    {
        var success = true;
    }
    
    function onFail (error) 
    {
        var errorMsg = error;
        var success = false;
    }
    
    var androidpedometer = cordova.plugins.AndroidCordovaPedometer;
    androidpedometer.stopPedometer(onSuccess, onFail);

For Outsystems

Simply download the Forge component for this plugin. (links provided later)
