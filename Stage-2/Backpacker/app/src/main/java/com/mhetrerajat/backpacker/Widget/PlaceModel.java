package com.mhetrerajat.backpacker.Widget;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rajatmhetre on 05/07/16.
 */
public class PlaceModel implements Parcelable{

    String name, vicinity;

    public PlaceModel() {
    }

    protected PlaceModel(Parcel in) {
        name = in.readString();
        vicinity = in.readString();
    }

    public static final Creator<PlaceModel> CREATOR = new Creator<PlaceModel>() {
        @Override
        public PlaceModel createFromParcel(Parcel in) {
            return new PlaceModel(in);
        }

        @Override
        public PlaceModel[] newArray(int size) {
            return new PlaceModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(vicinity);
    }
}
