package com.omenacle.agency.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.omenacle.agency.DataClasses.Route;
import com.omenacle.agency.R;

import java.util.ArrayList;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ViewHolder> {
    private ArrayList<Route> routes;
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    public RouteAdapter(RecyclerView recyclerView, ArrayList<Route> routes, DatabaseReference mDatabase) {
        this.routes = routes;
        this.recyclerView = recyclerView;
        this.mDatabase = mDatabase;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_row_view, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.routeView.setText(routes.get(i).getRoute());
        viewHolder.priceView.setText(routes.get(i).getPrice() + " FCFA");
        viewHolder.timeView.setText(routes.get(i).getTravel_time());
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView routeView, priceView, timeView;
        ImageView deleteView;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            routeView = itemView.findViewById(R.id.routeTextView);
            priceView = itemView.findViewById(R.id.priceTextView);
            timeView = itemView.findViewById(R.id.timeTextView);
            deleteView = itemView.findViewById(R.id.deletebtn);
            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = recyclerView.getChildLayoutPosition(itemView);
                    String key = routes.get(pos).getR_k();
                    mDatabase.child(key).removeValue();
                }
            });
        }
    }
}
