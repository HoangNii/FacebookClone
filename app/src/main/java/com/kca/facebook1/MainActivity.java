package com.kca.facebook1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int KEY_SELECT_FILE = 1200;

    private ValueCallback<Uri[]> uploadMessage;

    private QWebView qWebView;

    private View reload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.app_name));

        qWebView = findViewById(R.id.web_view);
        reload = findViewById(R.id.bt_reload);
        reload.setOnClickListener(v -> qWebView.reload());

        initWebView();

        qWebView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
        qWebView.loadUrl("https://m.facebook.com");
    }

    private void initWebView() {

        qWebView.setActivity(this);
        qWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        qWebView.getSettings().setAllowFileAccess(true);
        qWebView.getSettings().setAllowContentAccess(true);
        qWebView.getSettings().setAppCacheEnabled(true);
        qWebView.getSettings().setLoadWithOverviewMode(true);
        qWebView.getSettings().setBuiltInZoomControls(true);
        qWebView.getSettings().setDisplayZoomControls(true);
        qWebView.getSettings().setSupportZoom(true);
        qWebView.getSettings().setJavaScriptEnabled(true);

        qWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        qWebView.getSettings().setDomStorageEnabled(true);


        // AppRTC requires third party cookies to work
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptThirdPartyCookies(qWebView, true);


        qWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                reload.setAlpha(0);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                reload.setAlpha(1);
            }

        });

        qWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if(PermissionRequest.checkStore(MainActivity.this,12)){
                    if (uploadMessage != null) {
                        uploadMessage.onReceiveValue(null);
                        uploadMessage = null;
                    }
                    uploadMessage = filePathCallback;

                    Intent intent;
                    intent = fileChooserParams.createIntent();
                    try {
                        startActivityForResult(intent, KEY_SELECT_FILE);
                    } catch (Exception e) {
                        uploadMessage = null;
                        return false;
                    }
                    return true;
                }
                return false;
            }

        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KEY_SELECT_FILE) {
            if (uploadMessage== null)
                return;
            uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            uploadMessage = null;
        }
    }


    @Override
    public void onBackPressed() {
        if(qWebView.canGoBack()){
            qWebView.goBack();
        }else {
            super.onBackPressed();
        }
    }
}