package crypto.delta.exchange.openexchange

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import crypto.delta.exchange.openexchange.utils.setupWithNavController
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    private var connectivityDisposable: Disposable? = null
    private var internetDisposable: Disposable? = null
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)

        val menuView =
            bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        for (i in 0 until menuView.childCount) {
            val iconView: View =
                menuView.getChildAt(i).findViewById(androidx.navigation.ui.R.id.icon)
            val layoutParams: ViewGroup.LayoutParams = iconView.layoutParams
            val displayMetrics = resources.displayMetrics
            // If it is my special menu item change the size, otherwise take other size
            if (i == 2) {
                // set your height here
                layoutParams.height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    45f,
                    displayMetrics
                ).toInt()
                // set your width here
                layoutParams.width = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    45f,
                    displayMetrics
                ).toInt()
            } else {
                // set your height here
                layoutParams.height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    25f,
                    displayMetrics
                ).toInt()
                // set your width here
                layoutParams.width = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    25f,
                    displayMetrics
                ).toInt()
            }
            iconView.layoutParams = layoutParams
        }

        val navGraphIds =
            listOf(
                R.navigation.home,
                R.navigation.chart,
                R.navigation.order,
                R.navigation.wallet,
                R.navigation.settings
            )

        // Setup the bottom navigation view with a list of navigation graphs
        bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment,
            intent = intent
        )
    }

    override fun onResume() {
        super.onResume()

        connectivityDisposable = ReactiveNetwork.observeNetworkConnectivity(applicationContext)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { connectivity ->
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, connectivity.toString())
                    val state = connectivity.state()
                    val name = connectivity.typeName()
                    Log.i(TAG, String.format("state: %s, typeName: %s", state, name))
                }
            }

        internetDisposable = ReactiveNetwork.observeInternetConnectivity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isConnectedToInternet ->
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.Main) {
                        if (isConnectedToInternet) {
                            if (null != alertDialog) {
                                if (alertDialog!!.isShowing) {
                                    alertDialog!!.dismiss()
                                }
                            }
                        } else {
                            alertDialog =
                                AlertDialog.Builder(this@MainActivity, R.style.AlertDialogStyle)
                                    .setTitle("No Internet!!")
                                    .setMessage("Check your internet connectivity!")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setView(R.layout.progress_bar)
                                    .setCancelable(false)
                                    .show()
                        }
                    }
                }
            }
    }

    override fun onPause() {
        super.onPause()
        safelyDispose(connectivityDisposable)
        safelyDispose(internetDisposable)
    }

    private fun safelyDispose(disposable: Disposable?) {
        if (disposable != null && !disposable.isDisposed) {
            disposable.dispose()
        }
    }

    companion object {
        private const val TAG = "ReactiveNetwork"
    }
}
