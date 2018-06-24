package com.krp.findmovies.viewModels;

import android.content.Context;
import android.databinding.ObservableInt;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.krp.findmovies.R;
import com.krp.findmovies.adapters.MoviesAdapter;
import com.krp.findmovies.common.BaseUrl;
import com.krp.findmovies.interfaces.FmApiService;
import com.krp.findmovies.model.Review;
import com.krp.findmovies.model.Reviews;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieReviewsViewModel implements Parcelable {

    private FmApiService fmApiService;
    private int movieId;
    private Context context;
    private MoviesAdapter adapter;
    private ObservableInt progressbarVisibility;
    private ObservableInt noReviewsTvVisibility;
    private ObservableInt failureTvVisibility;

    private Reviews reviews;
    private List<RowViewModel> reviewsAdapterList;

    public MovieReviewsViewModel(Context context, int movieId) {
        this.context = context;
        this.movieId = movieId;
        progressbarVisibility = new ObservableInt(View.VISIBLE);
        noReviewsTvVisibility = new ObservableInt(View.GONE);
        failureTvVisibility = new ObservableInt(View.GONE);
        fetchMovieReviews();
    }

    public ObservableInt getProgressbarVisibility() {
        return progressbarVisibility;
    }

    public ObservableInt getNoReviewsTvVisibility() {
        return noReviewsTvVisibility;
    }

    public ObservableInt getFailureTvVisibility() {
        return failureTvVisibility;
    }

    public void setAdapter(MoviesAdapter adapter) {
        this.adapter = adapter;
    }

    public List<RowViewModel> getReviewsAdapterList() {
        return reviewsAdapterList;
    }

    private void fetchMovieReviews() {

        if (fmApiService == null) {
            fmApiService = BaseUrl.getFmApiService();
        }

        Call<Reviews> call = fmApiService.getReviews(movieId, context.getString(R.string.api_key));
        call.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {

                if (response.isSuccessful()) {
                    reviews = response.body();
                    if (reviews.getResults().size() > 0) {
                        updateList(reviews.getResults());
                    } else {
                        noReviewsTvVisibility.set(View.VISIBLE);
                    }
                }
                progressbarVisibility.set(View.GONE);
            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {
                failureTvVisibility.set(View.VISIBLE);
                progressbarVisibility.set(View.GONE);
            }
        });
    }

    private void updateList(List<Review> reviews) {
        reviewsAdapterList = new ArrayList<>();
        for (Review review : reviews) {
            reviewsAdapterList.add(new ReviewItemViewModel(review));
        }
        if (adapter != null) {
            adapter.setList(reviewsAdapterList);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.movieId);
        dest.writeParcelable(this.progressbarVisibility, flags);
        dest.writeParcelable(this.noReviewsTvVisibility, flags);
        dest.writeParcelable(this.failureTvVisibility, flags);
    }

    protected MovieReviewsViewModel(Parcel in) {
        this.movieId = in.readInt();
        this.progressbarVisibility = in.readParcelable(ObservableInt.class.getClassLoader());
        this.noReviewsTvVisibility = in.readParcelable(ObservableInt.class.getClassLoader());
        this.failureTvVisibility = in.readParcelable(ObservableInt.class.getClassLoader());
    }

    public static final Parcelable.Creator<MovieReviewsViewModel> CREATOR = new Parcelable.Creator<MovieReviewsViewModel>() {
        @Override
        public MovieReviewsViewModel createFromParcel(Parcel source) {
            return new MovieReviewsViewModel(source);
        }

        @Override
        public MovieReviewsViewModel[] newArray(int size) {
            return new MovieReviewsViewModel[size];
        }
    };
}
