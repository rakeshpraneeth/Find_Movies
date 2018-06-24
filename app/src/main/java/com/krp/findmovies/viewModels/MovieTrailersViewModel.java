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
import com.krp.findmovies.model.Trailer;
import com.krp.findmovies.model.Trailers;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieTrailersViewModel implements Parcelable {

    private int movieId;
    private FmApiService fmApiService;
    private Context context;
    private MoviesAdapter adapter;
    private ObservableInt progressbarVisibility;
    private ObservableInt noTrailersTvVisibility;
    private ObservableInt failureTvVisibility;

    private Trailers trailers;
    List<RowViewModel> trailersAdapterList;

    public MovieTrailersViewModel(Context context, int movieId) {
        this.movieId = movieId;
        this.context = context;
        progressbarVisibility = new ObservableInt(View.VISIBLE);
        noTrailersTvVisibility = new ObservableInt(View.GONE);
        failureTvVisibility = new ObservableInt(View.GONE);
        fetchMovieTrailers();
    }

    public ObservableInt getProgressbarVisibility() {
        return progressbarVisibility;
    }

    public ObservableInt getNoTrailersTvVisibility() {
        return noTrailersTvVisibility;
    }

    public ObservableInt getFailureTvVisibility() {
        return failureTvVisibility;
    }

    public void setAdapter(MoviesAdapter adapter) {
        this.adapter = adapter;
    }

    public List<RowViewModel> getTrailersAdapterList() {
        return trailersAdapterList;
    }

    private void fetchMovieTrailers() {

        if (fmApiService == null) {
            fmApiService = BaseUrl.getFmApiService();
        }

        Call<Trailers> call = fmApiService.getTrailers(movieId, context.getString(R.string.api_key));
        call.enqueue(new Callback<Trailers>() {
            @Override
            public void onResponse(Call<Trailers> call, Response<Trailers> response) {
                if (response.isSuccessful()) {
                    trailers = response.body();
                    if (trailers.getResults().size() > 0) {
                        updateList(trailers.getResults());
                    } else {
                        noTrailersTvVisibility.set(View.VISIBLE);
                    }
                }
                progressbarVisibility.set(View.GONE);
            }

            @Override
            public void onFailure(Call<Trailers> call, Throwable t) {
                failureTvVisibility.set(View.VISIBLE);
                progressbarVisibility.set(View.GONE);
            }
        });
    }

    public void updateList(List<Trailer> trailers) {
         trailersAdapterList = new ArrayList<>();
        for (Trailer trailer : trailers) {
            trailersAdapterList.add(new TrailerItemViewModel(trailer));
        }

        if (adapter != null) {
            adapter.setList(trailersAdapterList);
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
        dest.writeParcelable(this.noTrailersTvVisibility, flags);
        dest.writeParcelable(this.failureTvVisibility, flags);
        dest.writeParcelable(this.trailers, flags);
    }

    protected MovieTrailersViewModel(Parcel in) {
        this.movieId = in.readInt();
        this.fmApiService = in.readParcelable(FmApiService.class.getClassLoader());
        this.context = in.readParcelable(Context.class.getClassLoader());
        this.adapter = in.readParcelable(MoviesAdapter.class.getClassLoader());
        this.progressbarVisibility = in.readParcelable(ObservableInt.class.getClassLoader());
        this.noTrailersTvVisibility = in.readParcelable(ObservableInt.class.getClassLoader());
        this.failureTvVisibility = in.readParcelable(ObservableInt.class.getClassLoader());
        this.trailers = in.readParcelable(Trailers.class.getClassLoader());
    }

    public static final Creator<MovieTrailersViewModel> CREATOR = new Creator<MovieTrailersViewModel>() {
        @Override
        public MovieTrailersViewModel createFromParcel(Parcel source) {
            return new MovieTrailersViewModel(source);
        }

        @Override
        public MovieTrailersViewModel[] newArray(int size) {
            return new MovieTrailersViewModel[size];
        }
    };
}
