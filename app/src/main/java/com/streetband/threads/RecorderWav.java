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

public class RecorderWav extends HandlerThread{
    public static final String NAME = "RecorderWav";
    public static final int WHAT_DOWN = 1;

    private Handler mRecordHandler;
    private int BIG_PADDING = 80;

    private Map<Integer,Set<Note>> mSetMap;

    private long mStartTime;
    private int mTact;

    public RecorderWav(Map<Integer,Set<Note>> map, Context context, int tact) {
        super(NAME);
        mSetMap = map;
        BIG_PADDING *= Density.getDensity(context);
        mTact = tact;
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mRecordHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case WHAT_DOWN:
                        noteDown((int)msg.obj);
                        break;
                }
            }
        };
    }

    private void noteDown(int note){
        //TODO change time to tact
        Note note1 = new Note(System.currentTimeMillis() - mStartTime,0,note);
        if(mSetMap.containsKey(note)){
            mSetMap.get(note).add(note1);
        }else {
            mSetMap.put(note,new HashSet<Note>());
            mSetMap.get(note).add(note1);
        }
    }


    public void addRecordMessage(int WHAT,int note){
        mRecordHandler.obtainMessage(WHAT, note).sendToTarget();
    }

    public void setStartTime(){
        mStartTime = System.currentTimeMillis();
    }
}
