package com.streetband.threads;

import android.app.Activity;
import android.content.Context;
import android.os.HandlerThread;

import com.streetband.managers.ChineseDrumsLoader;
import com.streetband.managers.InstrumentManager;
import com.streetband.models.ChineseDrumsKit;
import com.streetband.models.GrandPiano;
import com.streetband.models.Instrument;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager extends Thread{
    private Context mContext;

    private ArrayList<Player> mPlayers = new ArrayList<>();

    public PlayerManager(Context context){
        mContext = context;
    }

    @Override
    public void run() {
        onPrepare();
    }

    public void onPrepare(){
        List<Instrument> instruments = InstrumentManager.getInstance().getInstrumentsList();
        for (Instrument instrument : instruments){
            Player player = null;
            if(instrument instanceof ChineseDrumsKit){
                player = new PlayerWav(mContext,instrument.getTracks().get(0).getNotesMap(), ChineseDrumsLoader.FOLDER);
            }else if(instrument instanceof GrandPiano){
                player = new PlayerMidi();
            }
            mPlayers.add(player);
        }
        for(HandlerThread handlerThread : mPlayers){
            handlerThread.start();
            handlerThread.getLooper();
        }
        onPlay();
    }

    public void onPlay(){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(Player player : mPlayers){
            player.play();
        }
    }
}
