package com.krp.findmovies.viewModels;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.databinding.ObservableInt;
import android.os.Parcel;
import android.os.Parcelable;
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

public class MoviesListViewModel implements Parcelable {

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
    private Context context;

    private List<RowViewModel> dashboardAdapterList;

    public MoviesListViewModel(Context context){

        this.context = context;
        mAppDatabase = AppDatabase.getInstance(context);
        dashboardAdapterList = new ArrayList<>();

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

    public List<RowViewModel> getDashboardAdapterList() {
        return dashboardAdapterList;
    }

    private void fetchData(String path) {
        retrievalFailureMsgVisibility.set(View.GONE);

        if (NetworkHandler.isNetworkAvailable(context)) {
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

        Call<MoviesResponse> call = fmApiService.getMoviesList(path, context.getString(R.string.api_key));

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
    }

    public void showFavourites(){
        updateList(favouriteMovies.getValue());
        retrievalFailureMsgVisibility.set(View.GONE);
        progressbarVisibility.set(View.GONE);
        noInternetVisibility.set(View.GONE);
    }

    public void updateList(List<Movie> movieList){

        if(dashboardAdapterList !=null){
            dashboardAdapterList.clear();
        }
        for(Movie movie: movieList){
            dashboardAdapterList.add(new MovieItemViewModel(movie));
        }
        adapter.setList(dashboardAdapterList);
        recyclerViewVisibility.set(View.VISIBLE);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.noInternetVisibility, flags);
        dest.writeParcelable(this.progressbarVisibility, flags);
        dest.writeParcelable(this.recyclerViewVisibility, flags);
        dest.writeParcelable(this.retrievalFailureMsgVisibility, flags);
    }

    protected MoviesListViewModel(Parcel in) {
        this.noInternetVisibility = in.readParcelable(ObservableInt.class.getClassLoader());
        this.progressbarVisibility = in.readParcelable(ObservableInt.class.getClassLoader());
        this.recyclerViewVisibility = in.readParcelable(ObservableInt.class.getClassLoader());
        this.retrievalFailureMsgVisibility = in.readParcelable(ObservableInt.class.getClassLoader());
    }

    public static final Parcelable.Creator<MoviesListViewModel> CREATOR = new Parcelable.Creator<MoviesListViewModel>() {
        @Override
        public MoviesListViewModel createFromParcel(Parcel source) {
            return new MoviesListViewModel(source);
        }

        @Override
        public MoviesListViewModel[] newArray(int size) {
            return new MoviesListViewModel[size];
        }
    };
}
