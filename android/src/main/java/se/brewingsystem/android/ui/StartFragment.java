/*
 * 
 */
package se.brewingsystem.android.ui;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.brewingsystem.android.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class StartFragment extends BaseFragment {


    /** The b. */
    private Bitmap b = null;

    /**
     * Instantiates a new start fragment.
     */
    public StartFragment() {
        // Required empty public constructor
    }

    /**
     * New instance.
     *
     * @return the start fragment
     */
    public static StartFragment newInstance() {
        return new StartFragment();
    }

    /**
     * Load bitmap from view.
     *
     * @param v the v
     * @return the bitmap
     */
    private static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(),
                v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getWidth(),
                v.getHeight());
        v.draw(c);
        return b;
    }

    /**
     * On create view.
     *
     * @param inflater the inflater
     * @param container the container
     * @param savedInstanceState the saved instance state
     * @return the view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start, container, false);
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        viewPager.setAdapter(new MyAdapter());
        return rootView;
    }


    /**
     * On pause.
     */
    @Override
    public void onPause() {
        b = loadBitmapFromView(getView());
        super.onPause();
    }

    /**
     * On destroy view.
     */
    @Override
    public void onDestroyView() {
        BitmapDrawable bd = new BitmapDrawable(b);
        View mView = getView();
        if (mView != null) {
            View mainLayout = mView.findViewById(R.id.main_layout);
            if (mainLayout != null) {
                mainLayout.setBackground(bd);
            }
        }
        b = null;
        super.onDestroyView();
    }

    /**
     * The Class MyAdapter.
     */
    public class MyAdapter extends FragmentStatePagerAdapter {
        
        /** The m titles. */
        private final String[] mTitles;
        
        /**
         * Instantiates a new my adapter.
         */
        public MyAdapter() {
            super(getChildFragmentManager());
            Context context = getActivity();
            if (context != null) {
                mTitles = new String[]{context.getString(R.string.recipe_header_text),
                        context.getString(R.string.protocol_header_text)};
            }else{
                mTitles = new String[]{"",""};
            }
        }

        /**
         * Gets the page title.
         *
         * @param position the position
         * @return the page title
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        /**
         * Gets the count.
         *
         * @return the count
         */
        @Override
        public int getCount() {
            return mTitles.length;
        }

        /**
         * Gets the item.
         *
         * @param position the position
         * @return the item
         */
        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return RecipeListFragment.newInstance();
                case 1:
                    return ProtocolListFragment.newInstance(null);
            }
            return RecipeListFragment.newInstance();
        }
    }

}
