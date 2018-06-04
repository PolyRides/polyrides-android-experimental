package com.polyrides.polyridesv2;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;


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

        toLocation.setText(ride.destination);
        fromLocation.setText(ride.origin);
        description.setText(ride.description);


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

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

                Fragment f = RideRequestFragment.newInstance(new ArrayList<Ride>());
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, f);
                transaction.commit();
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
}
