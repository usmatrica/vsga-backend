package com.vsga.backend.movie.network;

import com.vsga.backend.movie.network.movie.MovieDetail;
import com.vsga.backend.movie.network.movie.MovieResponse;
import com.vsga.backend.movie.network.review.ReviewResponse;
import com.vsga.backend.movie.network.trailer.TrailerResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by oiDutS on 25/11/2017.
 */

public interface TMDBInterface {

    @GET("movie/now_playing")
    Call<MovieResponse> getNowPlayingMovies(
            @Query("api_key")String api_key,
            @Query("page")int page
    );

    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(
            @Query("sort_by")String sort,
            @Query("api_key")String api_key,
            @Query("page")int page
    );

    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(
            @Query("sort_by")String sort,
            @Query("api_key")String api_key,
            @Query("page")int page
    );

    @GET("movie/upcoming")
    Call<MovieResponse> getUpcomingMovies(
            @Query("api_key")String api_key,
            @Query("page")int page
    );

    @GET("movie/{movie_id}")
    Call<MovieDetail> getMovieDetail(
            @Path("movie_id") int movie_id,
            @Query("api_key")String api_key
    );

    @GET("movie/{movie_id}/videos")
    Call<TrailerResponse> getMovieTrailer(
            @Path("movie_id") int movie_id,
            @Query("api_key")String api_key
    );

    @GET("movie/{movie_id}/reviews")
    Call<ReviewResponse> getMovieReviews(
            @Path("movie_id") int movie_id,
            @Query("api_key")String api_key,
            @Query("page")int page
    );
}
