package com.polyrides.polyridesv2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.polyrides.polyridesv2.dummy.MyRideRecyclerViewHolder;
import com.polyrides.polyridesv2.dummy.RideOfferAdapter;
import com.polyrides.polyridesv2.models.Ride;
import com.polyrides.polyridesv2.models.RideOffer;
import com.polyrides.polyridesv2.models.RideOffer2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RideOfferFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private FloatingActionButton addButton;
    private DatabaseReference offersReference;
    FirebaseRecyclerAdapter mAdapter;
    private String searchText = "";
    private RecyclerView recyclerView;
    private List<RideOffer> rides = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RideOfferFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RideOfferFragment newInstance(ArrayList<Ride> rides) {
        RideOfferFragment fragment = new RideOfferFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, 1);
        fragment.setArguments(args);
        args.putParcelableArrayList("rides", rides);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            rides = getArguments().getParcelableArrayList("rides");
        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);



        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                return true;
            }

        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                searchText = "";
                search();
                return false;
            }
        });

    }

    private void search() {
        List<RideOffer> items = new ArrayList<>();
        Query query;
        if (!searchText.isEmpty()) {
            for (RideOffer e : rides) {
                StringBuilder sb = new StringBuilder();
                sb.append(e.origin);
                sb.append(e.destination);
                sb.append(e.rideDescription);

                if (sb.toString().toLowerCase().contains(searchText.toLowerCase()))
                    items.add(e);
            }
        }
        else {
            items = rides;
        }

        Context context = recyclerView.getContext();
        RideOfferAdapter adapter = new RideOfferAdapter(items, mListener);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        recyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rides = new ArrayList<>();
        getActivity().setTitle("Ride Offers");
        View view = inflater.inflate(R.layout.fragment_ride_list, container, false);

        getActivity().setTitle(R.string.ride_offers_text);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);

        FirebaseDatabase.getInstance().getReference().child("RideOffer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    RideOffer r = d.getValue(RideOffer.class);
                    rides.add(r);
                }

                search();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        search();

        // Set the adapter
        if (view != null) {


            addButton = (FloatingActionButton) view.findViewById(R.id.addBtn);

            addButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent intent = new Intent(getActivity(), AddRideOfferActivity.class);
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
