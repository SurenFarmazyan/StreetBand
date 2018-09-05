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
import android.support.animation.DynamicAnimation;
import android.support.animation.FlingAnimation;
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

    //listeners
    private ScrollAndScaleListener mScrollAndScaleListener;
    private ScrollAndScaleListener mScrollAndScaleListener2;
    private ScrollY mScrollYListener;
    private CollapseListener mCollapseListener;
    private PopupWindow mPopupWindow;

    //tools
    private GestureDetector mGestureDetector;
    private FlingAnimation mFlingY;
    private FlingAnimation mFlingX;
    private ScaleGestureDetector mScaleDetector;
    private ViewTreeObserver mViewTreeObserver;
    private Paint mBoldLinePaint = new Paint();
    private Paint mLinePaint = new Paint();
    private Paint mRectPaint = new Paint();

    //params
    private float mBoardLength;

    //adapter
    private Adapter mAdapter;

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
    private int mSelectedPositionInRow = -1;
    private CustomEditBoard mSelectedEditBoard;


    private List<List<CustomEditBoard>> mEditBoards = new ArrayList<>();

    //for expanded
    private Helper[] mHelpers;
    private float mCoefficient;

    //for collapsed


    int x;
    int y;
    int row;
    boolean hasTouch;
    boolean isYFlinging;
    boolean needToReLayout = true;

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

        //tools
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mGestureDetector = new GestureDetector(context, new GestureListener());
        mFlingX = new FlingAnimation(this, DynamicAnimation.SCROLL_X);
        mFlingY = new FlingAnimation(this, DynamicAnimation.SCROLL_Y);
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
        if(mScrollAndScaleListener2 != null){
            mScrollAndScaleListener2.scrolled(mScrollX,mScrollY);
        }
        if(mScrollYListener != null){
            mScrollYListener.newPosition(mScrollY,hasTouch || isYFlinging);
        }
    }

    public void setAdapter(Adapter adapter){
        mAdapter = adapter;
        mEditBoards.clear();
        for(int i = 0; i < mAdapter.getRowCount();i++){
            mEditBoards.add(new ArrayList<CustomEditBoard>());
            for(int j = 0; j < mAdapter.getChildrenCountInRow(i); j++){
                CustomEditBoard customEditBoard = new CustomEditBoard(getContext());
                customEditBoard.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
                mAdapter.bind(i,j,customEditBoard);
                mEditBoards.get(i).add(customEditBoard);
                addView(customEditBoard);
            }
        }
        measure();
        needToReLayout = true;
        requestLayout();
    }


    public int getMinScrollY(){
        return mMinScrollY;
    }

    public void addScrollAndScaleListener(ScrollAndScaleListener scrollAndScaleListener) {
        mScrollAndScaleListener = scrollAndScaleListener;
    }

    public void addSecondScrollAndScaleListener(ScrollAndScaleListener scrollAndScaleListener){
        mScrollAndScaleListener2 = scrollAndScaleListener;
    }

    public void addCollapseListener(CollapseListener collapseListener){
        mCollapseListener = collapseListener;
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
        measure();

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
        if(mScrollAndScaleListener2 != null){
            mScrollAndScaleListener2.scaleChanged(mScaleX,mScaleY);
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

    public void setLength(float length){
        mBoardLength = length;
        if(mPureScrollX > length*BIG_PADDING){
            setScrollX(0);
        }
        requestLayout();
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
                if(mCollapseListener != null){
                    mCollapseListener.stateChanged(true);
                }
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
                if(mCollapseListener != null){
                    mCollapseListener.stateChanged(false);
                }
            }
        });

        animatorSet.start();
    }

    public void updateVisibility(){
        getGlobalVisibleRect(mVisibleArea);
        mVisibleWidth = (int) (mVisibleArea.width() / mScaleX);
//                mVisibleHeight = (int) (mVisibleArea.height() / mScaleY);
        if(!isExpanded){
            mMaxScrollY = mHeight - mVisibleArea.height();
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        Log.i(TAG,"onMeasure");
        mWidth = (int) (mBoardLength * BIG_PADDING);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
//        Log.i(TAG,"onLayout");
        if(needToReLayout) {
            needToReLayout = false;
            for (int j = 0; j < mEditBoards.size(); j++) {
                for (int k = 0; k < mEditBoards.get(j).size(); k++) {
                    CustomEditBoard v = mEditBoards.get(j).get(k);
                    v.layout((int) (v.getStart() * BIG_PADDING), j * (BIG_PADDING + SMALL_PADDING), (int) (v.getEnd() * BIG_PADDING), j * (BIG_PADDING + SMALL_PADDING) + BIG_PADDING);
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mScaleY > 1.5f) {
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
                hasTouch = true;
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
                hasTouch = false;
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
                return true;
            } else if (isDragging) {
                if (isDraggingFromLeft) {
                    if (x < mSelectedEditBoard.getLeft()) {
                        mSelectedEditBoard.setStart(mSelectedEditBoard.getStart() - 0.25f);
                        mAdapter.startChanged(mSelectedRow, mSelectedPositionInRow,mSelectedEditBoard.getStart());
                    } else if (x > mSelectedEditBoard.getLeft() + MEDIUM_PADDING * mScaleX) {
                        mSelectedEditBoard.setStart(mSelectedEditBoard.getStart() + 0.25f);
                        mAdapter.startChanged(mSelectedRow, mSelectedPositionInRow,mSelectedEditBoard.getStart());
                    }
                } else {
                    if (x > mSelectedEditBoard.getRight()) {
                        mSelectedEditBoard.setEnd(mSelectedEditBoard.getEnd() + 0.25f);
                        mAdapter.endChanged(mSelectedRow, mSelectedPositionInRow,mSelectedEditBoard.getEnd());
                    } else if (x < mSelectedEditBoard.getRight() - MEDIUM_PADDING * mScaleX) {
                        mSelectedEditBoard.setEnd(mSelectedEditBoard.getEnd() - 0.25f);
                        mAdapter.endChanged(mSelectedRow, mSelectedPositionInRow,mSelectedEditBoard.getEnd());
                    }
                }
                return true;
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(!isScaling && !isDragging) {
                if(e1.getX() <= mWidth - mVisibleArea.width()) {
                    mFlingX.setStartVelocity(-velocityX).setMinValue(0).setMaxValue(mWidth - mVisibleArea.width()).start();
                }
                if(e1.getY() <= mMaxScrollY) {
                    isYFlinging = true;
                    mFlingY.setStartVelocity(-velocityY).setMinValue(mMinScrollY).setMaxValue(mMaxScrollY).addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                            if(!canceled){
                                isYFlinging = false;
                            }
                        }
                    }).start();

                }
            }


            return super.onFling(e1, e2, velocityX, velocityY);
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
                        mSelectedPositionInRow = i;
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
            if (mScrollAndScaleListener2 != null) {
                mScrollAndScaleListener2.scaleChanged(mScaleX, mScaleY);
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
        void newPosition(int y,boolean fromInside);
    }

    public interface CollapseListener{
        void stateChanged(boolean expanded);
    }





    public static abstract class Adapter{


        public void notifyRowRemoved(int position){
            //TODO
        }

        public void notifyRowAdded(){
            //TODO
        }

        public void notifyTrackRemoved(){
            //TODO
        }

        public void notifyTrackAdded(){
            //TODO
        }

        public abstract void startChanged(int row, int positionInRow,float start);

        public abstract void endChanged(int row, int positionInRow,float end);

        public abstract void bind(int row, int positionInRow,CustomEditBoard customEditBoard);

        public abstract int getChildrenCountInRow(int row);

        public abstract int getRowCount();
    }
}
