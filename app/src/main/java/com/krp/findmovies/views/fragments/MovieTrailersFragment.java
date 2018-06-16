package com.krp.findmovies.views.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.krp.findmovies.R;
import com.krp.findmovies.adapters.MoviesAdapter;
import com.krp.findmovies.databinding.FragmentMovieTrailersBinding;
import com.krp.findmovies.viewModels.MovieTrailersViewModel;

public class MovieTrailersFragment extends Fragment {

    private static final String MOVIE_ID = "MovieId";

    FragmentMovieTrailersBinding binding;
    MovieTrailersViewModel viewModel;
    MoviesAdapter adapter;

    public static MovieTrailersFragment newInstance(int movieId) {

        Bundle args = new Bundle();
        args.putInt(MOVIE_ID, movieId);

        MovieTrailersFragment fragment = new MovieTrailersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_trailers, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int movieId = getMovieId();
        if (movieId != -1) {
            viewModel = new MovieTrailersViewModel(getContext(), movieId);
            adapter = new MoviesAdapter();
            viewModel.setAdapter(adapter);
            initializeRv();
        }
    }

    private void initializeRv() {
        binding.movieTrailersRV.setHasFixedSize(true);
        binding.movieTrailersRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.movieTrailersRV.setAdapter(adapter);

    }

    private int getMovieId() {
        return getArguments().getInt(MOVIE_ID, -1);
    }
}
