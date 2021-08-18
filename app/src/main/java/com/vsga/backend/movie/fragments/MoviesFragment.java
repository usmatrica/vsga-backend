package com.vsga.backend.movie.fragments;

import android.app.AlertDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcelable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.vsga.backend.movie.BuildConfig;
import com.vsga.backend.movie.MainActivity;
import com.vsga.backend.movie.R;
import com.vsga.backend.movie.adapters.MoviesAdapter;
import com.vsga.backend.movie.network.TMDBApi;
import com.vsga.backend.movie.network.TMDBInterface;
import com.vsga.backend.movie.network.movie.MovieResponse;
import com.vsga.backend.movie.network.movie.Movie;
import com.vsga.backend.movie.utils.Preferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesFragment extends Fragment {
    public MoviesFragment() {
        setHasOptionsMenu(true);
    }
    private final String MOVIE_DATA = "movie_data";
    private final String MOVIE_TYPE = "movie_sort_by";
    private final String NOW_PLAYING = "Now Playing";
    private final String POPULAR = "Popular";
    private final String TOP_RATED = "Top Rated";
    private final String UPCOMING = "Upcoming";

    @BindView(R.id.rv_movies)RecyclerView rvMovies;
    @BindView(R.id.ll_loading)LinearLayout llLoading;

    MoviesAdapter adapter;
    List<Movie> movieList = new ArrayList<>();
    Call<MovieResponse> movieResponseCall;

    GridLayoutManager gridLayoutManager;

    int page = 1;
    FrameLayout v;
    String sortBy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = (FrameLayout) inflater.inflate(R.layout.fragment_movies, container, false);

        ButterKnife.bind(this,v);
        sortBy = getMoviesPreference();

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        } else {
            gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        }
        adapter = new MoviesAdapter(getActivity(), movieList);
        rvMovies.setAdapter(adapter);
        rvMovies.setLayoutManager(gridLayoutManager);
        adapter.notifyDataSetChanged();

        if (savedInstanceState != null) {
            Log.d("TAG","savedinstance != null");
            List<Movie> savedList = savedInstanceState.getParcelableArrayList(MOVIE_DATA);
            adapter.setMovie(savedList);
            adapter.notifyDataSetChanged();
            rvMovies.scrollToPosition(0);
            setAppBarTitle();
            hideLoading();
        } else {
            switch (sortBy) {
                case POPULAR:
                    loadMovies(POPULAR);
                    break;
                case TOP_RATED:
                    loadMovies(TOP_RATED);
                    break;
                case NOW_PLAYING:
                    loadMovies(NOW_PLAYING);
                    break;
                case UPCOMING:
                    loadMovies(UPCOMING);
                    break;
            }
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        List<Movie> movies = adapter.getMovieData();
        outState.putParcelableArrayList(MOVIE_DATA, (ArrayList<? extends Parcelable>) movies);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movies_menu,menu);
//        final MenuItem searchMenuItem = menu.findItem(R.id.action_search);
//        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
//        searchView.setQueryHint("Search movies..");
//
//        searchView.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                menu.findItem(R.id.action_popular).setVisible(false);
//                menu.findItem(R.id.action_top_rated).setVisible(false);
//            }
//        });
//
//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                menu.findItem(R.id.action_popular).setVisible(true);
//                menu.findItem(R.id.action_top_rated).setVisible(true);
//                return false;
//            }
//        });
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(getActivity(),"Search submit..",Toast.LENGTH_SHORT).show();
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_filter:
                AlertDialog.Builder dialogBuilder;
                final AlertDialog alertDialog;
                View dialogview;
                dialogBuilder = new AlertDialog.Builder(getActivity());
                dialogBuilder.setCancelable(true);
                dialogview = getActivity().getLayoutInflater().inflate(R.layout.sort_dialog,null);
                dialogBuilder.setView(dialogview);
                alertDialog = dialogBuilder.create();
                LinearLayout ll_popular = (LinearLayout)dialogview.findViewById(R.id.popular);
                LinearLayout ll_toprated = (LinearLayout)dialogview.findViewById(R.id.toprated);
                LinearLayout ll_nowplay = (LinearLayout)dialogview.findViewById(R.id.nowplay);
                LinearLayout ll_upcoming = (LinearLayout)dialogview.findViewById(R.id.upcoming);
                TextView tv_popular = (TextView)dialogview.findViewById(R.id.tv_popular);
                TextView tv_toprated = (TextView)dialogview.findViewById(R.id.tv_toprated);
                TextView tv_nowplay = (TextView)dialogview.findViewById(R.id.tv_nowplaying);
                TextView tv_upcoming = (TextView)dialogview.findViewById(R.id.tv_upcoming);
                switch (sortBy) {
                    case POPULAR:
                        ll_popular.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        tv_popular.setTextColor(getResources().getColor(R.color.white));
                        break;
                    case TOP_RATED:
                        ll_toprated.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        tv_toprated.setTextColor(getResources().getColor(R.color.white));
                        break;
                    case NOW_PLAYING:
                        ll_nowplay.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        tv_nowplay.setTextColor(getResources().getColor(R.color.white));
                        break;
                    case UPCOMING:
                        ll_upcoming.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        tv_upcoming.setTextColor(getResources().getColor(R.color.white));
                        break;
                }
                ll_popular.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sortMovies(POPULAR);
                        alertDialog.dismiss();
                    }
                });
                ll_toprated.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sortMovies(TOP_RATED);
                        alertDialog.dismiss();
                    }
                });
                ll_nowplay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sortMovies(NOW_PLAYING);
                        alertDialog.dismiss();
                    }
                });
                ll_upcoming.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sortMovies(UPCOMING);
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                break;
//            case R.id.action_popular:
//                sortMovies(POPULAR);
//                break;
//            case R.id.action_top_rated:
//                sortMovies(TOP_RATED);
//                break;
//            case R.id.action_now_playing:
//                sortMovies(NOW_PLAYING);
//                break;
//            case R.id.action_upcoming:
//                sortMovies(UPCOMING);
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setAppBarTitle() {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(sortBy+" Movies");
    }

    public void loadMovies(final String movie_type) {
        final String sortByBefore = sortBy;
        sortBy = getMoviesPreference();
        try {
            if (BuildConfig.TMDB_API_TOKEN.isEmpty()) {
                Toast.makeText(getActivity(), "Api Key belum ada", Toast.LENGTH_SHORT).show();
                return;
            }
            if(movie_type != sortByBefore)  showLoading();
            setAppBarTitle();
            TMDBInterface apiService = TMDBApi.getClient().create(TMDBInterface.class);
            switch (movie_type) {
                case NOW_PLAYING:
                    movieResponseCall = apiService.getNowPlayingMovies(BuildConfig.TMDB_API_TOKEN, page);
                    break;
                case POPULAR:
                    movieResponseCall = apiService.getPopularMovies("popularity.desc",BuildConfig.TMDB_API_TOKEN, page);
                    break;
                case TOP_RATED:
                    movieResponseCall = apiService.getTopRatedMovies("vote_average.desc",BuildConfig.TMDB_API_TOKEN, page);
                    break;
                case UPCOMING:
                    movieResponseCall = apiService.getUpcomingMovies(BuildConfig.TMDB_API_TOKEN, page);
                    break;
            }
            movieResponseCall.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    if (!response.isSuccessful()) {
                        movieResponseCall = call.clone();
                        movieResponseCall.enqueue(this);
                        return;
                    }

                    if (response.body() == null) return;
                    if (response.body().getResults() == null) return;

                    clearList();
                    for (Movie movie : response.body().getResults()) {
                        if (movie.getTitle() != null && movie.getBackdropPath() != null) {
                            movieList.add(movie);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (movie_type != sortByBefore) {
                        rvMovies.scrollToPosition(0);
                        hideLoading();
                    }
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Log.d("ERROR", t.getMessage());
                    Toast.makeText(getActivity(), "ERROR FETCHING DATA", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.d("ERROR GET DATA",e.getMessage());
            Toast.makeText(getActivity(),e.getMessage().toString(),Toast.LENGTH_SHORT).show();
        }
    }

    public void showLoading() {
        rvMovies.setVisibility(View.GONE);
        llLoading.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        llLoading.setVisibility(View.GONE);
        rvMovies.setVisibility(View.VISIBLE);
    }

    public void clearList() {
        movieList.clear();
        adapter.notifyDataSetChanged();
    }

    public void sortMovies(String sort) {
        writeMoviesPreference(sort);
        loadMovies(sort);
    }

    public void writeMoviesPreference(String sort) {
        Preferences.writeSharedPreferences(
                getActivity(),
                MOVIE_TYPE,
                sort
        );
    }

    public String getMoviesPreference() {
        String sortByPreference = Preferences.readSharedPreferences(
                getActivity(),
                MOVIE_TYPE,
                POPULAR
        );
        return sortByPreference;
    }
}
