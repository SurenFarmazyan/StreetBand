package com.streetband.viewControllers;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.support.constraint.ConstraintLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.streetband.R;
import com.streetband.activities.MyTestActivity_2;
import com.streetband.utils.BounceInterpolator;

import java.io.IOException;

public class ChineseDrumsKit {
    public static final String FOLDER = "chineseDrumsKit";
    public static final String BIG_CLAP = "Big Clap.wav";
    public static final String BIG_BOOM = "Big round boom center.wav";
    public static final String BOOM_5 = "Boom_5.wav";
    public static final String BOOM_4 = "Boom_4.wav";
    public static final String BOOM_3 = "Boom_3.wav";
    public static final String BOOM_2 = "Boom_2.wav";
    public static final String BOOM_1 = "Boom_1.wav";


    //final params
    private ConstraintLayout mConstraintLayout;
    private Context mContext;

    private SoundPool mSoundPool;
    private AssetManager mAssetManager;


    private Animation mBigBoomAnim;
    private Animation mBigClapAnim;
    private Animation mBoom5Anim;

    private View mBigClap;
    private View mBigBoom;
    private View mBoom5;
    private View mBoom4;
    private View mBoom3;
    private View mBoom2;
    private View mBoom1;


    //dynamic params
    private int mBigClapId;
    private int mBigBoomId;
    private int mBoom5Id;
    private int mBoom4Id;
    private int mBoom3Id;
    private int mBoom2Id;
    private int mBoom1Id;



    public ChineseDrumsKit(Context context, ConstraintLayout constraintLayout) {
        mContext = context;
        mConstraintLayout = constraintLayout;
        init();
    }

    private void init(){
        mBigClap = mConstraintLayout.findViewById(R.id.big_clap);
        mBigBoom = mConstraintLayout.findViewById(R.id.big_boom);
        mBoom5 =   mConstraintLayout.findViewById(R.id.boom_5);
        mBoom4 =   mConstraintLayout.findViewById(R.id.boom_4);
        mBoom3 =   mConstraintLayout.findViewById(R.id.boom_3);
        mBoom2 =   mConstraintLayout.findViewById(R.id.boom_2);
        mBoom1 =   mConstraintLayout.findViewById(R.id.boom_1);

        mSoundPool = new SoundPool.Builder().setMaxStreams(10).setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()).build();
        mAssetManager = mContext.getAssets();

        try {

            AssetFileDescriptor assetFileDescriptor = mAssetManager.openFd(FOLDER + "/" +BIG_CLAP);
            mBigClapId = mSoundPool.load(assetFileDescriptor,1);

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

        } catch (IOException e) {
            e.printStackTrace();
        }

        mBigBoomAnim = AnimationUtils.loadAnimation(mContext, R.anim.big_boom_anim);
        BounceInterpolator interpolator = new BounceInterpolator(5, 300);
        mBigBoomAnim.setInterpolator(interpolator);

        mBigClapAnim = AnimationUtils.loadAnimation(mContext,R.anim.big_clap_anim);
        interpolator = new BounceInterpolator(0.5,300);
        mBigClapAnim.setInterpolator(interpolator);

        mBoom5Anim = AnimationUtils.loadAnimation(mContext,R.anim.boom_5_anim);

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
    }
}
