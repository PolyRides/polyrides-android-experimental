package com.polyrides.polyridesv2.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class RideOffer implements Parcelable {
    public String origin;
    public Double originLat;
    public Double originLon;
    public String destination;
    public Double destinationLat;
    public Double destinationLon;
    public String departureDate;
    public Integer seats;
    public List<String> riderIds;
    public String driverId;
    public Double cost;
    public String uid;
    public String description;

    protected RideOffer(Parcel in) {
        this.origin = in.readString();
        this.originLat = in.readDouble();
        this.originLon = in.readDouble();
        this.destination = in.readString();
        this.destinationLat = in.readDouble();
        this.destinationLon = in.readDouble();
        this.departureDate = in.readString();
        this.seats = in.readInt();
        in.readStringList(this.riderIds);
        this.driverId = in.readString();
        this.cost = in.readDouble();
        this.uid = in.readString();
        this.description = in.readString();

    }

    public RideOffer()
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
        dest.writeString(this.origin);
        dest.writeDouble(this.originLat);
        dest.writeDouble(this.originLon);
        dest.writeString(this.destination);
        dest.writeDouble(this.destinationLat);
        dest.writeDouble(this.destinationLon);
        dest.writeString(this.departureDate);
        dest.writeInt(this.seats);
        dest.writeStringList(this.riderIds);
        dest.writeString(this.driverId);
        dest.writeDouble(this.cost);
        dest.writeString(this.uid);
        dest.writeString(this.description);
    }
}
