package com.streetband.customViews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.streetband.R;

public class CustomNavigationDrawer extends ViewGroup {

    //final params
    private float mDensity;
    private float DRAGGING_SPACE = 20;


    //params
    private int mMinDrawerEndX = 50;
    private int mMaxDrawerEndX;
    private int mShadowRadius = 8;


    private NavigationListener mNavigationListener;

    //dynamic params
    private int mWidth;
    private int mHeight;

    private float mStartX;
    private int mDrawerOldX;
    private int mDrawerEndX;

    private boolean isDragging;

    public CustomNavigationDrawer(Context context) {
        this(context, null);
    }

    public CustomNavigationDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDensity = context.getResources().getDisplayMetrics().density;

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomNavigationDrawer);
            mMinDrawerEndX = (int) typedArray.getDimension(R.styleable.CustomNavigationDrawer_drawerEndX, 50 * mDensity);
            mShadowRadius = (int) typedArray.getDimension(R.styleable.CustomNavigationDrawer_shadowRadius, 8 * mDensity);
            typedArray.recycle();
        }

        mDrawerEndX = mMinDrawerEndX;
        DRAGGING_SPACE *= mDensity;
    }


    public void addNavigationListener(NavigationListener navigationListener) {
        mNavigationListener = navigationListener;
    }

    public int getPosition() {
        return mDrawerEndX - mShadowRadius;
    }

    public void setPosition(int position) {
        mDrawerEndX = position;
        layoutChanged();
    }

    public void closeAndOpen(final View thisV) {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator close = ObjectAnimator.ofInt(this, "position", mDrawerEndX, 0).setDuration(1000);
        close.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                removeView(getChildAt(1));
                addView(thisV);
            }
        });
        ObjectAnimator open = ObjectAnimator.ofInt(this, "position", 0, mMinDrawerEndX).setDuration(1000);
        set.play(close).before(open);
        set.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() > 2) {
            try {
                throw new Exception("This ViewGroup can't have more then two children");
            } catch (Exception e) {
                e.getMessage();
            }
        }

        mMaxDrawerEndX = getChildAt(1).getMeasuredWidth();

        layoutChanged();
    }

    private void layoutChanged() {
        View v = getChildAt(1);
        v.layout(mDrawerEndX - v.getMeasuredWidth(), 0, mDrawerEndX, v.getMeasuredHeight());
        v = getChildAt(0);
        v.layout(mDrawerEndX - mShadowRadius, 0, mDrawerEndX - mShadowRadius + v.getMeasuredWidth(), v.getMeasuredHeight());
        if (mNavigationListener != null) {
            mNavigationListener.navigationPosition(mDrawerEndX, mShadowRadius);
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = ev.getX();
                if (x < mDrawerEndX - mShadowRadius && x > mDrawerEndX - mShadowRadius - DRAGGING_SPACE) {
                    mStartX = x;
                    mDrawerOldX = mDrawerEndX;
                    isDragging = true;
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mDrawerEndX = mDrawerOldX + (int) (event.getX() - mStartX);
                mDrawerEndX = Math.max(mMinDrawerEndX, Math.min(mMaxDrawerEndX, mDrawerEndX));
                layoutChanged();
                break;
            case MotionEvent.ACTION_UP:
                isDragging = false;
                break;
        }
        return isDragging;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //INNER CLASSES
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public interface NavigationListener {
        void navigationPosition(int position, int shadowRadius);
    }
}
