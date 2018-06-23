package com.krp.findmovies.views.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import com.krp.findmovies.R;
import com.krp.findmovies.adapters.MoviesAdapter;
import com.krp.findmovies.databinding.ActivityDashboardBinding;
import com.krp.findmovies.model.Movie;
import com.krp.findmovies.viewModels.MoviesListViewModel;

import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    ActivityDashboardBinding binding;
    MoviesAdapter adapter;
    MoviesListViewModel viewModel;
    private boolean isShowingFavourites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);

        adapter = new MoviesAdapter();
        viewModel = ViewModelProviders.of(this).get(MoviesListViewModel.class);

        viewModel.setAdapter(adapter);
        viewModel.fetchPopularMovies();

        loadMoviesList();
        binding.setViewModel(viewModel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.popular:
                isShowingFavourites = false;
                setTitle(item.getTitle());
                viewModel.fetchPopularMovies();
                break;

            case R.id.topRated:
                isShowingFavourites = false;
                setTitle(item.getTitle());
                viewModel.fetchTopRatedMovies();
                break;

            case R.id.favourite:
                setTitle(item.getTitle());
                isShowingFavourites = true;
                retrieveFavourite();
                break;
        }
        return true;
    }

    private void retrieveFavourite() {
        viewModel.fetchFavourites();
        viewModel.getFavouriteMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                if (isShowingFavourites) {
                    viewModel.updateList(movies);
                }
            }
        });
    }

    private void loadMoviesList() {
        binding.moviesListRV.setHasFixedSize(true);
        binding.moviesListRV.setLayoutManager(new GridLayoutManager(this, 2));
        binding.moviesListRV.getLayoutManager().setMeasurementCacheEnabled(false);
        binding.moviesListRV.setAdapter(adapter);
    }
}
