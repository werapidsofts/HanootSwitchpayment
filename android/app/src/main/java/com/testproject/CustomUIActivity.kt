package com.testproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import com.oppwa.mobile.connect.exception.PaymentError
import com.oppwa.mobile.connect.exception.PaymentException
import com.oppwa.mobile.connect.payment.CheckoutInfo
import com.oppwa.mobile.connect.payment.card.CardPaymentParams
import com.oppwa.mobile.connect.provider.*
import com.testproject.common.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi


class CustomUIActivity : BasePaymentActivity(), ITransactionListener {

    private lateinit var checkoutId: String
    private var paymentProvider: OppPaymentProvider? = null
    lateinit var holder_edit_text:EditText
    lateinit var number_edit_text:EditText
    lateinit var expiry_month_edit_text:EditText
    lateinit var expiry_year_edit_text:EditText
    lateinit var cvv_edit_text:EditText
    lateinit var progress_bar_custom_ui:ProgressBar
    lateinit var button_pay_now:Button

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_custom_ui)
        holder_edit_text=findViewById(R.id.holder_edit_text)
        number_edit_text=findViewById(R.id.number_edit_text)
        expiry_month_edit_text=findViewById(R.id.expiry_month_edit_text)
        expiry_year_edit_text=findViewById(R.id.expiry_year_edit_text)
        cvv_edit_text=findViewById(R.id.cvv_edit_text)
        button_pay_now=findViewById(R.id.button_pay_now)
        progress_bar_custom_ui=findViewById(R.id.progress_bar_custom_ui)

        paymentProvider = OppPaymentProvider(this, Connect.ProviderMode.TEST)
        paymentProvider!!.setThreeDSWorkflowListener { this }

        initViews()
    }

    override fun onCheckoutIdReceived(checkoutId: String?) {
        super.onCheckoutIdReceived(checkoutId)

        checkoutId?.let {
            this.checkoutId = checkoutId
            requestCheckoutInfo(checkoutId)
        }
    }

    //region ITransactionListener methods
    override fun paymentConfigRequestSucceeded(checkoutInfo: CheckoutInfo) {
        /* Get the resource path from checkout info to request the payment status later */
        resourcePath = checkoutInfo.resourcePath

        runOnUiThread {
            showConfirmationDialog(checkoutInfo.amount.toString(),
                    checkoutInfo.currencyCode!!
            )
        }
    }

    override fun paymentConfigRequestFailed(paymentError: PaymentError) {
        hideProgressBar()
        showErrorDialog(paymentError)
    }

    @ExperimentalCoroutinesApi
    override fun transactionCompleted(transaction: Transaction) {
        if (transaction.transactionType == TransactionType.SYNC) {
            /* check the status of synchronous transaction */
            requestPaymentStatus(resourcePath!!)
        } else {
            /* wait fot the callback in the onNewIntent() */
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(transaction.redirectUrl)))
        }
    }

    override fun transactionFailed(transaction: Transaction, paymentError: PaymentError) {
        hideProgressBar()
        showErrorDialog(paymentError)
    }
    //endregion

    @ExperimentalCoroutinesApi
    private fun initViews() {
        holder_edit_text.setText(Constants.Config.CARD_HOLDER_NAME)
        number_edit_text.setText(Constants.Config.CARD_NUMBER)
        expiry_month_edit_text.setText(Constants.Config.CARD_EXPIRY_MONTH)
        expiry_year_edit_text.setText(Constants.Config.CARD_EXPIRY_YEAR)
        cvv_edit_text.setText(Constants.Config.CARD_CVV)
        progressBar = progress_bar_custom_ui

        button_pay_now.setOnClickListener {
            if (paymentProvider != null && checkFields()) {
                requestCheckoutId()
            }
        }
    }

    private fun checkFields(): Boolean {
        if (holder_edit_text.text.isEmpty() ||
                number_edit_text.text.isEmpty() ||
                expiry_month_edit_text.text.isEmpty() ||
                expiry_year_edit_text.text.isEmpty() ||
                cvv_edit_text.text.isEmpty()) {
            showAlertDialog(R.string.error_empty_fields)

            return false
        }

        return true
    }

    private fun requestCheckoutInfo(checkoutId: String) {
        try {
            paymentProvider!!.requestCheckoutInfo(checkoutId, this)
        } catch (e: PaymentException) {
            e.message?.let { showAlertDialog(it) }
        }
    }

    private fun pay(checkoutId: String) {
        try {
            val paymentParams = createPaymentParams(checkoutId)
            paymentParams.shopperResultUrl = getString(R.string.custom_ui_callback_scheme) + "://callback"
            val transaction = Transaction(paymentParams)

            paymentProvider!!.submitTransaction(transaction, this)
        } catch (e: PaymentException) {
            showErrorDialog(e.error)
        }
    }

    private fun createPaymentParams(checkoutId: String): CardPaymentParams {
        val cardHolder = holder_edit_text.text.toString()
        val cardNumber = number_edit_text.text.toString()
        val cardExpiryMonth = expiry_month_edit_text.text.toString()
        val cardExpiryYear = expiry_year_edit_text.text.toString()
        val cardCVV = cvv_edit_text.text.toString()

        return CardPaymentParams(
                checkoutId,
                Constants.Config.CARD_BRAND,
                cardNumber,
                cardHolder,
                cardExpiryMonth,
                "20$cardExpiryYear",
                cardCVV
        )
    }

    private fun showConfirmationDialog(amount: String, currency: String) {
        AlertDialog.Builder(this)
                .setMessage(String.format(getString(R.string.message_payment_confirmation), amount, currency))
                .setPositiveButton(R.string.button_ok) { _, _ -> pay(checkoutId) }
                .setNegativeButton(R.string.button_cancel) { _, _ -> hideProgressBar()}
                .setCancelable(false)
                .show()
    }

    private fun showErrorDialog(message: String) {
        runOnUiThread { showAlertDialog(message) }
    }

    private fun showErrorDialog(paymentError: PaymentError) {
        runOnUiThread { showErrorDialog(paymentError.errorMessage) }
    }
}