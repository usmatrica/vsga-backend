package com.vsga.backend.movie.network.movie;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class MovieDetail implements Parcelable {

	@SerializedName("overview")
	private String overview;

	@SerializedName("original_language")
	private String originalLanguage;

	@SerializedName("original_title")
	private String originalTitle;

	@SerializedName("runtime")
	private int runtime;

	@SerializedName("title")
	private String title;

	@SerializedName("poster_path")
	private String posterPath;

	@SerializedName("backdrop_path")
	private String backdropPath;

	@SerializedName("release_date")
	private String releaseDate;

	@SerializedName("genres")
	private List<Genre> genres;

	@SerializedName("popularity")
	private double popularity;

	@SerializedName("vote_average")
	private double voteAverage;

	@SerializedName("tagline")
	private String tagline;

	@SerializedName("id")
	private int id;

	@SerializedName("vote_count")
	private int voteCount;

	@SerializedName("budget")
	private int budget;

	@SerializedName("homepage")
	private String homepage;

	@SerializedName("status")
	private String status;

	private String moviesGenre;

    public String getMoviesGenre() {
        return moviesGenre;
    }

    public MovieDetail(String overview, String originalLanguage, String originalTitle,
                       int runtime, String title, String posterPath, String backdropPath,
                       String releaseDate, double voteAverage, String tagline, int id, int voteCount,
                       String homepage, String moviesGenre){
        this.overview = overview;
        this.originalLanguage = originalLanguage;
        this.originalTitle = originalTitle;
        this.runtime = runtime;
        this.title = title;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.tagline = tagline;
        this.id = id;
        this.voteCount = voteCount;
        this.homepage = homepage;
        this.moviesGenre = moviesGenre;
    }

    public void setOverview(String overview){
		this.overview = overview;
	}

	public String getOverview(){
		return overview;
	}

	public void setOriginalLanguage(String originalLanguage){
		this.originalLanguage = originalLanguage;
	}

	public String getOriginalLanguage(){
		return originalLanguage;
	}

	public void setOriginalTitle(String originalTitle){
		this.originalTitle = originalTitle;
	}

	public String getOriginalTitle(){
		return originalTitle;
	}

	public void setRuntime(int runtime){
		this.runtime = runtime;
	}

	public int getRuntime(){
		return runtime;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setPosterPath(String posterPath){
		this.posterPath = posterPath;
	}

	public String getPosterPath(){
		return posterPath;
	}

	public void setBackdropPath(String backdropPath){
		this.backdropPath = backdropPath;
	}

	public String getBackdropPath(){
		return backdropPath;
	}

	public void setReleaseDate(String releaseDate){
		this.releaseDate = releaseDate;
	}

	public String getReleaseDate(){
		return releaseDate;
	}

	public void setGenres(List<Genre> genres){
		this.genres = genres;
	}

	public List<Genre> getGenres(){
		return genres;
	}

	public void setPopularity(double popularity){
		this.popularity = popularity;
	}

	public double getPopularity(){
		return popularity;
	}

	public void setVoteAverage(double voteAverage){
		this.voteAverage = voteAverage;
	}

	public double getVoteAverage(){
		return voteAverage;
	}

	public void setTagline(String tagline){
		this.tagline = tagline;
	}

	public String getTagline(){
		return tagline;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setVoteCount(int voteCount){
		this.voteCount = voteCount;
	}

	public int getVoteCount(){
		return voteCount;
	}

	public void setBudget(int budget){
		this.budget = budget;
	}

	public int getBudget(){
		return budget;
	}

	public void setHomepage(String homepage){
		this.homepage = homepage;
	}

	public String getHomepage(){
		return homepage;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

    public String getMovieGenre() {
        String movieGenre = "";
        if (genres != null) {
            for (int i = 0; i < genres.size(); i++) {
                if (genres.get(i) == null) continue;
                if (i == genres.size() - 1) {
                    movieGenre = movieGenre.concat(genres.get(i).getName());
                } else {
                    movieGenre = movieGenre.concat(genres.get(i).getName() + ", ");
                }
            }
        }
        return movieGenre;
    }

	@Override
 	public String toString(){
		return 
			"MovieDetail{" + 
			"overview = '" + overview + '\'' + 
			",original_language = '" + originalLanguage + '\'' + 
			",original_title = '" + originalTitle + '\'' + 
			",runtime = '" + runtime + '\'' + 
			",title = '" + title + '\'' + 
			",poster_path = '" + posterPath + '\'' + 
			",backdrop_path = '" + backdropPath + '\'' + 
			",release_date = '" + releaseDate + '\'' + 
			",genres = '" + genres + '\'' + 
			",popularity = '" + popularity + '\'' + 
			",vote_average = '" + voteAverage + '\'' + 
			",tagline = '" + tagline + '\'' + 
			",id = '" + id + '\'' + 
			",vote_count = '" + voteCount + '\'' + 
			",budget = '" + budget + '\'' + 
			",homepage = '" + homepage + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.overview);
		dest.writeString(this.originalLanguage);
		dest.writeString(this.originalTitle);
		dest.writeInt(this.runtime);
		dest.writeString(this.title);
		dest.writeString(this.posterPath);
		dest.writeString(this.backdropPath);
		dest.writeString(this.releaseDate);
		dest.writeList(this.genres);
		dest.writeDouble(this.popularity);
		dest.writeDouble(this.voteAverage);
		dest.writeString(this.tagline);
		dest.writeInt(this.id);
		dest.writeInt(this.voteCount);
		dest.writeInt(this.budget);
		dest.writeString(this.homepage);
		dest.writeString(this.status);
		dest.writeString(this.moviesGenre);
	}

	protected MovieDetail(Parcel in) {
		this.overview = in.readString();
		this.originalLanguage = in.readString();
		this.originalTitle = in.readString();
		this.runtime = in.readInt();
		this.title = in.readString();
		this.posterPath = in.readString();
		this.backdropPath = in.readString();
		this.releaseDate = in.readString();
		this.genres = new ArrayList<Genre>();
		in.readList(this.genres, Genre.class.getClassLoader());
		this.popularity = in.readDouble();
		this.voteAverage = in.readDouble();
		this.tagline = in.readString();
		this.id = in.readInt();
		this.voteCount = in.readInt();
		this.budget = in.readInt();
		this.homepage = in.readString();
		this.status = in.readString();
		this.moviesGenre = in.readString();
	}

	public static final Parcelable.Creator<MovieDetail> CREATOR = new Parcelable.Creator<MovieDetail>() {
		@Override
		public MovieDetail createFromParcel(Parcel source) {
			return new MovieDetail(source);
		}

		@Override
		public MovieDetail[] newArray(int size) {
			return new MovieDetail[size];
		}
	};
}