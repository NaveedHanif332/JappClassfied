package com.jamaica.japp.Biding;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jamaica.japp.R;
import com.jamaica.japp.ad_detail.FragmentAdDetail;
import com.jamaica.japp.utills.SettingsMain;

import org.json.JSONException;

/**
 * A simple {@link Fragment} subclass.
 */
public class Biding_ViewPagerFragment extends Fragment {
    TabLayout tabLayout;
    Toolbar toolbar;
    SettingsMain settingsMain;
    static boolean isRtl;

    public Biding_ViewPagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_biding_view_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingsMain = new SettingsMain(getContext());
        tabLayout = view.findViewById(R.id.tab_layout);
        toolbar = getActivity().findViewById(R.id.toolbar);
        isRtl = settingsMain.getRTL();

        try {
            if (isRtl) {
                tabLayout.addTab(tabLayout.newTab().setText(FragmentAdDetail.jsonObjectBidTabs.getString("stats")));
                tabLayout.addTab(tabLayout.newTab().setText(FragmentAdDetail.jsonObjectBidTabs.getString("bid")));
            } else {
                tabLayout.addTab(tabLayout.newTab().setText(FragmentAdDetail.jsonObjectBidTabs.getString("bid")));
                tabLayout.addTab(tabLayout.newTab().setText(FragmentAdDetail.jsonObjectBidTabs.getString("stats")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(settingsMain.getMainColor()));

        final ViewPager viewPager = view.findViewById(R.id.pager);
        final Biding_Pager adapter = new Biding_Pager
                (getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        viewPager.setOffscreenPageLimit(1);
        if (isRtl) {
            tabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            if (FragmentAdDetail.buttonPress.equals("bidButton"))
                viewPager.setCurrentItem(1);
            else
                viewPager.setCurrentItem(0);
        } else {
            if (FragmentAdDetail.buttonPress.equals("bidButton"))
                viewPager.setCurrentItem(0);
            else
                viewPager.setCurrentItem(1);
        }
//        if (settingsMain.getRTL())
//            viewPager.setRotationY(180);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.reMeasureCurrentPage(tab.getPosition());
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

}

class Biding_Pager extends FragmentPagerAdapter {
    private int mNumOfTabs;

    Biding_Pager(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        if (Biding_ViewPagerFragment.isRtl) {
            switch (position) {
                case 0:
                    return new Bid_StatisticsFragment();
                case 1:
                    return new BidFragment();
                default:
                    return null;
            }
        } else {

            switch (position) {
                case 0:
                    return new BidFragment();
                case 1:
                    return new Bid_StatisticsFragment();
                default:
                    return null;
            }
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
