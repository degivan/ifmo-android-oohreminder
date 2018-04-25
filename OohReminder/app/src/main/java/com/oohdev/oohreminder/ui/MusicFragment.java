package com.oohdev.oohreminder.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oohdev.oohreminder.R;
import com.oohdev.oohreminder.core.api.music.MusicSearchProvider;
import com.oohdev.oohreminder.core.api.SearchProvider;
import com.oohdev.oohreminder.ui.search.SearchActivity;

public class MusicFragment extends ContentFragment {
    public static MusicFragment newInstance() {
        Bundle args = new Bundle();
        MusicFragment fragment = new MusicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music, container, false);
    }

    @Override
    public void addElement() {
        Intent intent = new Intent(getContext(), SearchActivity.class);
        startActivityForResult(intent, 0);
    }

    @NonNull
    @Override
    SearchProvider getSearchProvider() {
        return new MusicSearchProvider();
    }
}
