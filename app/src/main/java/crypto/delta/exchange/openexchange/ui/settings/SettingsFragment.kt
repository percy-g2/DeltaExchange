package crypto.delta.exchange.openexchange.ui.settings

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import crypto.delta.exchange.openexchange.R
import crypto.delta.exchange.openexchange.ui.base.BaseFragment
import crypto.delta.exchange.openexchange.utils.KotlinUtils
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.*
import kotlin.concurrent.schedule


class SettingsFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edtApiKey.setText(appPreferenceManager!!.apiKey!!)
        edtApiSecret.setText(appPreferenceManager!!.apiSecret!!)


        edtApiKey.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                if (editable!!.isNotEmpty()) {
                    appPreferenceManager!!.setApiKey(editable.toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        edtApiSecret.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                if (editable!!.isNotEmpty()) {
                    appPreferenceManager!!.setApiSecret(editable.toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        useTestNetServer.isChecked = appPreferenceManager!!.useTestNetServer!!

        useTestNetServer.setOnCheckedChangeListener { _, b ->
            val progressBar = KotlinUtils.showProgressBar(
                requireActivity(),
                requireContext().resources.getString(R.string.please_wait_restarting)
            )
            appPreferenceManager!!.setUseTestNetServer(useTestNetServer.isChecked)
            Timer().schedule(1000){
                progressBar.dismiss()
                val packageManager = requireContext().packageManager
                val intent = packageManager.getLaunchIntentForPackage(requireContext().packageName)
                val componentName = intent!!.component
                val mainIntent = Intent.makeRestartActivityTask(componentName)
                requireContext().startActivity(mainIntent)
                Runtime.getRuntime().exit(0)
            }

        }
    }
}