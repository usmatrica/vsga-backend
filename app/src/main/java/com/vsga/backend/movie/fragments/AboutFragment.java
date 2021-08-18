package com.vsga.backend.movie.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.vsga.backend.movie.MainActivity;
import com.vsga.backend.movie.R;

public class AboutFragment extends Fragment {


    public AboutFragment() {
        // Required empty public constructor
    }

    LinearLayout v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = (LinearLayout)inflater.inflate(R.layout.fragment_about, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("About");
        return v;
    }

}
