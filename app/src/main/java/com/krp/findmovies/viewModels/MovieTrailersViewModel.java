package com.krp.findmovies.viewModels;

import android.content.Context;
import android.databinding.ObservableInt;
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

public class MovieTrailersViewModel {

    private int movieId;
    private FmApiService fmApiService;
    private Context context;
    private MoviesAdapter adapter;
    private ObservableInt progressbarVisibility;
    private ObservableInt noTrailersTvVisibility;

    public MovieTrailersViewModel(Context context, int movieId) {
        this.movieId = movieId;
        this.context = context;
        progressbarVisibility = new ObservableInt(View.VISIBLE);
        noTrailersTvVisibility = new ObservableInt(View.GONE);
        fetchMovieTrailers();
    }

    public ObservableInt getProgressbarVisibility() {
        return progressbarVisibility;
    }

    public ObservableInt getNoTrailersTvVisibility() {
        return noTrailersTvVisibility;
    }

    public void setAdapter(MoviesAdapter adapter) {
        this.adapter = adapter;
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

                    if (response.body().getResults().size() > 0) {
                        updateList(response.body().getResults());
                    } else {
                        noTrailersTvVisibility.set(View.VISIBLE);
                    }
                }
                progressbarVisibility.set(View.GONE);
            }

            @Override
            public void onFailure(Call<Trailers> call, Throwable t) {
                progressbarVisibility.set(View.GONE);
            }
        });
    }

    private void updateList(List<Trailer> trailers) {
        List<RowViewModel> list = new ArrayList<>();
        for (Trailer trailer : trailers) {
            list.add(new TrailerItemViewModel(trailer));
        }

        if (adapter != null) {
            adapter.setList(list);
        }
    }
}
