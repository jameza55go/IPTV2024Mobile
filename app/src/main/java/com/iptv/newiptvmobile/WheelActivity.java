package com.iptv.newiptvmobile;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WheelActivity extends AppCompatActivity {
//    private GeckoView geckoView;
//    private static GeckoRuntime runtime;
//    private GeckoSession session;

    WebView webView;
    SharedPreferences db;
    String username,password,gameurl;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);
        db = getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        username=db.getString("user", "");
        password=db.getString("pass", "");
        gameurl=db.getString("gameurl", getString(R.string.game_url));
//        geckoView = findViewById(R.id.geckoview);
//        runtime = GeckoRuntimeProvider.get(this);
//        session = new GeckoSession();
//        session.open(runtime);
//        geckoView.setSession(session);
//        session.loadUri("https://www.inwiptv.com");
//        session.setNavigationDelegate(new GeckoSession.NavigationDelegate() {
//            @Nullable
//            @Override
//            public GeckoResult<AllowOrDeny> onLoadRequest(@NonNull GeckoSession session, @NonNull LoadRequest request) {
//                session.loadUri(request.uri);
//                return null;
//            }
//        });
        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        if (Build.VERSION.SDK_INT >= 21) {
            this.webView.getSettings().setMixedContentMode(2);
        }
        this.webView.getSettings().setUserAgentString("Android FWIPTV");
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        this.webView.getSettings().setLoadWithOverviewMode(true);
        this.webView.getSettings().setUseWideViewPort(true);
        this.webView.getSettings().setBuiltInZoomControls(false);
        this.webView.getSettings().setSupportZoom(false);
        webView.loadUrl(gameurl+"?u="+username+"&p="+password);
    }

    private class MyWebViewClient extends WebViewClient {
        private MyWebViewClient() {
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}