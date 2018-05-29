package com.polyrides.polyridesv2;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.polyrides.polyridesv2.dummy.*;
import com.polyrides.polyridesv2.models.Ride;
import com.polyrides.polyridesv2.models.RideOffer;
import com.polyrides.polyridesv2.models.SeatRequest;

import java.util.ArrayList;
import java.util.List;


public class AppMain extends AppCompatActivity implements
        RideOfferFragment.OnListFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        DashFragment.OnFragmentInteractionListener,
        NotificationsFragment.OnListFragmentInteractionListener,
        RideItemFragment.OnFragmentInteractionListener,
        RideRequestFragment.OnListFragmentInteractionListener {

    private List<Ride> rides = new ArrayList<>();
    private TextView mTextMessage;
    private String UserName = "";
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            Class fragmentClass = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    try {
                        fragment = RideOfferFragment.newInstance((ArrayList) rides);
                    } catch (Exception e) {
                        fragment = null;
                    }
                    break;
                case R.id.navigation_dashboard:
                    try {
                        fragment = RideRequestFragment.newInstance(null);
                    } catch (Exception e) {
                        fragment = null;
                    }
                    break;
                case R.id.navigation_notifications:
                    try {
                        fragment = NotificationsFragment.newInstance(1);
                    } catch (Exception e) {
                        fragment = null;
                    }
                    break;
                case R.id.navigation_account:
                    try {
                        fragment = (Fragment) SettingsFragment.newInstance(UserName, "none");
                    } catch (Exception e) {
                        fragment = null;
                    }
                    break;
            }

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, fragment, "RIDE_FRAGMENT").commit();

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_main2);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Fragment fragment = RideOfferFragment.newInstance(null);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment, "RIDE_FRAGMENT").commit();

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://mo9se82md9.execute-api.us-west-2.amazonaws.com")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        RideService rideService = retrofit.create(RideService.class);
//        Call<List<Ride>> rideCall = rideService.listRides();
//        rideCall.enqueue(new Callback<List<Ride>>() {
//            @Override
//            public void onResponse(Call<List<Ride>> call, Response<List<Ride>> response) {
//                rides = response.body();
//                Fragment fragment = RideOfferFragment.newInstance((ArrayList) rides);
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.container, fragment, "RIDE_FRAGMENT").commit();
//            }
//
//            @Override
//            public void onFailure(Call<List<Ride>> call, Throwable t) {
//                rides = new ArrayList<>();
//            }
//        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(RideOffer item) {
        RideOfferFragment frag = (RideOfferFragment) getSupportFragmentManager().findFragmentByTag("RIDE_FRAGMENT");
        if (frag != null && frag.isVisible()) {
            Fragment fragment = RideItemFragment.newInstance(item);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, fragment).addToBackStack("drilldownrides").commit();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onListFragmentInteraction(SeatRequest item) {

    }
}
