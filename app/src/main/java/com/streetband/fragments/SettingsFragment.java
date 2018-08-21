package com.streetband.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.streetband.R;
import com.streetband.customViews.CustomNumberController;

public class SettingsFragment extends Fragment{
    public static final int MAX_TACT = 120;
    public static final int MIN_TACT = 60;

    public static final int MAX_SONG_LENGTH = 30;
    public static final int MIN_SONG_LENGTH = 1;

    //view
    private CustomNumberController mTactCountController;
    private CustomNumberController mLengthCountController;


    //dynamic params
    private int mSongTact = MIN_TACT;
    private int mSongLength = MIN_SONG_LENGTH;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_settings,container,false);

        //binding
        mTactCountController = root.findViewById(R.id.settings_song_tact);
        mLengthCountController = root.findViewById(R.id.settings_song_length);

//        mTactCountController

        //song tact settings
        mTactCountController.addNumberChangedListener(new CustomNumberController.NumberChanged() {
            @Override
            public void onNumberChanged(int number) {
                //TODO
            }

            @Override
            public void onUpLimit() {
                Snackbar.make(root,getString(R.string.warring_tact,MIN_TACT,MAX_TACT),Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onDownLimit() {
                Snackbar.make(root,getString(R.string.warring_tact,MIN_TACT,MAX_TACT),Snackbar.LENGTH_SHORT).show();
            }
        });

        //song length settings
        mLengthCountController.addNumberChangedListener(new CustomNumberController.NumberChanged() {
            @Override
            public void onNumberChanged(int number) {
                //TODO
            }

            @Override
            public void onUpLimit() {
                Snackbar.make(root,getString(R.string.warring_length,MIN_SONG_LENGTH,MAX_SONG_LENGTH),Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onDownLimit() {
                Snackbar.make(root,getString(R.string.warring_length,MIN_SONG_LENGTH,MAX_SONG_LENGTH),Snackbar.LENGTH_SHORT).show();
            }
        });

        return root;
    }
}
