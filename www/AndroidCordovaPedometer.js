var exec = require('cordova/exec');
var pluginClassName = 'AndroidCordovaPedometer';

function Acpedometer()
{

}

Acpedometer.prototype.checkPermissionActivityRecognition = function(success, error)
{
    exec(success, error, pluginClassName, 'checkPermissionActivityRecognition', []);
}

Acpedometer.prototype.requestPermissionActivityRecognition = function(success, error)
{
    exec(success, error, pluginClassName, 'requestPermissionActivityRecognition', []);
}

Acpedometer.prototype.startPedometerUpdates = function(success, error)
{
    exec(success, error, pluginClassName, 'startPedometerUpdates', []);
}

Acpedometer.prototype.stopPedometer = function(success, error)
{
    exec(success, error, pluginClassName, 'stopPedometer', []);
}

Acpedometer.prototype.onStepping = function(callback)
{
    Acpedometer.prototype.onSteppingRecieved = callback;
}

Acpedometer.prototype.onSteppingRecieved = function(steps)
{
    console.log("On Steps")
    console.log(steps)
}

module.exports = new Acpedometer();