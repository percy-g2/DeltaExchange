package crypto.delta.exchange.openexchange.utils

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import crypto.delta.exchange.openexchange.R
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.concurrent.TimeUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


object KotlinUtils {
    fun generateSignature(signatureData: String, apiSecret: String): String? {
        val algorithm = "HmacSHA256"
        var digest: String? = null
        val tag = "Utils"
        try {
            val key = SecretKeySpec(apiSecret.toByteArray(charset("UTF-8")), algorithm)
            val mac = Mac.getInstance(algorithm)
            mac.init(key)
            val bytes = mac.doFinal(signatureData.toByteArray(charset("UTF-8")))
            val hash = StringBuilder()
            for (aByte in bytes) {
                val hex = Integer.toHexString(0xFF and aByte.toInt())
                if (hex.length == 1) {
                    hash.append('0')
                }
                hash.append(hex)
            }
            digest = hash.toString()
        } catch (e: Throwable) {
            Log.e(tag, e.message.toString())
        } catch (e: InvalidKeyException) {
            Log.e(tag, e.message.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e(tag, e.message.toString())
        }
        return digest
    }

    fun generateTimeStamp(): String {
        return (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())).toString()
    }

    fun showProgressBar(context: Context, dialogMsg: String): AlertDialog {
        val progressBar = ProgressBar(
            context,
            null,
            android.R.attr.progressBarStyleHorizontal)

        progressBar.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        progressBar.isIndeterminate = true

        val container = LinearLayout(context)

        container.addView(progressBar)

        val padding: Int = getDialogPadding(context)

        container.setPadding(
            padding, 0, padding, 0)

        val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.AlertDialogStyle).setMessage(dialogMsg).setView(container)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        return dialog
    }

    private fun getDialogPadding(context: Context): Int {
        val sizeAttr = intArrayOf(androidx.appcompat.R.attr.dialogPreferredPadding)
        val a = context.obtainStyledAttributes(TypedValue().data, sizeAttr)
        val size = a.getDimensionPixelSize(0, -1)
        a.recycle()
        return size
    }

    fun apiDetailsPresent(context: Context): Boolean {
        val appPreferenceManager = AppPreferenceManager(context)
        return !(appPreferenceManager.apiKey!!.isEmpty() || appPreferenceManager.apiSecret!!.isEmpty())
    }
}