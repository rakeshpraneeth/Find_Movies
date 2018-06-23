package com.krp.findmovies.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.view.View;

import com.krp.findmovies.R;
import com.krp.findmovies.adapters.MoviesAdapter;
import com.krp.findmovies.common.BaseUrl;
import com.krp.findmovies.database.AppDatabase;
import com.krp.findmovies.interfaces.FmApiService;
import com.krp.findmovies.model.Movie;
import com.krp.findmovies.model.MoviesResponse;
import com.krp.findmovies.utilities.NetworkHandler;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesListViewModel extends AndroidViewModel{

    private static final String POPULAR_MOVIES = "movie/popular";
    private static final String TOP_RATED_MOVIES = "movie/top_rated";

    private FmApiService fmApiService;

    private ObservableInt noInternetVisibility;
    private ObservableInt progressbarVisibility;
    private ObservableInt recyclerViewVisibility;
    private ObservableInt retrievalFailureMsgVisibility;

    MoviesAdapter adapter;

    private AppDatabase mAppDatabase;
    private LiveData<List<Movie>> favouriteMovies;
    private Application application;

    public MoviesListViewModel(@NonNull Application application){
        super(application);

        this.application = application;
        mAppDatabase = AppDatabase.getInstance(application);

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

    public LiveData<List<Movie>> getFavouriteMovies() {
        return favouriteMovies;
    }

    public void setAdapter(MoviesAdapter adapter) {
        this.adapter = adapter;
    }

    private void fetchData(String path) {
        retrievalFailureMsgVisibility.set(View.GONE);

        if (NetworkHandler.isNetworkAvailable(application)) {
            noInternetVisibility.set(View.GONE);
            progressbarVisibility.set(View.VISIBLE);
            makeServiceCall(path);
        } else {
            noInternetVisibility.set(View.VISIBLE);
            progressbarVisibility.set(View.GONE);
            recyclerViewVisibility.set(View.GONE);

        }
    }

    private void makeServiceCall(String path){
        if(fmApiService == null){
            fmApiService = BaseUrl.getFmApiService();
        }

        Call<MoviesResponse> call = fmApiService.getMoviesList(path, application.getString(R.string.api_key));

        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                if(response.isSuccessful()){

                    updateList(response.body().getResults());
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

    public void fetchPopularMovies(){
        fetchData(POPULAR_MOVIES);
    }

    public void fetchTopRatedMovies(){
        fetchData(TOP_RATED_MOVIES);
    }

    public void fetchFavourites(){

        favouriteMovies = mAppDatabase.findMoviesDao().getMovies();

        retrievalFailureMsgVisibility.set(View.GONE);
        progressbarVisibility.set(View.GONE);
        noInternetVisibility.set(View.GONE);
    }

    public void updateList(List<Movie> movieList){

        List<RowViewModel> list = new ArrayList<>();
        for(Movie movie: movieList){
            list.add(new MovieItemViewModel(movie));
        }
        adapter.setList(list);
        recyclerViewVisibility.set(View.VISIBLE);
    }
}
