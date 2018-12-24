/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xcuzme.app.ui;

import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;
import com.xcuzme.app.R;

import java.util.Locale;


public class TutorialActivity extends FragmentActivity
{
    static final int NUM_ITEMS = 7;

    MyAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;
    protected static boolean mIsSpain;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        mIsSpain = Locale.getDefault().getLanguage().equals("es");

        mAdapter = new MyAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
    }

    public static class MyAdapter extends FragmentStatePagerAdapter
    {
        public MyAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            return TutorialFragment.newInstance(position);
        }
    }

    public static class TutorialFragment extends Fragment
    {
        int mNum;

        private static final int PAGES[] = {R.drawable.tut_1, R.drawable.tut_2, R.drawable.tut_3, R.drawable.tut_4,
                R.drawable.tut_5, R.drawable.tut_6, R.drawable.tut_7};
        private static final int PAGES_ES[] = {R.drawable.tut_1_es, R.drawable.tut_2_es, R.drawable.tut_3_es, R.drawable.tut_4_es,
                R.drawable.tut_5_es, R.drawable.tut_6_es, R.drawable.tut_7_es};

        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
         */
        static TutorialFragment newInstance(int num)
        {
            TutorialFragment f = new TutorialFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_pager_item, container, false);
            ImageView iv = (ImageView) v.findViewById(R.id.image_tutorial);
            iv.setImageResource(mIsSpain ? PAGES_ES[mNum] : PAGES[mNum]);
            if (mNum == 0)
            {
                iv.setBackgroundColor(0xFF1976d2);
            }
            else
            {
                iv.setBackgroundColor(0xFFffffff);
            }
            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }
    }
}

