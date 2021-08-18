package com.vsga.backend.movie.fragments;


import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.vsga.backend.movie.MainActivity;
import com.vsga.backend.movie.R;
import com.vsga.backend.movie.adapters.FavoriteAdapter;
import com.vsga.backend.movie.database.DbOpenHelper;
import com.vsga.backend.movie.network.movie.MovieDetail;

public class FavoriteFragment extends Fragment {
    public FavoriteFragment() {
        // Required empty public constructor
    }

    private final String MOVIE_DATA = "movie_data";

    @BindView(R.id.rv_movies)RecyclerView rvMovies;

    private DbOpenHelper mDbHelper;
    FavoriteAdapter adapter;
    List<MovieDetail> movieList;

    GridLayoutManager gridLayoutManager;

    FrameLayout v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = (FrameLayout) inflater.inflate(R.layout.fragment_favorite, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("My Favorite");

        ButterKnife.bind(this,v);

        mDbHelper = new DbOpenHelper(getActivity().getApplicationContext());

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        } else {
            gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        }



        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        movieList = new ArrayList<>();
        movieList = mDbHelper.getFavorite();
        adapter = new FavoriteAdapter(getActivity(), movieList);
        rvMovies.setAdapter(adapter);
        rvMovies.setLayoutManager(gridLayoutManager);
        adapter.notifyDataSetChanged();
    }
}
