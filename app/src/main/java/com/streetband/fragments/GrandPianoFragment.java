package com.streetband.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.streetband.R;
import com.streetband.customViews.CustomPiano;

import org.billthefarmer.mididriver.MidiDriver;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class GrandPianoFragment extends Fragment {
    public static final String TAG = "GrandPianoFragment";
    private CustomPiano mCustomPiano;


    private BackgroundHandler mBackgroundHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mBackgroundHandler = new BackgroundHandler("background handler");
        mBackgroundHandler.start();
        mBackgroundHandler.getLooper();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_grand_piano,container,false);
        mCustomPiano = v.findViewById(R.id.piano_grand_piano);
        mCustomPiano.addNoteListener(new CustomPiano.NoteListener() {
            @Override
            public void notePressed(float note) {
                mBackgroundHandler.putMessage(BackgroundHandler.WHAT_PLAY,note);
            }

            @Override
            public void noteReleased(float note) {
                mBackgroundHandler.putMessage(BackgroundHandler.WHAT_STOP,note);
            }

            @Override
            public void noteChanged(float oldNote, float newNote) {
                mBackgroundHandler.putMessage(BackgroundHandler.WHAT_PLAY,newNote);
                mBackgroundHandler.putMessage(BackgroundHandler.WHAT_STOP,oldNote);
            }
        });

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBackgroundHandler.quit();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //INNER CLASSES
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private class BackgroundHandler extends HandlerThread{
        public static final int WHAT_PLAY = 1;
        public static final int WHAT_STOP = 2;

        private static final int VELOCITY = 120;//

        private MidiDriver mMidiDriver;

        private Handler mRequestHandler;

        private byte[] event;
        private boolean isReady;

        public BackgroundHandler(String name) {
            super(name);
            mMidiDriver = new MidiDriver();
        }

        @SuppressLint("HandlerLeak")
        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            mMidiDriver.start();
            mRequestHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what){
                        case WHAT_PLAY:
                            playNote((float)msg.obj);
                            break;
                        case WHAT_STOP:
                            stopNote((float)msg.obj);
                            break;
                    }
                }
            };
        }

        private void playNote(float note) {
            int octave = (int)note/7;
            float inOctave = note - octave*7;
            inOctave = inOctave*2;
            if(inOctave > 4){
                inOctave -= 1;
            }
            // Construct a note ON message for the middle C at maximum velocity on channel 1:
            event = new byte[3];
            event[0] = (byte) (0x90 | 0x00);  // 0x90 = note On, 0x00 = channel 1
            event[1] = (byte) (12 + octave*12 + inOctave);  // 0x3C = middle C
            event[2] = (byte) VELOCITY;  // 0x7F = the maximum velocity (127)

            // Internally this just calls write() and can be considered obsoleted:
            //midiDriver.queueEvent(event);

            // Send the MIDI event to the synthesizer.
            mMidiDriver.write(event);

        }

        private void stopNote(float note) {
            int octave = (int)note/7;
            float inOctave = note - octave*7;
            inOctave = inOctave*2;
            if(inOctave > 4){
                inOctave -= 1;
            }
            // Construct a note OFF message for the middle C at minimum velocity on channel 1:
            event = new byte[3];
            event[0] = (byte) (0x80 | 0x00);  // 0x80 = note Off, 0x00 = channel 1
            event[1] = (byte) (12 + octave*12 + inOctave);  // 0x3C = middle C
            event[2] = (byte) 0x00;  // 0x00 = the minimum velocity (0)

            // Send the MIDI event to the synthesizer.
            mMidiDriver.write(event);

        }


        private void putMessage(int what,float note){
            mRequestHandler.obtainMessage(what,note).sendToTarget();
        }

        @Override
        public boolean quit() {
            mMidiDriver.stop();
            return super.quit();
        }
    }
}
