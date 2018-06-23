package com.krp.findmovies.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.krp.findmovies.model.Movie;

import java.util.List;

@Dao
public interface FindMoviesDAO {

    @Query("SELECT * FROM movie")
    List<Movie> getMovies();

    @Query("SELECT isFavourite FROM movie WHERE id = :movieId")
    boolean isFavouriteMovie(int movieId);

    @Query("SELECT * FROM movie WHERE id = :movieId")
    Movie getMovie(int movieId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);
}
