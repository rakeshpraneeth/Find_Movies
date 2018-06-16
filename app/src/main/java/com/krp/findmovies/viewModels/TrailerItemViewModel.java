package com.krp.findmovies.viewModels;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.net.Uri;
import android.os.Parcel;
import android.view.View;
import android.widget.ImageView;

import com.krp.findmovies.R;
import com.krp.findmovies.model.Trailer;
import com.squareup.picasso.Picasso;

public class TrailerItemViewModel extends RowViewModel {

    private Trailer trailer;
    private static final String TRAILER_IMAGE_BASE_URL = "https://img.youtube.com/vi/";
    private static final String TRAILER_IMAGE_TYPE = "/hqdefault.jpg";
    private static final String YOUTUBE_URL = "http://www.youtube.com/watch?v=";

    public TrailerItemViewModel(Trailer trailer) {
        this.trailer = trailer;
    }

    public Trailer getTrailer() {
        return trailer;
    }

    @Override
    public int getLayout() {
        return R.layout.trailer_item;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.trailer, flags);
    }

    protected TrailerItemViewModel(Parcel in) {
        this.trailer = in.readParcelable(Trailer.class.getClassLoader());
    }

    public static final Creator<TrailerItemViewModel> CREATOR = new Creator<TrailerItemViewModel>() {
        @Override
        public TrailerItemViewModel createFromParcel(Parcel source) {
            return new TrailerItemViewModel(source);
        }

        @Override
        public TrailerItemViewModel[] newArray(int size) {
            return new TrailerItemViewModel[size];
        }
    };

    @BindingAdapter("setTrailerImage")
    public static void setTrailerImage(ImageView imageView, String key) {
        if (imageView != null && key != null && !key.isEmpty()) {
            String trailerPath = TRAILER_IMAGE_BASE_URL + key + TRAILER_IMAGE_TYPE;
            Picasso.with(imageView.getContext())
                    .load(trailerPath)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
        }
    }

    public void onTrailerItemClick(View view, String key) {
        if (view !=null && key != null && !key.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(YOUTUBE_URL + key));
            view.getContext().startActivity(intent);

        }
    }
}
