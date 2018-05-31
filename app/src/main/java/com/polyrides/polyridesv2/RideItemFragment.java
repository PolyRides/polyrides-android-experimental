package com.polyrides.polyridesv2;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
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
import com.polyrides.polyridesv2.addrides_flow.offers.AddRides_1;
import com.polyrides.polyridesv2.models.AppUser;
import com.polyrides.polyridesv2.models.Ride;
import com.polyrides.polyridesv2.models.RideOffer;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    private String mParam1;
    private String mParam2;
    private TextView toLocation;
    private TextView fromLocation;
    private TextView description;
    private boolean blockScroll = false;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ride_item, container, false);

        riderActionsCard = (CardView) view.findViewById(R.id.riderActionsCard);
        driverActionsCard = (CardView) view.findViewById(R.id.driverActionsCard);
        fromLocation = (TextView) view.findViewById(R.id.fromlocation);
        toLocation = (TextView) view.findViewById(R.id.tolocation);
        description = (TextView) view.findViewById(R.id.ridedesc);
        nestedScrollView = (NestedScrollView) view.findViewById(R.id.ridedescview);
        ridesAvailable = (TextView) view.findViewById(R.id.spotsAvailable);
        offerButton = (Button) view.findViewById(R.id.offerBtn);
        editButton = (Button) view.findViewById(R.id.editBtn);
        deleteButton = (Button) view.findViewById(R.id.deleteBtn);
        leaveRideButton = (Button) view.findViewById(R.id.leaveRideBtn);
        riderAddedLayout = (LinearLayout) view.findViewById(R.id.riderAddedLayout);
        riderNotAddedLayout = (LinearLayout) view.findViewById(R.id.riderNotAddedLayout);
        driverLayout = view.findViewById(R.id.driverProfile);
        driverName = view.findViewById(R.id.driverName);
        driverImageView = view.findViewById(R.id.driverImgView);
        driverCard = view.findViewById(R.id.driverCard);

        driverReference = FirebaseDatabase.getInstance().getReference("users/" + ride.driverId);

        driverReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AppUser thisUser = dataSnapshot.getValue(AppUser.class);

                String userProfileImg = thisUser.getPhoto();

                driverName.setText(thisUser.getName());

                if (userProfileImg != null) {
                    new ImageDownloaderTask(driverImageView).doInBackground(userProfileImg);
                }
                else {
                    driverImageView.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.default_user_image));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        offerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = mDatabase.child("seatRequests").push().getKey();

                Map<String, Object> data = new HashMap<>();
                data.put("offerId", ride.uid);
                data.put("riderId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                data.put("driverId", ride.driverId);

                ridesAvailable.setText("Seats Available: " + ride.seats);

                Map<String, Object> rideSetup = new HashMap<>();
                rideSetup.put("/seatRequests/" + key, data);

                mDatabase.updateChildren(rideSetup);

                Toast t = Toast.makeText(getContext(), "Request Accepted", Toast.LENGTH_SHORT);
                t.show();
            }
        });

        fromLocation.setText(ride.origin);
        toLocation.setText(ride.destination);
        description.setText(ride.description);
        ridesAvailable.setText("Rides Available: " + ride.seats);


        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(ride.driverId)) {
            driverActionsCard.setVisibility(View.VISIBLE);
            riderActionsCard.setVisibility(View.INVISIBLE);
            riderActionsCard.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Fragment f = EditRideOfferFragment.newInstance(ride);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container,f).commit();
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // what are we to do here?
                    Toast t = Toast.makeText(getContext(), "Ride Deleted", Toast.LENGTH_LONG);
                    t.show();

                    mDatabase.child("rideOffers").child(ride.uid).removeValue();

                    Fragment f = RideOfferFragment.newInstance(new ArrayList<Ride>());
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, f);
                    transaction.commit();
                }
            });

            driverCard.setVisibility(View.VISIBLE);
            //driverLayout.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        }
        else {
            driverActionsCard.setVisibility(View.INVISIBLE);
            driverActionsCard.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            riderActionsCard.setVisibility(View.VISIBLE);

            if (ride.riderIds.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
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

        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                blockScroll = true;
            }
        });
        googleMap.setOnCameraMoveCanceledListener(new GoogleMap.OnCameraMoveCanceledListener() {
            @Override
            public void onCameraMoveCanceled() {
                blockScroll = false;
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
