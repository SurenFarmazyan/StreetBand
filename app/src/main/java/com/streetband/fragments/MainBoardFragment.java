package com.streetband.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.streetband.R;
import com.streetband.activities.GeneralActivity;
import com.streetband.customViews.CustomAddedInstrument;
import com.streetband.customViews.CustomAddedInstrumentsList;
import com.streetband.customViews.CustomChineseDrumsEdge;
import com.streetband.customViews.CustomCursor;
import com.streetband.customViews.CustomEditBoard;
import com.streetband.customViews.CustomMainBoard;
import com.streetband.customViews.CustomNavigationDrawer;
import com.streetband.customViews.CustomSeekBar;
import com.streetband.customViews.Edge;
import com.streetband.managers.InstrumentManager;
import com.streetband.managers.SettingsManager;
import com.streetband.models.Instrument;
import com.streetband.models.Track;

import java.util.List;

import static com.streetband.customViews.CustomAddedInstrumentsList.*;

public class MainBoardFragment extends Fragment {

    //views
    private CustomMainBoard mCustomMainBoard;
    private CustomAddedInstrumentsList mAddedInstrumentsList;
    private CustomNavigationDrawer mCustomNavigationDrawer;


    //imported view
    private CustomSeekBar mCustomSeekBar;
    private CustomCursor mCustomCursor;


    //adapters
    private InstrumentsAdapter mInstrumentsAdapter;
    private MainBoardAdapter mMainBoardAdapter;

    //managers
    private InstrumentManager mInstrumentManager;
    private SettingsManager mSettingsManger;

    //dynamic view
    private PopupWindow mPopupWindow;


    //dynamic params
    private boolean isMainBoardScrolling;
    private boolean isActive;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isActive = true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //managers
        mSettingsManger = SettingsManager.getInstance();
        mSettingsManger.addSettingsManagerListener(new SettingsManager.SettingsManagerListener() {
            @Override
            public void songLengthChanged(int songLength) {
                if (getContext() != null)
                    mCustomMainBoard.setLength(songLength);
            }

            @Override
            public void tactChanged(int tact) {
                //TODO
            }
        });

        mInstrumentManager = InstrumentManager.getInstance();
        mInstrumentManager.addInstrumentManagerListener(new InstrumentManager.InstrumentManagerListener() {
            @Override
            public void instrumentAdded(Instrument instrument, int position) {
                mAddedInstrumentsList.notifyItemAdded();
                mMainBoardAdapter.notifyRowAdded();
            }

            @Override
            public void instrumentRemoved(int position) {
                mAddedInstrumentsList.notifyItemRemoved();
                mMainBoardAdapter.notifyRowRemoved(position);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCustomNavigationDrawer = (CustomNavigationDrawer) inflater.inflate(R.layout.fragment_main_board, container, false);


        //view binding
        mCustomMainBoard = mCustomNavigationDrawer.findViewById(R.id.main_board_custom_main_board);
        mAddedInstrumentsList = mCustomNavigationDrawer.findViewById(R.id.main_bord_instruments_list);

        mInstrumentsAdapter = new InstrumentsAdapter(mInstrumentManager.getInstrumentsList());
        mAddedInstrumentsList.setAdapter(mInstrumentsAdapter);


        mCustomSeekBar = ((GeneralActivity) getActivity()).getSeekBar();
        mCustomCursor = ((GeneralActivity) getActivity()).getCursor();


        mPopupWindow = popupWindow();
        mCustomMainBoard.addPopupWindow(mPopupWindow);
        mCustomMainBoard.setLength(mSettingsManger.getSongLength());
        mMainBoardAdapter = new MainBoardAdapter(mInstrumentManager.getInstrumentsList());
        mCustomMainBoard.setAdapter(mMainBoardAdapter);


        mCustomSeekBar.synchronizeWithMainBoard(mCustomMainBoard);


        return mCustomNavigationDrawer;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //views change
        mAddedInstrumentsList.addScrollListener(new ScrollListener() {
            @Override
            public void newScrollPosition(int scrollY, boolean fromInside) {
                if (fromInside) {
                    mCustomMainBoard.setScrollY(scrollY);
                }
            }
        });

        mCustomMainBoard.addCollapseListener(new CustomMainBoard.CollapseListener() {
            @Override
            public void stateChanged(boolean expanded) {
                if (expanded) {
                    ((GeneralActivity) getActivity()).editBoardOpened();
                }
            }
        });

        mCustomMainBoard.addScrollYListener(new CustomMainBoard.ScrollY() {
            @Override
            public void newPosition(int y, boolean fromInside) {
                if (fromInside) {
                    mAddedInstrumentsList.setScrollY(y);
                }
            }
        });

        mCustomNavigationDrawer.addNavigationListener(new CustomNavigationDrawer.NavigationListener() {
            @Override
            public void navigationPosition(int position, int shadowRadius) {
//                mCustomSeekBar.setLeft(position - shadowRadius);
//                mCustomSeekBar.updateVisibility();
//                mCustomCursor.setLeft(position - shadowRadius);
//                mCustomMainBoard.updateVisibility();
            }
        });
//        mCustomCursor.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if (isActive) {
//                    mCustomCursor.setLeft(mCustomNavigationDrawer.getPosition());
//                    mCustomSeekBar.setLeft(mCustomNavigationDrawer.getPosition());
//                }
//            }
//        });
    }

    @Override
    public void onPause() {
        super.onPause();
        isActive = false;
    }

    private PopupWindow popupWindow() {
        final PopupWindow popupWindow = new PopupWindow(getContext());
        View view;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        view = inflater.inflate(R.layout.popup_window, null);

        ClickListener clickListener = new ClickListener();

        view.findViewById(R.id.popup_menu_edit).setOnClickListener(clickListener);
        view.findViewById(R.id.popup_menu_delete).setOnClickListener(clickListener);

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_rect_background));
        popupWindow.setAnimationStyle(android.R.style.Animation_Translucent);
        return popupWindow;
    }

    public void closeRow() {
        mCustomMainBoard.closeRow();
        CustomAddedInstrumentsList customAddedInstruments = new CustomAddedInstrumentsList(getContext());
        customAddedInstruments.setAdapter(mInstrumentsAdapter);
        mCustomNavigationDrawer.closeAndOpen(customAddedInstruments);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //TODO
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////INNER CLASSES
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private class InstrumentHolder extends CustomAddedInstrumentsList.Holder implements CustomAddedInstrument.AddedInstrumentListener {
        private CustomAddedInstrument mInstrumentView;
        private Instrument mInstrument;

        public InstrumentHolder(View v) {
            super(v);
            mInstrumentView = (CustomAddedInstrument) v;
            mInstrumentView.addAddedInstrumentListener(this);
        }

        public void binding(Instrument instrument) {
            mInstrument = instrument;
            mInstrumentView.setVolume(instrument.getVolume());
            mInstrumentView.setInstrumentIcon(instrument.getIcon());
            mInstrumentView.setInstrumentName(instrument.getInstrumentName());
            mInstrumentView.setMuted(instrument.isMuted());
        }

        @Override
        public void volumeChanged(float volume) {
            mInstrument.setVolume(volume);
        }

        @Override
        public void muteChanged(boolean muted) {
            mInstrument.setMuted(muted);
        }

        @Override
        public void instrumentSelected() {
            ((GeneralActivity) getActivity()).instrumentSelected(mInstrument);
        }
    }


    private class InstrumentsAdapter extends CustomAddedInstrumentsList.Adapter<InstrumentHolder> {
        private List<Instrument> mInstruments;

        public InstrumentsAdapter(List<Instrument> instruments) {
            mInstruments = instruments;
        }

        @Override
        public InstrumentHolder onCreateViewHolder() {
            CustomAddedInstrument customAddedInstrument = new CustomAddedInstrument(getContext());


            return new InstrumentHolder(customAddedInstrument);
        }

        @Override
        public void onBindViewHolder(InstrumentHolder holder, int position) {
            holder.binding(mInstruments.get(position));
        }

        @Override
        public int getItemCount() {
            return mInstruments.size();
        }
    }


    //popup window
    private class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.popup_menu_edit:
                    Edge edge = mInstrumentManager.getInstrumentEdge(getContext(), mCustomMainBoard.getSelectedRow());
                    mCustomMainBoard.openRow(mCustomMainBoard.getSelectedRow());
                    edge.synchronizeWithMainBoard(mCustomMainBoard);
                    edge.setStartScrollY(mCustomMainBoard.getMinScrollY());
                    mCustomNavigationDrawer.closeAndOpen((View)edge);
                    break;

                case R.id.popup_menu_delete:

                    break;
            }
            mPopupWindow.dismiss();
        }
    }



    //custom main board adapter
    private class MainBoardAdapter extends CustomMainBoard.Adapter{
        List<Instrument> mInstruments;

        public MainBoardAdapter(List<Instrument> instruments) {
            mInstruments = instruments;
        }

        @Override
        public void startChanged(int row, int positionInRow,float start) {
            mInstruments.get(row).getTracks().get(positionInRow).setStart(start);
        }

        @Override
        public void endChanged(int row, int positionInRow,float end) {
            mInstruments.get(row).getTracks().get(positionInRow).setEnd(end);
        }

        @Override
        public void bind(int row, int positionInRow, CustomEditBoard customEditBoard) {
            Track track = mInstruments.get(row).getTracks().get(positionInRow);
            customEditBoard.setStart(track.getStart());
            customEditBoard.setEnd(track.getEnd());
            customEditBoard.setOctaveSum(mInstruments.get(row).getOctaveSum());
            customEditBoard.setNotesMap(mInstruments.get(row).getTracks().get(positionInRow).getNotesMap());
        }

        @Override
        public int getChildrenCountInRow(int row) {
            return mInstruments.get(row).getTracks().size();
        }

        @Override
        public int getRowCount() {
            return mInstruments.size();
        }
    }
}
