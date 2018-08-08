package com.keepingrack.tagmusicplayer.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.keepingrack.tagmusicplayer.R;
import com.keepingrack.tagmusicplayer.layout.GrayPanel;
import com.keepingrack.tagmusicplayer.layout.musicField.MusicLinearLayout;
import com.keepingrack.tagmusicplayer.layout.musicField.MusicScrollView;
import com.keepingrack.tagmusicplayer.layout.topField.KeyWordEditText;
import com.keepingrack.tagmusicplayer.layout.topField.MsgView;
import com.keepingrack.tagmusicplayer.layout.topField.RelateTagLayout;
import com.keepingrack.tagmusicplayer.layout.topField.RelateTagLink;
import com.keepingrack.tagmusicplayer.layout.topField.RelateTagScrollView;
import com.keepingrack.tagmusicplayer.layout.topField.SearchButton;
import com.keepingrack.tagmusicplayer.layout.topField.SearchSwitch;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;
import static com.keepingrack.tagmusicplayer.MainActivityLogic.*;

public class MusicListPageFragment extends Fragment {

    public boolean initProcessed = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View musicListPage = inflater.inflate(R.layout.music_list_page, container, false);
        activity.keyWordEditText = (KeyWordEditText) musicListPage.findViewById(R.id.keyWordEditText);
        activity.grayPanel = (GrayPanel) musicListPage.findViewById(R.id.grayPanel);
        activity.msgView = (MsgView) musicListPage.findViewById(R.id.msgView);
        activity.musicLinearLayout = (MusicLinearLayout) musicListPage.findViewById(R.id.linearLayout);
        activity.musicScrollView = (MusicScrollView) musicListPage.findViewById(R.id.scrollView);
        activity.searchSwitch = (SearchSwitch) musicListPage.findViewById(R.id.searchSwitch);
        activity.relateTagLayout = (RelateTagLayout) musicListPage.findViewById(R.id.relateTagLayout);
        activity.relateTagLink = (RelateTagLink) musicListPage.findViewById(R.id.switchRelateTagText);
        activity.relateTagScrollView = (RelateTagScrollView) musicListPage.findViewById(R.id.relateTagScrollView);
        activity.searchButton = (SearchButton) musicListPage.findViewById(R.id.searchButton);
        if (!initProcessed) {
            initProcess();
            initProcessed = true;
        }
        return musicListPage;
    }
}
