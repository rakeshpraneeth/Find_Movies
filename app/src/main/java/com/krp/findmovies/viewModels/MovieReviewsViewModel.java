package com.krp.findmovies.viewModels;

import android.content.Context;
import android.databinding.ObservableInt;
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

public class MovieReviewsViewModel {

    private FmApiService fmApiService;
    private int movieId;
    private Context context;
    private MoviesAdapter adapter;
    private ObservableInt progressbarVisibility;
    private ObservableInt noReviewsTvVisibility;
    private ObservableInt failureTvVisibility;

    public MovieReviewsViewModel(Context context, int movieId){
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

    private void fetchMovieReviews(){

        if(fmApiService == null){
            fmApiService = BaseUrl.getFmApiService();
        }

        Call<Reviews> call = fmApiService.getReviews(movieId,context.getString(R.string.api_key));
        call.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {

                if(response.isSuccessful()){
                    if(response.body().getResults().size() > 0) {
                        updateList(response.body().getResults());
                    }else{
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

    private void updateList(List<Review> reviews){
        List<RowViewModel> list = new ArrayList<>();
        for(Review review: reviews){
            list.add(new ReviewItemViewModel(review));
        }
        if(adapter !=null){
            adapter.setList(list);
        }
    }
}
