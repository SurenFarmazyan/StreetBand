package com.streetband.managers;

import java.util.ArrayList;
import java.util.List;

public class SettingsManager {
    private static SettingsManager mInstance;

    public static SettingsManager getInstance() {
        if(mInstance == null){
            mInstance = new SettingsManager();
        }
        return mInstance;
    }

    private SettingsManager(){}
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final int MAX_TACT = 120;
    public static final int MIN_TACT = 60;

    public static final int MAX_SONG_LENGTH = 30;
    public static final int MIN_SONG_LENGTH = 1;

    private List<SettingsManagerListener> mSettingsManagerListeners = new ArrayList<>();

    private int mSongLength;
    private int mTact;

    public int getSongLength() {
        return mSongLength;
    }

    public void setSongLength(int songLength) {
        mSongLength = songLength;

        for(SettingsManagerListener listener : mSettingsManagerListeners){
            listener.songLengthChanged(mSongLength);
        }
    }

    public int getTact() {
        return mTact;
    }

    public void setTact(int tact) {
        mTact = tact;

        for(SettingsManagerListener listener : mSettingsManagerListeners){
            listener.tactChanged(mTact);
        }
    }

    public int getMaxTact() {
        return MAX_TACT;
    }

    public int getMinTact() {
        return MIN_TACT;
    }

    public int getMaxSongLength() {
        return MAX_SONG_LENGTH;
    }

    public int getMinSongLength() {
        return MIN_SONG_LENGTH;
    }

    public void addSettingsManagerListener(SettingsManagerListener settingsManagerListener){
        mSettingsManagerListeners.add(settingsManagerListener);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //INNER CLASSES
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public interface SettingsManagerListener{
        void songLengthChanged(int songLength);
        void tactChanged(int tact);
    }
}
