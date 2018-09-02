package com.streetband.managers;

import android.content.Context;

import com.streetband.customViews.CustomChineseDrumsEdge;
import com.streetband.customViews.CustomPianoEdge;
import com.streetband.customViews.Edge;
import com.streetband.models.ChineseDrumsKit;
import com.streetband.models.GrandPiano;
import com.streetband.models.Instrument;
import com.streetband.models.Track;

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
                ChineseDrumsKit chineseDrumsKit = new ChineseDrumsKit(context,true);
                chineseDrumsKit.addTrack(new Track(0,5));
                addInstrument(chineseDrumsKit);
                break;
            case GrandPiano.NAME:
                GrandPiano grandPiano = new GrandPiano(context,true);
                grandPiano.addTrack(new Track(0,5));
                addInstrument(grandPiano);
                break;
        }
    }

    public Edge getInstrumentEdge(Context context,int row){
        Edge edge = null;
        Instrument instrument = mInstruments.get(row);
        if(instrument instanceof GrandPiano){
            edge = new CustomPianoEdge(context);
        }else if(instrument instanceof ChineseDrumsKit){
            edge = new CustomChineseDrumsEdge(context);
        }
        return edge;
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
