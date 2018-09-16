package com.streetband.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.streetband.R;
import com.streetband.activities.GeneralActivity;
import com.streetband.customViews.CustomPiano;
import com.streetband.managers.SettingsManager;
import com.streetband.threads.RecorderMidi;
import com.streetband.models.GrandPiano;

import org.billthefarmer.mididriver.MidiDriver;

public class GrandPianoFragment extends Fragment implements GeneralActivity.RecordListener{
    public static final String TAG = "GrandPianoFragment";
    public static final String KEY_GRAND_PIANO = "grandPiano";

    public static GrandPianoFragment newInstance(GrandPiano grandPiano){
        GrandPianoFragment fragment = new GrandPianoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_GRAND_PIANO,grandPiano);
        fragment.setArguments(bundle);
        return fragment;
    }

    private CustomPiano mCustomPiano;

    private BackgroundHandler mBackgroundHandler;

    private GrandPiano mGrandPiano;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGrandPiano = (GrandPiano) getArguments().getSerializable(KEY_GRAND_PIANO);

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
            public void notePressed(int id,float note) {
                mBackgroundHandler.putPlayMessage(BackgroundHandler.WHAT_PLAY,id,note);
            }

            @Override
            public void noteReleased(int id, float note) {
                mBackgroundHandler.putPlayMessage(BackgroundHandler.WHAT_STOP,id,note);
            }

            @Override
            public void noteChanged(int id, float oldNote, float newNote) {
                mBackgroundHandler.putPlayMessage(BackgroundHandler.WHAT_STOP,id,oldNote);
                mBackgroundHandler.putPlayMessage(BackgroundHandler.WHAT_PLAY,id,newNote);
            }
        });

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBackgroundHandler.quit();
    }

    @Override
    public void prepareRecording() {
        mBackgroundHandler.prepareRecording();
    }

    @Override
    public void startRecording() {
        mBackgroundHandler.startRecording();
    }

    @Override
    public void stopRecording() {
        mBackgroundHandler.startRecording();
    }

    @Override
    public void finishRecording() {
        mBackgroundHandler.finishRecording();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //INNER CLASSES
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private class BackgroundHandler extends HandlerThread{
        public static final int WHAT_PLAY = 1;
        public static final int WHAT_STOP = 2;

        private static final int VELOCITY = 120;//

        private MidiDriver mMidiDriver;

        private Handler mPlayHandler;
        private RecorderMidi mRecorder;

        private byte[] event;
        private boolean isReady;
        private boolean isRecording;

        public BackgroundHandler(String name) {
            super(name);
            mMidiDriver = new MidiDriver();
        }

        @SuppressLint("HandlerLeak")
        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            mMidiDriver.start();
            mPlayHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what){
                        case WHAT_PLAY:
                            playNote(msg.arg1,(float)msg.obj);
                            break;
                        case WHAT_STOP:
                            stopNote(msg.arg1,(float)msg.obj);
                            break;
                    }
                }
            };
        }

        private void playNote(int id,float note) {
            int octave = (int)note/7;
            float inOctave = note - octave*7;
            inOctave = inOctave*2;
            if(inOctave > 4){
                inOctave -= 1;
            }
            if(isRecording){
                mRecorder.addRecordMessage(RecorderMidi.WHAT_DOWN,id,(byte)(octave*12 + inOctave));
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

        private void stopNote(int id,float note) {
            int octave = (int)note/7;
            float inOctave = note - octave*7;
            inOctave = inOctave*2;
            if(inOctave > 4){
                inOctave -= 1;
            }
            if(isRecording){
                mRecorder.addRecordMessage(RecorderMidi.WHAT_UP,id,(byte)(octave*12 + inOctave));
            }
            // Construct a note OFF message for the middle C at minimum velocity on channel 1:
            event = new byte[3];
            event[0] = (byte) (0x80 | 0x00);  // 0x80 = note Off, 0x00 = channel 1
            event[1] = (byte) (12 + octave*12 + inOctave);  // 0x3C = middle C
            event[2] = (byte) 0x00;  // 0x00 = the minimum velocity (0)

            // Send the MIDI event to the synthesizer.
            mMidiDriver.write(event);

        }

        private void prepareRecording(){
            mRecorder = new RecorderMidi(mGrandPiano.getTracks().get(0).getNotesMap(),getContext(), SettingsManager.getInstance().getTact());
            mRecorder.start();
            mRecorder.getLooper();
        }

        private void startRecording(){
            mRecorder.setStartTime();
            isRecording = true;
        }

        private void finishRecording(){
            isRecording = false;
            mRecorder.quit();
            mRecorder = null;
        }


        private void putPlayMessage(int what, int id, float note){
            mPlayHandler.obtainMessage(what,id,0,note).sendToTarget();
        }


        @Override
        public boolean quit() {
            mMidiDriver.stop();
            return super.quit();
        }
    }
}
