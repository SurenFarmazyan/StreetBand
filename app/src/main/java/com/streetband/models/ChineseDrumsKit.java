package com.streetband.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.streetband.R;
import com.streetband.utils.BitmapOperations;

import java.util.ArrayList;
import java.util.List;

public class ChineseDrumsKit implements Instrument {
    public static final int ICON_SIZE = 80;
    public static final int OCTAVE_SUM = 2;
    public static final String NAME = "Chinese Drums Kit";
    private String mInstrumentName;
    private Bitmap mIcon;

    private List<Track> mTracks = new ArrayList<>();
    private float mStart = 0;
    private float mLength = 5;
    private float mVolume = 1.0f;
    private boolean isMuted;

    public ChineseDrumsKit(Context context,boolean prepareIcon) {
        mInstrumentName = context.getString(R.string.chinese_drums);
        if(prepareIcon) {
            float density = context.getResources().getDisplayMetrics().density;
            int width = (int) (density * ICON_SIZE);
            int height = (int) (density * ICON_SIZE);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(), R.drawable.chinese_drums_icon, options);

            options.inSampleSize = BitmapOperations.calculateInSampleSize(options, width, height) + 1;
            options.inJustDecodeBounds = false;

            mIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.chinese_drums_icon, options);
        }

    }


    @Override
    public void addTrack(Track track) {
        mTracks.add(track);
    }

    @Override
    public List<Track> getTracks() {
        return mTracks;
    }

    @Override
    public int getImageId() {
        return R.raw.drums;
    }

    @Override
    public int getOctaveSum() {
        return OCTAVE_SUM;
    }

    @Override
    public float getStart() {
        return mStart;
    }

    @Override
    public void setStart(float start) {
        mStart = start;
    }

    @Override
    public void setLength(float length) {
        mLength = length;
    }

    @Override
    public float getLength() {
        return mLength;
    }

    @Override
    public float getVolume() {
        return mVolume;
    }

    @Override
    public Bitmap getIcon() {
        return mIcon;
    }

    @Override
    public boolean isMuted() {
        return isMuted;
    }

    @Override
    public String getInstrumentName() {
        return mInstrumentName;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void setVolume(float volume) {
        mVolume = volume;
    }

    @Override
    public void setMuted(boolean muted) {
        isMuted = muted;
    }
}
