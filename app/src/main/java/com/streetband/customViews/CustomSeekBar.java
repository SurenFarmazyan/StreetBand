package com.streetband.customViews;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
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
    private int ARROW_PADDING = 15;


    //params
    private int mFlag = 2;
    private int mLength = 8;

    //tools
    private Path mArrow = new Path();
    private Paint mLinePaint = new Paint();
    private Paint mBoldLinePaint = new Paint();
    private Paint mFillPaint = new Paint();
    private Paint mTextPaint = new TextPaint();
    private Paint mRedPaint = new TextPaint();
    private ViewTreeObserver mViewTreeObserver;


    //dynamic params
    private int mWidth;
    private int mHeight;

    private boolean isDragging;
    private boolean isRecording = true;
    private float mPosition = 100;

    private int mScrollX = 0;
    private float mPureScrollX = 0;
    private float mScaleX = 1.0f;

    private Rect mVisibleArea = new Rect();
    private float mVisibleWidth;
    private RectF mRectF = new RectF();



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
        ARROW_PADDING *= mDensity;

        mLinePaint.setColor(Color.WHITE);
        mFillPaint.setColor(Color.parseColor("#90FFFFFF"));
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(20*mDensity);
        mTextPaint.setAntiAlias(true);
        mBoldLinePaint.setColor(Color.WHITE);
        mBoldLinePaint.setAntiAlias(true);
        mBoldLinePaint.setStyle(Paint.Style.STROKE);
        mBoldLinePaint.setStrokeWidth(mDensity);
        mRedPaint.setColor(Color.parseColor("#90A3000B"));

        mViewTreeObserver = getViewTreeObserver();
        mViewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getGlobalVisibleRect(mVisibleArea);
                mVisibleWidth = (int)(mVisibleArea.width()/mScaleX);
            }
        });


        mHeight = (int)(DEFAULT_HEIGHT * mDensity);
        preparePath();
    }

    private void preparePath(){
        mArrow.reset();
        mArrow.moveTo(mPosition,mHeight);
        mArrow.rLineTo(SMALL_PADDING,-ARROW_PADDING);
        mArrow.rLineTo(0,-ARROW_PADDING);
        mArrow.rLineTo(-2*SMALL_PADDING,0);
        mArrow.rLineTo(0,ARROW_PADDING);
        mArrow.close();
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

    public void setPosition(float position){
        mPosition = position*BIG_PADDING;
        preparePath();
        invalidate();
        if(position > 5 && mScrollX < mWidth - mVisibleWidth){
            mScrollX = (int)(BIG_PADDING*(position - 5));
            mPureScrollX = mScrollX/mScaleX;
            scrollTo(mScrollX,0);
        }
    }

    public void setLength(int length){
        mLength = length;
        mWidth = mLength*BIG_PADDING;
        invalidate();
    }

    public void setIsRecording(boolean isRecording){
        this.isRecording = isRecording;
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

        if(till > mLength){
            till = mLength + 1;
        }
         for(int i = start; i < till;i++){
             canvas.drawLine(offsetX,0,offsetX,mHeight,mLinePaint);
             canvas.drawText(String.valueOf(i),offsetX,MEDIUM_PADDING,mTextPaint);
            if(mScaleX > 1.5f){
                for(int j = 0; j < 3;j++){
                    offsetX += MEDIUM_PADDING*mScaleX;
                    canvas.drawLine(offsetX,MEDIUM_PADDING,offsetX,mHeight,mLinePaint);
                }
                offsetX += MEDIUM_PADDING*mScaleX;
            }else {
                offsetX += BIG_PADDING * mScaleX;
            }
         }
         canvas.drawRect(mScrollX,0,mPosition,mHeight,mRedPaint);
         canvas.drawPath(mArrow,mBoldLinePaint);
         canvas.drawPath(mArrow,mFillPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mArrow.computeBounds(mRectF,false);
                if(mRectF.contains(x,y)){
                    isDragging = true;
                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mPosition = x;
                mPosition = Math.max(0,mPosition);
                preparePath();
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                isDragging = false;
                invalidate();
                return true;
        }

        return false;
    }

}
