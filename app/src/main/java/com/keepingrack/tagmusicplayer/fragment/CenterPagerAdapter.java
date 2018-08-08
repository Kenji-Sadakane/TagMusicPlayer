package com.keepingrack.tagmusicplayer.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CenterPagerAdapter extends FragmentPagerAdapter {
    private static final int PAGE_NUM = 2;

    public CenterPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new MusicListPageFragment();
                break;
            case 1:
                fragment = new TagListPageFragment();
                break;
            default:
                fragment = new MusicListPageFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGE_NUM;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String pageTitle = "";
        switch (position) {
            case 0:
                pageTitle = "楽曲リスト";
                break;
            case 1:
                pageTitle = "タグリスト";
                break;
            default:
                pageTitle = "...";
        }
        return pageTitle;
    }
}
