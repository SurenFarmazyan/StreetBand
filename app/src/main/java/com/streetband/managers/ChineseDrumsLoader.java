package com.streetband.managers;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.SoundPool;

import java.io.IOException;

public class ChineseDrumsLoader {

    public static final String FOLDER = "chineseDrumsKit";
    public static final String BIG_CLAP = "A11.wav";
    public static final String BIG_CLAP_2 = "A12.wav";
    public static final String Clap_1 = "A13.wav";
    public static final String Clap_1_mask = "A14.wav";
    public static final String Clap_2 = "A15.wav";
    public static final String Clap_2_mask = "A16.wav";
    public static final String BIG_BOOM = "A0.wav";
    public static final String BOOM_5 = "A5.wav";
    public static final String BOOM_4 = "A4.wav";
    public static final String BOOM_3 = "A3.wav";
    public static final String BOOM_2 = "A2.wav";
    public static final String BOOM_1 = "A1.wav";
    public static final String Tak_1 = "A6.wav";
    public static final String Tak_2 = "A7.wav";
    public static final String Tak_3 = "A8.wav";
    public static final String Tak_4 = "A9.wav";
    public static final String Tak_5 = "A10.wav";

    public static int[] getIdArray(Context context, SoundPool soundPool) {

        AssetManager assetManager = context.getAssets();

        int[] idArray = new int[17];

        try {

            AssetFileDescriptor assetFileDescriptor = assetManager.openFd(FOLDER + "/" +BIG_CLAP);
            idArray[0] = soundPool.load(assetFileDescriptor,1);//big clad id

            assetFileDescriptor = assetManager.openFd(FOLDER + "/" +BIG_CLAP_2);
            idArray[1] = soundPool.load(assetFileDescriptor,1);//big clap2 id

            assetFileDescriptor = assetManager.openFd(FOLDER + "/" +Clap_1);
            idArray[2] = soundPool.load(assetFileDescriptor,1);//clap1 id

            assetFileDescriptor = assetManager.openFd(FOLDER + "/" +Clap_1_mask);
            idArray[3] = soundPool.load(assetFileDescriptor,1);//clap1 mask id

            assetFileDescriptor = assetManager.openFd(FOLDER + "/" +Clap_2);
            idArray[4] = soundPool.load(assetFileDescriptor,1);//clap2 id

            assetFileDescriptor = assetManager.openFd(FOLDER + "/" +Clap_2_mask);
            idArray[5] = soundPool.load(assetFileDescriptor,1);//clap2 mask id

            assetFileDescriptor = assetManager.openFd(FOLDER + "/" +BIG_BOOM);
            idArray[6] = soundPool.load(assetFileDescriptor,1);//big boom id

            assetFileDescriptor = assetManager.openFd(FOLDER + "/" + BOOM_5);
            idArray[7] = soundPool.load(assetFileDescriptor,1);//boom 5 id

            assetFileDescriptor = assetManager.openFd(FOLDER + "/" + BOOM_4);
            idArray[8] = soundPool.load(assetFileDescriptor,1);//boom 4 id

            assetFileDescriptor = assetManager.openFd(FOLDER + "/" + BOOM_3);
            idArray[9] = soundPool.load(assetFileDescriptor,1);//boom 3 id

            assetFileDescriptor = assetManager.openFd(FOLDER + "/" + BOOM_2);
            idArray[10] = soundPool.load(assetFileDescriptor,1);//boom 2 id

            assetFileDescriptor = assetManager.openFd(FOLDER + "/" + BOOM_1);
            idArray[11] = soundPool.load(assetFileDescriptor,1);//boom 1 id

            assetFileDescriptor = assetManager.openFd(FOLDER + "/" + Tak_1);
            idArray[12] = soundPool.load(assetFileDescriptor,1);//tak 1 id

            assetFileDescriptor = assetManager.openFd(FOLDER + "/" + Tak_2);
            idArray[13] = soundPool.load(assetFileDescriptor,1);//tak 2 id

            assetFileDescriptor = assetManager.openFd(FOLDER + "/" + Tak_3);
            idArray[14] = soundPool.load(assetFileDescriptor,1);//tak 3 id

            assetFileDescriptor = assetManager.openFd(FOLDER + "/" + Tak_4);
            idArray[15] = soundPool.load(assetFileDescriptor,1);//tak 4 id

            assetFileDescriptor = assetManager.openFd(FOLDER + "/" + Tak_5);
            idArray[16] = soundPool.load(assetFileDescriptor,1);//tak 5 id

        } catch (IOException e) {
            e.printStackTrace();
        }

        return idArray;
    }
}
