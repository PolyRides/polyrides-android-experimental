package com.polyrides.polyridesv2.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
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
    public String description;
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
        this.description = in.readString();
        this.driverId = in.readString();

    }

    public RideRequest()
    {

    }

    public static final Parcelable.Creator<RideOffer> CREATOR = new Parcelable.Creator<RideOffer>() {
        @Override
        public RideOffer createFromParcel(Parcel source) {
            return new RideOffer(source);
        }

        @Override
        public RideOffer[] newArray(int size) {
            return new RideOffer[size];
        }
    };

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
        dest.writeString(this.description);
        dest.writeString(this.driverId);
    }
}
