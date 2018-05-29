package com.polyrides.polyridesv2.dummy;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.polyrides.polyridesv2.R;
import com.polyrides.polyridesv2.models.SeatRequest;

public class NotificationsRecyclerViewHolder extends RecyclerView.ViewHolder {

    public final View mView;
    public SeatRequest request;
    public TextView requestData;
    public NotificationsRecyclerViewHolder(View itemView, SeatRequest request) {
        super(itemView);
        mView = itemView;
        this.request = request;
        requestData = (TextView) itemView.findViewById(R.id.notificationContent);

    }


}
