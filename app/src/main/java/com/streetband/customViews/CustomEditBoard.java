package com.streetband.customViews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.app.NotificationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.streetband.R;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CustomEditBoard extends View {
    public static final String TAG = "CustomEditBoard";

    //final params
    private float mDensity;
    private int BIG_PADDING = 80;
    private int MEDIUM_PADDING = 20;

    //colors
    private int GREEN_TRANSPARENT;
    private int WHITE_TRANSPARENT;

    //tools
    private Paint mBackgroundPaint = new Paint();
    private Paint mCornersPaint = new Paint();
    private Paint mSelectedPaint = new Paint();
    private Paint mStrokePaint = new Paint();
    private Paint mNotePaint = new Paint();
    private ViewTreeObserver mViewTreeObserver;

    //params
    private int mOctaveSum = 1;
    private float mStart = 0;
    private float mEnd = 4;

    //dynamic params
    private float mHeight;
    private float mWidth;
    private Rect mVisibleRect = new Rect();

    private float mScaleX = 1.0f;
    private float mScaleY = 1.0f;

    private boolean isSelected = false;
    private Note mSelectedNote;
    private int mSelectedRow;

    private boolean isDragging;
    private boolean isExpanding;
    private boolean isEnabled;

    private float mLastNoteLength = 0.25f;

    private Map<Integer,Set<Note>> mSetMap = new HashMap<>();
    private float mCoefficient;


    private int x;
    private int y;
    private int row;

    public CustomEditBoard(Context context) {
        this(context,null);
    }

    public CustomEditBoard(Context context, AttributeSet attrs) {
        super(context, attrs);

        if(attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomEditBoard);
            mStart = typedArray.getFloat(R.styleable.CustomEditBoard_start,0);
            mEnd = typedArray.getFloat(R.styleable.CustomEditBoard_song_length,4);
            mOctaveSum = typedArray.getInt(R.styleable.CustomEditBoard_octave_sum,1);
            typedArray.recycle();
        }
        mDensity = context.getResources().getDisplayMetrics().density;



        //colors
        GREEN_TRANSPARENT = Color.parseColor("#9635B24A");
        WHITE_TRANSPARENT = Color.parseColor("#50FFFFFF");

        mBackgroundPaint.setColor(GREEN_TRANSPARENT);
        mCornersPaint.setColor(GREEN_TRANSPARENT);
        mCornersPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mStrokePaint.setColor(Color.BLACK);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        preparePaints();
        mNotePaint.setColor(Color.GREEN);



        BIG_PADDING *= mDensity;
        MEDIUM_PADDING *= mDensity;

        measure();
//        mScaleY = ((float) BIG_PADDING)/mHeight;

        mCoefficient = ((float) (BIG_PADDING - 2 * MEDIUM_PADDING)) / (12 * mOctaveSum);
    }

    private void measure(){
        mWidth = (int)(getSongLength()*BIG_PADDING);
        mHeight = BIG_PADDING;
    }
    private void preparePaints(){
        if(isEnabled){
            mStrokePaint.setStrokeWidth(mDensity);
            mStrokePaint.setStrokeCap(Paint.Cap.BUTT);

            mSelectedPaint.setColor(WHITE_TRANSPARENT);
            mSelectedPaint.setStrokeWidth(mDensity);
            mSelectedPaint.setStyle(Paint.Style.FILL);
        }else {
            mStrokePaint.setStrokeWidth(4*mDensity);
            mStrokePaint.setStrokeCap(Paint.Cap.ROUND);

            mSelectedPaint.setColor(Color.WHITE);
            mSelectedPaint.setStrokeWidth(14*mDensity);
            mSelectedPaint.setStyle(Paint.Style.STROKE);
        }
    }

    public float getSongLength(){
        return mEnd - mStart;
    }

    public int getOctaveSum(){
        return mOctaveSum;
    }
    public float getCoefficient(){
        return mCoefficient;
    }

    public void setOctaveSum(int sum){
        mOctaveSum = sum;
        mCoefficient = ((float) (BIG_PADDING - 2 * MEDIUM_PADDING)) / (12 * mOctaveSum);
    }


    public float getStart(){
        return mStart;
    }
    public float getEnd(){
        return mEnd;
    }

    public void setEnd(float end){
        mEnd = end;
        mWidth = (int)(getSongLength()*BIG_PADDING*mScaleX);
        setRight((int)(mEnd*BIG_PADDING*mScaleX));
//        invalidate();
    }

    public void setStart(float start){
        mStart = start;
        mWidth = (int)(getSongLength()*BIG_PADDING)*mScaleX;
        setLeft((int)(mStart*BIG_PADDING*mScaleX));
//        invalidate();
    }

    public void setLength(float length){
        mEnd = mStart + length;
        mWidth = (int)(getSongLength()*BIG_PADDING)*mScaleX;
    }

    public void setBackgroundColor(int color){
        mBackgroundPaint.setColor(color);
    }

    public void setSelected(boolean isSelected){
        this.isSelected = isSelected;
        invalidate();
    }

    public void setScaleY(float scaleY){
        mScaleY = scaleY;
        mHeight = (BIG_PADDING - 2*MEDIUM_PADDING)*mScaleY + 2*MEDIUM_PADDING;
        setBottom(getTop() + (int)mHeight);
        invalidate();
    }
    public void setScaleX(float scaleX){
        mScaleX = scaleX;
        mWidth = (getSongLength()*BIG_PADDING*mScaleX);
        setLeft((int)(mStart*BIG_PADDING*mScaleX));
        setRight(getLeft() + (int)mWidth);
        invalidate();
    }

    public void onSingleTapUp(float x, float y) {
        if (isEnabled) {
            if (y < MEDIUM_PADDING || y > mHeight - MEDIUM_PADDING) {
                return;
            }
            x/=mScaleX;
            y -=MEDIUM_PADDING;
            y/=mScaleY;
            float start = x/BIG_PADDING;
            start = start - start%(0.25f);
            int row = (int)(y/mCoefficient);
            if (mSetMap.containsKey(row)) {
                Set<Note> set = mSetMap.get(row);
                for(Note note : set){
                    if(note.containsX(x)){
                        mSelectedNote = note;
                        return;
                    }
                }
                mSelectedNote = new Note(mStart + start,mStart + start +mLastNoteLength);
                set.add(mSelectedNote);
            } else {
                mSelectedNote = new Note(mStart + start,mStart + start+mLastNoteLength);
                mSetMap.put(row, new HashSet<Note>());
                mSetMap.get(row).add(mSelectedNote);
            }
            invalidate();
        }
    }

    public void open(float scaleFactorY){
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator backgroundColor = ObjectAnimator.ofInt(this,"backgroundColor",GREEN_TRANSPARENT,WHITE_TRANSPARENT).setDuration(1000);
        backgroundColor.setEvaluator(new ArgbEvaluator());

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this,"scaleY",1.0f,scaleFactorY).setDuration(1000);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this,"scaleX",mScaleX,mScaleX + 2.0f).setDuration(1000);

        animatorSet.play(backgroundColor).with(scaleY).before(scaleX);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isEnabled = true;
                preparePaints();
            }
        });

        animatorSet.start();
    }

    public void close(float scaleFactorY){
        isEnabled = false;
        mSelectedNote = null;
        preparePaints();
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator backgroundColor = ObjectAnimator.ofInt(this,"backgroundColor",WHITE_TRANSPARENT,GREEN_TRANSPARENT).setDuration(1000);
        backgroundColor.setEvaluator(new ArgbEvaluator());

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this,"scaleY",scaleFactorY,1.0f).setDuration(1000);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this,"scaleX",mScaleX,1.0f).setDuration(1000);

        animatorSet.play(scaleX).before(scaleY).before(backgroundColor);

        animatorSet.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int)mWidth,(int)mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.scale(mScaleX,1.0f);

        for(int row : mSetMap.keySet()){
            Set<Note> set = mSetMap.get(row);
//            Log.i(TAG,"row = " + row + " set size " + set.size());
            for( Note note : set){
                canvas.drawRect( (note.start - mStart)*BIG_PADDING,MEDIUM_PADDING + mCoefficient*row*mScaleY,(note.end - mStart)*BIG_PADDING,MEDIUM_PADDING + mCoefficient*(row + 1)*mScaleY,mNotePaint);
                if(isEnabled) {
                    canvas.drawRect((note.start - mStart) * BIG_PADDING, MEDIUM_PADDING + mCoefficient * row * mScaleY, (note.end - mStart) * BIG_PADDING, MEDIUM_PADDING + mCoefficient * (row + 1) * mScaleY, mStrokePaint);
                    if (mSelectedNote == note) {
                        canvas.drawRect((note.start - mStart) * BIG_PADDING, MEDIUM_PADDING + mCoefficient * row * mScaleY, (note.end - mStart) * BIG_PADDING, MEDIUM_PADDING + mCoefficient * (row + 1) * mScaleY, mSelectedPaint);
                    }
                }
            }
        }

        canvas.restore();


        canvas.drawRect(0,MEDIUM_PADDING, mWidth, mHeight - MEDIUM_PADDING,mBackgroundPaint);
        canvas.drawArc(0,0,2*MEDIUM_PADDING,2*MEDIUM_PADDING,-180,90,true,mCornersPaint);
        canvas.drawArc(mWidth - 2*MEDIUM_PADDING,0,mWidth,2*MEDIUM_PADDING,-90,90,true,mCornersPaint);
        canvas.drawArc(0,mHeight-2*MEDIUM_PADDING,2*MEDIUM_PADDING,mHeight,90,90,true,mCornersPaint);
        canvas.drawArc(mWidth - 2*MEDIUM_PADDING,mHeight-2*MEDIUM_PADDING,mWidth,mHeight,0,90,true,mCornersPaint);
        canvas.drawRect(MEDIUM_PADDING,0,mWidth-MEDIUM_PADDING,MEDIUM_PADDING,mCornersPaint);
        canvas.drawRect(MEDIUM_PADDING,mHeight-MEDIUM_PADDING,mWidth-MEDIUM_PADDING,mHeight,mCornersPaint);


        if(isSelected) {
            canvas.save();
            canvas.scale(1.0f, 0.2f);
            canvas.drawRoundRect(7*mDensity, 0, mWidth - 7*mDensity, mHeight * 5, 15 * mDensity, 15 * mDensity/0.2f, mSelectedPaint);
            canvas.restore();
            canvas.drawLine(7*mDensity,12*mDensity,7*mDensity,mHeight-12*mDensity,mStrokePaint);
            canvas.drawLine(mWidth - 7*mDensity,12*mDensity,mWidth - 7*mDensity,mHeight-12*mDensity,mStrokePaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isEnabled){

            x = (int)((event.getX())/mScaleX);
            y = (int)((event.getY() - MEDIUM_PADDING)/mScaleY);
            row = (int)(y/mCoefficient);

            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    if(mSetMap.containsKey(row)){
                      for(Note note : mSetMap.get(row)){
                          if(note.containsX(x)){
                              if(note.draggingX(x)){
                                  isDragging = true;
                                  isExpanding = true;
                                  mSelectedNote = note;
                                  mSelectedRow = row;
                                  invalidate();
                                  return true;
                              }else if(mSelectedNote != null){
                                  isDragging = true;
                                  isExpanding = false;
                                  mSelectedNote = note;
                                  mSelectedRow = row;
                                  invalidate();
                                  return true;
                              }
                              break;
                          }
                      }
                    }
                break;
                case MotionEvent.ACTION_MOVE:
                    if(isDragging){
                        if(isExpanding){
                            if(x > (mSelectedNote.end - mStart)*BIG_PADDING){
                                mSelectedNote.end += 0.25f;
                                invalidate();
                            }else if(x < (mSelectedNote.end - mStart - 0.25f)*BIG_PADDING ){
                                mSelectedNote.end -= 0.25f;
                                if(mSelectedNote.start == mSelectedNote.end){
                                    mSetMap.get(mSelectedRow).remove(mSelectedNote);
                                    mSelectedNote = null;
                                    isDragging = false;
                                }
                                invalidate();
                            }
                        }else {
                            if(row != mSelectedRow){
                                mSetMap.get(mSelectedRow).remove(mSelectedNote);
                                if(mSetMap.containsKey(row)){
                                    mSetMap.get(row).add(mSelectedNote);
                                }else {
                                    Set<Note> set = new HashSet<>();
                                    set.add(mSelectedNote);
                                    mSetMap.put(row, set);
                                }
                                mSelectedRow = row;
                                invalidate();
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (isDragging) {
                        isDragging = false;
                    }
                    break;
            }
        }

        return super.onTouchEvent(event);
    }

    private class Note {
        float start;
        float end;

        public Note(float start, float end) {
            this.start = start;
            this.end = end;
        }
        public boolean containsX(float x){
            return x>(start - mStart)*BIG_PADDING && x<(end - mStart)*BIG_PADDING;
        }
        public boolean draggingX(int x){
            return x > ((end - mStart)*BIG_PADDING - MEDIUM_PADDING/2) && x < ((end - mStart)*BIG_PADDING);
        }
    }
}
