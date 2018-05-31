package com.polyrides.polyridesv2.dummy;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.polyrides.polyridesv2.R;
import com.polyrides.polyridesv2.models.RideOffer;
import com.polyrides.polyridesv2.models.RideRequest;

public class MyRideRequestRecyclerViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final TextView toText;
    public final TextView fromText;
    public RideRequest mItem;

    public MyRideRequestRecyclerViewHolder(View view) {
        super(view);
        mView = view;
        toText = (TextView) view.findViewById(R.id.toText);
        fromText = (TextView) view.findViewById(R.id.fromText);
    }

    @Override
    public String toString() {
        return super.toString() + " '" + toText.getText() + "'";
    }
}