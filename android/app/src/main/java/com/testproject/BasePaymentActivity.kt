package com.testproject

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log

import com.oppwa.mobile.connect.checkout.meta.CheckoutActivityResult
import com.oppwa.mobile.connect.checkout.meta.CheckoutActivityResultContract
import com.oppwa.mobile.connect.checkout.meta.CheckoutSettings
import com.oppwa.mobile.connect.checkout.meta.CheckoutSkipCVVMode
import com.oppwa.mobile.connect.exception.PaymentError
import com.oppwa.mobile.connect.provider.Connect
import com.oppwa.mobile.connect.provider.Transaction
import com.oppwa.mobile.connect.provider.TransactionType
import com.oppwa.mobile.connect.utils.googlepay.CardPaymentMethodJsonBuilder
import com.oppwa.mobile.connect.utils.googlepay.PaymentDataRequestJsonBuilder
import com.oppwa.mobile.connect.utils.googlepay.TransactionInfoJsonBuilder
import com.oppwa.msa.MerchantServerApplication
import com.oppwa.msa.ServerMode
import com.testproject.common.Constants
import com.testproject.receiver.CheckoutBroadcastReceiver

import kotlinx.coroutines.ExperimentalCoroutinesApi

import org.json.JSONArray

private const val STATE_RESOURCE_PATH = "STATE_RESOURCE_PATH"

/**
 * Represents a base activity for making the payments with mobile sdk.
 * This activity handles payment callbacks.
 */
open class BasePaymentActivity : BaseActivity() {

    protected val checkoutLauncher = registerForActivityResult(
        CheckoutActivityResultContract()
    ) {
            result: CheckoutActivityResult -> this.handleCheckoutActivityResult(result)
    }

    protected var resourcePath: String? = null
    protected var isAsyncCallbackReceived = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            resourcePath = savedInstanceState.getString(STATE_RESOURCE_PATH)
        }
    }

    @ExperimentalCoroutinesApi
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        setIntent(intent)

        /* Check of the intent contains the callback scheme */

        if (hasCallBackScheme(intent)) {
            isAsyncCallbackReceived = true

            if (resourcePath != null) {
                requestPaymentStatus(resourcePath!!)
            }
        }
    }

    private fun handleCheckoutActivityResult(result: CheckoutActivityResult) {
        hideProgressBar()



        if (result.isCanceled) {
            return
        }

        if (result.isErrored) {
            val error: PaymentError? = result.paymentError
            error?.let { showAlertDialog(it.errorMessage) }

            return
        }

        /* Transaction completed */
        val transaction: Transaction? = result.transaction
        Log.d("statuswcecew", transaction.toString()+"0000")

        resourcePath = result.resourcePath

        /* Check the transaction type */
        if (transaction != null) {
            if (transaction.transactionType == TransactionType.SYNC) {
                /* Check the status of synchronous transaction */
                requestPaymentStatus(resourcePath!!)
            } else if (isAsyncCallbackReceived) {
                /* Asynchronous transaction is processed in the onNewIntent()
                   NOTE: Prior API level 29 the onNewIntent() will be called before ActivityResultCallback */
                requestPaymentStatus(resourcePath!!)
            }
        }
    }

    open fun onCheckoutIdReceived(checkoutId: String?) {
        if (checkoutId == null) {
            hideProgressBar()
            showAlertDialog(R.string.error_message)
        } else {
            resourcePath = null
            isAsyncCallbackReceived = false
        }
    }

     fun requestCheckoutId() {
        showProgressBar()



        MerchantServerApplication.requestCheckoutId(
            Constants.Config.AMOUNT,
            Constants.Config.CURRENCY,
            "PA",
            ServerMode.TEST,
            mapOf("notificationUrl" to Constants.NOTIFICATION_URL, "entityId" to Constants.MERCHANT_ID,
                "authentication.userId" to Constants.token_user_id, "authentication.password" to Constants.secret)
        ) { checkoutId, _ -> runOnUiThread { onCheckoutIdReceived(checkoutId) } }
    }

    protected fun requestPaymentStatus(resourcePath: String) {
        showProgressBar()
        Log.d("TAG", "requestPaymentStatus: "+ resourcePath)
        MerchantServerApplication.requestPaymentStatus(
            resourcePath
        ) { isSuccessful, _ -> runOnUiThread { onPaymentStatusReceived(isSuccessful) } }
    }

    protected fun createCheckoutSettings(checkoutId: String, callbackScheme: String): CheckoutSettings {
        return CheckoutSettings(checkoutId, Constants.Config.PAYMENT_BRANDS,
            Connect.ProviderMode.TEST)

            .setSkipCVVMode(CheckoutSkipCVVMode.FOR_STORED_CARDS)
            .setShopperResultUrl("$callbackScheme://callback")
         //   .setGooglePayPaymentDataRequestJson(getGooglePayPaymentDataRequestJson())
        /* Set componentName if you want to receive callbacks from the checkout */
                .setComponentName(ComponentName(packageName, CheckoutBroadcastReceiver::class.java.name))
    }

    private fun hasCallBackScheme(intent: Intent): Boolean {
        return intent.scheme == getString(R.string.checkout_ui_callback_scheme) ||
                intent.scheme == getString(R.string.payment_button_callback_scheme) ||
                intent.scheme == getString(R.string.custom_ui_callback_scheme)
    }

    @SuppressLint("LongLogTag")
    private fun onPaymentStatusReceived(isSuccessful: Boolean) {
        hideProgressBar()

        Log.d("statuswcecew", isSuccessful.toString())
       var resultCode=0
        val message = if (isSuccessful) {
            R.string.message_successful_payment

            resultCode = Activity.RESULT_OK;

        } else {
            R.string.message_unsuccessful_payment
            resultCode = Activity.RESULT_CANCELED;
        }


        val resultIntent = Intent()

        // Finish the activity with the result code and intent
        setResult(resultCode, resultIntent)
        finish()
        Log.d("CheckoutUIActivity fefe", "Result code: $resultCode"); // Add this line for logging
        // Pass the result to your CustomMethods module
        CustomMethods.onActivityResult(
            123 // You can choose any request code
            , resultCode, null)
    }

    private fun getGooglePayPaymentDataRequestJson() : String {
        val allowedPaymentMethods = JSONArray()
            .put(CardPaymentMethodJsonBuilder()
                .setAllowedAuthMethods(JSONArray()
                    .put("PAN_ONLY")
                    .put("CRYPTOGRAM_3DS")
                )
                .setAllowedCardNetworks(JSONArray()
                    .put("VISA")
                    .put("MASTERCARD")
                    .put("AMEX")
                    .put("DISCOVER")
                    .put("JCB")
                )
                .setGatewayMerchantId(Constants.MERCHANT_ID)
                .toJson()
            )

        val transactionInfo = TransactionInfoJsonBuilder()
            .setCurrencyCode(Constants.Config.CURRENCY)
            .setTotalPriceStatus("FINAL")
            .setTotalPrice(Constants.Config.AMOUNT)
            .toJson()

        val paymentDataRequest = PaymentDataRequestJsonBuilder()
            .setAllowedPaymentMethods(allowedPaymentMethods)
            .setTransactionInfo(transactionInfo)
            .toJson()

        return paymentDataRequest.toString()
    }
}