package com.polyrides.polyridesv2.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.util.List;

@IgnoreExtraProperties
public class RideOffer implements Parcelable {
    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Double getOriginLat() {
        return originLat;
    }

    public void setOriginLat(Double originLat) {
        this.originLat = originLat;
    }

    public Double getOriginLon() {
        return originLon;
    }

    public void setOriginLon(Double originLon) {
        this.originLon = originLon;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(Double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public Double getDestinationLon() {
        return destinationLon;
    }

    public void setDestinationLon(Double destinationLon) {
        this.destinationLon = destinationLon;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public List<String> getRiderIds() {
        return riderIds;
    }

    public void setRiderIds(List<String> riderIds) {
        this.riderIds = riderIds;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRideDescription() {
        return rideDescription;
    }

    public void setRideDescription(String rideDescription) {
        this.rideDescription = rideDescription;
    }

    @PropertyName("origin")
    public String origin;

    @PropertyName("originLat")
    public Double originLat;

    @PropertyName("originLon")
    public Double originLon;

    @PropertyName("destination")
    public String destination;

    @PropertyName("destinationLat")
    public Double destinationLat;

    @PropertyName("destinationLon")
    public Double destinationLon;

    @PropertyName("departureDate")
    public String departureDate;

    @PropertyName("seats")
    public Integer seats;

    @PropertyName("riderIds")
    public List<String> riderIds;

    @PropertyName("driverId")
    public String driverId;

    @PropertyName("cost")
    public Double cost;

    @PropertyName("uid")
    public String uid;

    @PropertyName("rideDescription")
    public String rideDescription;

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
        this.rideDescription = in.readString();

    }

    public RideOffer()
    {

    }


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
        dest.writeString(this.rideDescription);
    }
}
