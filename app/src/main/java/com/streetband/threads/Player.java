package com.streetband.threads;

import android.os.HandlerThread;

public abstract class Player extends HandlerThread{
    public Player(String name) {
        super(name);
    }

    abstract void play();

    abstract void stopPlay();

    abstract void resumePlay();

    abstract void pause();
}
