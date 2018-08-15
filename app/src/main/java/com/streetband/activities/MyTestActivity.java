package com.streetband.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.streetband.R;
import com.streetband.customViews.CustomEditBoard;
import com.streetband.customViews.CustomMainBoard;
import com.streetband.customViews.CustomSeekBar;

public class MyTestActivity extends AppCompatActivity {
    private CustomMainBoard mCustomMainBoard;
    private CustomSeekBar mCustomSeekBar;


    private PopupWindow mPopupWindow;
    private ImageView mSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mCustomMainBoard = findViewById(R.id.custom_main_board);
        mCustomSeekBar = findViewById(R.id.custom_seek_bar);
        mCustomSeekBar.synchronizeWithMainBoard(mCustomMainBoard);

        mCustomMainBoard.addRow();
        mCustomMainBoard.addChild(new CustomEditBoard(MyTestActivity.this),0);
        CustomEditBoard customEditBoard=new CustomEditBoard(MyTestActivity.this);
        customEditBoard.setStart(6.0f);
        customEditBoard.setLength(2.0f);
        mCustomMainBoard.addChild(customEditBoard,0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                mCustomMainBoard.addRow();
//                mCustomMainBoard.addRow();
//                mCustomMainBoard.addChild(new CustomEditBoard(MyTestActivity.this),1);
//                CustomEditBoard customEditBoard = new CustomEditBoard(MyTestActivity.this);
//                customEditBoard.setStart(4.25f);
//                customEditBoard.setLength(2);
//                mCustomMainBoard.addChild(customEditBoard,1);
//                mCustomMainBoard.addChild(new CustomEditBoard(MyTestActivity.this),2);
//                mCustomMainBoard.addRow();
//                customEditBoard = new CustomEditBoard(MyTestActivity.this);
////                customEditBoard.setStart(1.0f);
//                customEditBoard.setOctaveSum(5);
//                mCustomMainBoard.addChild(customEditBoard,3);
            }
        },1000);

        mPopupWindow = popupWindow();
        mCustomMainBoard.addPopupWindow(mPopupWindow);
        mSettings = findViewById(R.id.settings);
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomMainBoard.closeRow();
            }
        });
    }


    private PopupWindow popupWindow(){
        final PopupWindow popupWindow = new PopupWindow(this); // inflet your layout or diynamic add view
        View view;
        LayoutInflater inflater = LayoutInflater.from(MyTestActivity.this);
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

    private class ClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
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
