package crypto.delta.exchange.openexchange.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import crypto.delta.exchange.openexchange.R


class AppPreferenceManager(private val context: Context) {

    private val prefs: SharedPreferences = getEncryptedSharedPreferences()!!

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

    private fun getEncryptedSharedPreferences(): SharedPreferences? {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            "secret_shared_prefs_file",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}