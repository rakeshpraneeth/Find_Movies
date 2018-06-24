package com.krp.findmovies.views.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.krp.findmovies.R;
import com.krp.findmovies.adapters.MoviesAdapter;
import com.krp.findmovies.databinding.FragmentMovieReviewsBinding;
import com.krp.findmovies.viewModels.MovieReviewsViewModel;

public class MovieReviewsFragment extends Fragment {

    private static final String MOVIE_ID = "MovieId";
    private static final String VIEW_MODEL = "viewModel";

    FragmentMovieReviewsBinding binding;
    MoviesAdapter adapter;
    MovieReviewsViewModel viewModel;

    public static MovieReviewsFragment newInstance(int movieId) {

        Bundle args = new Bundle();
        args.putInt(MOVIE_ID, movieId);
        MovieReviewsFragment fragment = new MovieReviewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_reviews, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new MoviesAdapter();
        if (savedInstanceState == null) {
            int movieId = getMovieId();
            if (movieId != -1) {
                viewModel = new MovieReviewsViewModel(getContext(), movieId);
            }
        } else {
            viewModel = savedInstanceState.getParcelable(VIEW_MODEL);
            adapter.setList(viewModel.getReviewsAdapterList());
        }
        viewModel.setAdapter(adapter);
        initializeRv();
        binding.setViewModel(viewModel);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(VIEW_MODEL, viewModel);
    }

    private void initializeRv() {
        binding.movieReviewsRV.setHasFixedSize(true);
        binding.movieReviewsRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        binding.movieReviewsRV.addItemDecoration(dividerItemDecoration);
        binding.movieReviewsRV.setNestedScrollingEnabled(true);
        binding.movieReviewsRV.setAdapter(adapter);
    }

    private int getMovieId() {
        return getArguments().getInt(MOVIE_ID, -1);
    }
}
