package com.streetband.threads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.SparseArray;

import com.streetband.managers.SettingsManager;
import com.streetband.models.Note;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlayerWav extends Player {
    public static final String NAME = "PlayerWav";
    public static final int WHAT_PLAY = 1;
    public static final int WHAT_STOP = 2;
    public static final int WHAT_RESUME = 3;
    public static final int MAX_STREAMS = 10;

    private String FOLDER;
    private Handler mPlayHandler;
    private SoundPool mSoundPool;
    private Context mContext;
    private SettingsManager mSettingsManager;
    private AssetManager mAssetManager;

    private float mVolume = 1.0f;


    private Map<Integer, Set<Note>> mSetMap;
    private ArrayList<Note> mQueue = new ArrayList<>();
    private int[] mIdes = new int[17];
    private boolean[] mMarkedToUnload = new boolean[17];
    private int mLoadedCount;
    private int mCurrentPositionInQueue;
    private int mCurrentPositionInIdes;
    private float mCurrentPositionInTact;
    private int mTact;

    private long mStopTime;
    private long mStartTime;
    private long mEndTime;
    private boolean isReady;
    private boolean toPlay;

    public PlayerWav(Context context, Map<Integer, Set<Note>> setMap, String folder) {
        super(NAME);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build();
        mSoundPool = new SoundPool.Builder().setMaxStreams(MAX_STREAMS).setAudioAttributes(audioAttributes).build();
        mSettingsManager = SettingsManager.getInstance();
        mAssetManager = context.getAssets();
        mContext = context;
        isReady = false;
        mSetMap = setMap;
        FOLDER = folder;
        mTact = mSettingsManager.getTact();
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mPlayHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WHAT_PLAY:
                        onPlay();
                        break;
                    case WHAT_STOP:
                        onStop();
                        break;
                    case WHAT_RESUME:
                        onResume();
                        break;
                }
            }
        };

        for (Integer row : mSetMap.keySet()) {
            Set<Note> set = mSetMap.get(row);
            mQueue.addAll(set);
        }
        Collections.sort(mQueue);

        loader();
        isReady = true;
    }

    private void onPlay() {
        mStartTime = System.currentTimeMillis();
        mEndTime = (long) (4 * mSettingsManager.getSongLength() * 1000 / ((float) mTact / 60)) + mStartTime;

        while (toPlay) {
//            try {
            mCurrentPositionInTact = (System.currentTimeMillis() - mStartTime) * ((float) mTact / 60_000);
            while (mQueue.get(mCurrentPositionInQueue).getStart() <= mCurrentPositionInTact) {
                mSoundPool.play(mIdes[mQueue.get(mCurrentPositionInQueue).getNote()], mVolume, mVolume, 1, 0, 0);
                mMarkedToUnload[mQueue.get(mCurrentPositionInQueue).getNote()] = true;
                mCurrentPositionInQueue++;
                if (mCurrentPositionInQueue == mQueue.size()) {
                    toPlay = false;
                    break;
                }
            }

            loader();
//                Thread.sleep(20);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

    private void onStop() {
        mStopTime = System.currentTimeMillis();
        toPlay = false;
    }

    private void onResume() {
        mStartTime += System.currentTimeMillis() - mStopTime;
        toPlay = true;
        onPlay();
    }

    private void loader() {
        for (int i = mCurrentPositionInQueue; i < mQueue.size(); i++) {
            Note note = mQueue.get(i);
            if (note.getStart() > mCurrentPositionInTact + 2) {
                break;
            } else if (mLoadedCount >= 10) {
                break;
            }
            if (mIdes[note.getNote()] == 0) {
                mIdes[note.getNote()] = load(note.getNote());
                mLoadedCount++;
            } else if (mMarkedToUnload[note.getNote()]) {
                mMarkedToUnload[note.getNote()] = false;
            }
        }
        for (int i = 0; i < 17; i++) {
            if (mMarkedToUnload[i]) {
                mMarkedToUnload[i] = false;
                mSoundPool.unload(mIdes[i]);
                mLoadedCount--;
                mIdes[i] = 0;
            }
        }
    }

    private int load(int position) {
        try {
            AssetFileDescriptor assetFileDescriptor = mAssetManager.openFd(FOLDER + "/A" + position + ".wav");
            return mSoundPool.load(assetFileDescriptor, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setTact(int tact) {
        mTact = tact;
    }

    public void addMessage(int what) {
        mPlayHandler.obtainMessage(what).sendToTarget();
    }

    @Override
    public void play() {
        toPlay = true;
        addMessage(WHAT_PLAY);
    }
}
