package com.streetband.activities;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.streetband.R;
import com.streetband.customViews.CustomCountdown;
import com.streetband.viewControllers.ChineseDrumsKit;

import java.io.IOException;

public class MyTestActivity_2 extends AppCompatActivity {
    public static final String FOLDER = "metronom";

    private ImageButton mRecordButton;
    private CustomCountdown mCountdown;
    private SoundPool mSoundPool;
    private AssetManager mAssetManager;


    private int mMtronomBigId;
    private int mMtronomId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_2);

        new ChineseDrumsKit(MyTestActivity_2.this,(ConstraintLayout) findViewById(R.id.chinese_drums_kit));

        mCountdown = findViewById(R.id.custom_countdown);
        mRecordButton = findViewById(R.id.record);
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountdown.setVisibility(View.VISIBLE);
                new MyTestActivity_2.AsyncTask().execute();
            }
        });

        mSoundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()).build();

        mAssetManager = getAssets();
        try{
            AssetFileDescriptor assetFileDescriptor = mAssetManager.openFd(FOLDER + "/Metronom_Claves.wav");
            mMtronomBigId = mSoundPool.load(assetFileDescriptor,0);
            assetFileDescriptor = mAssetManager.openFd(FOLDER + "/Metronom_Taktell.wav");
            mMtronomId = mSoundPool.load(assetFileDescriptor,0);
        }catch (IOException e){
            e.getStackTrace();
        }
    }



    private class AsyncTask extends android.os.AsyncTask<Void,Float,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            float i = 1;
            while (i < 5){
                publishProgress(i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i+= 0.25f;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            float i = values[0];
            if(i%1 == 0){
                mCountdown.setSelectedNumber((int)i);
                mSoundPool.play(mMtronomBigId,1.0f,1.0f,0,0,0);
            }else {
                mSoundPool.play(mMtronomId,1.0f,1.0f,0,0,0);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mCountdown.setVisibility(View.GONE);
            mSoundPool.play(mMtronomBigId,1.0f,1.0f,0,0,0);
        }
    }
}
