package com.polyrides.polyridesv2.models;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
@SuppressLint("ParcelCreator")
public class RideRequest implements Parcelable {
    public String uid;
    public String destination;
    public Double destinationLat;
    public Double destinationLon;
    public String origin;
    public Double originLat;
    public Double originLon;
    public String departureDate;
    public String riderId;
    public String rideDescription;
    public String driverId;

    protected RideRequest(Parcel in) {
        this.uid = in.readString();
        this.destination = in.readString();
        this.destinationLat = in.readDouble();
        this.destinationLon = in.readDouble();
        this.origin = in.readString();
        this.originLat = in.readDouble();
        this.originLon = in.readDouble();
        this.departureDate = in.readString();
        this.riderId = in.readString();
        this.rideDescription = in.readString();
        this.driverId = in.readString();

    }

    public RideRequest()
    {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.destination);
        dest.writeDouble(this.destinationLat);
        dest.writeDouble(this.destinationLon);
        dest.writeString(this.origin);
        dest.writeDouble(this.originLat);
        dest.writeDouble(this.originLon);
        dest.writeString(this.departureDate);
        dest.writeString(this.riderId);
        dest.writeString(this.rideDescription);
        dest.writeString(this.driverId);
    }
}
