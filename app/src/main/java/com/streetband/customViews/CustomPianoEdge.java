package com.streetband.customViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.streetband.utils.Density;

import java.util.Locale;

public class CustomPianoEdge extends View implements Edge{

    //final params
    private float mDensity;
    private int mPhoneHeight;
    private int DEFAULT_WIDTH = 95;
    private int BIG_PADDING = 80;
    private int MEDIUM_PADDING = 20;
    private int SHADOW_RADIUS = 8;


    //params
    private int mOctaveSum = 8;


    //tools
    private Paint mPaint = new Paint();
    private Paint mWhitePaint = new Paint();
    private Paint mTextPaint = new Paint();

    //dynamic params
    private int mWidth;
    private int mHeight;

    private float mCoefficient;
    private float mPaddingY;
    private float mScaleY = 1.0f;

    private int mScrollY = 0;
    private int mStartScrollY = 0;


    public CustomPianoEdge(Context context) {
        this(context,null);
    }

    public CustomPianoEdge(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mDensity = Density.getDensity(context);
        mPhoneHeight = context.getResources().getDisplayMetrics().heightPixels;

        //tools
        mPaint.setColor(Color.BLACK);
        mWhitePaint.setColor(Color.WHITE);
        mTextPaint.setAntiAlias(true);


        BIG_PADDING *= mDensity;
        MEDIUM_PADDING *= mDensity;
        DEFAULT_WIDTH *= mDensity;
        SHADOW_RADIUS *= mDensity;

        mWidth = DEFAULT_WIDTH - SHADOW_RADIUS;
        mHeight = mOctaveSum*7*MEDIUM_PADDING;

        mCoefficient = ((float)(2 * MEDIUM_PADDING) / (12 * mOctaveSum));
        mPaddingY = mCoefficient;

    }

    @Override
    public void synchronizeWithMainBoard(CustomMainBoard customMainBoard) {
        customMainBoard.addSecondScrollAndScaleListener(new CustomMainBoard.ScrollAndScaleListener() {
            @Override
            public void scrolled(int x, int y) {
                if(mScrollY != y){
                    mScrollY = y;
                    scrollTo(0,mScrollY - mStartScrollY);
                }
            }
            @Override
            public void scaleChanged(float scaleX, float scaleY) {
                if(mScaleY != scaleY){
                    mScaleY = scaleY;
                    mPaddingY = mCoefficient*mScaleY;
                    mTextPaint.setTextSize(mPaddingY);
                    invalidate();
                }
            }
        });
    }

    @Override
    public void setStartScrollY(int scrollY) {
        mStartScrollY = scrollY;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth + SHADOW_RADIUS,mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0,MEDIUM_PADDING,mWidth,MEDIUM_PADDING + 2*MEDIUM_PADDING*mScaleY,mWhitePaint);
        float offsetY = MEDIUM_PADDING;
        for(int i = 0, j = 0; i < 7*mOctaveSum;i++,j++){
            if(j == 0){
                canvas.drawLine(0,offsetY,mWidth,offsetY,mPaint);
                offsetY += 1.5f*mPaddingY;
            }else if(j == 4){
                canvas.drawLine(0,offsetY,mWidth,offsetY,mPaint);
                offsetY += 1.5f*mPaddingY;
            }else {
                canvas.drawRect(0,offsetY - mPaddingY/2,mWidth - 2*MEDIUM_PADDING,offsetY + mPaddingY/2,mPaint);
                canvas.drawLine(mWidth - 2*MEDIUM_PADDING,offsetY,mWidth,offsetY,mPaint);
                if(j == 3){
                    offsetY += 1.5f*mPaddingY;
                }else if(j == 6){
                    j = -1;
                    offsetY += 1.5f*mPaddingY;
                    canvas.drawText(String.format(Locale.ENGLISH,"C%d",i/7),mWidth - MEDIUM_PADDING,offsetY,mTextPaint);
                }else {
                    offsetY += 2*mPaddingY;
                }
            }
        }
    }
}
