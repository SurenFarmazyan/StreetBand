package com.streetband.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.streetband.R;

public class CustomCursor extends View {
    private static final String TAG = "CustomCursor";


//    final params
    private float mDensity;
    private int BIG_PADDING = 80;
    private int MEDIUM_PADDING = 20;
    private int SMALL_PADDING = 10;
    private int OFFSET_Y = 48;
    private int ARROW_HEIGHT = 30;

//    params
    private float mPosition = 0;
    private boolean showLine = true;

//    tools
    private Path mArrow = new Path();
    private Paint mStrokePaint = new Paint();
    private Paint mFillPaint = new Paint();
    private Paint mBlackPaint = new Paint();


//    dynamic params
    private int mHeight;
    private RectF mRectF = new RectF();

    private boolean isDragging;

    public CustomCursor(Context context) {
        this(context,null);
    }

    public CustomCursor(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if(attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomCursor);
            mPosition = typedArray.getFloat(R.styleable.CustomCursor_position,0);
            showLine = typedArray.getBoolean(R.styleable.CustomCursor_show_line,true);
            typedArray.recycle();
        }

        mDensity = context.getResources().getDisplayMetrics().density;

        mStrokePaint.setColor(Color.WHITE);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(mDensity);
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setTextSize(10*mDensity);
        mFillPaint.setColor(Color.parseColor("#90FFFFFF"));
        mBlackPaint.setColor(Color.BLACK);

        BIG_PADDING *= mDensity;
        MEDIUM_PADDING *= mDensity;
        SMALL_PADDING *= mDensity;
        OFFSET_Y *= mDensity;
        ARROW_HEIGHT *= mDensity;

        mHeight = (int)(500*mDensity);
        preparePath();
    }


    public void setPosition(float position){
        mPosition = BIG_PADDING*position;
        preparePath();
        invalidate();
    }

    public void setShowLine(boolean show){
        showLine = show;
        invalidate();
    }

    private void preparePath(){
        mArrow.reset();
        mArrow.moveTo(mPosition,OFFSET_Y + ARROW_HEIGHT);
        mArrow.lineTo(mPosition + SMALL_PADDING,OFFSET_Y + MEDIUM_PADDING);
        mArrow.lineTo(mPosition + SMALL_PADDING,OFFSET_Y);
        mArrow.lineTo(mPosition - SMALL_PADDING,OFFSET_Y);
        mArrow.lineTo(mPosition - SMALL_PADDING,OFFSET_Y + MEDIUM_PADDING);
        mArrow.close();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(mArrow,mStrokePaint);
        canvas.drawPath(mArrow,mFillPaint);
        if(showLine) {
            canvas.drawLine(mPosition, OFFSET_Y + ARROW_HEIGHT, mPosition, mHeight, mStrokePaint);
        }
        if(isDragging){
            canvas.drawRoundRect(mPosition - MEDIUM_PADDING,OFFSET_Y - MEDIUM_PADDING,mPosition + MEDIUM_PADDING,OFFSET_Y,5*mDensity,5*mDensity,mBlackPaint);
            float tact = mPosition/BIG_PADDING;
            tact = tact - tact%0.25f;
            float width = mStrokePaint.measureText(String.valueOf(tact));
            canvas.drawText(String.valueOf(tact),mPosition - width/2,OFFSET_Y - 5*mDensity,mStrokePaint);
        }
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
