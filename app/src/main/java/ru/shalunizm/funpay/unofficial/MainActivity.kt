package ru.shalunizm.funpay.unofficial

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var prefs: SharedPreferences
    private var isDarkModeEnabled = false

    // URL для кнопок - замените на свои
    private val urlHome = "https://funpay.com/"
    private val altUrl = "https://funpay.freshdesk.com/"
    private val supUrl = "https://support.funpay.com"
    private val moneyUrl = "https://funpay.com/account/balance"
    private val chatUrl = "https://funpay.com/chat/"

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        isDarkModeEnabled = prefs.getBoolean("dark_mode", false)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.databaseEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT

        // Разрешает куки
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView, true)
        }

        // Обработотка загрузки страниц
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                return if (url.startsWith(urlHome) || url.startsWith(altUrl) || url.startsWith(supUrl)) {
                    false
                } else {
                    true
                }
            }
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                // Если URL нет в списка, возвращает на главную
                if (url != null) {
                    if (!url.startsWith(urlHome) && !url.startsWith(altUrl) && url.startsWith(supUrl)) {
                        view?.loadUrl(urlHome)
                    }
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                CookieManager.getInstance().flush()
            }
        }

        // Загрузка главной страницы при старте
        if (savedInstanceState == null) {
            webView.loadUrl(urlHome)
        } else {
            webView.restoreState(savedInstanceState)
        }

        // Кнопки переключения страниц
        findViewById<Button>(R.id.btnHome).setOnClickListener {
            webView.loadUrl(urlHome)
        }
        findViewById<Button>(R.id.btnMessage).setOnClickListener {
            webView.loadUrl(chatUrl)
        }
        findViewById<Button>(R.id.btnMoney).setOnClickListener {
            webView.loadUrl(moneyUrl)
        }

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            handleIntent(it)
        }
    }
    private fun handleIntent(intent: Intent) {
        val data: Uri? = intent.data
        if (data != null) {
            webView.loadUrl(data.toString())
        } else {
            webView.loadUrl(urlHome)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}