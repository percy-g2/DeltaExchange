package crypto.delta.exchange.openexchange.ui.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import crypto.delta.exchange.openexchange.api.DeltaApiClient
import crypto.delta.exchange.openexchange.api.DeltaExchangeApiEndPoints
import crypto.delta.exchange.openexchange.utils.AppPreferenceManager

open class BaseFragment : Fragment() {
    var service: DeltaExchangeApiEndPoints? = null
    var appPreferenceManager: AppPreferenceManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        service = DeltaApiClient().getClient(requireActivity())!!
            .create(DeltaExchangeApiEndPoints::class.java)
        appPreferenceManager = AppPreferenceManager(requireContext())
    }
}