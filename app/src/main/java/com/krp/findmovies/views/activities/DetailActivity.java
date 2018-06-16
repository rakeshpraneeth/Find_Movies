package com.krp.findmovies.views.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.krp.findmovies.R;
import com.krp.findmovies.databinding.ActivityDetailBinding;
import com.krp.findmovies.viewModels.MovieDetailViewModel;
import com.krp.findmovies.views.fragments.MovieTrailersFragment;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding binding;
    public static final String MOVIE_ID = "movieId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        int movieId = getIntent().getIntExtra(MOVIE_ID, -1);
        if (movieId != -1) {
            MovieDetailViewModel viewModel = new MovieDetailViewModel(this);
            viewModel.fetchData(movieId);
            binding.setViewModel(viewModel);

            loadFragment(R.id.movieTrailersFL, MovieTrailersFragment.newInstance(movieId));
        } else {
            Toast.makeText(this, getString(R.string.need_a_movie_id), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFragment(int containerId, Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(containerId, fragment)
                .commit();
    }
}
