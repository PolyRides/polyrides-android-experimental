package com.polyrides.polyridesv2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.polyrides.polyridesv2.dummy.MyRideRecyclerViewHolder;
import com.polyrides.polyridesv2.models.Ride;
import com.polyrides.polyridesv2.models.RideOffer;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RideRequestFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private ArrayList<Ride> rides;
    private FloatingActionButton addButton;
    private DatabaseReference offersReference;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RideRequestFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RideRequestFragment newInstance(ArrayList<Ride> rides) {
        RideRequestFragment fragment = new RideRequestFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, 1);
        fragment.setArguments(args);
        args.putParcelableArrayList("rides", rides);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            rides = getArguments().getParcelableArrayList("rides");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_list, container, false);

        getActivity().setTitle("Ride Requests");

        Query query = FirebaseDatabase.getInstance().getReference().child("rideRequests");
        FirebaseRecyclerOptions<RideOffer> options = new FirebaseRecyclerOptions.Builder<RideOffer>().setQuery(query, RideOffer.class).build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<RideOffer, MyRideRecyclerViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final MyRideRecyclerViewHolder holder, int position, @NonNull RideOffer model) {
                holder.mItem = model;
                holder.toText.setText(model.destination);
                holder.fromText.setText(model.origin);

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mListener) {
                            // Notify the active callbacks interface (the activity, if the
                            // fragment is attached to one) that an item has been selected.
                            mListener.onListFragmentInteraction(holder.mItem);
                        }
                    }
                });
            }

            @NonNull
            @Override
            public MyRideRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_ride, parent, false);
                return new MyRideRecyclerViewHolder(view);
            }
        };

        adapter.startListening();

        // Set the adapter
        if (view != null) {

            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
            Context context = recyclerView.getContext();
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(adapter);

            addButton = (FloatingActionButton) view.findViewById(R.id.addBtn);

            addButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent intent = new Intent(getActivity(), AddRideRequestActivity.class);
                    startActivity(intent);
              }
            });
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

//    public void ParentNavigate(Ride ride) {
//        Fragment frag = RideItemFragment.newInstance(ride);
//        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        transaction.replace(R.id.container, frag);
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(RideOffer item);
    }
}
