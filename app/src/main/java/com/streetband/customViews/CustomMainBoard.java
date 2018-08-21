package com.streetband.customViews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.PopupWindow;

import com.streetband.R;

import java.util.ArrayList;
import java.util.List;

public class CustomMainBoard extends ViewGroup {
    public static final String TAG = "CustomMainBoard";


    //final params
    private float mDensity;

    private int BIG_PADDING = 80;
    private int MEDIUM_PADDING = 20;
    private int SMALL_PADDING = 5;

    private ScrollAndScaleListener mScrollAndScaleListener;
    private ScrollY mScrollYListener;
    private PopupWindow mPopupWindow;

    //tools
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleDetector;
    private ViewTreeObserver mViewTreeObserver;
    private Paint mBoldLinePaint = new Paint();
    private Paint mLinePaint = new Paint();
    private Paint mRectPaint = new Paint();

    //params
    private float mBoardLength;


    //dynamic params
    private int mWidth;
    private int mHeight;
    private Rect mVisibleArea = new Rect();
    private float mVisibleWidth;

    private boolean isExpanded;
    private boolean isDragging;
    private boolean isDraggingFromLeft;


    private boolean isScaling;
    private boolean isScaleHor;
    private float mScaleX = 1.0f;
    private float mScaleY = 1.0f;
    private float mMinScaleX = 1.0f;
    private float mMinScaleY = 1.0f;
    private float mMaxScaleX = 4.0f;
    private float mMaxScaleY = 4.0f;


    private int mScrollX = 0;
    private int mScrollY = 0;
    private float mPureScrollX = 0;
    //    private int mScrollY = 0;
    private float mFocusX;
    private float mFocusY;
    private float mStartScrollX;
    private float mStartScaleX;
    private float mStartScrollY;
    private float mStartScaleY;
    private int mMaxScrollY;
    private int mMinScrollY;

    private int mSelectedRow = -1;
    private CustomEditBoard mSelectedEditBoard;


    private List<List<CustomEditBoard>> mEditBoards = new ArrayList<>();

    //for expanded
    private Helper[] mHelpers;
    private float mCoefficient;

    //for collapsed


    int x;
    int y;
    int row;

    public CustomMainBoard(Context context) {
        this(context, null);
    }

    public CustomMainBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "CustomMainBoard : canstructer");
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomMainBoard);
            mBoardLength = typedArray.getFloat(R.styleable.CustomMainBoard_board_length, 8);
            typedArray.recycle();
        }

        setWillNotDraw(false);

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mGestureDetector = new GestureDetector(context, new GestureListener());
        mViewTreeObserver = getViewTreeObserver();
        mViewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getGlobalVisibleRect(mVisibleArea);
                mVisibleWidth = (int) (mVisibleArea.width() / mScaleX);
//                mVisibleHeight = (int) (mVisibleArea.height() / mScaleY);
                if(!isExpanded){
                    mMaxScrollY = mHeight - mVisibleArea.height();
                }
            }
        });

        mDensity = context.getResources().getDisplayMetrics().density;


        //paint
        mBoldLinePaint.setColor(Color.WHITE);
        mBoldLinePaint.setStrokeWidth(2 * mDensity);
        mLinePaint.setColor(Color.WHITE);
        mRectPaint.setColor(Color.parseColor("#262626"));

        BIG_PADDING *= mDensity;
        MEDIUM_PADDING *= mDensity;
        SMALL_PADDING *= mDensity;

        measure();
    }

    private void measure() {
        mWidth = (int) (mBoardLength * BIG_PADDING);
        mHeight = (int) ((mEditBoards.size() - 1) * (BIG_PADDING + SMALL_PADDING) + (BIG_PADDING - 2 * MEDIUM_PADDING) * mScaleY + 2 * MEDIUM_PADDING + SMALL_PADDING);
    }

    private void prepareBackground() {
        CustomEditBoard customEditBoard = mEditBoards.get(mSelectedRow).get(0);
        mHelpers = new Helper[7 * customEditBoard.getOctaveSum() + 1];
        float offsetY = 0;
        mHelpers[0] = new Helper(offsetY, true);
        mCoefficient = customEditBoard.getCoefficient();
        offsetY += mCoefficient;
        for (int i = 1, j = 0; i < mHelpers.length; i++, j++) {
            if (j == 3) {
                mHelpers[i] = new Helper(offsetY, true);
                offsetY += mCoefficient;
            } else if (j == 6) {
                j = -1;
                mHelpers[i] = new Helper(offsetY, true);
                offsetY += mCoefficient;
            } else {
                mHelpers[i] = new Helper(offsetY, false);
                offsetY += 2 * mCoefficient;
            }

        }
    }

    private void scroll() {
        mPureScrollX = mScrollX / mScaleX;
//        mScrollY = (int) ((mMinScrollY - mScrollY )/ mScaleY) + mMinScrollY;
        super.scrollTo(mScrollX, mScrollY);
        if (mScrollAndScaleListener != null) {
            mScrollAndScaleListener.scrolled(mScrollX, mScrollY);
        }

    }

    public void addScrollAndScaleListener(ScrollAndScaleListener scrollAndScaleListener) {
        mScrollAndScaleListener = scrollAndScaleListener;
    }

    public void addScrollYListener(ScrollY scrollY){
        mScrollYListener = scrollY;
    }

    public void addPopupWindow(PopupWindow popupWindow) {
        mPopupWindow = popupWindow;
    }

    public void setScrollY(int scrollY) {
        mScrollY = scrollY;
        scroll();
    }
    public void setScrollX(int scrollX){
        mScrollX = scrollX;
        scroll();
    }

    //not to use only for animation
    public void setScaleY(float scaleY) {
        mScaleY = scaleY;
        mHeight = (int) ((mEditBoards.size() - 1) * (BIG_PADDING + SMALL_PADDING) + (BIG_PADDING - 2 * MEDIUM_PADDING) * mScaleY + 2 * MEDIUM_PADDING + SMALL_PADDING);
//        mVisibleHeight = (int) (mVisibleArea.height() / mScaleY);
        int t = (int)((BIG_PADDING - 2*MEDIUM_PADDING)*(mScaleY - 1));

        if (isExpanded) {
            mMaxScrollY = mMinScrollY + (int) ((BIG_PADDING - 2 * MEDIUM_PADDING) * mScaleY + 2 * MEDIUM_PADDING) - mVisibleArea.height();
        }
        for (int i = 0; i < mEditBoards.get(mSelectedRow).size(); i++) {
            CustomEditBoard customEditBoard = mEditBoards.get(mSelectedRow).get(i);
            customEditBoard.setScaleY(mScaleY);
            customEditBoard.setBottom(customEditBoard.getTop() + BIG_PADDING + t);
        }
        for(int i = mSelectedRow + 1; i < mEditBoards.size();i++){
            for(int j = 0; j < mEditBoards.get(i).size();j++){
                CustomEditBoard customEditBoard = mEditBoards.get(i).get(j);
                customEditBoard.layout(customEditBoard.getLeft(),i*(BIG_PADDING + SMALL_PADDING) + t,customEditBoard.getRight(),i*(BIG_PADDING + SMALL_PADDING) + BIG_PADDING +t);
            }
        }
    }


    public void setScaleX(float scaleX) {
        mScaleX = scaleX;
        mWidth = (int) (mBoardLength * BIG_PADDING * mScaleX);
        mVisibleWidth = (int) (mVisibleArea.width() / mScaleX);
        if (isExpanded) {
            for (int i = 0; i < mEditBoards.get(mSelectedRow).size(); i++) {
                CustomEditBoard customEditBoard = mEditBoards.get(mSelectedRow).get(i);
                customEditBoard.setScaleX(mScaleX);
            }
        } else {
            for (int i = 0; i < mEditBoards.size(); i++) {
                for (int j = 0; j < mEditBoards.get(i).size(); j++) {
                    CustomEditBoard customEditBoard = mEditBoards.get(i).get(j);
                    customEditBoard.setScaleX(mScaleX);
                }
            }
        }
        if(mScrollAndScaleListener != null){
            mScrollAndScaleListener.scaleChanged(mScaleX,mScaleY);
        }
    }

    public void onScrollY(int distanceY){
        mScrollY += distanceY;
        mScrollY = Math.max(mMinScrollY, Math.min(mScrollY, mMaxScrollY));
        scroll();
    }

    public void addRow() {
        mEditBoards.add(new ArrayList<CustomEditBoard>());
        mHeight += BIG_PADDING + SMALL_PADDING;
        if (!isExpanded) {
            mMaxScrollY = mHeight - mVisibleArea.height();
        }
    }

    public void addChild(CustomEditBoard customEditBoard, int row) {
        if (row >= mEditBoards.size()) {
            return;
        }
        mEditBoards.get(row).add(customEditBoard);

        addView(customEditBoard);
    }

    public int getSelectedRow(){
        if(mSelectedEditBoard == null)
            mSelectedRow = -1;
        return mSelectedRow;
    }

    public void openRow(int row) {
        if(row < 0 || row >= mEditBoards.size() || isExpanded){
            return;
        }
        mSelectedEditBoard.setSelected(false);
        mSelectedRow = row;
        AnimatorSet animatorSet = new AnimatorSet();
        mMinScaleY = (float) (mVisibleArea.height() - 2 * MEDIUM_PADDING) / (BIG_PADDING - 2 * MEDIUM_PADDING);
        mMinScrollY = row * (BIG_PADDING + SMALL_PADDING);

        prepareBackground();

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1.0f, mMinScaleY).setDuration(1000);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", mScaleX, mScaleX + 2.0f).setDuration(1000);
        ObjectAnimator scrollY = ObjectAnimator.ofInt(this, "scrollY", mScrollY, mMinScrollY).setDuration(1000);
        ObjectAnimator scrollX = ObjectAnimator.ofInt(this,"scrollX", mScrollX,mSelectedEditBoard.getLeft()).setDuration(1000);
        for (int i = 0; i < mEditBoards.get(row).size(); i++) {
            CustomEditBoard customEditBoard = mEditBoards.get(row).get(i);
            customEditBoard.open(mMinScaleY);
            mMaxScaleY = mMinScaleY + 4 * customEditBoard.getOctaveSum();
        }

        animatorSet.play(scaleY).with(scrollY).before(scaleX).before(scrollX);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isExpanded = true;
                mMaxScrollY = mMinScrollY + (int) ((BIG_PADDING - 2 * MEDIUM_PADDING) * mScaleY + 2 * MEDIUM_PADDING) - mVisibleArea.height();
            }
        });

        animatorSet.start();
    }

    public void closeRow() {
        if(!isExpanded){
            return;
        }
        isExpanded = false;
        int y =  (mSelectedRow + 1)*(BIG_PADDING - SMALL_PADDING) - mVisibleArea.height();
        y = Math.max(0,y);

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", mScaleY, 1.0f).setDuration(1000);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", mScaleX, 1.0f).setDuration(1000);
        ObjectAnimator scrollY = ObjectAnimator.ofInt(this, "scrollY", mScrollY, y).setDuration(1000);
        ObjectAnimator scrollX = ObjectAnimator.ofInt(this,"scrollX", mScrollX,(int)(mSelectedEditBoard.getStart()*BIG_PADDING)).setDuration(1000);
        for (int i = 0; i < mEditBoards.get(mSelectedRow).size(); i++) {
            mEditBoards.get(mSelectedRow).get(i).close(mMinScaleY);
        }
        animatorSet.play(scaleX).with(scrollX).before(scaleY).before(scrollY);

        mMinScaleY = 1.0f;

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mMinScrollY = 0;
                mMaxScrollY = mHeight - mVisibleArea.height();
                mSelectedEditBoard = null;
            }
        });

        animatorSet.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        Log.i(TAG,"onMeasure");
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
//        Log.i(TAG,"onLayout");
        for (int j = 0; j < mEditBoards.size(); j++) {
            for (int k = 0; k < mEditBoards.get(j).size(); k++) {
                CustomEditBoard v = mEditBoards.get(j).get(k);
                v.layout((int)(v.getStart()*BIG_PADDING),j*(BIG_PADDING + SMALL_PADDING),(int)(v.getEnd()*BIG_PADDING),j*(BIG_PADDING + SMALL_PADDING) + BIG_PADDING);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mScaleY > 1.5f) {
//            CustomEditBoard customEditBoard = mEditBoards.get(mSelectedRow).get(0);
//            mCoefficient = customEditBoard.getCoefficient();
            for (int i = 0; i < mHelpers.length; i++) {
                Helper helper = mHelpers[i];
                if (helper.isLine) {
                    canvas.drawLine(mScrollX, mMinScrollY + MEDIUM_PADDING + helper.offsetY * mScaleY, mScrollX + mVisibleArea.width(), mMinScrollY + MEDIUM_PADDING + helper.offsetY * mScaleY, mLinePaint);
                } else {
                    canvas.drawRect(mScrollX, mMinScrollY + MEDIUM_PADDING + helper.offsetY * mScaleY, mScrollX + mVisibleArea.width(), mMinScrollY + MEDIUM_PADDING + (helper.offsetY + mCoefficient) * mScaleY, mRectPaint);
                }
            }
        }

        int start = (int)(mPureScrollX/BIG_PADDING);
        float offsetX =  start*BIG_PADDING*mScaleX;
        int till = (int)((mPureScrollX + mVisibleWidth)/BIG_PADDING) + 1;

        Log.i(TAG,"start = " + start + " offsetX = " + offsetX + " till = " + till);

        for (int i = start; i < till; i++) {
            canvas.drawLine(offsetX, mScrollY, offsetX, mScrollY + mVisibleArea.height(), mBoldLinePaint);
            if (mScaleX > 1.5f) {
                for(int j = 0; j < 3; j++){
                    offsetX += MEDIUM_PADDING*mScaleX;
                    canvas.drawLine(offsetX,mScrollY,offsetX,mScrollY + mVisibleArea.height(), mLinePaint);
                }
                offsetX += MEDIUM_PADDING*mScaleX;
            }else {
                offsetX += BIG_PADDING * mScaleX;
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = (int) event.getX() + mScrollX;
        y = (int) event.getY() + mScrollY;
        row = y / (BIG_PADDING + SMALL_PADDING);

        mScaleDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mSelectedEditBoard != null && !isExpanded) {
                    if (y < mSelectedEditBoard.getTop() || y > mSelectedEditBoard.getBottom()) {
                        return true;
                    }
                    if (x > mSelectedEditBoard.getLeft() && x < mSelectedEditBoard.getLeft() + MEDIUM_PADDING) {
                        isDraggingFromLeft = true;
                        isDragging = true;
                    } else if (x < mSelectedEditBoard.getRight() && x > mSelectedEditBoard.getRight() - MEDIUM_PADDING) {
                        isDraggingFromLeft = false;
                        isDragging = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isDragging) {
                    isDragging = false;
                }
                if(mScrollYListener != null){
                    mScrollYListener.isScrolling(false);
                }
                break;
        }

        return true;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //INNER CLASSES
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!isScaling && !isDragging) {
                mScrollX += distanceX;
                mScrollY += distanceY;

                mScrollX = Math.max(0, Math.min(mScrollX, mWidth - mVisibleArea.width()));
                mScrollY = Math.max(mMinScrollY, Math.min(mScrollY, mMaxScrollY));
                scroll();
                if(mScrollYListener != null){
                    mScrollYListener.isScrolling(true);
                    mScrollYListener.onScrollY((int)distanceY);
                }
                return true;
            } else if (isDragging) {
                if (isDraggingFromLeft) {
                    if (x < mSelectedEditBoard.getLeft()) {
                        mSelectedEditBoard.setStart(mSelectedEditBoard.getStart() - 0.25f);
                    } else if (x > mSelectedEditBoard.getLeft() + MEDIUM_PADDING * mScaleX) {
                        mSelectedEditBoard.setStart(mSelectedEditBoard.getStart() + 0.25f);
                    }
                } else {
                    if (x > mSelectedEditBoard.getRight()) {
                        mSelectedEditBoard.setEnd(mSelectedEditBoard.getEnd() + 0.25f);
                    } else if (x < mSelectedEditBoard.getRight() - MEDIUM_PADDING * mScaleX) {
                        mSelectedEditBoard.setEnd(mSelectedEditBoard.getEnd() - 0.25f);
                    }
                }
                return true;
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if(e.getY() > mHeight)
                return false;
            if (isExpanded) {
                for(int i = 0; i < mEditBoards.get(mSelectedRow).size();i++){
                    CustomEditBoard customEditBoard = mEditBoards.get(mSelectedRow).get(i);
                    if(x > customEditBoard.getLeft() && x < customEditBoard.getRight()){
                        customEditBoard.onSingleTapUp(x - customEditBoard.getLeft(),y - customEditBoard.getTop());
                        break;
                    }
                }
            } else {
                if (mSelectedEditBoard != null) {
                    if (x > mSelectedEditBoard.getLeft() && x < mSelectedEditBoard.getRight() && row == mSelectedEditBoard.getTop() / (BIG_PADDING + SMALL_PADDING)) {
                        if (mPopupWindow != null) {
                            mPopupWindow.showAsDropDown(mSelectedEditBoard, x - mSelectedEditBoard.getLeft() - (int) (120 * mDensity), -(int) (mSelectedEditBoard.getHeight() + 20 * mDensity));
                        }
                        return true;
                    }
                    mSelectedEditBoard.setSelected(false);
                    mSelectedEditBoard = null;
                }
                for (int i = 0; i < mEditBoards.get(row).size(); i++) {
                    CustomEditBoard customEditBoard = mEditBoards.get(row).get(i);
                    if (x > customEditBoard.getLeft() && x < customEditBoard.getRight()) {
                        mSelectedEditBoard = customEditBoard;
                        mSelectedEditBoard.setSelected(true);
                        mSelectedRow = row;
                        return true;
                    }
                }
            }
            return super.onSingleTapUp(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            isScaling = true;
            isScaleHor = detector.getCurrentSpanX() > detector.getCurrentSpanY();

            if (isScaleHor) {
                mStartScrollX = mPureScrollX;
                mFocusX = detector.getFocusX() / mScaleX;
                mStartScaleX = mScaleX;
            } else {
                mStartScrollY = (mScrollY - mMinScrollY) / mScaleY;
                mFocusY = detector.getFocusY() / mScaleY;
                mStartScaleY = mScaleY;
            }

            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (isScaleHor) {
                mScaleX *= detector.getScaleFactor();

                mScaleX = Math.max(mMinScaleX, Math.min(mMaxScaleY, mScaleX));
                mScrollX = (int) (mStartScrollX * mScaleX + mFocusX * (mScaleX - mStartScaleX));


                mScrollX = Math.max(0, Math.min(mScrollX, mWidth - mVisibleArea.width()));

                setScaleX(mScaleX);
                scroll();

            } else if (isExpanded) {
                mScaleY *= detector.getScaleFactor();

                mScaleY = Math.max(mMinScaleY, Math.min(mMaxScaleY, mScaleY));
                mScrollY = (int) (mStartScrollY * mScaleY + mMinScrollY + mFocusY * (mScaleY - mStartScaleY));

                setScaleY(mScaleY);
                mScrollY = Math.max(mMinScrollY, Math.min(mScrollY, mMaxScrollY));
                scroll();

            }
            if (mScrollAndScaleListener != null) {
                mScrollAndScaleListener.scaleChanged(mScaleX, mScaleY);
            }

            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            isScaling = false;
        }
    }

    private class Helper {
        float offsetY;
        boolean isLine;

        public Helper(float offsetY, boolean isLine) {
            this.offsetY = offsetY;
            this.isLine = isLine;
        }
    }

    public interface ScrollAndScaleListener {
        void scrolled(int x, int y);

        void scaleChanged(float scaleX, float scaleY);
    }

    public interface ScrollY{
        void onScrollY(int y);
        void isScrolling(boolean isScrolling);
    }
}
