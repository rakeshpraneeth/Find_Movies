package com.krp.findmovies.viewModels;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.View;
import android.widget.ImageView;

import com.krp.findmovies.R;
import com.krp.findmovies.common.BaseUrl;
import com.krp.findmovies.database.AppDatabase;
import com.krp.findmovies.interfaces.FmApiService;
import com.krp.findmovies.model.Movie;
import com.krp.findmovies.utilities.FindMoviesExecutors;
import com.krp.findmovies.utilities.FragmentUtils;
import com.krp.findmovies.utilities.NetworkHandler;
import com.krp.findmovies.views.fragments.MovieReviewsFragment;
import com.krp.findmovies.views.fragments.MovieTrailersFragment;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailViewModel implements Parcelable {

    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w185/";

    private ObservableField<Movie> movie;
    private ObservableInt progressbarVisibility;
    private ObservableInt noInternetVisibility;
    private ObservableInt retrievalFailureMsgVisibility;
    private ObservableInt detailsVisibility;
    private ObservableBoolean isFavourite;

    private FmApiService fmApiService;

    private AppDatabase mAppDatabase;
    private Context context;

    public MovieDetailViewModel(Context context, int movieId) {
        this.context = context;
        mAppDatabase = AppDatabase.getInstance(context);

        movie = new ObservableField<>();
        progressbarVisibility = new ObservableInt(View.VISIBLE);
        noInternetVisibility = new ObservableInt(View.GONE);
        retrievalFailureMsgVisibility = new ObservableInt(View.GONE);
        detailsVisibility = new ObservableInt(View.GONE);
        isFavourite = new ObservableBoolean(false);

        fetchData(movieId);
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

    private void fetchData(int movieId) {
        if (NetworkHandler.isNetworkAvailable(context)) {
            makeServiceCall(movieId);
        } else {
            checkIfMovieIsInDatabase(movieId);
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

    private void checkIfSelectedMovieIsFavourite(final int movieId) {
        FindMoviesExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                boolean value = mAppDatabase.findMoviesDao().isFavouriteMovie(movieId);
                isFavourite.set(value);
            }
        });
    }

    private void checkIfMovieIsInDatabase(final int movieId) {
        FindMoviesExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Movie movieFromDb = mAppDatabase.findMoviesDao().getMovieById(movieId);
                if (movieFromDb != null) {
                    movie.set(movieFromDb);
                    isFavourite.set(movieFromDb.isFavourite());
                    detailsVisibility.set(View.VISIBLE);
                } else {
                    noInternetVisibility.set(View.VISIBLE);
                }
                progressbarVisibility.set(View.GONE);
            }
        });
    }

    public void onFavouriteFABClicked(View fab, boolean favouriteValue) {

        if (!favouriteValue) {
            // If it is not favourite, add to favourite.
            movie.get().setFavourite(true);
            isFavourite.set(true);
            performMovieOperation(false);
        } else {
            // If it is already favourite, remove from favourite.
            movie.get().setFavourite(false);
            isFavourite.set(false);
            performMovieOperation(true);
        }
    }

    private void performMovieOperation(final boolean isDeletingMovie) {
        FindMoviesExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (isDeletingMovie) {
                    mAppDatabase.findMoviesDao().deleteMovie(movie.get());
                } else {
                    mAppDatabase.findMoviesDao().insertMovie(movie.get());
                }
            }
        });
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.progressbarVisibility, flags);
        dest.writeParcelable(this.noInternetVisibility, flags);
        dest.writeParcelable(this.retrievalFailureMsgVisibility, flags);
        dest.writeParcelable(this.detailsVisibility, flags);
        dest.writeParcelable(this.isFavourite, flags);
    }

    protected MovieDetailViewModel(Parcel in) {
        this.progressbarVisibility = in.readParcelable(ObservableInt.class.getClassLoader());
        this.noInternetVisibility = in.readParcelable(ObservableInt.class.getClassLoader());
        this.retrievalFailureMsgVisibility = in.readParcelable(ObservableInt.class.getClassLoader());
        this.detailsVisibility = in.readParcelable(ObservableInt.class.getClassLoader());
        this.isFavourite = in.readParcelable(ObservableBoolean.class.getClassLoader());
    }

    public static final Creator<MovieDetailViewModel> CREATOR = new Creator<MovieDetailViewModel>() {
        @Override
        public MovieDetailViewModel createFromParcel(Parcel source) {
            return new MovieDetailViewModel(source);
        }

        @Override
        public MovieDetailViewModel[] newArray(int size) {
            return new MovieDetailViewModel[size];
        }
    };
}
