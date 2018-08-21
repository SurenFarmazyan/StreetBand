package com.streetband.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.streetband.R;
import com.streetband.activities.GeneralActivity;
import com.streetband.customViews.CustomAddedInstrument;
import com.streetband.customViews.CustomAddedInstrumentsList;
import com.streetband.customViews.CustomCursor;
import com.streetband.customViews.CustomEditBoard;
import com.streetband.customViews.CustomMainBoard;
import com.streetband.customViews.CustomNavigationDrawer;
import com.streetband.customViews.CustomSeekBar;

public class MainBoardFragment extends Fragment {

    //views
    private CustomMainBoard mCustomMainBoard;
    private CustomAddedInstrumentsList mAddedInstrumentsList;
    private CustomNavigationDrawer mCustomNavigationDrawer;


    //imported view
    private CustomSeekBar mCustomSeekBar;
    private CustomCursor mCustomCursor;

    //dynamic view
    private PopupWindow mPopupWindow;


    //dynamic params
    private boolean isMainBoardScrolling;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCustomNavigationDrawer = (CustomNavigationDrawer) inflater.inflate(R.layout.fragment_main_board,container,false);


        //view binding
        mCustomMainBoard = mCustomNavigationDrawer.findViewById(R.id.main_board_custom_main_board);
        mAddedInstrumentsList = mCustomNavigationDrawer.findViewById(R.id.main_bord_instruments_list);


        mAddedInstrumentsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAddedInstrumentsList.setAdapter(new InstrumentsAdapter());
        mCustomSeekBar = ((GeneralActivity)getActivity()).getSeekBar();
        mCustomCursor = ((GeneralActivity)getActivity()).getCursor();

        mCustomMainBoard.addRow();
        mCustomMainBoard.addChild(new CustomEditBoard(getContext()), 0);
        CustomEditBoard customEditBoard = new CustomEditBoard(getContext());
        customEditBoard.setStart(6.0f);
        customEditBoard.setLength(2.0f);
        mCustomMainBoard.addChild(customEditBoard, 0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCustomMainBoard.addRow();
                mCustomMainBoard.addRow();
                mCustomMainBoard.addChild(new CustomEditBoard(getContext()), 1);
                CustomEditBoard customEditBoard = new CustomEditBoard(getContext());
                customEditBoard.setStart(4.25f);
                customEditBoard.setLength(2);
                mCustomMainBoard.addChild(customEditBoard, 1);
                mCustomMainBoard.addChild(new CustomEditBoard(getContext()), 2);
                mCustomMainBoard.addRow();
                customEditBoard = new CustomEditBoard(getContext());
                customEditBoard.setStart(1.0f);
                customEditBoard.setOctaveSum(5);
                mCustomMainBoard.addChild(new CustomEditBoard(getContext()), 3);
                mCustomMainBoard.addRow();
                mCustomMainBoard.addChild(new CustomEditBoard(getContext()), 4);
                mCustomMainBoard.addRow();
                mCustomMainBoard.addChild(new CustomEditBoard(getContext()), 5);
                mCustomMainBoard.addRow();
                mCustomMainBoard.addChild(new CustomEditBoard(getContext()), 6);
                mCustomMainBoard.addRow();
                mCustomMainBoard.addChild(new CustomEditBoard(getContext()), 7);
                mCustomMainBoard.addRow();
                mCustomMainBoard.addChild(new CustomEditBoard(getContext()), 8);
                mCustomMainBoard.addRow();
                mCustomMainBoard.addChild(new CustomEditBoard(getContext()), 9);
            }
        }, 1000);

        mPopupWindow = popupWindow();
        mCustomMainBoard.addPopupWindow(mPopupWindow);

        mAddedInstrumentsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(!isMainBoardScrolling)
                    mCustomMainBoard.onScrollY(dy);
            }
        });

        mCustomMainBoard.addScrollYListener(new CustomMainBoard.ScrollY() {
            @Override
            public void onScrollY(int y) {
//                if(y < 0){
//                    y *= 1.05f;
//                }
                mAddedInstrumentsList.scrollBy(0,y);
            }

            @Override
            public void isScrolling(boolean isScrolling) {
                isMainBoardScrolling = isScrolling;
            }
        });

        mCustomNavigationDrawer.addNavigationListener(new CustomNavigationDrawer.NavigationListener() {
            @Override
            public void navigationPosition(int position,int shadowRadius) {
                mCustomSeekBar.setLeft(position - shadowRadius);
                mCustomCursor.setLeft(position - shadowRadius);
            }
        });

        mCustomSeekBar.synchronizeWithMainBoard(mCustomMainBoard);

        return mCustomNavigationDrawer;
    }

    private PopupWindow popupWindow() {
        final PopupWindow popupWindow = new PopupWindow(getContext()); // inflet your layout or diynamic add view
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




    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////INNER CLASSES
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private class InstrumentHolder extends RecyclerView.ViewHolder{

        public InstrumentHolder(View itemView) {
            super(itemView);
        }
    }



    private class InstrumentsAdapter extends RecyclerView.Adapter<InstrumentHolder>{


        @NonNull
        @Override
        public InstrumentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CustomAddedInstrument customAddedInstrument = new CustomAddedInstrument(getContext());
            return new InstrumentHolder(customAddedInstrument);
        }

        @Override
        public void onBindViewHolder(@NonNull InstrumentHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }


    private class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.popup_menu_edit:
                    mCustomMainBoard.openRow(mCustomMainBoard.getSelectedRow());
                    break;

                case R.id.popup_menu_delete:

                    break;
            }
            mPopupWindow.dismiss();
        }
    }
}
