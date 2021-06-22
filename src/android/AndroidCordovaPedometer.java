package com.jvcaalim.cordova.androidpedometer;

import android.util.Log;

import android.os.Build;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class AndroidCordovaPedometer extends CordovaPlugin implements SensorEventListener 
{
    public static CordovaWebView gWebView;

    private static final String PERMISSION_ID_ACTIVITY_RECOGNITION = "android.permission.ACTIVITY_RECOGNITION";
    private static final int REQUEST_CODE_ENABLE_PERMISSION = 3256;

    private static final String KEY_ERROR = "error";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_RESULT_PERMISSION = "hasPermission";
    private static String stepsCallBack = "cordova.plugins.AndroidCordovaPedometer.onSteppingRecieved";

    private SensorManager sensorManager; 
    private Sensor stepSensor;

    private boolean isSensorRunning;
    private boolean isFirstRun = true;
    private int totalSteps = 0;
    private int previousSteps = 0;
    private int currentSteps = 0;

    private CallbackContext permissionsCallback;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) 
    {
        super.initialize(cordova, webView);
        gWebView = webView;
        sensorManager = (SensorManager) cordova.getActivity().getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onResume(boolean multitasking) 
    {
        super.onResume(multitasking);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException 
	{
        if ( action.equals("checkPermissionActivityRecognition") ) 
		{
            cordova.getThreadPool().execute
            (
                new Runnable() 
                {
                    public void run() 
                    {
                        checkPermissionActivityRecognition(callbackContext);
                    }
                }
            );

            return true;
        }
        else if( action.equals("requestPermissionActivityRecognition") )
        {
            cordova.getThreadPool().execute
            (
                new Runnable() 
                {
                    public void run() 
                    {
                        requestPermissionActivityRecognition(callbackContext);
                    }
                }
            );

            return true;
        }
        else if( action.equals("startPedometerUpdates") )
        {
            cordova.getThreadPool().execute
            (
                new Runnable() 
                {
                    public void run() 
                    {
                        startPedometerUpdates();
                    }
                }
            );

            return true;
        }
        else if( action.equals("stopPedometer") )
        {
            cordova.getThreadPool().execute
            (
                new Runnable() 
                {
                    public void run() 
                    {
                        stopPedometer();
                    }
                }
            );

            return true;
        }

        return false;
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException 
    {
        if (permissionsCallback == null) 
        {
            return;
        }

        JSONObject returnObj = new JSONObject();
        if (permissions != null && permissions.length > 0) 
        {
            boolean hasPermission = permissions[0].equals(PERMISSION_ID_ACTIVITY_RECOGNITION);

            addProperty(returnObj, KEY_RESULT_PERMISSION, hasPermission);
            permissionsCallback.success(returnObj);
        } 
        else 
        {
            addProperty(returnObj, KEY_ERROR, "Request Permission");
            addProperty(returnObj, KEY_MESSAGE, "Unknown error.");
            permissionsCallback.error(returnObj);
        }
        permissionsCallback = null;
    }

    private void checkPermissionActivityRecognition(CallbackContext callbackContext)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) 
        {
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, KEY_RESULT_PERMISSION, true);
            callbackContext.success(returnObj);
        }
        else 
        {
            JSONObject returnObj = new JSONObject();
            addProperty( returnObj, KEY_RESULT_PERMISSION, cordova.hasPermission(PERMISSION_ID_ACTIVITY_RECOGNITION) );
            callbackContext.success(returnObj);
        }
    }

    private void requestPermissionActivityRecognition(CallbackContext callbackContext)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) 
        {
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, KEY_RESULT_PERMISSION, true);
            callbackContext.success(returnObj);
        }
        else
        {
            permissionsCallback = callbackContext;
            cordova.requestPermission(this, REQUEST_CODE_ENABLE_PERMISSION, PERMISSION_ID_ACTIVITY_RECOGNITION);
        }
    }

    private void addProperty(JSONObject obj, String key, Object value) 
    {
        try 
        {
            if (value == null) 
            {
                obj.put(key, JSONObject.NULL);
            } 
            else 
            {
                obj.put(key, value);
            }
        } 
        catch (JSONException ignored) 
        {
            //Believe exception only occurs when adding duplicate keys, so just ignore it
        }
    }

    private void startPedometerUpdates()
    {
        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null)
        {
            isSensorRunning = true;
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
        else
        {
            isSensorRunning = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) 
    {
        if(isSensorRunning)
        {
            if(isFirstRun)
            {
                isFirstRun = false;
                previousSteps = (int) sensorEvent.values[0];
            }
            totalSteps = (int) sensorEvent.values[0];
            currentSteps = totalSteps - previousSteps;

            //fire the stepping event
            try 
            {
                String callBack = "javascript:" + stepsCallBack + "(" + currentSteps + ")";
                if(gWebView != null)
                {
                    gWebView.sendJavascript(callBack);
                }
                else 
                {
                    //gWebView is null
                }
            } 
            catch (Exception e) 
            {
                //error
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) 
    {
        //required Override
    }

    @Override
    public void onDestroy() 
    {
        stopPedometer();
    }

    private void stopPedometer()
    {
        if(isSensorRunning == true)
        {
            isSensorRunning= false;
            sensorManager.unregisterListener(this);
        }
    }

}
