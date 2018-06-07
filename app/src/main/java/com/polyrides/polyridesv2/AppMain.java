package com.polyrides.polyridesv2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polyrides.polyridesv2.models.Ride;
import com.polyrides.polyridesv2.models.RideOffer;
import com.polyrides.polyridesv2.models.RideRequest;
import com.polyrides.polyridesv2.models.SeatRequest;

import java.util.ArrayList;
import java.util.List;


public class AppMain extends AppCompatActivity implements
        RideOfferFragment.OnListFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        DashFragment.OnFragmentInteractionListener,
        NotificationsFragment.OnListFragmentInteractionListener,
        RideItemFragment.OnFragmentInteractionListener,
        RideRequestFragment.OnListFragmentInteractionListener,
        EditRideOfferFragment.OnFragmentInteractionListener,
        RideRequestItemFragment.OnFragmentInteractionListener,
        EditRideRequestFragment.OnFragmentInteractionListener {

    private List<Ride> rides = new ArrayList<>();
    private TextView mTextMessage;
    private String UserName = "";
    private String userUid = "";
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
                case R.id.navigation_account:
                    try {
                        fragment = (Fragment) ProfileFragment.newInstance(userUid);
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

        Intent intent = getIntent();

        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (intent.hasExtra("offerUid") || intent.hasExtra("requestUid")) {

            if (intent.hasExtra("offerUid")) {
                String s = (String) intent.getExtras().get("offerUid");
                FirebaseDatabase.getInstance().getReference("RideOffer")
                        .orderByChild("uid").equalTo(s).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        RideOffer f = null;
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            f = d.getValue(RideOffer.class);
                        }
                        Fragment fragment = RideItemFragment.newInstance(f);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.addToBackStack(null);
                        ft.replace(R.id.container, fragment).commit();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
            else {
                String s = (String) intent.getExtras().get("requestUid");
                FirebaseDatabase.getInstance().getReference("RideRequest")
                        .orderByChild("uid").equalTo(s).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        RideRequest f = null;
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            f = d.getValue(RideRequest.class);
                        }
                        Fragment fragment = RideRequestItemFragment.newInstance(f);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.addToBackStack(null);
                        ft.replace(R.id.container, fragment).commit();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

        }

        Fragment fragment = RideOfferFragment.newInstance(null);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.container, fragment, "RIDE_FRAGMENT").commit();

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
    public void onListFragmentInteraction(RideRequest item) {
            Fragment fragment = RideRequestItemFragment.newInstance(item);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, fragment).addToBackStack("drilldownrides").commit();
    }

    @Override
    public void onListFragmentInteraction(SeatRequest item) {

    }
}
