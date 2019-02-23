package com.streetband.threads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import com.streetband.models.Note;
import com.streetband.utils.Density;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RecorderMidi extends HandlerThread {
    public static final String TAG = "RecorderMidi";
    private static final String NAME = "RecorderMidi";
    public static final int WHAT_DOWN = 1;
    public static final int WHAT_UP = 2;

    private Handler mRecordHandler;
    private int BIG_PADDING = 80;


    private Map<Integer,Set<Note>> mSetMap;
    private Note[] mCurrentNotes = new Note[10];

    private long mStartTime;
    private int mTact;
    private float mPaddingInTime;


    public RecorderMidi(Map<Integer,Set<Note>> map, Context context, int tact) {
        super(NAME);
        mSetMap = map;
        BIG_PADDING *= Density.getDensity(context);
        mTact = tact;
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mPaddingInTime = 4*60/mTact*1000;
        mRecordHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case WHAT_DOWN:
                        noteDown(msg.arg1,msg.arg2);
                        break;
                    case WHAT_UP:
                        noteUp(msg.arg1);
                        break;
                }
            }
        };
    }

    private void noteDown(int id,int note){
        float start = (System.currentTimeMillis() - mStartTime)/mPaddingInTime;
        mCurrentNotes[id] = new Note(start,0,note);
    }

    private void noteUp(int id){
        float end = (System.currentTimeMillis() - mStartTime)/mPaddingInTime;
        Note note = mCurrentNotes[id];
        note.setEnd(end);
        if(mSetMap.containsKey(note.getNote())){
            mSetMap.get(note.getNote()).add(note);
        }else {
            Set<Note> set = new HashSet<>();
            set.add(note);
            mSetMap.put(note.getNote(),set);
        }
    }

    public void addRecordMessage(int WHAT,int id,int note){
        mRecordHandler.obtainMessage(WHAT,id, note).sendToTarget();
    }

    public void setStartTime(){
        mStartTime = System.currentTimeMillis();
    }
}
