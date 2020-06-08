package crypto.delta.exchange.openexchange.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import crypto.delta.exchange.openexchange.R

class AppPreferenceManager(private val context: Context) {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    val apiKey: String?
        get() = prefs.getString(context.resources.getString(R.string.api_key_preference), "")

    fun setApiKey(value: String?) {
        prefs.edit().putString(context.resources.getString(R.string.api_key_preference), value!!).apply()
    }

    val apiSecret: String?
        get() = prefs.getString(context.resources.getString(R.string.api_secret_preference), "")

    fun setApiSecret(value: String?) {
        prefs.edit().putString(context.resources.getString(R.string.api_secret_preference), value!!).apply()
    }
}