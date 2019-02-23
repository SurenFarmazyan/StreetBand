package com.streetband.models;

import android.support.annotation.NonNull;

public class Note implements Comparable<Note>{
    private float mStart;
    private float mEnd;
    private int mNote;

    public Note(float start, float end, int note) {
        mStart = start;
        mEnd = end;
        mNote = note;
    }

    public float getStart() {
        return mStart;
    }

    public void setStart(float start) {
        mStart = start;
    }

    public float getEnd() {
        return mEnd;
    }

    public void setEnd(float end) {
        mEnd = end;
    }

    public int getNote() {
        return mNote;
    }

    public void setNote(int note) {
        mNote = note;
    }

    @Override
    public int compareTo(@NonNull Note o) {
        if(mStart - o.getStart() > 0){
            return 1;
        }else if(mStart - o.getStart() < 0){
            return -1;
        }else {
            return 0;
        }
    }
}
