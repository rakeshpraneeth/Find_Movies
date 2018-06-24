package com.krp.findmovies.views.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.krp.findmovies.R;
import com.krp.findmovies.databinding.ActivityDetailBinding;
import com.krp.findmovies.model.Movie;
import com.krp.findmovies.viewModels.MovieDetailViewModel;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding binding;
    public static final String MOVIE = "movie";
    public static final String VIEW_MODEL = "viewModel";
    private MovieDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        if (savedInstanceState == null) {
            Movie movie = getIntent().getParcelableExtra(MOVIE);
            if (movie != null) {
                if (viewModel == null) {
                    viewModel = new MovieDetailViewModel(this, movie.getId());
                }
            }
        } else {
            viewModel = savedInstanceState.getParcelable(VIEW_MODEL);
        }
        binding.setViewModel(viewModel);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(VIEW_MODEL, viewModel);
    }
}
