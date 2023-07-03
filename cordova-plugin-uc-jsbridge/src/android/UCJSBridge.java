package org.apache.cordova.ucjsbridge;

import static one.upswing.sdk.UpswingSdkKt.upswingSdkLogout;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import one.upswing.sdk.UpswingSdk;

public class UCJSBridge extends CordovaPlugin {

  protected static UCJSBridge instance = null;
  protected static Context applicationContext = null;
  private static Activity cordovaActivity = null;
  protected static final String TAG = "UCJSBridge";

  @Override
  protected void pluginInitialize() {
    instance = this;
    cordovaActivity = this.cordova.getActivity();
    applicationContext = cordovaActivity.getApplicationContext();

    this.cordova.getThreadPool().execute(new Runnable() {
      public void run() {
        try {
          Log.d(TAG, "Starting UCJSBridge Plugin");
        } catch (Exception e) {
          Log.d(TAG, "Exception In UCJSBridge Plugin");
        }
      }
    });
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    try {
      if (action.equals("initUpswingSDK")) {
        this.initUpswingSDK(callbackContext, args.getString(0));
      } else if (action.equals("logOutFromUpswingSDK")) {
        this.logOutFromUpswingSDK(callbackContext);
      } else {
        callbackContext.error("Invalid Action: " + action);
        return false;
      }

      Log.d(TAG, "Current Action: " + action);
    } catch (Exception e) {
      handleExceptionWithContext(e, callbackContext);
      return false;
    }

    return true;
  }

  public void initUpswingSDK(final CallbackContext callbackContext, final String partnerUID) {
    cordova.getThreadPool().execute(new Runnable() {
      public void run() {
        try {
          if (partnerUID != null && !partnerUID.isEmpty()) {
            int statusBarColor = cordovaActivity.getResources().getColor(
                cordovaActivity.getResources().getIdentifier("cdv_splashscreen_background", "color",
                    cordovaActivity.getPackageName()));
            UpswingSdk upswingSdk = new UpswingSdk.Builder(cordovaActivity, partnerUID)
                .setStatusBarColor(statusBarColor)
                .setDeviceLockedEnabled(true)
                .build();
            upswingSdk.initializeSdk();
            Log.d(TAG, "initUpswingSDK: SDK Initialized");
            callbackContext.success("SDK Initialized");
          } else {
            Log.d(TAG, "initUpswingSDK: Invalid partnerUID");
            callbackContext.error("Invalid partnerUID");
          }
        } catch (Exception e) {
          handleExceptionWithContext(e, callbackContext);
        }
      }
    });
  }

  public void logOutFromUpswingSDK(final CallbackContext callbackContext) {
    cordova.getThreadPool().execute(new Runnable() {
      public void run() {
        try {
          upswingSdkLogout(cordovaActivity);
          Log.d(TAG, "logOutFromUpswingSDK: Success");
          callbackContext.success("logOutFromUpswingSDK: Success");
        } catch (Exception e) {
          handleExceptionWithContext(e, callbackContext);
        }
      }
    });
  }

  protected static void handleExceptionWithContext(Exception e, CallbackContext context) {
    String msg = e.toString();
    Log.d(TAG, "Exception: " + msg);
    context.error(msg);
  }
}
