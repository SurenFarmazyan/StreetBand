package com.streetband.activities;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.streetband.R;
import com.streetband.customViews.CustomCountdown;
import com.streetband.customViews.CustomCursor;
import com.streetband.customViews.CustomSeekBar;
import com.streetband.fragments.MainBoardFragment;
import com.streetband.fragments.SettingsFragment;

import java.io.IOException;

public class GeneralActivity extends AppCompatActivity {
    private static final String METRONOME_FOLDER = "metronome";

    //views
    private ImageButton mStopButton;
    private ImageButton mPlayButton;
    private ImageButton mRecordButton;
    private CheckBox mMetronomeBox;
    private Button mDoneButton;
    private ImageView mSettingsView;

    //custom views
    private CustomCountdown mCountdown;
    private CustomSeekBar mCustomSeekBar;
    private CustomCursor mCustomCursor;

    //tools
    private SoundPool mMetronomeSoundPool;
    private AssetManager mAssetManager;
    private FragmentManager mFragmentManager;


    //


    private int mMetronomeBigId;
    private int mMetronomeId;

    private boolean isInSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        //view binding
        mStopButton = findViewById(R.id.toolbar_stop);
        mPlayButton = findViewById(R.id.toolbar_play);
        mRecordButton = findViewById(R.id.toolbar_record);
        mMetronomeBox = findViewById(R.id.toolbar_metronome);
        mDoneButton = findViewById(R.id.toolbar_done);
        mSettingsView = findViewById(R.id.toolbar_settings);

        mCountdown = findViewById(R.id.main_countdown);
        mCustomSeekBar = findViewById(R.id.toolbar_customSeekBar);
        mCustomCursor = findViewById(R.id.main_cursor);


        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().add(R.id.main_container,new MainBoardFragment()).commit();

        //tools
        mMetronomeSoundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()).build();
        mAssetManager = getAssets();
        try{
            AssetFileDescriptor assetFileDescriptor = mAssetManager.openFd(METRONOME_FOLDER + "/Metronom_Claves.wav");
            mMetronomeBigId = mMetronomeSoundPool.load(assetFileDescriptor,0);
            assetFileDescriptor = mAssetManager.openFd(METRONOME_FOLDER + "/Metronom_Taktell.wav");
            mMetronomeId = mMetronomeSoundPool.load(assetFileDescriptor,0);
        }catch (IOException e){
            e.getStackTrace();
        }

        //View clicks
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountdown.setVisibility(View.VISIBLE);
                mRecordButton.setBackgroundColor(Color.RED);
                mPlayButton.setBackgroundColor(Color.GREEN);
                new AsyncTask().execute();
            }
        });

        mSettingsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentManager.beginTransaction().replace(R.id.main_container,new SettingsFragment()).commit();
                mDoneButton.setVisibility(View.VISIBLE);
                mCustomCursor.setShowLine(false);
                isInSettings = true;
            }
        });
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isInSettings){
                    isInSettings = false;
                    mDoneButton.setVisibility(View.GONE);
                    mCustomCursor.setShowLine(true);
                    mFragmentManager.beginTransaction().replace(R.id.main_container,new MainBoardFragment()).commit();
                }
            }
        });
    }


    public CustomSeekBar getSeekBar(){
        return mCustomSeekBar;
    }

    public CustomCursor getCursor(){
        return mCustomCursor;
    }

    @Override
    public void onBackPressed() {
        //TODO

        super.onBackPressed();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///INNER CLASSES
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

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
                mMetronomeSoundPool.play(mMetronomeBigId,1.0f,1.0f,0,0,0);
            }else {
                mMetronomeSoundPool.play(mMetronomeId,1.0f,1.0f,0,0,0);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mCountdown.setVisibility(View.GONE);
            mMetronomeSoundPool.play(mMetronomeBigId,1.0f,1.0f,0,0,0);
        }
    }
}
