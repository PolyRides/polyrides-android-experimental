package com.polyrides.polyridesv2.dummy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.polyrides.polyridesv2.R;
import com.polyrides.polyridesv2.RideOfferFragment;
import com.polyrides.polyridesv2.RideRequestFragment;
import com.polyrides.polyridesv2.models.RideOffer;

import java.util.List;

public class RideOfferAdapter extends RecyclerView.Adapter<MyRideRecyclerViewHolder> {

    private List<RideOffer> rides;
    private RideOfferFragment.OnListFragmentInteractionListener mListener;
    public RideOfferAdapter(List<RideOffer> rides, RideOfferFragment.OnListFragmentInteractionListener mListener) {
        this.rides = rides;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public MyRideRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_ride, parent, false);
        return new MyRideRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyRideRecyclerViewHolder holder, int position) {
        holder.mItem = rides.get(position);
        holder.toText.setText(rides.get(position).destination);
        holder.fromText.setText(rides.get(position).origin);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return rides.size();
    }
}
