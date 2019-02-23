package com.streetband.fragments;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.streetband.R;
import com.streetband.activities.GeneralActivity;
import com.streetband.managers.ChineseDrumsLoader;
import com.streetband.managers.SettingsManager;
import com.streetband.models.ChineseDrumsKit;
import com.streetband.threads.RecorderMidi;
import com.streetband.threads.RecorderWav;
import com.streetband.utils.BounceInterpolator;

import java.io.IOException;

public class ChineseDrumsKitFragment extends Fragment implements GeneralActivity.RecordListener{
    public static final String KEY_CHINESE_DRUMS = "ChineseDrumsKitFragment";


    public static ChineseDrumsKitFragment newInstance(ChineseDrumsKit chineseDrumsKit) {

        Bundle args = new Bundle();
        args.putSerializable(KEY_CHINESE_DRUMS,chineseDrumsKit);
        ChineseDrumsKitFragment fragment = new ChineseDrumsKitFragment();
        fragment.setArguments(args);
        return fragment;
    }


    //final params
    private RecorderWav mRecorder;
    private SoundPool mSoundPool;


    private Animation mBigBoomAnim;
    private Animation mBigClapAnim;
    private Animation mBoom5Anim;

    private View mBigClap;
    private View mBigClap2;
    private View mClap1;
    private View mClap1Mask;
    private View mClap2;
    private View mClap2Mask;
    private View mBigBoom;
    private View mBoom5;
    private View mBoom4;
    private View mBoom3;
    private View mBoom2;
    private View mBoom1;
    private View mTak1;
    private View mTak2;
    private View mTak3;
    private View mTak4;
    private View mTak5;


    //dynamic params
    private int[] idArray;

    //
    private boolean isRecording;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSoundPool = new SoundPool.Builder().setMaxStreams(20).setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()).build();

        idArray = ChineseDrumsLoader.getIdArray(getContext(), mSoundPool);

        //animations
        mBigBoomAnim = AnimationUtils.loadAnimation(getContext(), R.anim.big_boom_anim);
        BounceInterpolator interpolator = new BounceInterpolator(5, 300);
        mBigBoomAnim.setInterpolator(interpolator);

        mBigClapAnim = AnimationUtils.loadAnimation(getContext(),R.anim.big_clap_anim);
        interpolator = new BounceInterpolator(0.5,300);
        mBigClapAnim.setInterpolator(interpolator);

        mBoom5Anim = AnimationUtils.loadAnimation(getContext(),R.anim.boom_5_anim);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chinese_drums_kit,container,false);


        mBigClap = root.findViewById(R.id.big_clap);
        mBigClap2 = root.findViewById(R.id.big_clap_2);
        mClap1 = root.findViewById(R.id.clap_1);
        mClap1Mask = root.findViewById(R.id.clap_1_mask);
        mClap2 = root.findViewById(R.id.clap_2);
        mClap2Mask = root.findViewById(R.id.clap_2_mask);
        mBigBoom = root.findViewById(R.id.big_boom);
        mBoom5 =   root.findViewById(R.id.boom_5);
        mBoom4 =   root.findViewById(R.id.boom_4);
        mBoom3 =   root.findViewById(R.id.boom_3);
        mBoom2 =   root.findViewById(R.id.boom_2);
        mBoom1 =   root.findViewById(R.id.boom_1);
        mTak1 =    root.findViewById(R.id.tak_1);
        mTak2 =    root.findViewById(R.id.tak_2);
        mTak3 =    root.findViewById(R.id.tak_3);
        mTak4 =    root.findViewById(R.id.tak_4);
        mTak5 =    root.findViewById(R.id.tak_5);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBigClap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(isRecording){
                        mRecorder.addRecordMessage(RecorderWav.WHAT_DOWN,11);
                    }
                    mSoundPool.play(idArray[0], 1.0f, 1.0f, 1, 0, 1.0f);
                    mBigClap.startAnimation(mBigClapAnim);
                    return true;
                }
                return false;
            }
        });

        mBigClap2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(isRecording){
                        mRecorder.addRecordMessage(RecorderWav.WHAT_DOWN,12);
                    }
                    mSoundPool.play(idArray[1], 1.0f, 1.0f, 1, 0, 1.0f);
                    mBigClap2.startAnimation(mBigClapAnim);
                    return true;
                }
                return false;
            }
        });

        mClap1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(isRecording){
                        mRecorder.addRecordMessage(RecorderWav.WHAT_DOWN,13);
                    }
                    mSoundPool.play(idArray[2], 1.0f, 1.0f, 1, 0, 1.0f);
                    mClap1.startAnimation(mBigBoomAnim);
                    return true;
                }
                return false;
            }
        });
        mClap1Mask.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(isRecording){
                        mRecorder.addRecordMessage(RecorderWav.WHAT_DOWN,14);
                    }
                    mSoundPool.play(idArray[3], 1.0f, 1.0f, 1, 0, 1.0f);
                    mClap1.startAnimation(mBoom5Anim);
                    return true;
                }
                return false;
            }
        });

        mClap2.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(isRecording){
                        mRecorder.addRecordMessage(RecorderWav.WHAT_DOWN,15);
                    }
                    mSoundPool.play(idArray[4], 1.0f, 1.0f, 1, 0, 1.0f);
                    mClap2.startAnimation(mBigBoomAnim);
                    return true;
                }
                return false;
            }
        });
        mClap2Mask.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(isRecording){
                        mRecorder.addRecordMessage(RecorderWav.WHAT_DOWN,16);
                    }
                    mSoundPool.play(idArray[5], 1.0f, 1.0f, 1, 0, 1.0f);
                    mClap2.startAnimation(mBoom5Anim);
                    return true;
                }
                return false;
            }
        });

        mBigBoom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(isRecording){
                        mRecorder.addRecordMessage(RecorderWav.WHAT_DOWN,0);
                    }
                    mSoundPool.play(idArray[6], 1.0f, 1.0f, 1, 0, 1.0f);
                    mBigBoom.startAnimation(mBigBoomAnim);
                    return true;
                }
                return false;
            }
        });

        mBoom5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(isRecording){
                        mRecorder.addRecordMessage(RecorderWav.WHAT_DOWN,5);
                    }
                    mSoundPool.play(idArray[7],1.0f,1.0f,1,0,1.0f);
                    mBoom5.startAnimation(mBoom5Anim);
                    return true;
                }
                return false;
            }
        });
        mBoom4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(isRecording){
                        mRecorder.addRecordMessage(RecorderWav.WHAT_DOWN,4);
                    }
                    mSoundPool.play(idArray[8],1.0f,1.0f,1,0,1.0f);
                    mBoom4.startAnimation(mBoom5Anim);
                    return true;
                }
                return false;
            }
        });
        mBoom3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(isRecording){
                        mRecorder.addRecordMessage(RecorderWav.WHAT_DOWN,3);
                    }
                    mSoundPool.play(idArray[9],1.0f,1.0f,1,0,1.0f);
                    mBoom3.startAnimation(mBoom5Anim);
                    return true;
                }
                return false;
            }
        });
        mBoom2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(isRecording){
                        mRecorder.addRecordMessage(RecorderWav.WHAT_DOWN,2);
                    }
                    mSoundPool.play(idArray[10],1.0f,1.0f,1,0,1.0f);
                    mBoom2.startAnimation(mBoom5Anim);
                    return true;
                }
                return false;
            }
        });
        mBoom1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(isRecording){
                        mRecorder.addRecordMessage(RecorderWav.WHAT_DOWN,1);
                    }
                    mSoundPool.play(idArray[11],1.0f,1.0f,1,0,1.0f);
                    mBoom1.startAnimation(mBoom5Anim);
                    return true;
                }
                return false;
            }
        });
        mTak1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(isRecording){
                        mRecorder.addRecordMessage(RecorderWav.WHAT_DOWN,6);
                    }
                    mSoundPool.play(idArray[12],1.0f,1.0f,1,0,1.0f);
                    return true;
                }
                return false;
            }
        });
        mTak2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(isRecording){
                        mRecorder.addRecordMessage(RecorderWav.WHAT_DOWN,7);
                    }
                    mSoundPool.play(idArray[13],1.0f,1.0f,1,0,1.0f);
                    return true;
                }
                return false;
            }
        });
        mTak3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(isRecording){
                        mRecorder.addRecordMessage(RecorderWav.WHAT_DOWN,8);
                    }
                    mSoundPool.play(idArray[14],1.0f,1.0f,1,0,1.0f);
                    return true;
                }
                return false;
            }
        });
        mTak4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(isRecording){
                        mRecorder.addRecordMessage(RecorderWav.WHAT_DOWN,9);
                    }
                    mSoundPool.play(idArray[15],1.0f,1.0f,1,0,1.0f);
                    return true;
                }
                return false;
            }
        });
        mTak5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(isRecording){
                        mRecorder.addRecordMessage(RecorderWav.WHAT_DOWN,10);
                    }
                    mSoundPool.play(idArray[16],1.0f,1.0f,1,0,1.0f);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSoundPool.release();
    }

    @Override
    public void prepareRecording() {
        mRecorder = new RecorderWav(((ChineseDrumsKit)getArguments().getSerializable(KEY_CHINESE_DRUMS)).getTracks().get(0).getNotesMap()
                ,getContext(), SettingsManager.getInstance().getTact());
    }

    @Override
    public void startRecording() {
        mRecorder.setStartTime();
        mRecorder.start();
        mRecorder.getLooper();
        isRecording = true;
    }

    @Override
    public void stopRecording() {
        //TODO
    }

    @Override
    public void finishRecording() {
        isRecording = false;
        mRecorder.quit();
    }
}
