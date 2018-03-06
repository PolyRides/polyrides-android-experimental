package com.polyrides.polyridesv2;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.polyrides.polyridesv2.dummy.*;
import com.polyrides.polyridesv2.dummy.DummyContent;
import com.polyrides.polyridesv2.models.Ride;
import com.polyrides.polyridesv2.web.RideService;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppMain extends AppCompatActivity implements
        RideFragment.OnListFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        DashFragment.OnFragmentInteractionListener,
        NotificationsFragment.OnListFragmentInteractionListener,
        RideItemFragment.OnFragmentInteractionListener {

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
                        fragment = RideFragment.newInstance((ArrayList) rides);
                    } catch (Exception e) {
                        fragment = null;
                    }
                    break;
                case R.id.navigation_dashboard:
                    try {
                        fragment = DashFragment.newInstance("tt", "tt");
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

        AccessToken accessToken =  AccessToken.getCurrentAccessToken();

        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                             UserName = object.getString("name");
                        }
                        catch (Exception e){
                            Log.e("stack trace", e.getStackTrace().toString());
                        }

                    }
                });
        request.executeAsync();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://mo9se82md9.execute-api.us-west-2.amazonaws.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RideService rideService = retrofit.create(RideService.class);
        Call<List<Ride>> rideCall = rideService.listRides();
        rideCall.enqueue(new Callback<List<Ride>>() {
            @Override
            public void onResponse(Call<List<Ride>> call, Response<List<Ride>> response) {
                rides = response.body();
                Fragment fragment = RideFragment.newInstance((ArrayList) rides);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container, fragment, "RIDE_FRAGMENT").commit();
            }

            @Override
            public void onFailure(Call<List<Ride>> call, Throwable t) {
                rides = new ArrayList<>();
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(DummyNotificationContent.DummyNotificationItem item) {

    }

    @Override
    public void onListFragmentInteraction(Ride item) {
        RideFragment frag = (RideFragment) getSupportFragmentManager().findFragmentByTag("RIDE_FRAGMENT");
        if (frag != null && frag.isVisible()) {
            Fragment fragment = RideItemFragment.newInstance(item);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, fragment).addToBackStack("drilldownrides").commit();
        }
    }
}
