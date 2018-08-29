package com.streetband.customViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.support.animation.DynamicAnimation;
import android.support.animation.FlingAnimation;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.streetband.utils.Density;

import java.util.ArrayList;

public class CustomAddedInstrumentsList extends ViewGroup {
    public static final String TAG = "CustomAddedInstruments";

    //final params
    public  int DEFAULT_WIDTH = 270;
    private int BIG_PADDING = 80;
    private int MEDIUM_PADDING = 20;
    private int PADDING = 10;
    private int SMALL_PADDING = 5;
    private int SHADOW_RADIUS=8;
    private float mDensity;

    //adapter
    private Adapter mAdapter;

    //tools
    private Paint mPaint = new Paint();
    private Paint mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private GestureDetector mGestureDetector;
    private FlingAnimation mFlingAnimation;
    private ScrollListener mScrollListener;

    //dynamic params
    private int mWidth;
    private int mHeight;
    private int mPhoneHeight;

    private int mScrollY = 0;
    private int mMinScrollY = 0;
    private int mMaxScrollY = 0;
    private int mMaxCount;
    private int mCount;
    private int mChildHeight;
    private int mTopPosition;
    private int mBottomOffsetY;
    private int mTopOffsetY;

    private boolean mMustAddNew = true;
    private int mChildrenHeight;

    ////////////////////////////////////////////
    private ArrayList<Holder> mViewHolders = new ArrayList<>();

    private boolean isRelayoutNeeded = true;

    private boolean hasTouch;
    private boolean isFlinging;


    public CustomAddedInstrumentsList(Context context) {
        this(context,null);
    }

    public CustomAddedInstrumentsList(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDensity = Density.getDensity(context);
        mPhoneHeight = context.getResources().getDisplayMetrics().heightPixels;

        mGestureDetector = new GestureDetector(context, new GestureListener());
        mFlingAnimation = new FlingAnimation(this, DynamicAnimation.SCROLL_Y);

        mPaint.setColor(Color.parseColor("#bbbbbb"));
        mPaint.setStrokeWidth(mDensity);


        DEFAULT_WIDTH *= mDensity;
        PADDING *= mDensity;
        SMALL_PADDING *= mDensity;
        SHADOW_RADIUS *= mDensity;

        mShadowPaint.setShadowLayer(SHADOW_RADIUS, 0, 0, Color.BLACK);
        mShadowPaint.setColor(Color.parseColor("#FF2B2B2B"));

        setLayerType(LAYER_TYPE_SOFTWARE, mShadowPaint);
    }

    public void setAdapter(Adapter adapter){
        mAdapter = adapter;
        isRelayoutNeeded = true;
        mViewHolders.clear();
        addChildes();
    }

    public void addScrollListener(ScrollListener scrollListener){
        mScrollListener = scrollListener;
    }

    public void notifyItemAdded(){
        mMaxCount++;

        if(mMustAddNew){
            mViewHolders.add(mAdapter.onCreateViewHolder());
            Holder holder = mViewHolders.get(mViewHolders.size() - 1);
            mAdapter.onBindViewHolder(holder, mViewHolders.size() - 1);
            View v = holder.getView();
            addView(v);
            v.measure(mWidth,MeasureSpec.AT_MOST);
            mChildrenHeight += v.getMeasuredHeight();
            if(mCount == 0){
                mChildHeight = v.getMeasuredHeight();
            }
            mCount++;
            v.layout(0,mBottomOffsetY,v.getMeasuredWidth(),mBottomOffsetY + mChildHeight);
            mBottomOffsetY += mChildHeight;
            if(mChildrenHeight > mPhoneHeight){
                mMustAddNew = false;
            }
        }

        mMaxScrollY = mMaxCount*mChildHeight - mWidth - PADDING;
    }

    public void notifyItemRemoved(){
        //TODO
    }

    private void drawBackground(){
        Bitmap bitmap = Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.ARGB_8888);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getContext().getResources(),bitmap);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawRect(0,0,getMeasuredWidth() - SHADOW_RADIUS,mHeight,mShadowPaint);

        canvas.drawLine(getMeasuredWidth() - SMALL_PADDING  - SHADOW_RADIUS,mHeight/2 -PADDING,getMeasuredWidth() - SMALL_PADDING  - SHADOW_RADIUS,mHeight/2 + PADDING,mPaint);
        canvas.drawLine(getMeasuredWidth() - 2*SMALL_PADDING  - SHADOW_RADIUS,mHeight/2 -PADDING,getMeasuredWidth() - 2*SMALL_PADDING  - SHADOW_RADIUS,mHeight/2 +PADDING,mPaint);
        canvas.drawLine(getMeasuredWidth() - 3*SMALL_PADDING  - SHADOW_RADIUS,mHeight/2 -PADDING,getMeasuredWidth() - 3*SMALL_PADDING  - SHADOW_RADIUS,mHeight/2 +PADDING,mPaint);
        super.setBackground(bitmapDrawable);
    }

    private void addChildes(){
        mTopOffsetY = 0;
        mBottomOffsetY = 0;
        mMaxCount = mAdapter.getItemCount();
        mChildrenHeight = 0;
        for(int i = 0; i < mMaxCount;i++){
            if(mChildrenHeight > mPhoneHeight){
                mMustAddNew = false;
                break;
            }
            mViewHolders.add(mAdapter.onCreateViewHolder());
            View v = mViewHolders.get(mViewHolders.size() - 1).getView();
            addView(v);
            v.measure(mWidth,MeasureSpec.AT_MOST);
            mChildrenHeight += v.getMeasuredHeight();
            mCount++;
        }
        requestLayout();
    }

    @Override
    public void setScrollY(int scrollY) {
        mScrollY = scrollY;
        if(mScrollY < mTopOffsetY){
            if(mTopPosition == 0){
                mTopPosition = mCount;
            }
            mTopPosition--;
            View v = getChildAt(mTopPosition);
            mTopOffsetY -= mChildHeight;
            mBottomOffsetY -= mChildHeight;
            v.layout(0,mTopOffsetY,v.getRight(),mTopOffsetY + mChildHeight);
            mAdapter.onBindViewHolder(mViewHolders.get(mTopPosition),mTopOffsetY/mChildHeight);
        }else if(mScrollY > mTopOffsetY + mChildHeight && mScrollY < mMaxScrollY - mChildHeight){
            View v = getChildAt(mTopPosition);
            mAdapter.onBindViewHolder(mViewHolders.get(mTopPosition),mBottomOffsetY/mChildHeight);
            mTopOffsetY += mChildHeight;
            mBottomOffsetY += mChildHeight;
            v.layout(0,mBottomOffsetY - mChildHeight,v.getRight(),mBottomOffsetY);
            if(mTopPosition == mCount - 1){
                mTopPosition = -1;
            }
            mTopPosition++;
        }
        super.setScrollY(mScrollY);
        if(mScrollListener != null){
            mScrollListener.newScrollPosition(mScrollY,hasTouch || isFlinging);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if(mode == MeasureSpec.EXACTLY) {
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
        }else {
            mWidth = DEFAULT_WIDTH;
        }
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(mWidth,mHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(mAdapter == null){
            return;
        }
        if(isRelayoutNeeded) {
            isRelayoutNeeded = false;
            drawBackground();

            for (int i = 0; i < mViewHolders.size(); i++) {
                Holder holder = mViewHolders.get(i);
                mAdapter.onBindViewHolder(holder, i);
                View v = holder.getView();
                if(i == 0){
                    mChildHeight = v.getMeasuredHeight();
                }
                v.layout(0, mBottomOffsetY, mWidth, mBottomOffsetY + mChildHeight);
                mBottomOffsetY += v.getMeasuredHeight();
            }
            mMaxScrollY = mMaxCount*mChildHeight - mWidth - PADDING;
            mTopPosition = 0;
            mCount = mViewHolders.size();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                hasTouch = true;
                break;
            case MotionEvent.ACTION_UP:
                hasTouch = false;
                break;
        }
        return true;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    //Inner classes
    /////////////////////////////////////////////////////////////////////////////////////////////////


    private class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mScrollY += distanceY;

            mScrollY = Math.max(mMinScrollY,Math.min(mMaxScrollY,mScrollY));

            setScrollY(mScrollY);

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1.getY() <= mMaxScrollY) {
                isFlinging = true;
                mFlingAnimation.setStartVelocity(-velocityY).setMinValue(mMinScrollY).addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                        if(!canceled){
                            isFlinging = false;
                        }
                    }
                }).setMaxValue(mMaxScrollY).start();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            float y = mScrollY + e.getY();
            float x = e.getX();
            int position = ((int)(y/mChildHeight))%mCount + mTopPosition;
            if(position >= mCount){
                position -= mCount;
            }
            CustomAddedInstrument customAddedInstrument = (CustomAddedInstrument)getChildAt(position);
            customAddedInstrument.onSingleTapUp(x,y - customAddedInstrument.getTop());

            return super.onSingleTapUp(e);
        }
    }

    public interface ScrollListener{
        void newScrollPosition(int scrollY,boolean fromOutside);
    }

    public static class Holder{
        private View mView;

        public Holder(View v){
            mView = v;
        }

        public View getView(){
            return mView;
        }
    }


    public static abstract class Adapter<T extends Holder>{

        public abstract T onCreateViewHolder();

        public abstract void onBindViewHolder(T holder,int position);

        public abstract int getItemCount();

    }
}
