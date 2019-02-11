package com.omenacle.agency;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omenacle.agency.Adapters.VipSeatsLocationListAdapter;
import com.omenacle.agency.DataClasses.Route;
import com.omenacle.agency.DataClasses.Seats;

import java.util.ArrayList;
import java.util.List;

public class VipSeatsActivity extends AppCompatActivity implements VipSeatsLocationListAdapter.SeatsAdapterCallback {

    private DatabaseReference mDatabase;
    VipSeatsLocationListAdapter seatsLocationListAdapter;
    ProgressDialog pd;
    List<Route> mRouteList = new ArrayList<>();
    List<Seats> mSeatList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private String branch_key;
    private String agency_key;
    private String currentCheckState = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seats);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("r").keepSynced(true);
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        agency_key = intent.getStringExtra("AGENCY_KEY");

        RecyclerView locationRecyclerView = findViewById(R.id.locationRecyclerView);
        locationRecyclerView.setHasFixedSize(true);
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(VipSeatsActivity.this));
        seatsLocationListAdapter = new VipSeatsLocationListAdapter(mRouteList, mSeatList, locationRecyclerView, this);
        locationRecyclerView.setAdapter(seatsLocationListAdapter);
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
                pd = new ProgressDialog(VipSeatsActivity.this );
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
                    if (mRoute.getB_k().equals(branch_key)){
                        mRouteList.add(mRoute);
                    }
                }

                Toast.makeText(VipSeatsActivity.this, getResources().getString(R.string.select_route), Toast.LENGTH_LONG).show();
                //If route list is not empty
                if (mRouteList != null){
                    Log.d("RouteList", mRouteList.toString());
                    seatsLocationListAdapter.notifyDataSetChanged();
                }

                pd.dismiss();
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.d("LocationError", errorMessage);
                pd.dismiss();
            }
        });

        getSeats();
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

    void getSeats(){

        ChildEventListener mChildEventListenr = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateData(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabase.child("vs").orderByChild("a_k").equalTo(agency_key).addChildEventListener(mChildEventListenr);
    }

    private void fetchData(DataSnapshot dataSnapshot) {
        Seats mSeat = dataSnapshot.getValue(Seats.class);

        if(mSeat != null && mSeat.getA_k().equals(agency_key)){
            mSeatList.add(mSeat);
            seatsLocationListAdapter.notifyDataSetChanged();
        }
    }

    private void updateData(DataSnapshot dataSnapshot) {
        Seats mSeat = dataSnapshot.getValue(Seats.class);
        if(mSeat != null && mSeat.getA_k().equals(agency_key)){
            for (int i = 0; i < mSeatList.size(); i++){
                if ( mSeatList.get(i).getR_k().equals(mSeat.getR_k()))
                {
                    mSeatList.set(i, mSeat);
                    seatsLocationListAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onClickCallback(int pos) {
        Seats newSeat = null;
        if(mSeatList.size() <= pos)
        {
            newSeat = new Seats();
            newSeat.setA_k(agency_key);
            newSeat.setR_k(mRouteList.get(pos).getR_k());
        }
        else {
            newSeat = mSeatList.get(pos);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(VipSeatsActivity.this);
        final View v = getLayoutInflater().inflate(R.layout.seat_dialog, null);
        //Intiliase views
        final EditText seatEditText = v.findViewById(R.id.numberOfSeats);
        Button buttonAdd = v.findViewById(R.id.btnAdd);
        Button buttonRemove = v.findViewById(R.id.btnRemove);

        builder.setView(v);
        final AlertDialog dialog = builder.create();
        dialog.show();

        final Seats finalNewSeat = newSeat;
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(seatEditText.getText()) && currentCheckState != null)
                {
                    Log.d("State", currentCheckState);
                    long number = Long.parseLong(seatEditText.getText().toString());

                    switch (currentCheckState) {
                        case "morning":
                            finalNewSeat.setM(finalNewSeat.getM() + number);
                            break;
                        case "afternoon":
                            finalNewSeat.setA(finalNewSeat.getA() + number);
                            break;
                        case "night":
                            finalNewSeat.setN(finalNewSeat.getN() + number);
                            break;
                    }

                    mDatabase.child("vs").child(finalNewSeat.getR_k()).setValue(finalNewSeat).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dialog.dismiss();
                        }
                    });

                }
            }
        });

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(seatEditText.getText()) && currentCheckState != null)
                {
                    Log.d("State", currentCheckState);
                    long number = Long.parseLong(seatEditText.getText().toString());

                    switch (currentCheckState) {
                        case "morning":
                            finalNewSeat.setM(finalNewSeat.getM() - number);
                            break;
                        case "afternoon":
                            finalNewSeat.setA(finalNewSeat.getA() - number);
                            break;
                        case "night":
                            finalNewSeat.setN(finalNewSeat.getN() - number);
                            break;
                    }

                    mDatabase.child("vs").child(finalNewSeat.getR_k()).setValue(finalNewSeat).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dialog.dismiss();
                        }
                    });

                }
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.morning:
                if (checked)
                    currentCheckState = "morning";
                    break;
            case R.id.afternoon:
                if (checked)
                    currentCheckState = "afternoon";
                    break;
            case R.id.night:
                if (checked)
                    currentCheckState = "night";
                    break;
        }

    }
}
