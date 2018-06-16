package com.krp.findmovies.interfaces;

import com.krp.findmovies.model.MovieDetail;
import com.krp.findmovies.model.MoviesResponse;
import com.krp.findmovies.model.Reviews;
import com.krp.findmovies.model.Trailers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by rakeshpraneeth.
 * This interface is used to handle all the API calls using Retrofit.
 * It contains methods such as GET,POST,...
 * Each operation will have a method and some parameters depending on the type of operation we perform.
 */

public interface FmApiService {

    // This is used to get list of particular movies.
    @GET("3/{movieType}")
    Call<MoviesResponse> getMoviesList(@Path(value = "movieType", encoded = true) String movieType, @Query("api_key") String apiKey);

    @GET("3/movie/{movieId}")
    Call<MovieDetail> getMovieDetail(@Path(value = "movieId") int movieId,
                                     @Query("api_key") String apiKey);

    @GET("3/movie/{movieId}/videos")
    Call<Trailers> getTrailers(@Path("movieId") int movieId, @Query("api_key") String apiKey);

    @GET("3/movie/{movieId}/reviews")
    Call<Reviews> getReviews(@Path("movieId") int movieId, @Query("api_key") String apiKey);
}
