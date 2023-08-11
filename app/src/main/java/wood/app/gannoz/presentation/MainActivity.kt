package wood.app.gannoz.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.firebase.remoteconfig.BuildConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import wood.app.gannoz.R
import wood.app.gannoz.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var url: String? = null
    private val remoteConfig by lazy {
        FirebaseRemoteConfig.getInstance()
    }

    private var imageUri: Uri? = null
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null

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
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build()
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
            @Suppress("DEPRECATION")
            connectivityManager.run {
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
        startActivity(PlaceholderActivity.newIntent(this))
    }

    private fun getUrlFromConfig(savedInstanceState: Bundle?) {
        try {
            remoteConfig.fetchAndActivate().addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    url = remoteConfig.getString("url")
                    if (url.isNullOrBlank() || checkIsEmu()) {
                        launchPlaceholder()
                    }
                    else {
                        setWebView(savedInstanceState, url!!)
                        saveUrl(url!!)
                    }
                } else {
                    launchPlaceholder()
                }
            }
        }
        catch (e: Exception) {
            launchNoInternet()
        }
    }

    private fun launchNoInternet() {
        val intent = NoInternetActivity.getIntent(this)
        startActivity(intent)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView(savedInstanceState: Bundle?, url: String) {
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        binding.webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                binding.webView.loadUrl(url ?: "")
                return true
            }
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                binding.webView.loadUrl(request?.url?.toString() ?: "")
                return true
            }
        }

        binding.webView.webChromeClient = object : WebChromeClient() {

            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                mFilePathCallback = null
                mFilePathCallback = filePathCallback

                takePhoto()

                return true
            }
        }
        with(binding.webView.settings){
            javaScriptEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            domStorageEnabled = true
            databaseEnabled = true
            setSupportZoom(false)
            allowFileAccess = true
            allowContentAccess = true
            javaScriptCanOpenWindowsAutomatically = true
        }


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

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri = result.data?.data ?: return@registerForActivityResult
            mFilePathCallback?.onReceiveValue(arrayOf(uri))
            mFilePathCallback = null
        }
    }

    private fun takePhoto() {
        val photoFile : File?
        val authorities : String = this.packageName + ".provider"
        try {
            photoFile = createImageFile()
            imageUri = FileProvider.getUriForFile(this, authorities, photoFile)
        } catch(e: IOException) {
            e.printStackTrace()
        }

        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        val photo = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val chooserIntent = Intent.createChooser(photo, "Image Chooser")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf<Parcelable>(captureIntent))
        startForResult.launch(chooserIntent)
    }
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val imageStorageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), "AndroidExampleFolder"
        )
        if (!imageStorageDir.exists()) {
            imageStorageDir.mkdirs()
        }

        val imageFileName = "JPEG_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}"
        return File(imageStorageDir, File.separator + imageFileName + ".jpg")
    }
}