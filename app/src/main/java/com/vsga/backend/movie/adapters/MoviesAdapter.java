package com.vsga.backend.movie.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.vsga.backend.movie.MovieDetailActivity;
import com.vsga.backend.movie.R;
import com.vsga.backend.movie.network.movie.Movie;

/**
 * Created by oiDutS on 26/11/2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    Context context;
    List<Movie> movieList;

    public MoviesAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {
        String rating = String.valueOf(movieList.get(position).getVoteAverage());
        String poster_url = "https://image.tmdb.org/t/p/w500" + movieList.get(position).getPosterPath();

        String release_year = movieList.get(position).getReleaseDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = format.parse(release_year);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat("yyyy");
        release_year = format.format(newDate);

        holder.title.setText(movieList.get(position).getTitle());
        holder.tahun.setText(release_year);
        holder.rating.setText(rating);
        Glide.with(context)
                .load(poster_url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.poster);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public List<Movie> getMovieData() {
        return this.movieList;
    }

    public void setMovie(List<Movie> movie) {
        movieList.addAll(movie);
        notifyDataSetChanged();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_poster)
        ImageView poster;
        @BindView(R.id.pb_thumbnail)
        ProgressBar progressBar;
        @BindView(R.id.tv_title)
        TextView title;
        @BindView(R.id.tv_release_year)
        TextView tahun;
        @BindView(R.id.tv_rating)
        TextView rating;

        public MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            poster.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    poster.getViewTreeObserver().removeOnPreDrawListener(this);
                    final int width = poster.getMeasuredWidth();
                    //Toast.makeText(context,String.valueOf(width),Toast.LENGTH_SHORT).show();
                    poster.getLayoutParams().height = (int) (width * 1.5);
                    return true;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Movie clickedDataItem = movieList.get(pos);
                        Intent intent = new Intent(context, MovieDetailActivity.class);
                        intent.putExtra(MovieDetailActivity.LOAD_TYPE, "online");
                        intent.putExtra(MovieDetailActivity.KEY_MOVIE_ID, clickedDataItem.getId());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
