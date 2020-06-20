package crypto.delta.exchange.openexchange

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import crypto.delta.exchange.openexchange.utils.KotlinUtils
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        applicationVersion.text = String.format(
            "%s %s",
            getString(R.string.version),
            KotlinUtils.getAppVersion(this@SplashActivity)
        )
        val splashTimeOut = 2000
        /*
         * Showing splash screen with a timer. This will be useful when you
         * want to show case your app logo / company
         */
        lifecycleScope.launch {
            delay(splashTimeOut.toLong())
            // This method will be executed once the timer is over
            // Start your app main activity
            val i = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(i)
            // close this activity
            //finish()
        }
    }
}