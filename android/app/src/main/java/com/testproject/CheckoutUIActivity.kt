package com.testproject


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.testproject.common.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi


/**
 * Represents an activity for making payments via {@link CheckoutActivity}.
 */
 class CheckoutUIActivity : BasePaymentActivity() {
    lateinit var amount_text_view:TextView
    lateinit var progress_bar_checkout_ui:ProgressBar
    lateinit var button_proceed_to_checkout:Button

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_checkout_ui)
        amount_text_view=findViewById(R.id.amount_text_view)
        progress_bar_checkout_ui=findViewById(R.id.progress_bar_checkout_ui)
        button_proceed_to_checkout=findViewById(R.id.button_proceed_to_checkout)

        val amount = Constants.Config.AMOUNT + " " + Constants.Config.CURRENCY

       // amount_text_view.text = amount
        progressBar = progress_bar_checkout_ui

        button_proceed_to_checkout.setOnClickListener {
            requestCheckoutId()
        }
    }



    override fun onCheckoutIdReceived(checkoutId: String?) {
        super.onCheckoutIdReceived(checkoutId)
        if (checkoutId != null) {
            openCheckoutUI(checkoutId)
        }
    }

    private fun openCheckoutUI(checkoutId: String) {
        val checkoutSettings = createCheckoutSettings(
                checkoutId,
                getString(R.string.checkout_ui_callback_scheme)
        )

        /* Start the checkout activity */
        checkoutLauncher.launch(checkoutSettings)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("CheckoutUIActivity dede", "Result code: $resultCode"); // Add this line for logging



    }

}