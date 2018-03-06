package com.polyrides.polyridesv2.web;

import com.polyrides.polyridesv2.models.Ride;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Joelb on 3/3/2018.
 */

public interface RideService {

    @GET("https://mo9se82md9.execute-api.us-west-2.amazonaws.com/testing/RideApp2")
    Call<List<Ride>> listRides();

}
