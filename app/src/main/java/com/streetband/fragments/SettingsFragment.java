package com.streetband.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.streetband.R;
import com.streetband.customViews.CustomNumberController;
import com.streetband.managers.SettingsManager;

public class SettingsFragment extends Fragment{


    //view
    private CustomNumberController mTactCountController;
    private CustomNumberController mLengthCountController;

    //managers
    private SettingsManager mSettingsManger;

    //dynamic params
    private int mSongLength;
    private int mTact;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_settings,container,false);

        //binding
        mTactCountController = root.findViewById(R.id.settings_song_tact);
        mLengthCountController = root.findViewById(R.id.settings_song_length);

        //managers
        mSettingsManger = SettingsManager.getInstance();
        mSongLength = mSettingsManger.getSongLength();
        mTact = mSettingsManger.getTact();
        mTactCountController.setCurrentNumber(mTact);
        mLengthCountController.setCurrentNumber(mSongLength);

        //song tact settings
        mTactCountController.addNumberChangedListener(new CustomNumberController.NumberChanged() {
            @Override
            public void onNumberChanged(int number) {
                mTact = number;
            }

            @Override
            public void onUpLimit() {
                Snackbar.make(root,getString(R.string.warring_tact,mSettingsManger.getMinTact(),mSettingsManger.getMaxTact()),Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onDownLimit() {
                Snackbar.make(root,getString(R.string.warring_tact,mSettingsManger.getMinTact(),mSettingsManger.getMaxTact()),Snackbar.LENGTH_SHORT).show();
            }
        });

        //song length settings
        mLengthCountController.addNumberChangedListener(new CustomNumberController.NumberChanged() {
            @Override
            public void onNumberChanged(int number) {
                mSongLength = number;
            }

            @Override
            public void onUpLimit() {
                Snackbar.make(root,getString(R.string.warring_length,mSettingsManger.getMinSongLength(),mSettingsManger.getMaxSongLength()),Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onDownLimit() {
                Snackbar.make(root,getString(R.string.warring_length,mSettingsManger.getMinSongLength(),mSettingsManger.getMaxSongLength()),Snackbar.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    public void publishUpdates(){
        if(mSongLength != mSettingsManger.getSongLength()){
            mSettingsManger.setSongLength(mSongLength);
        }
        if(mTact != mSettingsManger.getTact()){
            mSettingsManger.setTact(mTact);
        }
    }
}
