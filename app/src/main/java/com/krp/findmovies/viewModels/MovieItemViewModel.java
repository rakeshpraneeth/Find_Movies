package com.krp.findmovies.viewModels;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.view.View;
import android.widget.ImageView;

import com.krp.findmovies.R;
import com.krp.findmovies.model.Movie;
import com.krp.findmovies.views.activities.DetailActivity;
import com.squareup.picasso.Picasso;

public class MovieItemViewModel extends RowViewModel {

    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w185/";

    private Movie movie;

    public MovieItemViewModel(Movie movie){
        this.movie = movie;
    }

    public Movie getMovie() {
        return movie;
    }

    @Override
    public int getLayout() {
        return R.layout.movie_custom_item;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.movie, flags);
    }

    protected MovieItemViewModel(Parcel in) {
        this.movie = in.readParcelable(Movie.class.getClassLoader());
    }

    public static final Creator<MovieItemViewModel> CREATOR = new Creator<MovieItemViewModel>() {
        @Override
        public MovieItemViewModel createFromParcel(Parcel source) {
            return new MovieItemViewModel(source);
        }

        @Override
        public MovieItemViewModel[] newArray(int size) {
            return new MovieItemViewModel[size];
        }
    };

    @BindingAdapter("setImage")
    public static void setImage(final ImageView imageView, String path){
        if(imageView != null){
            Picasso.with(imageView.getContext())
                    .load(IMAGE_BASE_URL+path)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);

        }
    }

    public void onMovieItemClick(View view){

        if(view !=null){
            Intent intent = new Intent(view.getContext(), DetailActivity.class);
            intent.putExtra(DetailActivity.MOVIE,movie);
            view.getContext().startActivity(intent);
        }
    }
}
