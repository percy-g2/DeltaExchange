package crypto.delta.exchange.openexchange.utils

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
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

    val currentProductId: String?
        get() = prefs.getString(context.resources.getString(R.string.product_id_preference), "27")

    fun setCurrentProductId(value: String?) {
        prefs.edit().putString(context.resources.getString(R.string.product_id_preference), value!!)
            .apply()
    }

    val currentProductSymbol: String?
        get() = prefs.getString(
            context.resources.getString(R.string.product_symbol_preference),
            "BTCUSD"
        )

    fun setCurrentProductSymbol(value: String?) {
        prefs.edit()
            .putString(context.resources.getString(R.string.product_symbol_preference), value!!)
            .apply()
    }

    val currentChartResolution: String?
        get() = prefs.getString(
            context.resources.getString(R.string.chart_resolution_preference),
            "5"
        )

    fun setChartResolution(value: String?) {
        prefs.edit()
            .putString(context.resources.getString(R.string.chart_resolution_preference), value!!)
            .apply()
    }

    val productSymbolsList: MutableSet<String>?
        get() = prefs.getStringSet(
            context.resources.getString(R.string.product_symbols_list_preference),
            mutableSetOf()
        )

    fun setProductSymbolsList(value: MutableSet<String>?) {
        prefs.edit()
            .putStringSet(
                context.resources.getString(R.string.product_symbols_list_preference),
                value!!
            )
            .apply()
    }

    private fun getEncryptedSharedPreferences(): SharedPreferences? {

        val spec = KeyGenParameterSpec.Builder(
            MasterKey.DEFAULT_MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
            .build()

        val masterKey = MasterKey.Builder(context)
            .setKeyGenParameterSpec(spec)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "secret_shared_prefs_file",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}