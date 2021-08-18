package com.vsga.backend.movie.database;

import android.provider.BaseColumns;

/**
 * Created by oiDutS on 09/12/2017.
 */

public class DbContract {
    public static class MoviesContract implements BaseColumns {
        public static final String TABLE_NAME = "favorite_movies";
        public static final String TMDB_ID = "tmdb_id";
        public static final String TITLE = "title";
        public static final String ORI_TITLE = "ori_title";
        public static final String ORI_LAN = "ori_lan";
        public static final String GENRE = "genre";
        public static final String TAG = "tagline";
        public static final String HOMEPAGE = "homepage";
        public static final String RELEASE_DATE = "release_date";
        public static final String RUNTIME = "runtime";
        public static final String RATING = "rating";
        public static final String VOTE_COUNT = "vote_count";
        public static final String OVERVIEW = "overview";
        public static final String BACKDROP_URL = "backdrop_url";
        public static final String POSTER_URL = "poster_url";
    }
}
