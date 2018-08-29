package com.streetband.fragments;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.streetband.R;
import com.streetband.utils.BounceInterpolator;

import java.io.IOException;

public class ChineseDrumsKitFragment extends Fragment {
    public static final String FOLDER = "chineseDrumsKit";
    public static final String BIG_CLAP = "Big Clap.wav";
    public static final String BIG_CLAP_2 = "Big_Clap_2.wav";
    public static final String Clap_1 = "Clap_1.wav";
    public static final String Clap_1_mask = "CLap_1_mask.wav";
    public static final String Clap_2 = "Clap_2.wav";
    public static final String Clap_2_mask = "Clap_2_mask.wav";
    public static final String BIG_BOOM = "Big round boom center.wav";
    public static final String BOOM_5 = "Boom_5.wav";
    public static final String BOOM_4 = "Boom_4.wav";
    public static final String BOOM_3 = "Boom_3.wav";
    public static final String BOOM_2 = "Boom_2.wav";
    public static final String BOOM_1 = "Boom_1.wav";
    public static final String Tak_1 = "Tak_1.wav";
    public static final String Tak_2 = "Tak_2.wav";
    public static final String Tak_3 = "Tak_3.wav";
    public static final String Tak_4 = "Tak_4.wav";
    public static final String Tak_5 = "Tak_5.wav";


    //final params


    private SoundPool mSoundPool;
    private AssetManager mAssetManager;


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
    private int mBigClapId;
    private int mBigClap2Id;
    private int mClap1Id;
    private int mClap1MaskId;
    private int mClap2Id;
    private int mClap2MaskId;
    private int mBigBoomId;
    private int mBoom5Id;
    private int mBoom4Id;
    private int mBoom3Id;
    private int mBoom2Id;
    private int mBoom1Id;
    private int mTak1Id;
    private int mTak2Id;
    private int mTak3Id;
    private int mTak4Id;
    private int mTak5Id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSoundPool = new SoundPool.Builder().setMaxStreams(20).setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()).build();
        mAssetManager = getContext().getAssets();

        try {

            AssetFileDescriptor assetFileDescriptor = mAssetManager.openFd(FOLDER + "/" +BIG_CLAP);
            mBigClapId = mSoundPool.load(assetFileDescriptor,1);

            assetFileDescriptor = mAssetManager.openFd(FOLDER + "/" +BIG_CLAP_2);
            mBigClap2Id = mSoundPool.load(assetFileDescriptor,1);

            assetFileDescriptor = mAssetManager.openFd(FOLDER + "/" +Clap_1);
            mClap1Id = mSoundPool.load(assetFileDescriptor,1);

            assetFileDescriptor = mAssetManager.openFd(FOLDER + "/" +Clap_1_mask);
            mClap1MaskId = mSoundPool.load(assetFileDescriptor,1);

            assetFileDescriptor = mAssetManager.openFd(FOLDER + "/" +Clap_2);
            mClap2Id = mSoundPool.load(assetFileDescriptor,1);

            assetFileDescriptor = mAssetManager.openFd(FOLDER + "/" +Clap_2_mask);
            mClap2MaskId = mSoundPool.load(assetFileDescriptor,1);

            assetFileDescriptor = mAssetManager.openFd(FOLDER + "/" +BIG_BOOM);
            mBigBoomId = mSoundPool.load(assetFileDescriptor,1);

            assetFileDescriptor = mAssetManager.openFd(FOLDER + "/" + BOOM_5);
            mBoom5Id = mSoundPool.load(assetFileDescriptor,1);

            assetFileDescriptor = mAssetManager.openFd(FOLDER + "/" + BOOM_4);
            mBoom4Id = mSoundPool.load(assetFileDescriptor,1);

            assetFileDescriptor = mAssetManager.openFd(FOLDER + "/" + BOOM_3);
            mBoom3Id = mSoundPool.load(assetFileDescriptor,1);

            assetFileDescriptor = mAssetManager.openFd(FOLDER + "/" + BOOM_2);
            mBoom2Id = mSoundPool.load(assetFileDescriptor,1);

            assetFileDescriptor = mAssetManager.openFd(FOLDER + "/" + BOOM_1);
            mBoom1Id = mSoundPool.load(assetFileDescriptor,1);

            assetFileDescriptor = mAssetManager.openFd(FOLDER + "/" + Tak_1);
            mTak1Id = mSoundPool.load(assetFileDescriptor,1);

            assetFileDescriptor = mAssetManager.openFd(FOLDER + "/" + Tak_2);
            mTak2Id = mSoundPool.load(assetFileDescriptor,1);

            assetFileDescriptor = mAssetManager.openFd(FOLDER + "/" + Tak_3);
            mTak3Id = mSoundPool.load(assetFileDescriptor,1);

            assetFileDescriptor = mAssetManager.openFd(FOLDER + "/" + Tak_4);
            mTak4Id = mSoundPool.load(assetFileDescriptor,1);

            assetFileDescriptor = mAssetManager.openFd(FOLDER + "/" + Tak_5);
            mTak5Id = mSoundPool.load(assetFileDescriptor,1);

        } catch (IOException e) {
            e.printStackTrace();
        }

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
                    mSoundPool.play(mBigClapId, 1.0f, 1.0f, 1, 0, 1.0f);
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
                    mSoundPool.play(mBigClap2Id, 1.0f, 1.0f, 1, 0, 1.0f);
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
                    mSoundPool.play(mClap1Id, 1.0f, 1.0f, 1, 0, 1.0f);
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
                    mSoundPool.play(mClap1MaskId, 1.0f, 1.0f, 1, 0, 1.0f);
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
                    mSoundPool.play(mClap2Id, 1.0f, 1.0f, 1, 0, 1.0f);
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
                    mSoundPool.play(mClap2MaskId, 1.0f, 1.0f, 1, 0, 1.0f);
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
                    mSoundPool.play(mBigBoomId, 1.0f, 1.0f, 1, 0, 1.0f);
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
                    mSoundPool.play(mBoom5Id,1.0f,1.0f,1,0,1.0f);
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
                    mSoundPool.play(mBoom4Id,1.0f,1.0f,1,0,1.0f);
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
                    mSoundPool.play(mBoom3Id,1.0f,1.0f,1,0,1.0f);
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
                    mSoundPool.play(mBoom2Id,1.0f,1.0f,1,0,1.0f);
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
                    mSoundPool.play(mBoom1Id,1.0f,1.0f,1,0,1.0f);
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
                    mSoundPool.play(mTak1Id,1.0f,1.0f,1,0,1.0f);
                    return true;
                }
                return false;
            }
        });
        mTak2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    mSoundPool.play(mTak2Id,1.0f,1.0f,1,0,1.0f);
                    return true;
                }
                return false;
            }
        });
        mTak3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    mSoundPool.play(mTak3Id,1.0f,1.0f,1,0,1.0f);
                    return true;
                }
                return false;
            }
        });
        mTak4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    mSoundPool.play(mTak4Id,1.0f,1.0f,1,0,1.0f);
                    return true;
                }
                return false;
            }
        });
        mTak5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    mSoundPool.play(mTak5Id,1.0f,1.0f,1,0,1.0f);
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
}
