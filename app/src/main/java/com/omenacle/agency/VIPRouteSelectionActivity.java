package com.omenacle.agency;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omenacle.agency.Adapters.LocationListAdapter;
import com.omenacle.agency.Adapters.VIPLocationListAdapter;
import com.omenacle.agency.DataClasses.Route;

import java.util.ArrayList;
import java.util.List;

public class VIPRouteSelectionActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    RecyclerView locationRecyclerView;
    VIPLocationListAdapter locationListAdapter;
    ProgressDialog pd;
    List<Route> mRouteList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private String branch_key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_selection);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("vr").keepSynced(true);
        mAuth = FirebaseAuth.getInstance();

        locationRecyclerView = findViewById(R.id.locationRecyclerView);
        locationRecyclerView.setHasFixedSize(true);
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(VIPRouteSelectionActivity.this));
        locationListAdapter = new VIPLocationListAdapter(mRouteList, locationRecyclerView);
        locationRecyclerView.setAdapter(locationListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        branch_key = currentUser.getUid();

            getRoutes(new OnGetFirebaseDataListener() {
                @Override
                public void onStart() {
                    pd = new ProgressDialog(VIPRouteSelectionActivity.this );
                    pd.setTitle(getResources().getString(R.string.fetching_routes_available));
                    pd.setMessage(getResources().getString(R.string.please_wait));
                    pd.setCancelable(true);
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();
                }

                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    mRouteList.clear();
                    for (DataSnapshot routeSnapshot : dataSnapshot.getChildren()){
                        Route mRoute = routeSnapshot.getValue(Route.class);
                        Log.d("LocationFragment", mRoute.toString());
                        if (mRoute.getB_k().equals(branch_key)){
                            mRouteList.add(mRoute);
                        }
                    }

                    Toast.makeText(VIPRouteSelectionActivity.this, getResources().getString(R.string.select_route), Toast.LENGTH_LONG).show();
                    //If route list is not empty
                    if (mRouteList != null){
                        Log.d("RouteList", mRouteList.toString());
                        locationListAdapter.notifyDataSetChanged();
                    }

                    pd.dismiss();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.d("LocationError", errorMessage);
                    pd.dismiss();
                }
            });
    }

    void getRoutes(final OnGetFirebaseDataListener listener){
        listener.onStart();
        ValueEventListener mGetRouteListListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure(databaseError.getMessage());
            }
        };
        mDatabase.child("vr").orderByChild("b_k").equalTo(branch_key).addListenerForSingleValueEvent(mGetRouteListListener);
    }

}
