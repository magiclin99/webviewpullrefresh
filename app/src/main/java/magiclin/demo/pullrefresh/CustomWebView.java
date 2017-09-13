package magiclin.demo.pullrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by gary on 8/10/17.
 */

public class CustomWebView extends WebView {

    private boolean overScrollY;

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        if (overScrollY && scrollY == 0) {
            ((WebViewPullRefreshLayout) getParent()).onOverScroll(-deltaY);
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        this.overScrollY = clampedY;
    }
}
