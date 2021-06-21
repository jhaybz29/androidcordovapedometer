package com.jvcaalim.cordova.androidpedometer;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class AndroidCordovaPedometer extends CordovaPlugin 
{
    private static final String PERMISSION_ID_ACTIVITY_RECOGNITION = "android.permission.ACTIVITY_RECOGNITION";
    private static final int REQUEST_CODE_ENABLE_PERMISSION = 3256;

    private static final String KEY_ERROR = "error";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_RESULT_PERMISSION = "hasPermission";

    private CallbackContext permissionsCallback;

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
            addProperty(returnObj, KEY_ERROR, ACTION_REQUEST_PERMISSION);
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
}
