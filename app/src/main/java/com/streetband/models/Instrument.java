package com.streetband.models;

import android.graphics.Bitmap;

import java.util.List;

public interface Instrument {

    void addTrack(Track track);

    List<Track> getTracks();

    int getImageId();

    int getOctaveSum();

    float getStart();

    void setStart(float start);

    void setLength(float length);

    float getLength();

    float getVolume();

    Bitmap getIcon();

    boolean isMuted();

    String getInstrumentName();

    String getName();

    void setVolume(float volume);

    void setMuted(boolean muted);
}
