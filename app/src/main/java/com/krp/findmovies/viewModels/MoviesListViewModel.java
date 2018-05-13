package com.krp.findmovies.viewModels;

import android.content.Context;
import android.databinding.ObservableInt;
import android.view.View;

import com.krp.findmovies.R;
import com.krp.findmovies.adapters.MoviesAdapter;
import com.krp.findmovies.common.BaseUrl;
import com.krp.findmovies.interfaces.FmApiService;
import com.krp.findmovies.model.Movie;
import com.krp.findmovies.model.MoviesResponse;
import com.krp.findmovies.utilities.NetworkHandler;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesListViewModel {

    private Context context;
    private FmApiService fmApiService;

    private ObservableInt noInternetVisibility;
    private ObservableInt progressbarVisibility;
    private ObservableInt recyclerViewVisibility;
    private ObservableInt retrievalFailureMsgVisibility;

    MoviesAdapter adapter;

    private String path;

    public MoviesListViewModel(Context context){
        this.context = context;
        progressbarVisibility = new ObservableInt(View.VISIBLE);
        noInternetVisibility = new ObservableInt(View.GONE);
        recyclerViewVisibility = new ObservableInt(View.GONE);
        retrievalFailureMsgVisibility = new ObservableInt(View.GONE);
    }

    public ObservableInt getProgressbarVisibility() {
        return progressbarVisibility;
    }

    public ObservableInt getNoInternetVisibility() {
        return noInternetVisibility;
    }

    public ObservableInt getRecyclerViewVisibility() {
        return recyclerViewVisibility;
    }

    public ObservableInt getRetrievalFailureMsgVisibility() {
        return retrievalFailureMsgVisibility;
    }

    public void setAdapter(MoviesAdapter adapter) {
        this.adapter = adapter;
    }

    public void fetchData(String path) {
        retrievalFailureMsgVisibility.set(View.GONE);
        if (NetworkHandler.isNetworkAvailable(context)) {
            this.path = path;
            noInternetVisibility.set(View.GONE);
            progressbarVisibility.set(View.VISIBLE);
            makeServiceCall();
        } else {
            noInternetVisibility.set(View.VISIBLE);
            progressbarVisibility.set(View.GONE);
            recyclerViewVisibility.set(View.GONE);

        }
    }

    private void makeServiceCall(){
        if(fmApiService == null){
            fmApiService = BaseUrl.getFmApiService();
        }

        Call<MoviesResponse> call = fmApiService.getMoviesList(path, context.getString(R.string.api_key));

        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                if(response.isSuccessful()){

                    updateList(response.body());
                }
                progressbarVisibility.set(View.GONE);
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {

                recyclerViewVisibility.set(View.GONE);
                retrievalFailureMsgVisibility.set(View.VISIBLE);
                progressbarVisibility.set(View.GONE);
            }
        });
    }

    private void updateList(MoviesResponse moviesResponse){

        List<RowViewModel> list = new ArrayList<>();
        for(Movie movie: moviesResponse.getResults()){
            list.add(new MovieItemViewModel(movie));
        }
        adapter.setList(list);
        recyclerViewVisibility.set(View.VISIBLE);
    }
}
