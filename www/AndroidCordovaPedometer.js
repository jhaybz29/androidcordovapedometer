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

module.exports = new Acpedometer();