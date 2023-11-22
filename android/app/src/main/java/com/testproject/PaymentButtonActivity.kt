package com.testproject


import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import com.oppwa.mobile.connect.checkout.dialog.PaymentButtonFragment
import com.oppwa.mobile.connect.exception.PaymentException
import com.testproject.common.Constants

import kotlinx.coroutines.ExperimentalCoroutinesApi


/**
 * Represents an activity for making payments via {@link PaymentButtonSupportFragment}.
 */
class PaymentButtonActivity : BasePaymentActivity() {

    private lateinit var paymentButtonFragment: PaymentButtonFragment
    lateinit var amount_text_view:TextView
    lateinit var progress_bar_payment_button:ProgressBar

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_payment_button)
        amount_text_view=findViewById(R.id.amount_text_view)
        progress_bar_payment_button=findViewById(R.id.progress_bar_payment_button)

        val amount = Constants.Config.AMOUNT + " " + Constants.Config.CURRENCY
        amount_text_view.text = amount

        progressBar = progress_bar_payment_button

        initPaymentButton()
    }

    override fun onCheckoutIdReceived(checkoutId: String?) {
        super.onCheckoutIdReceived(checkoutId)

        checkoutId?.let { pay(checkoutId) }
    }

    @ExperimentalCoroutinesApi
    private fun initPaymentButton() {
        paymentButtonFragment = supportFragmentManager.findFragmentById(R.id.payment_button_fragment) as PaymentButtonFragment

        paymentButtonFragment.paymentBrand = Constants.Config.PAYMENT_BUTTON_BRAND
        paymentButtonFragment.paymentButton.apply {
            setOnClickListener {
                requestCheckoutId()
            }

            /* Customize the payment button (except Google Pay button) */
            setBackgroundResource(R.drawable.drop_in_button_background)
        }
    }

    private fun pay(checkoutId: String) {
        val checkoutSettings = createCheckoutSettings(
                checkoutId,
                getString(R.string.payment_button_callback_scheme)
        )

        try {
            paymentButtonFragment.setActivityResultLauncher(checkoutLauncher)
            paymentButtonFragment.submitTransaction(checkoutSettings)
        } catch (e: PaymentException) {
            showAlertDialog(R.string.error_message)
        }
    }
}