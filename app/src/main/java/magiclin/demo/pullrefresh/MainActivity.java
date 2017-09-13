package magiclin.demo.pullrefresh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private CustomWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        webView = (CustomWebView) findViewById(R.id.webView);
        webView.loadUrl("https://github.com/");
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress != 100) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        WebViewPullRefreshLayout refreshLayout = (WebViewPullRefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setCallback(new WebViewPullRefreshLayout.Callback<TextView>() {
            @Override
            public void onReachThreshold(TextView indicatorView) {
                indicatorView.setText("Release to refresh");
            }

            @Override
            public void onReleaseRefresh(TextView indicatorView) {
                webView.clearCache(true);
                webView.reload();
            }

            @Override
            public void onReset(TextView indicatorView) {
                indicatorView.setText("Pull to refresh");
            }
        });
    }
}
