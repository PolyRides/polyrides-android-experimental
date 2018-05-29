package com.polyrides.polyridesv2;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polyrides.polyridesv2.addrides_flow.offers.AddRides_1;
import com.polyrides.polyridesv2.addrides_flow.offers.AddRides_2;
import com.polyrides.polyridesv2.addrides_flow.offers.AddRides_3;
import com.polyrides.polyridesv2.addrides_flow.offers.AddRides_4;
import com.polyrides.polyridesv2.addrides_flow.requests.AddRideRequest_1;
import com.polyrides.polyridesv2.addrides_flow.requests.AddRideRequest_2;
import com.polyrides.polyridesv2.addrides_flow.requests.AddRideRequest_3;
import com.polyrides.polyridesv2.addrides_flow.requests.AddRideRequest_4;

import java.util.HashMap;
import java.util.Map;

public class AddRideRequestActivity extends AppCompatActivity implements AddRideRequest_1.OnFragmentInteractionListener,
        AddRideRequest_2.OnFragmentInteractionListener,
        AddRideRequest_3.OnFragmentInteractionListener,
        AddRideRequest_4.OnFragmentInteractionListener {

    private TextView mTextMessage;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ride2);

        this.setTitle("Add a Ride Request");
        mTextMessage = (TextView) findViewById(R.id.message);

        Fragment f = AddRideRequest_1.newInstance("","");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, f);
        transaction.commit();

        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public void SendOfferToFirebase() {
        String key = mDatabase.child("rideRequests").push().getKey();

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("origin", origin);
        data.put("originLat", originLat);
        data.put("originLon", originLon);
        data.put("destination", destination);
        data.put("destinationLat", destinationLat);
        data.put("destinationLon", destinationLon);
        data.put("departureDate", departureDate);
        data.put("seats", seats);
        data.put("driverId", driverId);
        data.put("cost", cost);
        data.put("uid", key);
        data.put("description", description);

        Map<String,Object> endpoints = new HashMap<String, Object>();
        endpoints.put("/rideRequests/" + key, data);

        mDatabase.updateChildren(endpoints);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private String origin;
    private Double originLat;
    private Double originLon;
    private String destination;
    private Double destinationLat;
    private Double destinationLon;
    private String departureDate;
    private Integer seats;
    private String driverId;
    private Double cost;
    private String uid;
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
