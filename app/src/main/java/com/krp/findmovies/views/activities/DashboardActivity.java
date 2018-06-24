package com.krp.findmovies.views.activities;

import android.arch.lifecycle.Observer;
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

    private static final String VIEW_MODEL = "viewModel";
    private static final String SHOWING_TYPE = "showingType";
    ActivityDashboardBinding binding;
    MoviesAdapter adapter;
    MoviesListViewModel viewModel;
    private boolean isShowingFavourites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);

        adapter = new MoviesAdapter();
        if (savedInstanceState == null) {
            viewModel = new MoviesListViewModel(this);
            viewModel.fetchPopularMovies();
        } else {
            isShowingFavourites = savedInstanceState.getBoolean(SHOWING_TYPE);
            viewModel = savedInstanceState.getParcelable(VIEW_MODEL);
            if (viewModel.getDashboardAdapterList().size() > 0) {
                adapter.setList(viewModel.getDashboardAdapterList());
            }
        }
        viewModel.setAdapter(adapter);
        loadMoviesList();
        retrieveFavourite();
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
                viewModel.showFavourites();
                break;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(VIEW_MODEL, viewModel);
        outState.putBoolean(SHOWING_TYPE, isShowingFavourites);
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
