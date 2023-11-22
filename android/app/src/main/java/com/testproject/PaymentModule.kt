package com.testproject

import android.content.Intent
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class PaymentModule(reactContext: ReactApplicationContext?) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String {
        return "PaymentModule"
    }

    @ReactMethod
    fun startPaymentActivity() {
        // Start the PaymentButtonActivity here
        val intent = Intent(reactApplicationContext, PaymentButtonActivity::class.java)
        reactApplicationContext.startActivity(intent)
    }
}