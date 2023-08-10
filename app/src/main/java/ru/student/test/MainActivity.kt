package ru.student.test

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.remoteconfig.BuildConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import ru.student.test.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var url: String? = null
    private val remoteConfig by lazy {
        FirebaseRemoteConfig.getInstance()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.webView.restoreState(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setRemoteConfigSettings()
        getSavedUrl()

        if (url.isNullOrBlank()) {
            getUrlFromConfig(savedInstanceState)
        }
        else {
            if (!isNetworkAvailable(this)) {
                launchNoInternet()
            }
            setWebView(savedInstanceState, url!!)
        }

        handleOnBackPressed()


    }

    private fun setRemoteConfigSettings() {
        val settings =
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(0).build()
        remoteConfig.setConfigSettingsAsync(settings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_default)
    }

    private fun handleOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    //Ignore
                }
            }
        })
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }

    private fun launchPlaceholder() {
        Toast.makeText(this, "Placeholder", Toast.LENGTH_SHORT).show() //TODO: add placeholder
    }

    private fun getUrlFromConfig(savedInstanceState: Bundle?) {
        try {
            remoteConfig.fetchAndActivate().addOnCompleteListener(this) {
                if (!it.isSuccessful) {
                    launchPlaceholder()
                }
            }
        }
        catch (e: Exception) {
            launchNoInternet()
        }

        url = remoteConfig.getString("url")
        if (url.isNullOrBlank() || checkIsEmu()) {
            launchPlaceholder()
        } else {
            setWebView(savedInstanceState, url!!)
            saveUrl(url!!)
        }
    }

    private fun launchNoInternet() {
        val intent = Intent(this, NoInternetActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView(savedInstanceState: Bundle?, url: String) {
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                cookieManager.flush()
            }
        }
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.settings.useWideViewPort = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.databaseEnabled = true
        binding.webView.settings.setSupportZoom(false)
        binding.webView.settings.allowFileAccess = true
        binding.webView.settings.allowContentAccess = true
        binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true
        if (savedInstanceState != null) {
            binding.webView.restoreState(savedInstanceState)
        } else {
            binding.webView.loadUrl(url)
        }
    }

    private fun checkIsEmu(): Boolean {
        if (BuildConfig.DEBUG) return false // when developer use this build on emulator
        val phoneModel = Build.MODEL
        val buildProduct = Build.PRODUCT
        val buildHardware = Build.HARDWARE
        val brand = Build.BRAND
        return (Build.FINGERPRINT.startsWith("generic")
                || phoneModel.contains("google_sdk")
                || phoneModel.lowercase(Locale.getDefault()).contains("droid4x")
                || phoneModel.contains("Emulator")
                || phoneModel.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || buildHardware.equals("goldfish")
                || brand.contains("google")
                || buildHardware.equals("vbox86")
                || buildProduct.equals("sdk")
                || buildProduct.equals("google_sdk")
                || buildProduct.equals("sdk_x86")
                || buildProduct.equals("vbox86p")
                || Build.BOARD.lowercase(Locale.getDefault()).contains("nox")
                || Build.BOOTLOADER.lowercase(Locale.getDefault()).contains("nox")
                || buildHardware.lowercase(Locale.getDefault()).contains("nox")
                || buildProduct.lowercase(Locale.getDefault()).contains("nox"))
                || brand.startsWith("generic") && Build.DEVICE.startsWith("generic")
    }

    private fun getSavedUrl() {
        val sharedPreferences = getSharedPreferences(
            "AppPreferences", Context.MODE_PRIVATE
        )
        Log.d("URL", sharedPreferences.getString("url", null).toString())
        url = sharedPreferences.getString("url", null)
    }

    private fun saveUrl(url: String) {
        // Сохраняем ссылку локально
        val sharedPreferences = getSharedPreferences(
            "AppPreferences",
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString("url", url)
        editor.apply()
    }
}