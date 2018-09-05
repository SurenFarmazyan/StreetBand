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
    private static final String NAME = "RecorderMidi";
    public static final int WHAT_DOWN = 1;
    public static final int WHAT_UP = 2;

    private Handler mRecordHandler;
    private int BIG_PADDING = 80;


    private Map<Integer,Set<Note>> mSetMap;
    private Note[] mCurrentNotes = new Note[10];

    private long mStartTime;
    private int mTact;

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
        mRecordHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case WHAT_DOWN:
                        noteDown(msg.arg1,(int)msg.obj);
                        break;
                    case WHAT_UP:
                        noteUp(msg.arg1);
                        break;
                }
            }
        };
    }

    private void noteDown(int id,int note){
        //TODO change time to tact
        mCurrentNotes[id] = new Note((System.currentTimeMillis() - mStartTime),0,note);
    }

    private void noteUp(int id){
        //TODO change time to tact
        Note note = mCurrentNotes[id];
        note.setEnd((System.currentTimeMillis() - mStartTime));
        if(mSetMap.containsKey(note.getNote()/12)){
            mSetMap.get(note.getNote()/12).add(note);
        }else {
            Set<Note> set = new HashSet<>();
            set.add(note);
            mSetMap.put(note.getNote()/12,set);
        }
    }

    public void addRecordMessage(int WHAT,int id,int note){
        mRecordHandler.obtainMessage(WHAT,id, note);
    }

    public void setStartTime(){
        mStartTime = System.currentTimeMillis();
    }
}
