package comm.hyperonline.techsh.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import comm.hyperonline.techsh.R;
import comm.hyperonline.techsh.utils.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewActivity2 extends AppCompatActivity {


    public static void sendIntent(Context context, String url, String title) {
        Intent intent = new Intent(context, WebViewActivity2.class);
        intent.putExtra(Constant.URL, url);
        intent.putExtra(Constant.TITLE, title);
        context.startActivity(intent);
    }

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    private String url;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view2);
        ButterKnife.bind(this);

        checkArgument();
        initToolbar();
        setData();


    }


    private void checkArgument() {
        url = getIntent().getStringExtra(Constant.URL);
        title = getIntent().getStringExtra(Constant.TITLE);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String color = getPreferences().getString(Constant.HEADER_COLOR, Constant.HEAD_COLOR);
        if (!color.equals("")){
            toolbar.setBackgroundColor(Color.parseColor(color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor(color));
            }
        }
    }



    public SharedPreferences getPreferences() {

        return getSharedPreferences(
                Constant.MyPREFERENCES, Context.MODE_PRIVATE);
    }


    private void setData() {
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }


    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            showProgress();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            hideProgress();
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
            hideProgress();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            hideProgress();
            Toast.makeText(WebViewActivity2.this, "در حال بارگذاری", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }
}
