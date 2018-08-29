package com.streetband.managers;

import android.content.Context;

import com.streetband.models.ChineseDrumsKit;
import com.streetband.models.GrandPiano;
import com.streetband.models.Instrument;

import java.util.ArrayList;
import java.util.List;

public class InstrumentManager {
    private static InstrumentManager mInstance;
    public static InstrumentManager getInstance() {
        if(mInstance == null){
            mInstance = new InstrumentManager();
        }
        return mInstance;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    private List<Instrument> mInstruments = new ArrayList<>();
    private InstrumentManagerListener mInstrumentManagerListener;


    private InstrumentManager(){}


    public List<Instrument> getInstrumentsList(){
        return mInstruments;
    }

    public List<Instrument> getAllInstrumentsList(Context context){
        List<Instrument> allInstruments = new ArrayList<>();
        allInstruments.add(new ChineseDrumsKit(context,false));
        allInstruments.add(new GrandPiano(context,false));
        return allInstruments;
    }

    public void addInstrument(Instrument instrument){
        mInstruments.add(instrument);
        if(mInstrumentManagerListener != null){
            mInstrumentManagerListener.instrumentAdded(instrument,mInstruments.size() - 1);
        }
    }

    public void addInstrument(String name,Context context){
        switch (name){
            case ChineseDrumsKit.NAME:
                addInstrument(new ChineseDrumsKit(context,true));
                break;
            case GrandPiano.NAME:
                addInstrument(new GrandPiano(context,true));
                break;
        }
    }

    public void removeInstrument(int position){
        mInstruments.remove(position);
        if(mInstrumentManagerListener != null){
            mInstrumentManagerListener.instrumentRemoved(position);
        }
    }
    public void addInstrumentManagerListener(InstrumentManagerListener instrumentManagerListener){
        mInstrumentManagerListener = instrumentManagerListener;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //INNER CLASSES
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public interface InstrumentManagerListener{
        void instrumentAdded(Instrument instrument,int position);
        void instrumentRemoved(int position);
    }
}
