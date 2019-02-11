package com.omenacle.agency.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omenacle.agency.DataClasses.Route;
import com.omenacle.agency.DataClasses.Seats;
import com.omenacle.agency.R;
import com.omenacle.agency.ValidationActivity;

import java.util.List;

public class SeatsLocationListAdapter extends RecyclerView.Adapter<SeatsLocationListAdapter.ViewHolder> {
    private List<Route> mRouteList;
    private List<Seats> mSeatList;
    private Context context;
    private RecyclerView mRecyclerView;
    private SeatsAdapterCallback listener;

    public SeatsLocationListAdapter(List<Route> mRouteList, List<Seats> mSeatList, RecyclerView mRecyclerView, Context ctx) {
        this.mRouteList = mRouteList;
        this.mSeatList = mSeatList;
        this.mRecyclerView = mRecyclerView;
        try {
            this.listener = ((SeatsAdapterCallback) ctx);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        context = parent.getContext();
        CardView mLocationContainer = (CardView) LayoutInflater.from(context).inflate(R.layout.seats_row_design, parent, false);
        return new ViewHolder(mLocationContainer);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mLocation.setText(mRouteList.get(position).getRoute());
        holder.fare.setText(String.valueOf(mRouteList.get(position).getPrice()) + " FCFA");
        holder.type.setText("Classic");
        if(!mSeatList.isEmpty())
        {
            Log.d("Test", position + " : "+ mSeatList.size());
            if(position >= mSeatList.size()){

                holder.seatLeft.setText(
                        "Morning: "+ 0
                                +" left\nAfternoon: "+ 0
                                +" left\nNight: "+ 0
                                +" left");
            }
            else
            {
                if (mRouteList.get(position).getR_k().equals(mSeatList.get(position).getR_k()))
                {
                    holder.seatLeft.setText(
                            "Morning: "+ mSeatList.get(position).getM()
                                    +" left\nAfternoon: "+ mSeatList.get(position).getA()
                                    +" left\nNight: "+ mSeatList.get(position).getN()
                                    +" left");

                }

            }

        }
    }

    @Override
    public int getItemCount() {
        return mRouteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mLocation, fare, type, seatLeft;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLocation = itemView.findViewById(R.id.from_to_location);
            fare = itemView.findViewById(R.id.fare);
            type = itemView.findViewById(R.id.type);
            seatLeft = itemView.findViewById(R.id.seatLeft);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = mRecyclerView.getChildLayoutPosition(v);
                    Log.d("Position", String.valueOf(position));
                    listener.onClickCallback(position);
                }
            });
        }
    }

    public interface SeatsAdapterCallback
    {
        void onClickCallback(int pos);
    }
}
