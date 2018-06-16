package com.krp.findmovies.viewModels;

import android.os.Parcel;

import com.krp.findmovies.R;
import com.krp.findmovies.model.Review;

public class ReviewItemViewModel extends RowViewModel{

    private Review review;

    public ReviewItemViewModel(Review review){
        this.review = review;
    }

    public Review getReview() {
        return review;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.review, flags);
    }

    protected ReviewItemViewModel(Parcel in) {
        this.review = in.readParcelable(Review.class.getClassLoader());
    }

    public static final Creator<ReviewItemViewModel> CREATOR = new Creator<ReviewItemViewModel>() {
        @Override
        public ReviewItemViewModel createFromParcel(Parcel source) {
            return new ReviewItemViewModel(source);
        }

        @Override
        public ReviewItemViewModel[] newArray(int size) {
            return new ReviewItemViewModel[size];
        }
    };

    @Override
    public int getLayout() {
        return R.layout.review_item;
    }
}
