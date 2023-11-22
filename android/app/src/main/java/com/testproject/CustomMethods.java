package com.testproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.testproject.common.Constants;


public class CustomMethods extends ReactContextBaseJavaModule   {
//public class CustomMethods extends ReactContextBaseJavaModule  {

CheckoutUIActivity checkoutUIActivity;
static ReactApplicationContext reactAppContext;


  static   Callback paymentCallback;
    private static final int REQUEST_CODE_CHECKOUT = 123; // You can choose any request code

    public CustomMethods(ReactApplicationContext reactContext) {
        super(reactContext);
        reactAppContext = reactContext;
    }

    @Override
    public String getName() {
        return "CustomMethods";
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @ReactMethod
    public void initiatePaymentWith(String argument, Callback callback) {

        // Your native code logic goes here
        // For example, you can call native payment libraries, perform actions, and get a result
        String result = "Payment initiated with: " + argument;
        // Store the callback for later use


    Log.d("data", argument+"--");

    JsonObject jsonData = new Gson().fromJson(argument, JsonObject.class);
    String amount = jsonData.get("amount").getAsString();
    String currency = jsonData.get("currency").getAsString();
    String merchantId = jsonData.get("merchant_ID").getAsString();
    String token_user_id = jsonData.get("token_user_id").getAsString();
    String secret = jsonData.get("secret_key").getAsString();

        Constants.MERCHANT_ID = merchantId;
//        Constants.Config.CURRENCY = currency;
//        Constants.Config.AMOUNT = amount;
//        Constants.token_user_id = token_user_id;
//        Constants.secret = secret;

      //  Log.d("data", argument+"--");


        // callback.invoke(result);

        // Create an intent to start the CheckoutUIActivity
        Activity currentActivity = getCurrentActivity();
        if (currentActivity != null) {
            Intent intent = new Intent(currentActivity, CheckoutUIActivity.class);
            currentActivity.startActivityForResult(intent, REQUEST_CODE_CHECKOUT);

            // Store the callback for later use
            paymentCallback = callback;
        }
    }




    public static void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_CHECKOUT) {
            if (resultCode == Activity.RESULT_OK) {
                // The checkout was successful
                String result = "Payment successful";
                if (paymentCallback != null) {
                    paymentCallback.invoke(result);
                }
            } else {
                // The checkout was canceled or failed
                String result = "Payment canceled or failed";
                if (paymentCallback != null) {
                    paymentCallback.invoke(result);
                }
            }
        }
    }

}