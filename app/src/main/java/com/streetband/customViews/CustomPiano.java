package com.streetband.customViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.animation.DynamicAnimation;
import android.support.animation.FlingAnimation;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.streetband.R;
import com.streetband.utils.Density;

public class CustomPiano extends View {
    public static final String TAG = "CustomPiano";

    //final params
    private int BIG_PADDING = 80;
    private int MEDIUM_PADDING = 20;
    private int SMALL_PADDING = 5;
    private int SHADOW_RADIUS = 10;
    private int BOARD_HEIGHT = 80;
    private float mDensity;
    private int mPhoneWidth;
    private String mScrollText;

    //params
    private int mOctaveSum = 9;
    private int mWhiteKeyWidth = 80;
    private int mBlackKeyWidth = 40;

    //listeners
    private NoteListener mNoteListener;

    //tools
    private Paint mWhitePaint = new Paint();
    private Paint mBlackPaint = new Paint();
    private Paint mOrangePaint = new Paint();
    private Paint mShadowPaint = new Paint();
    private Paint mTextPaint = new Paint();
    private GestureDetector mGestureDetector;
    private FlingAnimation mFlingAnimation;

    //to draw
    private Bitmap mBitmap;
    private RectF mScrollRectF;
    private RectF mRightRectF;
    private RectF mLeftRectF;

    //dynamic params
    private int mWidth;
    private int mHeight;
    private int mVisibleWidth;

    private int mScrollX;
    private int mMinScrollX;
    private int mMaxScrollX;

    private boolean isScrollEnabled;
    private boolean isFlinging;


    //    private Set<Float> mPressedKeys = new HashSet<>();
    private float[] mPointers = new float[10];

    {
        for (int i = 0; i < mPointers.length; i++) {
            mPointers[i] = -1;
        }
    }


    public CustomPiano(Context context) {
        this(context, null);
    }

    public CustomPiano(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mDensity = Density.getDensity(context);
        mPhoneWidth = getResources().getDisplayMetrics().widthPixels;
        mScrollText = context.getString(R.string.scroll);

        //tools
        mWhitePaint.setColor(Color.WHITE);
        mWhitePaint.setShadowLayer(SHADOW_RADIUS, 0, SHADOW_RADIUS, Color.BLACK);
        setLayerType(LAYER_TYPE_SOFTWARE, mWhitePaint);
        mShadowPaint.setColor(Color.parseColor("#60FFFFFF"));
        mShadowPaint.setShadowLayer(SHADOW_RADIUS,SHADOW_RADIUS,0,Color.BLACK);
        mBlackPaint.setColor(Color.BLACK);
        mBlackPaint.setShadowLayer(SHADOW_RADIUS, 0, 0, Color.BLACK);
        mTextPaint.setTextSize(11 * mDensity);
        mTextPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        mTextPaint.setAntiAlias(true);
        mOrangePaint.setColor(getResources().getColor(R.color.orange));
        mOrangePaint.setShadowLayer(SHADOW_RADIUS, 0, 0, getResources().getColor(R.color.orange));

        mGestureDetector = new GestureDetector(context, new GestureListener());
        mFlingAnimation = new FlingAnimation(this, DynamicAnimation.SCROLL_X);


        BIG_PADDING *= mDensity;
        MEDIUM_PADDING *= mDensity;
        SMALL_PADDING *= mDensity;
        SHADOW_RADIUS *= mDensity;
        BOARD_HEIGHT *= mDensity;
        mWhiteKeyWidth *= mDensity;
        mBlackKeyWidth *= mDensity;

        mWidth = BIG_PADDING * 7 * mOctaveSum;

        mScrollX = (mWidth - mPhoneWidth)/2;
        mMinScrollX = 5*mWhiteKeyWidth;
        mMaxScrollX = mWidth - mPhoneWidth - 6*mWhiteKeyWidth;

        prepareBitmap();
    }

    private void prepareBitmap() {
        prepareToDraw();
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grand_piano_board);
        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mPhoneWidth, (int)(BOARD_HEIGHT+ 3 * mDensity));
        Canvas canvas = new Canvas(mBitmap);
        canvas.drawRect(0, BOARD_HEIGHT, mPhoneWidth, BOARD_HEIGHT + 3 * mDensity, mTextPaint);


        canvas.drawRoundRect(mScrollRectF, 5 * mDensity, 5 * mDensity, mWhitePaint);
        canvas.drawText(mScrollText, (mPhoneWidth - mTextPaint.measureText(mScrollText)) / 2, MEDIUM_PADDING + 2 * SMALL_PADDING, mTextPaint);
        canvas.drawRoundRect(mLeftRectF,5 * mDensity, 5 * mDensity, mWhitePaint);
        canvas.drawRoundRect(mRightRectF,5 * mDensity, 5 * mDensity, mWhitePaint);
    }

    private void prepareToDraw() {
        mScrollRectF = new RectF(mPhoneWidth / 2 - 2 * MEDIUM_PADDING, MEDIUM_PADDING, mPhoneWidth / 2 + 2 * MEDIUM_PADDING, 3 * MEDIUM_PADDING);
        mRightRectF = new RectF(mPhoneWidth - BIG_PADDING,MEDIUM_PADDING,mPhoneWidth - BIG_PADDING - 2*MEDIUM_PADDING,3 * MEDIUM_PADDING);
        mLeftRectF = new RectF(mPhoneWidth - 2*BIG_PADDING,MEDIUM_PADDING,mPhoneWidth - 2*BIG_PADDING - 2*MEDIUM_PADDING,3 * MEDIUM_PADDING);
    }

    //this method must be called in create
    public void addNoteListener(NoteListener noteListener){
        mNoteListener = noteListener;
    }

    @Override
    public void setScrollX(int value) {
        mScrollX = value;
        mScrollX = Math.max(mMinScrollX, Math.min(mMaxScrollX, mScrollX));
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mVisibleWidth = MeasureSpec.getSize(widthMeasureSpec);

        setMeasuredDimension(mVisibleWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int offsetX = -mScrollX % mWhiteKeyWidth;
        int till = (mScrollX + mPhoneWidth) / mWhiteKeyWidth + 1;
        if (till > 7 * mOctaveSum) {
            till = 7 * mOctaveSum;
        }
        for (int i = mScrollX / mWhiteKeyWidth, j = mScrollX % (7 * mWhiteKeyWidth) / mWhiteKeyWidth; i < till; i++, j++) {
            if (j == 0 || j == 3) {
                if (isPressedKeyContains((float) i)) {
                    canvas.drawRoundRect(offsetX + 2*mDensity, BOARD_HEIGHT, offsetX + mWhiteKeyWidth - 2*mDensity, mHeight - SMALL_PADDING, 5 * mDensity, 5 * mDensity, mShadowPaint);
                }else {
                    canvas.drawRoundRect(offsetX, BOARD_HEIGHT, offsetX + mWhiteKeyWidth - mDensity, mHeight, 5 * mDensity, 5 * mDensity, mWhitePaint);
                }
            } else {
                if (isPressedKeyContains((float) i)) {
                    canvas.drawRoundRect(offsetX + 2*mDensity, BOARD_HEIGHT, offsetX + mWhiteKeyWidth - 2*mDensity, mHeight - SMALL_PADDING, 5 * mDensity, 5 * mDensity, mShadowPaint);
                }else{
                    canvas.drawRoundRect(offsetX, BOARD_HEIGHT, offsetX + mWhiteKeyWidth - mDensity, mHeight, 5 * mDensity, 5 * mDensity, mWhitePaint);
                }
                canvas.drawRoundRect(offsetX - mBlackKeyWidth / 2, BOARD_HEIGHT, offsetX + mBlackKeyWidth / 2, mHeight - BIG_PADDING, 5 * mDensity, 5 * mDensity, mBlackPaint);
                if (isPressedKeyContains(i - 0.5f)) {
                    canvas.drawRoundRect(offsetX - mBlackKeyWidth / 2, BOARD_HEIGHT, offsetX + mBlackKeyWidth / 2, mHeight - BIG_PADDING, 5 * mDensity, 5 * mDensity, mWhitePaint);
                }
                if (j == 6) {
                    j = -1;
                }
            }
            offsetX += mWhiteKeyWidth;
        }

        canvas.drawBitmap(mBitmap, 0, 0, mTextPaint);

        if (isScrollEnabled) {
            canvas.drawCircle(mPhoneWidth / 2, 2 * MEDIUM_PADDING, SMALL_PADDING, mOrangePaint);
        } else {
            canvas.drawCircle(mPhoneWidth / 2, 2 * MEDIUM_PADDING, SMALL_PADDING, mBlackPaint);
        }
    }

    private boolean isPressedKeyContains(float i) {
        for (float j : mPointers) {
            if (i == j) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerIndex = event.getActionIndex();
//        Log.i(TAG,"action id = " + event.getPointerId(pointerIndex));
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);
        if (isScrollEnabled) {
            mGestureDetector.onTouchEvent(event);
        } else {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
//                    Log.i(TAG,"action down id = " + event.getPointerId(pointerIndex));
                    if (y > BOARD_HEIGHT) {
                        x += mScrollX;
                        float i = (int) (x / mWhiteKeyWidth);
                        if (y < mHeight - BIG_PADDING) {
                            x = x - (int) i / 7 * (7 * mWhiteKeyWidth);//in octave
                            int j = (int) i % 7;
                            if (j == 0 || j == 3) {
                                if (x > j * mWhiteKeyWidth + 1.5 * mBlackKeyWidth) {
                                    i += 0.5f;
                                }
                            } else if (j == 2 || j == 6) {
                                if (x < j * mWhiteKeyWidth + mBlackKeyWidth / 2) {
                                    i -= 0.5f;
                                }
                            } else {
                                if (x < j * mWhiteKeyWidth + mBlackKeyWidth / 2) {
                                    i -= 0.5f;
                                } else if (x > j * mWhiteKeyWidth + 1.5 * mBlackKeyWidth) {
                                    i += 0.5f;
                                }
                            }
                        }
                        mNoteListener.notePressed(i);
                        mPointers[event.getPointerId(pointerIndex)] = i;
//                        Log.i(TAG,"pointer added id = " + event.getPointerId(pointerIndex) + " note = " + i);
                        invalidate();
                    } else {
                        if (mScrollRectF.contains(x, y)) {
                            isScrollEnabled = !isScrollEnabled;
                            invalidate();
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(y > BOARD_HEIGHT){
                        x+=mScrollX;
                        float i = (int) (x / mWhiteKeyWidth);
                        if (y < mHeight - BIG_PADDING) {
                            x = x - (int) i / 7 * (7 * mWhiteKeyWidth);//in octave
                            int j = (int) i % 7;
                            if (j == 0 || j == 3) {
                                if (x > j * mWhiteKeyWidth + 1.5 * mBlackKeyWidth) {
                                    i += 0.5f;
                                }
                            } else if (j == 2 || j == 6) {
                                if (x < j * mWhiteKeyWidth + mBlackKeyWidth / 2) {
                                    i -= 0.5f;
                                }
                            } else {
                                if (x < j * mWhiteKeyWidth + mBlackKeyWidth / 2) {
                                    i -= 0.5f;
                                } else if (x > j * mWhiteKeyWidth + 1.5 * mBlackKeyWidth) {
                                    i += 0.5f;
                                }
                            }
                        }
                        if(mPointers[event.getPointerId(pointerIndex)] != i){
                            mNoteListener.noteChanged(mPointers[event.getPointerId(pointerIndex)],i);
                            mPointers[event.getPointerId(pointerIndex)] = i;
                            invalidate();
                        }
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_UP:
                    mNoteListener.noteReleased(mPointers[event.getPointerId(pointerIndex)]);
                    mPointers[event.getPointerId(pointerIndex)] = -1;
//                    Log.i(TAG,"pointer removed id = " + event.getPointerId(pointerIndex));
                    invalidate();
                    break;

            }
        }
        return true;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //INNER CLASSES
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            if (mScrollRectF.contains(e.getX(), e.getY())) {
                isScrollEnabled = !isScrollEnabled;
                invalidate();
                return true;
            }
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(e2.getY() > BOARD_HEIGHT) {
                mScrollX += distanceX;

                setScrollX(mScrollX);

                return true;
            }
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            isFlinging = true;
//            mFlingAnimation.setStartVelocity(-velocityX).addEndListener(new DynamicAnimation.OnAnimationEndListener() {
//                @Override
//                public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
//                    if(!canceled){
//                        isFlinging = false;
//                    }
//                }
//            }).start();

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    public interface NoteListener{
        void notePressed(float note);
        void noteReleased(float note);
        void noteChanged(float oldNote, float newNote);
    }
}
