package com.krp.findmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Trailers implements Parcelable {

    private int id;
    private List<Trailer> results;

    public int getId() {
        return id;
    }

    public List<Trailer> getResults() {
        return results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeTypedList(this.results);
    }

    public Trailers() {
    }

    protected Trailers(Parcel in) {
        this.id = in.readInt();
        this.results = in.createTypedArrayList(Trailer.CREATOR);
    }

    public static final Parcelable.Creator<Trailers> CREATOR = new Parcelable.Creator<Trailers>() {
        @Override
        public Trailers createFromParcel(Parcel source) {
            return new Trailers(source);
        }

        @Override
        public Trailers[] newArray(int size) {
            return new Trailers[size];
        }
    };
}
