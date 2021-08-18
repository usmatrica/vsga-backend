package com.vsga.backend.movie.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.vsga.backend.movie.network.movie.MovieDetail;

import static com.vsga.backend.movie.database.DbContract.MoviesContract;

/**
 * Created by oiDutS on 09/12/2017.
 */

public class DbOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movies";
    private static final int DATABASE_VERSION = 3;

    private static final String SQL_CREATE_TABLE_FAVORITE_MOVIES =
            "CREATE TABLE "+MoviesContract.TABLE_NAME+ "( "+
                    MoviesContract._ID+" INTEGER PRIMARY KEY, "+
                    MoviesContract.TMDB_ID+" INTEGER, "+
                    MoviesContract.TITLE+" TEXT, "+
                    MoviesContract.ORI_TITLE+" TEXT, "+
                    MoviesContract.ORI_LAN+" TEXT, "+
                    MoviesContract.GENRE+" TEXT, "+
                    MoviesContract.TAG+" TEXT, "+
                    MoviesContract.HOMEPAGE+" TEXT, "+
                    MoviesContract.RELEASE_DATE+" TEXT, "+
                    MoviesContract.RUNTIME+" INTEGER, "+
                    MoviesContract.RATING+" DOUBLE, "+
                    MoviesContract.VOTE_COUNT+" INTEGER, "+
                    MoviesContract.OVERVIEW+" TEXT, "+
                    MoviesContract.BACKDROP_URL+" TEXT, "+
                    MoviesContract.POSTER_URL+" TEXT"+
            "); ";

    private static final String SQL_DROP_TABLE_MOVIES =
            "DROP TABLE IF EXISTS "+MoviesContract.TABLE_NAME;

    public DbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_FAVORITE_MOVIES);
        Log.d("DATABASE","DATABASE CREATE");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE_MOVIES);
        Log.d("DATABASE","DATABASE DELETE");
        onCreate(db);
    }

    public boolean isFavorite(int tmdb_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM "+MoviesContract.TABLE_NAME+" WHERE "+
                        MoviesContract.TMDB_ID+ "= ?", new String[]{String.valueOf(tmdb_id)}
        );
        int totalRow = cursor.getCount();
        db.close();
        return totalRow>0;
    }

    public List<MovieDetail> getFavorite() {
        List <MovieDetail> favoriteList =  new ArrayList<MovieDetail>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+MoviesContract.TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                MovieDetail movieDetail = new MovieDetail(
                        cursor.getString(cursor.getColumnIndex(MoviesContract.OVERVIEW)),
                        cursor.getString(cursor.getColumnIndex(MoviesContract.ORI_LAN)),
                        cursor.getString(cursor.getColumnIndex(MoviesContract.ORI_TITLE)),
                        cursor.getInt(cursor.getColumnIndex(MoviesContract.RUNTIME)),
                        cursor.getString(cursor.getColumnIndex(MoviesContract.TITLE)),
                        cursor.getString(cursor.getColumnIndex(MoviesContract.POSTER_URL)),
                        cursor.getString(cursor.getColumnIndex(MoviesContract.BACKDROP_URL)),
                        cursor.getString(cursor.getColumnIndex(MoviesContract.RELEASE_DATE)),
                        cursor.getDouble(cursor.getColumnIndex(MoviesContract.RATING)),
                        cursor.getString(cursor.getColumnIndex(MoviesContract.TAG)),
                        cursor.getInt(cursor.getColumnIndex(MoviesContract.TMDB_ID)),
                        cursor.getInt(cursor.getColumnIndex(MoviesContract.VOTE_COUNT)),
                        cursor.getString(cursor.getColumnIndex(MoviesContract.HOMEPAGE)),
                        cursor.getString(cursor.getColumnIndex(MoviesContract.GENRE))
                );
                favoriteList.add(movieDetail);
            }while (cursor.moveToNext());
        }

        db.close();
        return favoriteList;
    }

    public long addFavorite(MovieDetail movieDetail) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(MoviesContract.TMDB_ID, movieDetail.getId());
        cv.put(MoviesContract.TITLE, movieDetail.getTitle());
        cv.put(MoviesContract.ORI_TITLE, movieDetail.getOriginalTitle());
        cv.put(MoviesContract.ORI_LAN, movieDetail.getOriginalLanguage());
        cv.put(MoviesContract.GENRE, movieDetail.getMovieGenre());
        cv.put(MoviesContract.TAG, movieDetail.getTagline());
        cv.put(MoviesContract.HOMEPAGE, movieDetail.getHomepage());
        cv.put(MoviesContract.RELEASE_DATE, movieDetail.getReleaseDate());
        cv.put(MoviesContract.RUNTIME, movieDetail.getRuntime());
        cv.put(MoviesContract.RATING, movieDetail.getVoteAverage());
        cv.put(MoviesContract.VOTE_COUNT, movieDetail.getVoteCount());
        cv.put(MoviesContract.OVERVIEW, movieDetail.getOverview());
        cv.put(MoviesContract.BACKDROP_URL, movieDetail.getBackdropPath());
        cv.put(MoviesContract.POSTER_URL, movieDetail.getPosterPath());

        long rowInserted = db.insert(MoviesContract.TABLE_NAME,null,cv);
        db.close();
        return rowInserted;
    }

    public boolean deleteFavorite(MovieDetail movieDetail) {
        SQLiteDatabase db =  getWritableDatabase();

        int rowDeleted = db.delete(
                MoviesContract.TABLE_NAME, MoviesContract.TMDB_ID+" = ?",
                new String[]{String.valueOf(movieDetail.getId())}
        );
        db.close();
        return rowDeleted>0;
    }
}
