package com.polyrides.polyridesv2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polyrides.polyridesv2.models.RideOffer;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditRideOfferFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditRideOfferFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditRideOfferFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private SupportPlaceAutocompleteFragment autocompleteFragmentTo;
    private SupportPlaceAutocompleteFragment autocompleteFragmentFrom;
    private TextView toText;
    private TextView fromText;
    private RideOffer ride;
    private EditText description;
    private TextView numSeats;
    private TextView cost;
    private Button saveBtn;
    private DatabaseReference mDatabase;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public EditRideOfferFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static EditRideOfferFragment newInstance(RideOffer ride) {
        EditRideOfferFragment fragment = new EditRideOfferFragment();
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

        getActivity().setTitle("Edit Ride Offer");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_ride_offer, container, false);

        toText = v.findViewById(R.id.toText);
        fromText = v.findViewById(R.id.fromText);
        description = v.findViewById(R.id.description);
        numSeats = v.findViewById(R.id.numSeats);
        cost = v.findViewById(R.id.cost);
        saveBtn = v.findViewById(R.id.saveBtn);

        toText.setText(ride.destination);
        fromText.setText(ride.origin);
        description.setText(ride.description);
        numSeats.setText(ride.seats.toString());
        cost.setText(ride.cost.toString());
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // verify and submit.

                mDatabase = FirebaseDatabase.getInstance().getReference();

                ride.description = description.getText().toString();
                ride.seats = Integer.valueOf(numSeats.getText().toString());
                ride.cost = Double.valueOf(cost.getText().toString());

                Map<String, Object> data = new HashMap<String, Object>();
                data.put("origin", ride.origin);
                data.put("originLat", ride.originLat);
                data.put("originLon", ride.originLon);
                data.put("destination", ride.destination);
                data.put("destinationLat", ride.destinationLat);
                data.put("destinationLon", ride.destinationLon);
                data.put("departureDate", ride.departureDate);
                data.put("driverId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                data.put("uid", ride.uid);
                data.put("rideDescription", ride.description);
                data.put("seats", ride.seats);
                data.put("cost", ride.cost);

                Map<String,Object> endpoints = new HashMap<>();
                endpoints.put("/RideOffer/" + ride.uid, data);

                mDatabase.updateChildren(endpoints);

                Fragment f = RideItemFragment.newInstance(ride);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container,f).commit();
            }
        });

        autocompleteFragmentTo = (SupportPlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragmentTo);

        autocompleteFragmentFrom = (SupportPlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragmentFrom);

        autocompleteFragmentTo.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                toText.setText(place.getAddress().toString());

                ride.destination = place.getAddress().toString();
                ride.destinationLat  = place.getLatLng().latitude;
                ride.destinationLon = place.getLatLng().longitude;
            }

            @Override
            public void onError(Status status) {

            }
        });

        autocompleteFragmentFrom.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                fromText.setText(place.getAddress().toString());

                ride.origin = place.getAddress().toString();
                ride.originLat = place.getLatLng().latitude;
                ride.originLon = place.getLatLng().longitude;

            }

            @Override
            public void onError(Status status) {

            }
        });





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
