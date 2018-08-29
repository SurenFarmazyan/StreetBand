package com.streetband.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.streetband.R;
import com.streetband.managers.InstrumentManager;
import com.streetband.models.Instrument;
import com.streetband.utils.Density;

import java.util.List;

public class InstrumentsActivity extends AppCompatActivity {
    private ViewPager mViewPager;

    private InstrumentManager mInstrumentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruments);
        mViewPager = findViewById(R.id.instruments_pager);
        mViewPager.setPageMargin(Math.round(25* Density.getDensity(this)));

        mInstrumentManager = InstrumentManager.getInstance();
        mViewPager.setAdapter(new Adapter(mInstrumentManager.getAllInstrumentsList(this),this));
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //INNER CLASSES
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    private class Adapter extends PagerAdapter{
        private List<Instrument> mInstruments;

        private LayoutInflater mInflater;

        public Adapter(List<Instrument> instruments, Context context) {
            mInstruments = instruments;
            mInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View root = mInflater.inflate(R.layout.item_instrument,container,false);
            ImageView imageView = root.findViewById(R.id.item_instrument_image);
            TextView textView = root.findViewById(R.id.item_instrument_name);

            final Instrument instrument = mInstruments.get(position);
            imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),instrument.getImageId()));
            textView.setText(instrument.getInstrumentName());
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mInstrumentManager.addInstrument(instrument.getName(),getApplicationContext());
                    Intent intent = new Intent(InstrumentsActivity.this,GeneralActivity.class);
                    startActivity(intent);
                }
            });
            container.addView(root);
            return root;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View)(object));
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return mInstruments.size();
        }


    }
}
