package com.polyrides.polyridesv2;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polyrides.polyridesv2.models.AppUser;
import com.polyrides.polyridesv2.models.Ride;
import com.polyrides.polyridesv2.models.RideRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RideRequestItemFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RideRequestItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RideRequestItemFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RideRequest ride;
    private DatabaseReference requestorReference;

    private TextView toLocation;
    private TextView fromLocation;
    private TextView description;
    private Button editButton;
    private Button deleteButton;
    private CardView requestorCard;
    private LinearLayout requestorLayout;
    private ImageView requestorImageView;
    private TextView requestorName;
    private CardView riderActionsCard;
    private LinearLayout riderActionsLayout;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private TextView dateView;
    private Button offerBtn;
    private Boolean rideAccepted;
    private TextView spotsAvailable;
    private CardView driverActionsCard;
    private LinearLayout riderNotAddedLayout;


    private OnFragmentInteractionListener mListener;

    public RideRequestItemFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RideRequestItemFragment newInstance(RideRequest request) {
        RideRequestItemFragment fragment = new RideRequestItemFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, request);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ride = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ride_request_item, container, false);

        toLocation = v.findViewById(R.id.tolocation);
        fromLocation = v.findViewById(R.id.fromlocation);
        description = v.findViewById(R.id.ridedesc);
        editButton = v.findViewById(R.id.editBtn);
        deleteButton = v.findViewById(R.id.deleteBtn);
        requestorCard = v.findViewById(R.id.requestorCard);
        requestorLayout = v.findViewById(R.id.requestorLayout);
        requestorImageView = v.findViewById(R.id.requestorImgView);
        requestorName = v.findViewById(R.id.requestorName);
        riderActionsCard = v.findViewById(R.id.riderActionsCard);
        riderActionsLayout = v.findViewById(R.id.riderActionsLayout);
        dateView = v.findViewById(R.id.dateView);
        offerBtn = v.findViewById(R.id.offerBtn);
        spotsAvailable = v.findViewById(R.id.spotsAvailable);
        driverActionsCard = v.findViewById(R.id.driverActionsCard);
        riderNotAddedLayout = v.findViewById(R.id.riderNotAddedLayout);

        toLocation.setText(ride.destination);
        fromLocation.setText(ride.origin);
        description.setText(ride.rideDescription);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (ride.driverId.equals(uid)) {
            rideAccepted = true;
            spotsAvailable.setText("You're already the driver for this ride.");
            offerBtn.setText("Leave Ride");
        }
        else {
            rideAccepted = false;
        }

        if (!ride.riderId.equals(uid)) {
            driverActionsCard.setVisibility(View.VISIBLE);
            riderNotAddedLayout.setVisibility(View.VISIBLE);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String s = ride.departureDate;
        Date d = new Date( ((long) Double.parseDouble(s)) * 1000);
        SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM/dd/yy hh:mm:ss aa");
        String date = dateFormat.format(d);
        dateView.setText(date);

        requestorReference = FirebaseDatabase.getInstance().getReference("Profile/" + ride.riderId);

        requestorReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AppUser thisUser = dataSnapshot.getValue(AppUser.class);

                String userProfileImg = thisUser.getPhoto();

                requestorName.setText(thisUser.getFirstName() + " " + thisUser.getLastName());

                if (userProfileImg != null) {
                    new ImageDownloaderTask(requestorImageView).doInBackground(userProfileImg);
                }
                else {
                    requestorImageView.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.default_user_image));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(ride.riderId)) {
            requestorCard.setVisibility(View.VISIBLE);
            riderActionsCard.setVisibility(View.VISIBLE);
        }
        else {
            requestorLayout.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            riderActionsLayout.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        }



        requestorCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = ProfileFragment.newInstance(ride.riderId);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.container, f);
                transaction.commit();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = EditRideRequestFragment.newInstance(ride);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.container, f);
                transaction.commit();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what are we to do here?
                Toast t = Toast.makeText(getContext(), "Ride Deleted", Toast.LENGTH_LONG);
                t.show();

                mDatabase.child("RideRequest").child(ride.uid).removeValue();

                if (ride.driverId != null && !ride.driverId.equals("")) {
                    SendRequestMessage(ride.driverId, ride.uid, "One of your ride requests has been deleted.");
                }

                Fragment f = RideRequestFragment.newInstance(new ArrayList<Ride>());
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, f);
                transaction.commit();
            }
        });

        offerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rideAccepted) {
                    rideAccepted = false;
                    // leave ride functionality
                    Map<String, Object> setupDriver = new HashMap<>();
                    setupDriver.put("/RideRequest/" + ride.uid + "/driverId", "");
                    FirebaseDatabase.getInstance().getReference().updateChildren(setupDriver);

                    Toast t = Toast.makeText(getContext(), "You've been removed from this ride.", Toast.LENGTH_SHORT);
                    t.show();

                    spotsAvailable.setText("Accept the offer to sign up as a driver.");
                    offerBtn.setText("Accept Offer");

                    SendRequestMessage(ride.riderId, ride.uid, "A driver has been removed from your ride.");
                }
                else {
                    // add ride functionality
                    rideAccepted = true;

                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    Map<String, Object> setupDriver = new HashMap<>();
                    setupDriver.put("/RideRequest/" + ride.uid + "/driverId", uid);
                    FirebaseDatabase.getInstance().getReference().updateChildren(setupDriver);

                    Toast t = Toast.makeText(getContext(), "Request Accepted", Toast.LENGTH_SHORT);
                    t.show();

                    spotsAvailable.setText("You're already the driver for this ride.");
                    offerBtn.setText("Leave Ride");

                    SendRequestMessage(ride.riderId, ride.uid, "A driver has signed up for your ride.");
                }
            }
        });

        // Inflate the layout for this fragment
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng v = getLocationFromAddress(ride.origin);
        LatLng v2 = getLocationFromAddress(ride.destination);
        googleMap.addMarker(new MarkerOptions().position(v).title("Start").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        googleMap.addMarker(new MarkerOptions().position(v2).title("End").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(v);
        builder.include(v2);
        LatLngBounds bounds = builder.build();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 15));

        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
            }
        });
        googleMap.setOnCameraMoveCanceledListener(new GoogleMap.OnCameraMoveCanceledListener() {
            @Override
            public void onCameraMoveCanceled() {
            }
        });
    }

    public LatLng getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(getContext());
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            double d = location.getLatitude();
            double d2 = location.getLongitude();

            p1 = new LatLng(location.getLatitude(),
                    location.getLongitude());

            return p1;
        } catch (Exception e) {
            Exception v = e;
            Log.e("fail", e.getMessage() + e.getStackTrace());
            return null;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public void SendRequestMessage(final String target, final String uid, final String message) {

        FirebaseDatabase.getInstance().getReference("Profile").orderByChild("uid").equalTo(target).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    AppUser user = d.getValue(AppUser.class);

                    JSONObject not = new JSONObject();
                    JSONObject notificationData = new JSONObject();
                    JSONObject data = new JSONObject();
                    try {
                        not.put("to", user.getDeviceToken());
                        notificationData.put("body", message);
                        notificationData.put("title", "PolyRides");
                        not.put("notification", notificationData);
                        data.put("requestUid", uid);
                        not.put("data",data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String bodyData = not.toString();

                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), bodyData);

                    OkHttpClient client = new OkHttpClient();
                    String url = "https://fcm.googleapis.com/fcm/send";
                    final Request request = new Request.Builder()
                            .url(url)
                            .header("Authorization", "key=AIzaSyA_se-58n4sJP5ckp7NVURTuvgmDZamxiU")
                            .header("Content-Type", "application/json")
                            .post(body)
                            .build();
                    try {
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String s = response.body().toString();
                                String s2 = s;
                            }
                        });

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
