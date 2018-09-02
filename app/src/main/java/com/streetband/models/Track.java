package com.streetband.models;

import com.streetband.customViews.CustomEditBoard;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Track {
    private float mStart;
    private float mEnd;

    private Map<Integer,Set<Note>> mSetMap = new HashMap<>();

    public Track(int start, int end) {
        mStart = start;
        mEnd = end;
    }

    public Map<Integer,Set<Note>> getNotesMap(){
        return mSetMap;
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
}
