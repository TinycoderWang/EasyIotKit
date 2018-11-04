package wang.tinycoder.easyiotkit.module.webview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import wang.tinycoder.easyiotkit.R;
import wang.tinycoder.easyiotkit.app.Constants;
import wang.tinycoder.easyiotkit.base.BaseActivity;
import wang.tinycoder.easyiotkit.util.DensityUtils;

/**
 * Progect：EasyIotKit
 * Package：wang.tinycoder.easyiotkit.module.webview
 * Desc：
 * Author：TinycoderWang
 * CreateTime：2018/8/26 17:50
 */
public class WebViewActivity extends BaseActivity {


    @BindView(R.id.fl_content)
    FrameLayout mFlContent;

    // webview
    private WebView mWebContent;
    private boolean showLoadding;
    private boolean isFirst = true;

    @Override
    public int getLayoutId() {
        return R.layout.activity_web_view;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
//        // 标题
//        String title = getIntent().getStringExtra(Constants.WEB_TITLE);

        // 加载页面
        final String targUrl = getIntent().getStringExtra(Constants.WEB_URL);
        if (!TextUtils.isEmpty(targUrl)) {
            // 创建webview
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mWebContent = new WebView(getApplicationContext());
            mWebContent.setLayoutParams(params);
            mFlContent.addView(mWebContent);

            // 初始化webview
            // webview监听
//            mWebContent.setWebChromeClient(new WebChromeClient(){});
            mWebContent.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    if (targUrl.equals(url) && isFirst) {
                        showLoadding = true;
                        isFirst = false;
                        startLoad();
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if (targUrl.equals(url) && showLoadding) {
                        stopLoad();
                        showLoadding = false;
                    }
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    if (showLoadding) {
                        stopLoad();
                        showLoadding = false;
                    }
                }
            });

            WebSettings webSettings = mWebContent.getSettings();
            // 允许使用js
            webSettings.setJavaScriptEnabled(true);
            // 设置自适应屏幕，两者合用
            webSettings.setUseWideViewPort(true); // 将图片调整到适合webview的大小
            webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
            // 缩放操作
            webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
            webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
            webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

            /**
             * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
             * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
             * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
             * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
             */
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            webSettings.setAllowFileAccess(false); //设置可以访问文件
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
            webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
            webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
            webSettings.setAppCacheEnabled(false);
            webSettings.setDomStorageEnabled(true);

            mWebContent.clearCache(true);
            mWebContent.clearHistory();

            mWebContent.loadUrl(targUrl);
        } else {
            TextView tv = new TextView(this);
            tv.setText("加载失败...");
            tv.setTextSize(DensityUtils.dip2sp(this, 16));
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            tv.setLayoutParams(layoutParams);
            tv.setGravity(Gravity.CENTER);
            mFlContent.addView(tv);
        }
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }


    private void startLoad() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showLoading();
            }
        });
    }

    private void stopLoad() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideLoading();
            }
        });
    }


    @Override
    protected void onDestroy() {
        if (mWebContent != null) {
            mWebContent.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebContent.clearHistory();
            ((ViewGroup) mWebContent.getParent()).removeView(mWebContent);
            mWebContent.destroy();
            mWebContent = null;
        }
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        if (mWebContent.canGoBack()) {
            mWebContent.goBack();
        } else {
            super.onBackPressed();
        }
    }

}
