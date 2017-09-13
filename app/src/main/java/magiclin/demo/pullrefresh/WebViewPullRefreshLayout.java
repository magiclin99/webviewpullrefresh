package magiclin.demo.pullrefresh;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class WebViewPullRefreshLayout<T extends View> extends FrameLayout {
    private boolean enable;
    private T indicatorView;
    private View contentView;

    private final float boundary;
    private float threshold;

    private Callback<T> callback;

    private int indicatorHeight;

    private OnLayoutChangeListener layoutDetector = new OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            indicatorHeight = indicatorView.getHeight();
            setMargin(indicatorView, -indicatorHeight);
            removeOnLayoutChangeListener(layoutDetector);
        }
    };

    public WebViewPullRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        addOnLayoutChangeListener(layoutDetector);
        threshold = getResources().getDisplayMetrics().density * 50;
        boundary = getResources().getDisplayMetrics().density * 150;
    }

    public void setCallback(Callback<T> callback) {
        this.callback = callback;
        callback.onReset(indicatorView);
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(0);
        indicatorView = (T) getChildAt(1);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                enable = true;
                if (getMargin(indicatorView) > threshold) {
                    callback.onReachThreshold(indicatorView);
                }
                break;
            default:
                enable = false;
                if (getMargin(indicatorView) > threshold) {
                    callback.onReleaseRefresh(indicatorView);
                }
                animIndicatorBack();
                break;
        }
        return contentView.onTouchEvent(event);
    }

    private void animIndicatorBack() {
        if (indicatorView.getY() + indicatorHeight <= 0) {
            return;
        }

        ValueAnimator anim = ValueAnimator.ofInt(getMargin(indicatorView), -indicatorHeight);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                FrameLayout.LayoutParams layoutParams = (LayoutParams) indicatorView.getLayoutParams();
                layoutParams.topMargin = val;
                indicatorView.setLayoutParams(layoutParams);
            }
        });
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                callback.onReset(indicatorView);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.setDuration(200);
        anim.start();
    }

    public void onOverScroll(int y) {
        if (enable) {
            FrameLayout.LayoutParams layout = (LayoutParams) indicatorView.getLayoutParams();
            layout.topMargin += computeScrollDelta(y);
            indicatorView.setLayoutParams(layout);
        }
    }

    private int computeScrollDelta(int scrollY) {
        float top = Math.max(0F, indicatorView.getY());
        float d = boundary - top;
        return (int) (scrollY * Math.min(0.5, d / boundary));
    }

    private void setMargin(View view, int margin) {
        FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
        layout.topMargin += margin;
        view.setLayoutParams(layout);
    }

    private int getMargin(View v) {
        FrameLayout.LayoutParams layout = (LayoutParams) v.getLayoutParams();
        return layout.topMargin;
    }

    public interface Callback<T extends View> {
        void onReachThreshold(T indicatorView);

        void onReleaseRefresh(T indicatorView);

        void onReset(T indicatorView);
    }
}
