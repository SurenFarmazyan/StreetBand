package com.streetband.customViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.streetband.R;

public class CustomAddedInstrument extends View {

    //    final params
    private float mDensity;
    private int BIG_PADDING = 80;
    private int MEDIUM_PADDING = 20;
    private int SMALL_PADDING = 5;

    //    tools
    private Paint mStrokePaint = new Paint();
    private Paint mDynamicPaint = new Paint();
    private Paint mBackPaint = new Paint();
    private Paint mWhitTPaint = new Paint();
    private Path mDynamicPath = new Path();
    private Path mHeadphonePath = new Path();

    //    params
    private String mInstrumentName = "";
    private Bitmap mInstrumentIcon;
    private float mVolume;

    private AddedInstrumentListener mAddedInstrumentListener;

    //colors
    private int CHECKED_COLOR;
    private int UNCHECKED_COLOR;

    //    dynamic params
    private int mHeight;
    private int mWidth;

    private float mCircleX;

    private boolean isMuted;


    public CustomAddedInstrument(Context context) {
        this(context, null);
    }

    public CustomAddedInstrument(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {

        }

        mDensity = context.getResources().getDisplayMetrics().density;

        CHECKED_COLOR = R.color.colorPrimary;
        UNCHECKED_COLOR = Color.WHITE;

        mDynamicPaint.setColor(UNCHECKED_COLOR);
        mDynamicPaint.setStrokeWidth(2 * mDensity);
        mDynamicPaint.setStyle(Paint.Style.STROKE);
        mDynamicPaint.setAntiAlias(true);

        mStrokePaint.setColor(Color.WHITE);
        mStrokePaint.setTextSize(10 * mDensity);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(2 * mDensity);
        mStrokePaint.setAntiAlias(true);

        mBackPaint.setColor(Color.GREEN);
        mWhitTPaint.setColor(Color.parseColor("#80FFFFFF"));

        BIG_PADDING *= mDensity;
        MEDIUM_PADDING *= mDensity;
        SMALL_PADDING *= mDensity;

        mWidth = 3 * BIG_PADDING;
        mHeight = BIG_PADDING + SMALL_PADDING;

        mCircleX = BIG_PADDING + SMALL_PADDING;

        prepareDynamicPath();
        prepareHeadphonePath();
    }

    private void prepareDynamicPath() {
        mDynamicPath.reset();
        mDynamicPath.moveTo(2 * SMALL_PADDING, MEDIUM_PADDING + SMALL_PADDING);
        mDynamicPath.rLineTo(SMALL_PADDING, 0);
        mDynamicPath.rLineTo(SMALL_PADDING, -SMALL_PADDING);
        mDynamicPath.rLineTo(0, MEDIUM_PADDING);
        mDynamicPath.rLineTo(-SMALL_PADDING, -SMALL_PADDING);
        mDynamicPath.rLineTo(-SMALL_PADDING, 0);
        mDynamicPath.close();

        mDynamicPath.moveTo(2 * SMALL_PADDING, 2 * MEDIUM_PADDING);
        mDynamicPath.rLineTo(3 * SMALL_PADDING, -MEDIUM_PADDING);
    }

    private void prepareHeadphonePath() {
        mHeadphonePath.reset();

        mHeadphonePath.addRoundRect(2 * MEDIUM_PADDING + 2 * mDensity, 2 * MEDIUM_PADDING - (int) (1.5 * SMALL_PADDING), 2 * MEDIUM_PADDING + SMALL_PADDING, 2 * MEDIUM_PADDING + (int) (0.5 * SMALL_PADDING), 2 * mDensity, 2 * mDensity, Path.Direction.CW);
        mHeadphonePath.addRoundRect(3 * MEDIUM_PADDING - SMALL_PADDING, 2 * MEDIUM_PADDING - (int) (1.5 * SMALL_PADDING), 3 * MEDIUM_PADDING - 2 * mDensity, 2 * MEDIUM_PADDING + (int) (0.5 * SMALL_PADDING), 2 * mDensity, 2 * mDensity, Path.Direction.CW);

        mHeadphonePath.moveTo(2 * MEDIUM_PADDING, 2 * MEDIUM_PADDING);
        mHeadphonePath.rLineTo(0, -2 * SMALL_PADDING);
        mHeadphonePath.arcTo(2 * MEDIUM_PADDING, MEDIUM_PADDING, 3 * MEDIUM_PADDING, 2 * MEDIUM_PADDING, 180, 180, true);
        mHeadphonePath.rLineTo(0, 2 * SMALL_PADDING);
    }

    public void setInstrumentName(String instrumentName) {
        mInstrumentName = instrumentName;
        invalidate();
    }

    public void setInstrumentIcon(Bitmap instrumentIcon) {
        mInstrumentIcon = instrumentIcon;
        invalidate();
    }

    public void setVolume(float volume) {
        mCircleX = volume * 4 * MEDIUM_PADDING + 4 * MEDIUM_PADDING + SMALL_PADDING;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float y = event.getY();
                if (x > mCircleX - 2 * SMALL_PADDING && x < mCircleX + 2 * SMALL_PADDING && y > MEDIUM_PADDING + SMALL_PADDING && y < 2 * MEDIUM_PADDING) {
                    return true;
                } else if (x > SMALL_PADDING && x < SMALL_PADDING + MEDIUM_PADDING) {
                    if (isMuted) {
                        mDynamicPaint.setColor(UNCHECKED_COLOR);
                    } else {
                        mDynamicPaint.setColor(CHECKED_COLOR);
                    }
                    isMuted = !isMuted;
                    if(mAddedInstrumentListener != null){
                        mAddedInstrumentListener.muteChanged(isMuted);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mCircleX = x;
                mCircleX = Math.max(4 * MEDIUM_PADDING + SMALL_PADDING, Math.min(8 * MEDIUM_PADDING - SMALL_PADDING, mCircleX));
                if (mAddedInstrumentListener != null) {
                    mAddedInstrumentListener.volumeChanged((mCircleX - BIG_PADDING) / BIG_PADDING);
                }
                invalidate();
                return true;

        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawText(mInstrumentName, SMALL_PADDING, MEDIUM_PADDING, mStrokePaint);
        canvas.drawPath(mDynamicPath, mDynamicPaint);
        canvas.drawPath(mHeadphonePath, mStrokePaint);

        canvas.drawRoundRect(4 * MEDIUM_PADDING, MEDIUM_PADDING + SMALL_PADDING, 8 * MEDIUM_PADDING, 2 * MEDIUM_PADDING - SMALL_PADDING, 5 * mDensity, 5 * mDensity, mBackPaint);
        canvas.drawCircle(mCircleX, MEDIUM_PADDING + 2 * SMALL_PADDING, 2 * SMALL_PADDING, mStrokePaint);
        canvas.drawCircle(mCircleX, MEDIUM_PADDING + 2 * SMALL_PADDING, 2 * SMALL_PADDING, mWhitTPaint);

        canvas.drawLine(BIG_PADDING, 2 * SMALL_PADDING, 4 * MEDIUM_PADDING + 2 * SMALL_PADDING, 2 * SMALL_PADDING, mStrokePaint);
        canvas.drawLine(2 * BIG_PADDING - SMALL_PADDING, 2 * SMALL_PADDING, 2 * BIG_PADDING + SMALL_PADDING, 2 * SMALL_PADDING, mStrokePaint);
        canvas.drawLine(2 * BIG_PADDING, SMALL_PADDING, 2 * BIG_PADDING, 3 * SMALL_PADDING, mStrokePaint);

        if (mInstrumentIcon != null) {
            canvas.drawBitmap(mInstrumentIcon, 9 * MEDIUM_PADDING, SMALL_PADDING, mBackPaint);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //INNER CLASSES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    public interface AddedInstrumentListener {
        void volumeChanged(float volume);

        void muteChanged(boolean muted);

        void instrumentSelceted();
    }
}
