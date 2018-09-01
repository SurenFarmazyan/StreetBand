package com.streetband.customViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.streetband.R;
import com.streetband.utils.BitmapOperations;
import com.streetband.utils.Density;

public class CustomChineseDrumsEdge extends View implements Edge{
    private static final String TAG = "CustomChineseDrumsEdge";
    private static final int OCTAVE_SUM = 2;

    //final params
    private float mDensity;
    private int DEFAULT_WIDTH = 95;
    private int BIG_PADDING = 80;
    private int MEDIUM_PADDING = 20;
    private int SMALL_PADDING = 5;
    private int SHADOW_RADIUS = 8;

    //tools
    private Paint mPaint = new Paint();
    private Paint mStrokePaint = new Paint();
    private Paint mShadowPaint = new Paint();

    //dynamic params
    private int mWidth;
    private int mHeight;

    private int mScrollY = 0;
    private int mStartScrollY = 0;

    private float mScaleY = 1.0f;
    private float mScaleCoefficient = 1.0f;
    private float mMerge = 0;
    private float mCoefficient = 0;


    private Bitmap[] mBitmaps = new Bitmap[17];

    public CustomChineseDrumsEdge(Context context) {
        this(context,null);
    }

    public CustomChineseDrumsEdge(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mDensity = Density.getDensity(context);

        DEFAULT_WIDTH *= mDensity;
        BIG_PADDING *= mDensity;
        MEDIUM_PADDING *= mDensity;
        SMALL_PADDING *= mDensity;
        SHADOW_RADIUS *= mDensity;

        mWidth = DEFAULT_WIDTH;

        mCoefficient = ((float) (BIG_PADDING - 2 * MEDIUM_PADDING)) / (12 * OCTAVE_SUM);

        prepareBitmaps(context);
    }
    private void prepareBitmaps(Context context){
        mBitmaps[0] = BitmapOperations.getBitmapFromVectorDrawable(context,R.drawable.v_drums_bass,MEDIUM_PADDING,MEDIUM_PADDING);
        for(int i = 1; i <= 5; i++){
            mBitmaps[i] = BitmapOperations.getBitmapFromVectorDrawable(context, R.drawable.v_drums_boom,MEDIUM_PADDING,MEDIUM_PADDING);
        }
        for(int i = 6; i <= 10;i++){
            mBitmaps[i] = BitmapOperations.getBitmapFromVectorDrawable(context,R.drawable.v_drums_bell,MEDIUM_PADDING,MEDIUM_PADDING);
        }
        mBitmaps[11] = BitmapOperations.getBitmapFromVectorDrawable(context,R.drawable.v_drums_cymbal,MEDIUM_PADDING,MEDIUM_PADDING);
        mBitmaps[12] = BitmapOperations.getBitmapFromVectorDrawable(context,R.drawable.v_drums_cymbal,MEDIUM_PADDING,MEDIUM_PADDING);
        mBitmaps[13] = BitmapOperations.getBitmapFromVectorDrawable(context,R.drawable.v_drums_cymbal_open,MEDIUM_PADDING,MEDIUM_PADDING);
        mBitmaps[14] = BitmapOperations.getBitmapFromVectorDrawable(context,R.drawable.v_drums_cymbal_close,MEDIUM_PADDING,MEDIUM_PADDING);
        mBitmaps[15] = BitmapOperations.getBitmapFromVectorDrawable(context,R.drawable.v_drums_cymbal_open,MEDIUM_PADDING,MEDIUM_PADDING);
        mBitmaps[16] = BitmapOperations.getBitmapFromVectorDrawable(context,R.drawable.v_drums_cymbal_close,MEDIUM_PADDING,MEDIUM_PADDING);
    }

    @Override
    public void setScrollY(int value) {
        mScrollY = value;
        super.setScrollY(value);
    }

    @Override
    public void synchronizeWithMainBoard(CustomMainBoard customMainBoard){
        customMainBoard.addSecondScrollAndScaleListener(new CustomMainBoard.ScrollAndScaleListener() {
            @Override
            public void scrolled(int x, int y) {
                setScrollY(y - mStartScrollY);
            }

            @Override
            public void scaleChanged(float scaleX, float scaleY) {
                mScaleY = scaleY;
                mScaleCoefficient = (mCoefficient*mScaleY)/MEDIUM_PADDING;
                if(mScaleCoefficient >= 1.0f){
                    mMerge = (mCoefficient*mScaleY - MEDIUM_PADDING)/2;
                    mScaleCoefficient = 1.0f;
                }else {
                    mMerge = 0;
                }
                invalidate();
            }
        });
    }

    @Override
    public void setStartScrollY(int scrollY) {
        mStartScrollY = scrollY;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mHeight = 2*MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(mWidth,mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipRect(0,0,mWidth - SHADOW_RADIUS,mHeight);
        float offsetY = MEDIUM_PADDING;
        for(int i = 0; i <= OCTAVE_SUM*12;i++){
            canvas.drawLine(0,offsetY,mWidth -SHADOW_RADIUS,offsetY,mStrokePaint);
            offsetY += mCoefficient*mScaleY;
        }
        if(mScaleCoefficient < 1.0f) {
            canvas.save();
            canvas.scale(1.0f, mScaleCoefficient);
        }
        offsetY = MEDIUM_PADDING/mScaleCoefficient;
        for(int i = 0; i < mBitmaps.length; i++){
            canvas.drawBitmap(mBitmaps[i],(mWidth - SHADOW_RADIUS - MEDIUM_PADDING)/2,offsetY + mMerge,mPaint);
            offsetY += MEDIUM_PADDING + 2*mMerge;
        }
        if(mScaleCoefficient < 1.0f) {
            canvas.restore();
        }
    }
}
