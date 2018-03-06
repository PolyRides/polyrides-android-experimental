package com.polyrides.polyridesv2.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Joelb on 3/3/2018.
 */

public class Ride implements Serializable, Parcelable {

    public String RideName;

    public String Date;

    public String StartLocation;

    public String EndLocation;

    public String Description;

    public int SpotsAvailable;

    public String OfferedBy;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.RideName);
        dest.writeString(this.Date);
        dest.writeString(this.StartLocation);
        dest.writeString(this.EndLocation);
        dest.writeString(this.Description);
        dest.writeInt(this.SpotsAvailable);
        dest.writeString(this.OfferedBy);
    }

    public Ride() {
    }

    protected Ride(Parcel in) {
        this.RideName = in.readString();
        this.Date = in.readString();
        this.StartLocation = in.readString();
        this.EndLocation = in.readString();
        this.Description = in.readString();
        this.SpotsAvailable = in.readInt();
        this.OfferedBy = in.readString();
    }

    public static final Parcelable.Creator<Ride> CREATOR = new Parcelable.Creator<Ride>() {
        @Override
        public Ride createFromParcel(Parcel source) {
            return new Ride(source);
        }

        @Override
        public Ride[] newArray(int size) {
            return new Ride[size];
        }
    };
}
