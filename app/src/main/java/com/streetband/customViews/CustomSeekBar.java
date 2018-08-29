package com.streetband.customViews;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.streetband.R;

public class CustomSeekBar extends View {
    public static final String TAG = "CustomSeekBar";
    public static final int DEFAULT_HEIGHT = 30;


    //final params
    private float mDensity;
    private int BIG_PADDING = 80;
    private int MEDIUM_PADDING = 20;
    private int SMALL_PADDING = 10;
    private int OFFSET_Y = 20;
    private Paint mLinePaint = new Paint();
    private ViewTreeObserver mViewTreeObserver;

    //params
    private int mFlag = 2;
    private int mLength = 8;


    //dynamic params
    private int mWidth;
    private int mHeight;

    private int mScrollX = 0;
    private float mPureScrollX = 0;
    private float mScaleX = 1.0f;

    private Rect mVisibleArea = new Rect();
    private float mVisibleWidth;



    public CustomSeekBar(Context context) {
        this(context,null);
    }

    public CustomSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomSeekBar);

        mFlag = typedArray.getInteger(R.styleable.CustomSeekBar_tact,2);
        mLength = typedArray.getInt(R.styleable.CustomSeekBar_length,8);

        typedArray.recycle();

        //final params
        mDensity = context.getResources().getDisplayMetrics().density;

        BIG_PADDING *= mDensity;
        MEDIUM_PADDING *= mDensity;
        SMALL_PADDING *= mDensity;
        OFFSET_Y *= mDensity;

        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setTextSize(20*mDensity);

        mViewTreeObserver = getViewTreeObserver();
        mViewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getGlobalVisibleRect(mVisibleArea);
                mVisibleWidth = (int)(mVisibleArea.width()/mScaleX);
            }
        });


        mHeight = (int)(DEFAULT_HEIGHT * mDensity);
    }



    public void synchronizeWithMainBoard(CustomMainBoard customMainBoard){
        customMainBoard.addScrollAndScaleListener(new CustomMainBoard.ScrollAndScaleListener() {
            @Override
            public void scrolled(int x, int y) {
                mScrollX = x;
                mPureScrollX = mScrollX/mScaleX;
                scrollTo(mScrollX,0);
            }

            @Override
            public void scaleChanged(float scaleX, float scaleY) {
                mScaleX = scaleX;
                mVisibleWidth = mVisibleArea.width()/mScaleX;
                invalidate();
            }
        });
    }

    public void setLength(int length){
        mLength = length;
        mWidth = mLength*BIG_PADDING;
        invalidate();
    }

    public void updateVisibility(){
        getGlobalVisibleRect(mVisibleArea);
        mVisibleWidth = (int)(mVisibleArea.width()/mScaleX);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = mLength*BIG_PADDING;
        setMeasuredDimension(mWidth,mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int start = (int)(mPureScrollX/BIG_PADDING);
        float offsetX = start*BIG_PADDING*mScaleX;
        int till = (int)((mPureScrollX + mVisibleWidth)/BIG_PADDING) + 1;

        Log.i(TAG,"start = " + start + " offsetX = " + offsetX + " till = " + till);
        if(till > mLength){
            till = mLength + 1;
        }
         for(int i = start; i < till;i++){
             canvas.drawLine(offsetX,0,offsetX,mHeight,mLinePaint);
             canvas.drawText(String.valueOf(i),offsetX,OFFSET_Y,mLinePaint);
            if(mScaleX > 1.5f){
                for(int j = 0; j < 3;j++){
                    offsetX += MEDIUM_PADDING*mScaleX;
                    canvas.drawLine(offsetX,OFFSET_Y,offsetX,mHeight,mLinePaint);

                }
                offsetX += MEDIUM_PADDING*mScaleX;
            }else {
                offsetX += BIG_PADDING * mScaleX;
            }
         }
    }

}
