package com.krp.findmovies.views.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import com.krp.findmovies.R;
import com.krp.findmovies.adapters.MoviesAdapter;
import com.krp.findmovies.databinding.ActivityDashboardBinding;
import com.krp.findmovies.viewModels.MoviesListViewModel;

public class DashboardActivity extends AppCompatActivity {

    ActivityDashboardBinding binding;
    MoviesAdapter adapter;
    MoviesListViewModel viewModel;

    private static final String POPULAR_MOVIES = "movie/popular";
    private static final String TOP_RATED_MOVIES = "movie/top_rated";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);

        adapter = new MoviesAdapter();
        viewModel = new MoviesListViewModel(this);
        viewModel.setAdapter(adapter);
        viewModel.fetchData(POPULAR_MOVIES);
        setTitle(getString(R.string.popular));

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
                setTitle(item.getTitle());
                if (viewModel != null) {
                    viewModel.fetchData(POPULAR_MOVIES);
                }
                break;

            case R.id.topRated:
                setTitle(item.getTitle());
                if (viewModel != null) {
                    viewModel.fetchData(TOP_RATED_MOVIES);
                }
                break;

        }
        return true;
    }

    private void loadMoviesList() {
        binding.moviesListRV.setHasFixedSize(true);
        binding.moviesListRV.setLayoutManager(new GridLayoutManager(this, 2));
        binding.moviesListRV.getLayoutManager().setMeasurementCacheEnabled(false);
        binding.moviesListRV.setAdapter(adapter);
    }
}