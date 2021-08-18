package com.vsga.backend.movie;

import android.content.Intent;
import android.os.Parcelable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.vsga.backend.movie.adapters.AllReviewsAdapter;
import com.vsga.backend.movie.network.TMDBApi;
import com.vsga.backend.movie.network.TMDBInterface;
import com.vsga.backend.movie.network.review.Review;
import com.vsga.backend.movie.network.review.ReviewResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllReviewsActivity extends AppCompatActivity {

    private final String REVIEW = "review";

    @BindView(R.id.rv_all_review)RecyclerView rv_reviews;
    @BindView(R.id.pb_allreview)ProgressBar pb_review;

    ActionBar actionBar;

    AllReviewsAdapter reviewsAdapter;
    List<Review> reviewList;
    Call<ReviewResponse> reviewResponseCall;
    LinearLayoutManager reviewLayout;

    Intent intent;
    int movie_id;
    String movie_title;
    int page = 1;
    private boolean pagesOver = false;
    private boolean loading = false;
    private int previousTotal = 0;
    private int visibleThreshold = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reviews);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        actionBar.setElevation(0);

        ButterKnife.bind(this);

        intent = getIntent();
        movie_id = intent.getIntExtra("movie_id",0);
        movie_title = intent.getStringExtra("movie_title");
        actionBar.setTitle(movie_title);

        reviewLayout = new LinearLayoutManager(AllReviewsActivity.this, LinearLayoutManager.VERTICAL, false);
        reviewList = new ArrayList<>();
        reviewsAdapter = new AllReviewsAdapter(this,reviewList);
        rv_reviews.setLayoutManager(reviewLayout);
        rv_reviews.setAdapter(reviewsAdapter);
        reviewsAdapter.notifyDataSetChanged();

        rv_reviews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!pagesOver) {
                    int visibleItemCount = reviewLayout.getChildCount();
                    int totalItemCount = reviewLayout.getItemCount();
                    int firstVisibleItem = reviewLayout.findFirstVisibleItemPosition();

                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                        loadReviews();
                        loading = true;
                    }
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        if (savedInstanceState != null) {
            pb_review.setVisibility(View.GONE);
            List<Review> savedReviews = savedInstanceState.getParcelableArrayList(REVIEW);
            reviewsAdapter.setReview(savedReviews);
            reviewsAdapter.notifyDataSetChanged();
            return;
        }
        loadReviews();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        List<Review> reviews = reviewsAdapter.getReviewData();
        outState.putParcelableArrayList(REVIEW,(ArrayList<? extends Parcelable>) reviews);
        super.onSaveInstanceState(outState);
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

    public void loadReviews() {
        try {
            if (BuildConfig.TMDB_API_TOKEN.isEmpty()) {
                Toast.makeText(AllReviewsActivity.this, "Api Key belum ada", Toast.LENGTH_SHORT).show();
                return;
            }
            TMDBInterface apiService = TMDBApi.getClient().create(TMDBInterface.class);
            reviewResponseCall = apiService.getMovieReviews(movie_id,BuildConfig.TMDB_API_TOKEN,page);
            reviewResponseCall.enqueue(new Callback<ReviewResponse>() {
                @Override
                public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                    if (!response.isSuccessful()) {
                        reviewResponseCall = call.clone();
                        reviewResponseCall.enqueue(this);
                        return;
                    }

                    if (response.body() == null) return;
                    if (response.body().getResults() == null) return;
                    if (response.body().getResults().size() == 0) return;

                    for (Review review : response.body().getResults()) {
                        if (review != null && review.getAuthor() != null && review.getContent() != null)
                            reviewList.add(review);
                    }
                    reviewsAdapter.notifyDataSetChanged();
                    pb_review.setVisibility(View.GONE);

                    if (response.body().getPage() == response.body().getTotalPages()) {
                        pagesOver = true;
                    } else {
                        page++;
                    }
                }

                @Override
                public void onFailure(Call<ReviewResponse> call, Throwable t) {
                    Log.d("ERROR", t.getMessage());
                    Toast.makeText(AllReviewsActivity.this, "ERROR FETCHING DATA REVIEW", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.d("ERROR GET DATA REVIEW",e.getMessage());
            Toast.makeText(AllReviewsActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
        }
    }
}
