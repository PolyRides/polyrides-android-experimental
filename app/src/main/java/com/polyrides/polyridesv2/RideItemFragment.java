package com.polyrides.polyridesv2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.polyrides.polyridesv2.models.AppUser;
import com.polyrides.polyridesv2.models.Ride;
import com.polyrides.polyridesv2.models.RideOffer;

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
 * {@link RideItemFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RideItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RideItemFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RideOffer ride;
    // TODO: Rename and change types of parameters
    private TextView toLocation;
    private TextView fromLocation;
    private TextView description;
    private NestedScrollView nestedScrollView;
    private TextView ridesAvailable;
    private Button offerButton;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private CardView riderActionsCard;
    private CardView driverActionsCard;
    private Button editButton;
    private Button deleteButton;
    private Button leaveRideButton;
    private LinearLayout riderAddedLayout;
    private LinearLayout riderNotAddedLayout;
    private LinearLayout driverLayout;
    private ImageView driverImageView;
    private TextView driverName;
    private DatabaseReference driverReference;
    private CardView driverCard;
    private TextView dateView;
    private Boolean alreadyAccepted;

    private OnFragmentInteractionListener mListener;

    public RideItemFragment() {
        // Required empty public constructor
    }

    public static RideItemFragment newInstance(RideOffer ride) {
        RideItemFragment fragment = new RideItemFragment();
        Bundle args = new Bundle();
        args.putParcelable("ride", ride);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ride = getArguments().getParcelable("ride");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Ride Offer");
        if (ride.riderIds == null) {
            ride.riderIds = new ArrayList<>();
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ride_item, container, false);
        if (ride.riderIds != null) {
            alreadyAccepted = ride.riderIds.contains(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        else {
            alreadyAccepted = false;
        }


        riderActionsCard = view.findViewById(R.id.riderActionsCard);
        driverActionsCard = view.findViewById(R.id.driverActionsCard);
        fromLocation = view.findViewById(R.id.fromlocation);
        toLocation = view.findViewById(R.id.tolocation);
        description = view.findViewById(R.id.ridedesc);
        nestedScrollView = view.findViewById(R.id.ridedescview);
        ridesAvailable = view.findViewById(R.id.spotsAvailable);
        offerButton = view.findViewById(R.id.offerBtn);
        editButton = view.findViewById(R.id.editBtn);
        deleteButton = view.findViewById(R.id.deleteBtn);
        leaveRideButton = view.findViewById(R.id.leaveRideBtn);
        riderAddedLayout = view.findViewById(R.id.riderAddedLayout);
        riderNotAddedLayout = view.findViewById(R.id.riderNotAddedLayout);
        driverLayout = view.findViewById(R.id.driverProfile);
        driverName = view.findViewById(R.id.driverName);
        driverImageView = view.findViewById(R.id.driverImgView);
        driverCard = view.findViewById(R.id.driverCard);
        dateView = view.findViewById(R.id.dateView);

        driverReference = FirebaseDatabase.getInstance().getReference("Profile/" + ride.driverId);

        driverReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AppUser thisUser = dataSnapshot.getValue(AppUser.class);

                String userProfileImg = thisUser.getPhoto();

                driverName.setText(thisUser.getFirstName() + " " + thisUser.getLastName());

                FirebaseStorage
                        .getInstance().getReference().child("images/" + thisUser.getUid() + ".jpeg")
                        .getBytes(10 * 1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        Bitmap fin = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);


                        driverImageView.setImageBitmap(fin);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        driverImageView.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.default_user_image));
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        offerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


                if (!alreadyAccepted) {
                    if (ride.seats == ride.riderIds.size()  || (ride.riderIds != null && ride.riderIds.contains(uid))) {
                        Toast t = Toast.makeText(getContext(), "This ride has no more seats available.", Toast.LENGTH_SHORT);
                        t.show();
                    }
                    else if (ride.riderIds != null && ride.riderIds.contains(uid)) {
                        Toast t = Toast.makeText(getContext(), "You've already joined this ride.", Toast.LENGTH_SHORT);
                        t.show();
                    }
                    else {
                        if (ride.riderIds == null) {
                            ride.riderIds = new ArrayList<>();
                        }

                        ride.riderIds.add(uid);

                        Map<String, Object> setupRiders = new HashMap<>();
                        setupRiders.put("/RideOffer/" + ride.uid + "/riderIds", ride.riderIds);

                        mDatabase.updateChildren(setupRiders);

                        Toast t = Toast.makeText(getContext(), "Request Accepted", Toast.LENGTH_SHORT);
                        ridesAvailable.setText("Rides Available: " + (ride.seats - ride.riderIds.size()));
                        t.show();


                        SendOfferMessage(ride.driverId, ride.uid, "One of your rides has a new rider.");


                        alreadyAccepted = true;
                        ridesAvailable.setText("You've already accepted this ride.");
                        offerButton.setText("Leave Ride");

                        //riderAddedLayout.setVisibility(View.VISIBLE);
                        //riderNotAddedLayout.setLayoutParams(new LinearLayout.LayoutParams(0,0));
                    }
                }
                else {

                    ride.riderIds.remove(uid);

                    Map<String, Object> setupRiders = new HashMap<>();
                    setupRiders.put("/RideOffer/" + ride.uid + "/riderIds", ride.riderIds);


                    mDatabase.updateChildren(setupRiders);

                    Toast t = Toast.makeText(getContext(), "You have been removed.", Toast.LENGTH_SHORT);
                    ridesAvailable.setText("Rides Available: " + (ride.seats - ride.riderIds.size()));
                    offerButton.setText("Accept Offer");
                    alreadyAccepted = false;
                    t.show();
                }


            }
        });

        fromLocation.setText(ride.origin);
        toLocation.setText(ride.destination);
        description.setText(ride.rideDescription);
        ridesAvailable.setText("Rides Available: " + (ride.seats - ride.riderIds.size()));

        String s = ride.departureDate;
        Date d = new Date( ((long) Double.parseDouble(s)) * 1000);
        SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM/dd/yy hh:mm:ss aa");
        //dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("GMT-8"));
        String date = dateFormat.format(d);
        dateView.setText(date);


        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(ride.driverId)) {
            driverActionsCard.setVisibility(View.VISIBLE);
            riderActionsCard.setVisibility(View.INVISIBLE);
            riderActionsCard.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment f = EditRideOfferFragment.newInstance(ride);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.container,f).commit();
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // what are we to do here?
                    Toast t = Toast.makeText(getContext(), "Ride Deleted", Toast.LENGTH_LONG);
                    t.show();

                    if (ride.riderIds != null)
                    for (String s : ride.riderIds) {
                        SendOfferMessage(s, ride.uid, "One of your rides has been deleted.");
                    }

                    mDatabase.child("RideOffer").child(ride.uid).removeValue();

                    Fragment f = RideOfferFragment.newInstance(new ArrayList<Ride>());
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, f);
                    transaction.commit();
                }
            });

            //driverCard.setVisibility(View.VISIBLE);
            //driverLayout.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        }
        else {
            driverActionsCard.setVisibility(View.INVISIBLE);
            driverActionsCard.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            riderActionsCard.setVisibility(View.VISIBLE);

            if (ride.riderIds != null && ride.riderIds.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                riderAddedLayout.setVisibility(View.VISIBLE);
                riderNotAddedLayout.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            }
            else {
                riderNotAddedLayout.setVisibility(View.VISIBLE);
                riderAddedLayout.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            }
            driverCard.setVisibility(View.VISIBLE);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        driverCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = ProfileFragment.newInstance(ride.driverId);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.container, f);
                transaction.commit();
            }
        });

        return view;
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


    }

    public LatLng getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(getContext());
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,1);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);

            p1 = new LatLng(location.getLatitude(),
                    location.getLongitude());

            return p1;
        } catch (Exception e) {
            Exception v = e;
            Log.e("fail", e.getMessage() + e.getStackTrace());
            return null;
        }
    }

    public void SendOfferMessage(final String target, final String uid, final String message) {

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
                        data.put("offerUid", uid);
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
}
