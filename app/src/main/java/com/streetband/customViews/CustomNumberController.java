package com.streetband.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.streetband.R;

import static android.view.View.MeasureSpec.EXACTLY;

public class CustomNumberController extends View{
    public static final String TAG = "CustomNumberController";

    //final params
    private Context mContext;
    private float mDensity;
    private int DEFAULT_WIDTH = 50;
    private int DEFAULT_HEIGHT = 120;
    private int INTERVAL = 4;

    //params
    private int mMaxNumber;
    private int mMinNumber;
    private Bitmap mUpArrow;
    private Bitmap mDownArrow;
    private int mUpVectorId;
    private int mDownVectorId;

    private NumberChanged mNumberChanged;

    //tools
    private Paint mBlackPaint = new Paint();
    private Paint mTextPaint = new Paint();

    //dynamic params
    private int mWidth;
    private int mHeight;
    private int mPartHeight;


    private int mCurrentNumber;
    private boolean isScrolling;
    private float mStartY;


    public CustomNumberController(Context context) {
        this(context,null);
    }

    public CustomNumberController(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mDensity = context.getResources().getDisplayMetrics().density;
        mContext = context;
        if(attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomNumberController);

            mMaxNumber = typedArray.getInt(R.styleable.CustomNumberController_max_number,10);
            mMinNumber = typedArray.getInt(R.styleable.CustomNumberController_min_number,1);

            mUpVectorId = typedArray.getResourceId(R.styleable.CustomNumberController_up_arrow,R.drawable.ic_arrow_upward_black_24dp);
            mDownVectorId = typedArray.getResourceId(R.styleable.CustomNumberController_down_arrow,R.drawable.ic_arrow_downward_black_24dp);
            typedArray.recycle();
        }
        mBlackPaint.setStyle(Paint.Style.STROKE);
        mBlackPaint.setStrokeWidth(mDensity);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setAntiAlias(true);

        DEFAULT_WIDTH *= mDensity;
        DEFAULT_HEIGHT *= mDensity;
        INTERVAL *= mDensity;

        mCurrentNumber = mMinNumber;
    }

    public void setCurrentNumber(int number){
        if(number > mMinNumber || number < mMinNumber){
            Log.e(TAG,"number " + number + " is out of bounds");
            return;
        }
        mCurrentNumber = number;
        invalidate();
    }

    public void addNumberChangedListener(NumberChanged numberChanged){
        mNumberChanged = numberChanged;
    }

    private Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);

            drawable = (DrawableCompat.wrap(drawable)).mutate();

        Bitmap bitmap = Bitmap.createBitmap(mWidth,
                mPartHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private void up(){
        if(mCurrentNumber == mMaxNumber){
            mNumberChanged.onUpLimit();
        }else {
            mCurrentNumber++;
            invalidate();
            if(mNumberChanged != null){
                mNumberChanged.onNumberChanged(mCurrentNumber);
            }
        }
    }
    private void down(){
        if(mCurrentNumber == mMinNumber){
            if(mNumberChanged != null){
                mNumberChanged.onDownLimit();
            }
        }else {
            mCurrentNumber--;
            invalidate();
            if(mNumberChanged != null){
                mNumberChanged.onNumberChanged(mCurrentNumber);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        switch (mode){
            case EXACTLY:
                mWidth = MeasureSpec.getSize(widthMeasureSpec);
                break;
                default:
                   mWidth = DEFAULT_WIDTH;
        }
        mode = MeasureSpec.getMode(heightMeasureSpec);

        switch (mode){
            case EXACTLY:
                mHeight = MeasureSpec.getSize(heightMeasureSpec);
                default:
                    mHeight = DEFAULT_HEIGHT;
        }
        mPartHeight = mHeight/3;
        mUpArrow = getBitmapFromVectorDrawable(mContext,mUpVectorId);
        mDownArrow = getBitmapFromVectorDrawable(mContext,mDownVectorId);
        mTextPaint.setTextSize(mPartHeight/1.5f);
        setMeasuredDimension(mWidth,mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {

            canvas.drawBitmap(mUpArrow,0,0,mBlackPaint);
            canvas.drawBitmap(mDownArrow,0,2*mPartHeight,mBlackPaint);
            String number = String.valueOf(mCurrentNumber);
            float width = mTextPaint.measureText(number);
            canvas.drawRect(1,mPartHeight,mWidth,2*mPartHeight,mBlackPaint);
            canvas.drawText(number,mWidth/2 - width/2,2*mPartHeight - mPartHeight/3.5f,mTextPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(y < mPartHeight){
                    up();
                }else if(y < 2*mPartHeight){
                    isScrolling = true;
                    mStartY = y;
                }else {
                    down();
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if(isScrolling){
                    if(y > mStartY + INTERVAL){
                        mStartY = y;
                        down();
                    }else if(y< mStartY - INTERVAL){
                        mStartY = y;
                        up();
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isScrolling = false;
        }


        return true;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////INNER CLASSES
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public interface NumberChanged{
        void onNumberChanged(int number);
        void onUpLimit();
        void onDownLimit();
    }
}
