package com.krp.findmovies.viewModels;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.View;
import android.widget.ImageView;

import com.krp.findmovies.R;
import com.krp.findmovies.common.BaseUrl;
import com.krp.findmovies.database.AppDatabase;
import com.krp.findmovies.interfaces.FmApiService;
import com.krp.findmovies.model.Movie;
import com.krp.findmovies.utilities.FragmentUtils;
import com.krp.findmovies.utilities.NetworkHandler;
import com.krp.findmovies.views.fragments.MovieReviewsFragment;
import com.krp.findmovies.views.fragments.MovieTrailersFragment;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailViewModel {

    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w185/";

    private ObservableField<Movie> movie;
    private ObservableInt progressbarVisibility;
    private ObservableInt noInternetVisibility;
    private ObservableInt retrievalFailureMsgVisibility;
    private ObservableInt detailsVisibility;
    private ObservableBoolean isFavourite;

    private Context context;
    private FmApiService fmApiService;

    private AppDatabase mAppDatabase;

    public MovieDetailViewModel(Context context) {
        this.context = context;

        movie = new ObservableField<>();
        progressbarVisibility = new ObservableInt(View.VISIBLE);
        noInternetVisibility = new ObservableInt(View.GONE);
        retrievalFailureMsgVisibility = new ObservableInt(View.GONE);
        detailsVisibility = new ObservableInt(View.GONE);
        isFavourite = new ObservableBoolean(false);

        mAppDatabase = AppDatabase.getInstance(context);
    }

    public ObservableInt getProgressbarVisibility() {
        return progressbarVisibility;
    }

    public ObservableInt getNoInternetVisibility() {
        return noInternetVisibility;
    }

    public ObservableInt getRetrievalFailureMsgVisibility() {
        return retrievalFailureMsgVisibility;
    }

    public ObservableField<Movie> getMovie() {
        return movie;
    }

    public ObservableInt getDetailsVisibility() {
        return detailsVisibility;
    }

    public ObservableBoolean getIsFavourite() {
        return isFavourite;
    }

    public void fetchData(int movieId) {
        if (NetworkHandler.isNetworkAvailable(context)) {
            makeServiceCall(movieId);
        } else {
            noInternetVisibility.set(View.VISIBLE);
            progressbarVisibility.set(View.GONE);
        }

    }

    private void makeServiceCall(final int movieId) {

        if (fmApiService == null) {
            fmApiService = BaseUrl.getFmApiService();
        }

        Call<Movie> call = fmApiService.getMovieDetail(movieId, context.getString(R.string.api_key));

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {

                if (response.isSuccessful()) {
                    movie.set(response.body());
                    checkIfSelectedMovieIsFavourite(movieId);
                    detailsVisibility.set(View.VISIBLE);
                    FragmentUtils.replaceFragment(context, R.id.movieTrailersFL, MovieTrailersFragment.newInstance(movieId));
                    FragmentUtils.replaceFragment(context, R.id.movieReviewsFL, MovieReviewsFragment.newInstance(movieId));
                }
                progressbarVisibility.set(View.GONE);
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {

                retrievalFailureMsgVisibility.set(View.VISIBLE);
                progressbarVisibility.set(View.GONE);
            }
        });
    }

    @BindingAdapter("setDetailImage")
    public static void setDetailImage(final ImageView imageView, String urlPath) {
        if (imageView != null) {
            Picasso.with(imageView.getContext())
                    .load(IMAGE_BASE_URL + urlPath)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);

        }
    }

    @BindingAdapter("setRating")
    public static void setRating(AppCompatRatingBar appCompatRatingBar, double voteAverage) {

        if (appCompatRatingBar != null) {
            appCompatRatingBar.setRating((float) voteAverage);
        }
    }

    private void checkIfSelectedMovieIsFavourite(int movieId) {
        boolean value = mAppDatabase.findMoviesDao().isFavouriteMovie(movieId);
        isFavourite.set(value);
    }

    public void onFavouriteFABClicked(View fab, boolean favouriteValue) {

        if (!favouriteValue) {
            // If it is not favourite, add to favourite.
            movie.get().setFavourite(true);
            isFavourite.set(true);
            mAppDatabase.findMoviesDao().insertMovie(movie.get());
        } else {
            // If it is already favourite, remove from favourite.
            movie.get().setFavourite(false);
            isFavourite.set(false);
            mAppDatabase.findMoviesDao().deleteMovie(movie.get());
        }
    }
}
