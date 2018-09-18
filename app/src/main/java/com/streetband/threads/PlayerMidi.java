package com.streetband.threads;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.streetband.managers.SettingsManager;
import com.streetband.models.Note;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class PlayerMidi extends Player {
    public static final String NAME = "PlayerMidi";
    public static final int WHAT_PLAY = 1;
    public static final int WHAT_STOP = 2;
    public static final int WHAT_RESUME = 3;
    public static final int MAX_STREAMS = 10;

    private Handler mPlayHandler;
    private AssetManager mAssetManager;
    private MidiDriver mMidiDriver;
    private byte[] event;

    //settings
    private static final int VELOCITY = 120;
    private float mVolume = 1.0f;
    private int mTact;
    private int mLength;

    private Map<Integer, Set<Note>> mSetMap;
    private ArrayList<Note> mQueue = new ArrayList<>();
    private int mCurrentStartPositionInQueue;
    private int mCurrentEndPositionInQueue;
    private float mCurrentPositionInTact;
    private float mPaddingInTime;

    private long mStopTime;
    private long mStartTime;
    private long mEndTime;
    private boolean isReady;
    private boolean toPlay;
    private boolean mPlay;


    public PlayerMidi(Context context, Map<Integer, Set<Note>> setMap) {
        super(NAME);
        mSetMap = setMap;
        SettingsManager settingsManager = SettingsManager.getInstance();
        mTact = settingsManager.getTact();
        mLength = settingsManager.getSongLength();

        mAssetManager = context.getAssets();
        mMidiDriver = new MidiDriver();

        isReady = false;
        mPaddingInTime = 4 * 60 / mTact * 1000;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mMidiDriver.start();
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

        isReady = true;
    }

    private void onPlay() {
        mStartTime = System.currentTimeMillis();
        mEndTime = (long) (4 * mLength * 1000 / ((float) mTact / 60)) + mStartTime;

        while (toPlay) {
            try {
                mCurrentPositionInTact = (System.currentTimeMillis() - mStartTime) / mPaddingInTime;
                while (mPlay && mQueue.get(mCurrentStartPositionInQueue).getStart() <= mCurrentPositionInTact) {//start playing notes
                    playNote(mQueue.get(mCurrentStartPositionInQueue).getNote());
                    mCurrentStartPositionInQueue++;
                    if (mCurrentStartPositionInQueue == mQueue.size()) {
                        mPlay = false;
                        break;
                    }
                }

                while (mQueue.get(mCurrentEndPositionInQueue).getEnd() <= mCurrentPositionInTact) {//stop playing note
                    stopNote(mQueue.get(mCurrentEndPositionInQueue).getNote());
                    mCurrentEndPositionInQueue++;
                    if(mCurrentEndPositionInQueue == mQueue.size()){
                        toPlay = false;
                        break;
                    }
                }
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void onStop() {
        //TODO
    }

    private void onResume() {
        //TODO
    }

    private void playNote(int note) {
        // Construct a note ON message for the middle C at maximum velocity on channel 1:
        event = new byte[3];
        event[0] = (byte) (0x90 | 0x00);  // 0x90 = note On, 0x00 = channel 1
        event[1] = (byte) (12 + note);  // 0x3C = middle C
        event[2] = (byte) VELOCITY;  // 0x7F = the maximum velocity (127)

        // Internally this just calls write() and can be considered obsoleted:
        //midiDriver.queueEvent(event);

        // Send the MIDI event to the synthesizer.
        mMidiDriver.write(event);

    }

    private void stopNote(int note) {
        // Construct a note OFF message for the middle C at minimum velocity on channel 1:
        event = new byte[3];
        event[0] = (byte) (0x80 | 0x00);  // 0x80 = note Off, 0x00 = channel 1
        event[1] = (byte) (12 + note);  // 0x3C = middle C
        event[2] = (byte) 0x00;  // 0x00 = the minimum velocity (0)

        // Send the MIDI event to the synthesizer.
        mMidiDriver.write(event);

    }

    @Override
    void play() {
        toPlay = true;
        mPlay = true;
        mPlayHandler.obtainMessage(WHAT_PLAY).sendToTarget();
    }

    @Override
    public boolean quit() {
        mMidiDriver.stop();
        return super.quit();
    }
}
