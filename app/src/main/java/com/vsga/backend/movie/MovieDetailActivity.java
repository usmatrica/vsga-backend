package com.vsga.backend.movie;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import com.vsga.backend.movie.adapters.ReviewsAdapter;
import com.vsga.backend.movie.adapters.TrailerAdapter;
import com.vsga.backend.movie.database.DbOpenHelper;
import com.vsga.backend.movie.network.TMDBApi;
import com.vsga.backend.movie.network.TMDBInterface;
import com.vsga.backend.movie.network.movie.MovieDetail;
import com.vsga.backend.movie.network.review.Review;
import com.vsga.backend.movie.network.review.ReviewResponse;
import com.vsga.backend.movie.network.trailer.Trailer;
import com.vsga.backend.movie.network.trailer.TrailerResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {
    private final String TRAILER = "trailer";
    private final String REVIEW = "review";
    public static final String LOAD_TYPE = "load_type";
    public static final String KEY_MOVIE_ID = "movie_id";
    public static final String PARCELABLE_MOVIE = "parcelable_movie";


    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.cl)
    CoordinatorLayout cl;

    @BindView(R.id.btn_favorite)
    ImageView btn_favorite;

    @BindView(R.id.iv_backdrop)
    ImageView iv_backdrop;
    @BindView(R.id.iv_poster)
    ImageView iv_poster;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_release)
    TextView tv_release;
    @BindView(R.id.tv_runtime)
    TextView tv_runtime;
    @BindView(R.id.tv_rating)
    TextView tv_rating;
    @BindView(R.id.tv_vote_count)
    TextView tv_vote;
    @BindView(R.id.tv_overview)
    TextView tv_overview;
    @BindView(R.id.tv_ori_title)
    TextView tv_ori_title;
    @BindView(R.id.tv_ori_lan)
    TextView tv_ori_lan;
    @BindView(R.id.tv_genre)
    TextView tv_genre;
    @BindView(R.id.tv_tag)
    TextView tv_tag;
    @BindView(R.id.tv_home)
    TextView tv_home;
    @BindView(R.id.tv_readmore)
    TextView tv_readmore;
    @BindView(R.id.tl_desc)
    TableLayout tl_desc;

    @BindView(R.id.layout_trailer)
    LinearLayout layout_trailer;
    @BindView(R.id.layout_reviews)
    LinearLayout layout_reviews;
    @BindView(R.id.rv_trailer)
    RecyclerView rv_trailer;
    @BindView(R.id.tv_no_trailer)
    TextView tv_no_trailer;
    @BindView(R.id.rv_reviews)
    RecyclerView rv_reviews;
    @BindView(R.id.tv_no_review)
    TextView tv_no_review;
    @BindView(R.id.ll_allreview)
    LinearLayout ll_all_review;

    private static String title;
    private static String ori_title;
    private static String ori_lan;
    private static String genre;
    private static String tag;
    private static String homepage;
    private static String release_date;
    private static String runtime;
    private static String rating;
    private static String vote_count;
    private static String overview;
    private static String backdrop_url;
    private static String poster_url;
    private static int tmdb_id;

    private MovieDetail movieDetail;

    String getLoadType;
    int scrollRange = -1;
    int movie_id;
    int page = 1;
    Intent intent;
    AlertDialog dialog;
    boolean isTrailerLoad = false;
    boolean isReviewsLoad = false;
    private boolean isFavorite = false;

    private DbOpenHelper mDbHelper;

    Call<MovieDetail> movieResponseCall;

    TrailerAdapter trailerAdapter;
    List<Trailer> trailerList;
    Call<TrailerResponse> trailerResponseCall;
    LinearLayoutManager trailerLayout;

    ReviewsAdapter reviewsAdapter;
    List<Review> reviewList;
    Call<ReviewResponse> reviewResponseCall;
    LinearLayoutManager reviewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new SpotsDialog(MovieDetailActivity.this, R.style.customDialog);

        intent = getIntent();
        getLoadType = intent.getStringExtra(LOAD_TYPE);
        mDbHelper = new DbOpenHelper(getApplicationContext());

        if (getLoadType.equals("favorite")) {
            layout_reviews.setVisibility(View.GONE);
            layout_trailer.setVisibility(View.GONE);
            setSize();
            movieDetail = intent.getExtras().getParcelable(PARCELABLE_MOVIE);
            setData();
            isFavorite = checkFavorite();
            changeFavoriteIcon();
            setContent();
        } else {
            movie_id = intent.getIntExtra(KEY_MOVIE_ID, 0);

            trailerLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            trailerList = new ArrayList<>();
            trailerAdapter = new TrailerAdapter(this, trailerList);
            rv_trailer.setLayoutManager(trailerLayout);
            rv_trailer.setAdapter(trailerAdapter);
            trailerAdapter.notifyDataSetChanged();

            reviewLayout = new LinearLayoutManager(MovieDetailActivity.this, LinearLayoutManager.VERTICAL, false);
            reviewList = new ArrayList<>();
            reviewsAdapter = new ReviewsAdapter(this, reviewList);
            rv_reviews.setLayoutManager(reviewLayout);
            rv_reviews.setAdapter(reviewsAdapter);
            reviewsAdapter.notifyDataSetChanged();

            setSize();
            if (savedInstanceState != null) {
                List<Trailer> savedTrailer = savedInstanceState.getParcelableArrayList(TRAILER);
                trailerAdapter.setTrailer(savedTrailer);
                List<Review> savedReview = savedInstanceState.getParcelableArrayList(REVIEW);
                reviewsAdapter.setReview(savedReview);
                setContent();
                return;
            }
            dialog.show();
            loadTrailer();
            loadReviews();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!getLoadType.equals("favorite")) {
            List<Trailer> trailer = trailerAdapter.getTrailerData();
            outState.putParcelableArrayList(TRAILER, (ArrayList<? extends Parcelable>) trailer);
            List<Review> reviews = reviewsAdapter.getReviewData();
            outState.putParcelableArrayList(REVIEW, (ArrayList<? extends Parcelable>) reviews);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setSize() {
        //backdrop sama colapse toolbar
        int backdropHeight = (int) (getResources().getDisplayMetrics().widthPixels / 1.77);
        iv_backdrop.getLayoutParams().width = getResources().getDisplayMetrics().widthPixels;
        iv_backdrop.getLayoutParams().height = backdropHeight;
        collapsingToolbarLayout.getLayoutParams().height = backdropHeight;
        //poster
        int posterWidth;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            posterWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.3);
        } else {
            posterWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.17);
        }
        int posterHeight = (int) (posterWidth * 1.5);
        iv_poster.getLayoutParams().width = posterWidth;
        iv_poster.getLayoutParams().height = posterHeight;
    }

    public String convertDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat("dd MMM yyyy");
        String release_date = format.format(newDate);
        return release_date;
    }

    public void loadTrailer() {
        try {
            if (BuildConfig.TMDB_API_TOKEN.isEmpty()) {
                Toast.makeText(MovieDetailActivity.this, "Api Key belum ada", Toast.LENGTH_SHORT).show();
                return;
            }
            TMDBInterface apiService = TMDBApi.getClient().create(TMDBInterface.class);
            trailerResponseCall = apiService.getMovieTrailer(movie_id, BuildConfig.TMDB_API_TOKEN);
            trailerResponseCall.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                    isTrailerLoad = true;
                    loadMovie();
                    if (!response.isSuccessful()) {
                        trailerResponseCall = call.clone();
                        trailerResponseCall.enqueue(this);
                        return;
                    }

                    if (response.body() == null) return;
                    if (response.body().getResults() == null) return;

                    for (Trailer trailer : response.body().getResults()) {
                        if (trailer != null && trailer.getSite() != null && trailer.getSite().equals("YouTube") && trailer.getType() != null) {
                            trailerList.add(trailer);
                        }
                    }
                    if (trailerList.isEmpty()) {
                        rv_trailer.setVisibility(View.GONE);
                        tv_no_trailer.setVisibility(View.VISIBLE);
                    }
                    trailerAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {
                    Log.d("ERROR", t.getMessage());
                    Toast.makeText(MovieDetailActivity.this, "ERROR FETCHING DATA TRAILER", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.d("ERROR GET DATA TRAILER", e.getMessage());
            Toast.makeText(MovieDetailActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void loadReviews() {
        try {
            if (BuildConfig.TMDB_API_TOKEN.isEmpty()) {
                Toast.makeText(MovieDetailActivity.this, "Api Key belum ada", Toast.LENGTH_SHORT).show();
                return;
            }
            TMDBInterface apiService = TMDBApi.getClient().create(TMDBInterface.class);
            reviewResponseCall = apiService.getMovieReviews(movie_id, BuildConfig.TMDB_API_TOKEN, page);
            reviewResponseCall.enqueue(new Callback<ReviewResponse>() {
                @Override
                public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                    isReviewsLoad = true;
                    loadMovie();
                    if (!response.isSuccessful()) {
                        reviewResponseCall = call.clone();
                        reviewResponseCall.enqueue(this);
                        return;
                    }

                    if (response.body() == null) return;
                    if (response.body().getResults() == null) return;
                    if (response.body().getResults().size() == 0) return;

                    int result = response.body().getResults().size();
                    int max = (result >= 3) ? 3 : result;
                    int reviewData = 0;
                    do {
                        reviewList.add(response.body().getResults().get(reviewData));
                        reviewData++;
                    }
                    while (reviewData < max);

                    reviewsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<ReviewResponse> call, Throwable t) {
                    Log.d("ERROR", t.getMessage());
                    Toast.makeText(MovieDetailActivity.this, "ERROR FETCHING DATA REVIEW", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.d("ERROR GET DATA REVIEW", e.getMessage());
            Toast.makeText(MovieDetailActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void loadMovie() {
        if (!isTrailerLoad) return;
        if (!isReviewsLoad) return;
        try {
            if (BuildConfig.TMDB_API_TOKEN.isEmpty()) {
                Toast.makeText(MovieDetailActivity.this, "Api Key belum ada", Toast.LENGTH_SHORT).show();
                return;
            }
            TMDBInterface apiService = TMDBApi.getClient().create(TMDBInterface.class);
            movieResponseCall = apiService.getMovieDetail(movie_id, BuildConfig.TMDB_API_TOKEN);
            movieResponseCall.enqueue(new Callback<MovieDetail>() {
                @Override
                public void onResponse(Call<MovieDetail> call, final Response<MovieDetail> response) {
                    if (!response.isSuccessful()) {
                        movieResponseCall = call.clone();
                        movieResponseCall.enqueue(this);
                        return;
                    }
                    if (response.body() == null) return;

                    movieDetail = response.body();
                    setData();
                    isFavorite = checkFavorite();
                    changeFavoriteIcon();
                    setContent();
                }

                @Override
                public void onFailure(Call<MovieDetail> call, Throwable t) {
                    Log.d("ERROR", t.getMessage());
                    Toast.makeText(MovieDetailActivity.this, "ERROR FETCHING DATA MOVIES", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.d("ERROR GET DATA MOVIES", e.getMessage());
            Toast.makeText(MovieDetailActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void setData() {
        backdrop_url = "https://image.tmdb.org/t/p/w780/" + movieDetail.getBackdropPath();
        poster_url = "https://image.tmdb.org/t/p/w780/" + movieDetail.getPosterPath();
        title = movieDetail.getTitle();
        release_date = convertDate(movieDetail.getReleaseDate());
        runtime = String.valueOf(movieDetail.getRuntime()) + " min";
        rating = String.valueOf(movieDetail.getVoteAverage());
        vote_count = String.valueOf(movieDetail.getVoteCount());
        overview = movieDetail.getOverview();
        ori_title = movieDetail.getOriginalTitle();
        ori_lan = movieDetail.getOriginalLanguage();
        genre = (getLoadType.equals("favorite")) ? movieDetail.getMoviesGenre() : movieDetail.getMovieGenre();
        tag = movieDetail.getTagline();
        homepage = movieDetail.getHomepage();
        tmdb_id = movieDetail.getId();
    }

    public void setContent() {
        Glide
                .with(getApplicationContext())
                .load(backdrop_url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv_backdrop);

        Glide
                .with(getApplicationContext())
                .load(poster_url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv_poster);

        tv_title.setText(title);
        tv_release.setText(release_date);
        tv_runtime.setText(runtime);
        tv_rating.setText(rating);
        tv_vote.setText(vote_count);
        tv_overview.setText(overview);
        tv_ori_title.setText(ori_title);
        tv_ori_lan.setText(ori_lan);
        tv_genre.setText(genre);
        tv_tag.setText(tag);
        tv_home.setText(homepage);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    collapsingToolbarLayout.setTitle("");
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (Math.abs(scrollRange + verticalOffset) == 0) {
                    collapsingToolbarLayout.setTitle(title);
                    collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.colorPrimary));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.transparent));
                    isShow = false;
                }
            }
        });

        if (!getLoadType.equals("favorite")) {
            if (reviewList.size() == 0) {
                rv_reviews.setVisibility(View.GONE);
                tv_no_review.setVisibility(View.VISIBLE);
            } else {
                ll_all_review.setVisibility(View.VISIBLE);
            }
        }

        ll_all_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieDetailActivity.this, AllReviewsActivity.class);
                intent.putExtra("movie_id", movie_id);
                intent.putExtra("movie_title", title);
                startActivity(intent);
            }
        });

        tv_readmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_readmore.setVisibility(View.GONE);
                tl_desc.setVisibility(View.VISIBLE);
            }
        });

        btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    boolean isDeleteSuccess = mDbHelper.deleteFavorite(movieDetail);
                    isFavorite = !isDeleteSuccess;
                    Snackbar.make(cl, "Removed from favorite", Snackbar.LENGTH_SHORT).show();
                } else {
                    isFavorite = mDbHelper.addFavorite(movieDetail) > 0;
                    Snackbar.make(cl, "Added to favorite", Snackbar.LENGTH_SHORT).show();
                }
                changeFavoriteIcon();
            }
        });

        dialog.dismiss();
    }

    private void changeFavoriteIcon() {
        btn_favorite.setImageResource(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_empty);
    }

    private boolean checkFavorite() {
        return mDbHelper.isFavorite(tmdb_id);
    }
}
