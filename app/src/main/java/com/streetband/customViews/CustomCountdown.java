package com.streetband.customViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class CustomCountdown extends View {

    //final params
    private float mDensity;
    private float PADDING = 60;
    private float TEXT_SIZE = 80;

    //tools
    private Paint mPaint = new Paint();

    //colors
    private int RED_TRANSPARENT;
    private int DIRTY_WHITE;

    //dynamic params
    private int mWidth;
    private int mHeight;

    private int mSelectedNumber;

    public CustomCountdown(Context context) {
        this(context,null);
    }

    public CustomCountdown(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mDensity = context.getResources().getDisplayMetrics().density;


        RED_TRANSPARENT = Color.parseColor("#C8FF0008");
        DIRTY_WHITE = Color.parseColor("#bbbbbb");

        mWidth = (int)(mDensity*400);
        mHeight = (int)(mDensity*100);

        PADDING *= mDensity;
        TEXT_SIZE *= mDensity;
        mPaint.setTextSize(TEXT_SIZE);

    }

    public void setSelectedNumber(int n){
        mSelectedNumber = n;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth,mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(RED_TRANSPARENT);
        canvas.drawRoundRect(0,0,mWidth,mHeight,10*mDensity,10*mDensity,mPaint);
        mPaint.setColor(DIRTY_WHITE);
        float offsetX = PADDING;
        for(int i = 1; i <= 4;i++){
            if(i == mSelectedNumber){
                mPaint.setColor(Color.WHITE);
                canvas.drawText(String.valueOf(i),offsetX,TEXT_SIZE ,mPaint);
                mPaint.setColor(DIRTY_WHITE);
            }else {
                canvas.drawText(String.valueOf(i),offsetX,TEXT_SIZE ,mPaint);
            }
            offsetX += TEXT_SIZE;
        }

    }
}
