package com.krp.findmovies.viewModels;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.View;
import android.widget.ImageView;

import com.krp.findmovies.R;
import com.krp.findmovies.common.BaseUrl;
import com.krp.findmovies.interfaces.FmApiService;
import com.krp.findmovies.model.MovieDetail;
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

    private ObservableField<MovieDetail> movieDetail;
    private ObservableInt progressbarVisibility;
    private ObservableInt noInternetVisibility;
    private ObservableInt retrievalFailureMsgVisibility;
    private ObservableInt detailsVisibility;

    private Context context;
    private FmApiService fmApiService;

    public MovieDetailViewModel(Context context) {
        this.context = context;

        movieDetail = new ObservableField<>();
        progressbarVisibility = new ObservableInt(View.VISIBLE);
        noInternetVisibility = new ObservableInt(View.GONE);
        retrievalFailureMsgVisibility = new ObservableInt(View.GONE);
        detailsVisibility = new ObservableInt(View.GONE);
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

    public ObservableField<MovieDetail> getMovieDetail() {
        return movieDetail;
    }

    public ObservableInt getDetailsVisibility() {
        return detailsVisibility;
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

        Call<MovieDetail> call = fmApiService.getMovieDetail(movieId, context.getString(R.string.api_key));

        call.enqueue(new Callback<MovieDetail>() {
            @Override
            public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {

                if (response.isSuccessful()) {
                    movieDetail.set(response.body());
                    detailsVisibility.set(View.VISIBLE);
                    FragmentUtils.replaceFragment(context, R.id.movieTrailersFL, MovieTrailersFragment.newInstance(movieId));
                    FragmentUtils.replaceFragment(context, R.id.movieReviewsFL, MovieReviewsFragment.newInstance(movieId));
                }
                progressbarVisibility.set(View.GONE);
            }

            @Override
            public void onFailure(Call<MovieDetail> call, Throwable t) {

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
}
