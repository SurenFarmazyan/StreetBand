package com.streetband.customViews;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomAddedInstrumentsList  extends RecyclerView{


    //final params
    private int PADDING = 10;
    private int SMALL_PADDING = 5;
    private int SHADOW_RADIUS=8;
    private float mDensity;

    //tools
    private Paint mPaint = new Paint();
    private Paint mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);


    //dynamic params
    private int mHeight;

    public CustomAddedInstrumentsList(Context context) {
        super(context);
        init(context);
    }

    public CustomAddedInstrumentsList(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomAddedInstrumentsList(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);

        mHeight = MeasureSpec.getSize(heightSpec);
    }

    private void init(Context context){
        mDensity = context.getResources().getDisplayMetrics().density;

        setWillNotDraw(false);
        mPaint.setColor(Color.parseColor("#bbbbbb"));
        mPaint.setStrokeWidth(mDensity);
        PADDING *= mDensity;
        SMALL_PADDING *= mDensity;
        SHADOW_RADIUS *= mDensity;

        mShadowPaint.setShadowLayer(SHADOW_RADIUS, 0, 0, Color.BLACK);
        mShadowPaint.setColor(Color.parseColor("#FF2B2B2B"));

        setLayerType(LAYER_TYPE_SOFTWARE, mShadowPaint);
    }

    @Override
    public void onDraw(Canvas c) {
        c.drawRect(0,0,getMeasuredWidth() - SHADOW_RADIUS,mHeight,mShadowPaint);

//        c.drawRect(getMeasuredWidth() - SHADOW_RADIUS - SMALL_PADDING/2,0,getMeasuredWidth() - SHADOW_RADIUS,mHeight,mShadowPaint);


        c.drawLine(getMeasuredWidth() - SMALL_PADDING  - SHADOW_RADIUS,mHeight/2 -PADDING,getMeasuredWidth() - SMALL_PADDING  - SHADOW_RADIUS,mHeight/2 + PADDING,mPaint);
        c.drawLine(getMeasuredWidth() - 2*SMALL_PADDING  - SHADOW_RADIUS,mHeight/2 -PADDING,getMeasuredWidth() - 2*SMALL_PADDING  - SHADOW_RADIUS,mHeight/2 +PADDING,mPaint);
        c.drawLine(getMeasuredWidth() - 3*SMALL_PADDING  - SHADOW_RADIUS,mHeight/2 -PADDING,getMeasuredWidth() - 3*SMALL_PADDING  - SHADOW_RADIUS,mHeight/2 +PADDING,mPaint);

    }
}
