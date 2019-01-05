package com.omenacle.agency.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omenacle.agency.DataClasses.Route;
import com.omenacle.agency.R;
import com.omenacle.agency.VIPValidationActivity;

import java.util.List;

public class VIPLocationListAdapter extends RecyclerView.Adapter<VIPLocationListAdapter.ViewHolder> {
    private List<Route> mRouteList;
    private Context context;
    private RecyclerView mRecyclerView;

    public VIPLocationListAdapter(List<Route> mRouteList, RecyclerView mRecyclerView) {
        this.mRouteList = mRouteList;
        this.mRecyclerView = mRecyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        context = parent.getContext();
        CardView mLocationContainer = (CardView) LayoutInflater.from(context).inflate(R.layout.location_row_design, parent, false);
        return new ViewHolder(mLocationContainer);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.mLocation.setText(mRouteList.get(position).getRoute());
        holder.fare.setText(String.valueOf(mRouteList.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return mRouteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mLocation, fare;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLocation = itemView.findViewById(R.id.from_to_location);
            fare = itemView.findViewById(R.id.fare);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = mRecyclerView.getChildLayoutPosition(v);
                    String location = mRouteList.get(position).getRoute();
                    String timetravel = mRouteList.get(position).getTravel_time();
                    Intent intent = new Intent(context, VIPValidationActivity.class);
                    intent.putExtra("ROUTE", location);
                    intent.putExtra("TIME_TRAVEL", timetravel);
                    context.startActivity(intent);
                }
            });
        }
    }
}
