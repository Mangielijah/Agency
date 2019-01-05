package com.omenacle.agency;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
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
import com.omenacle.agency.Adapters.RouteAdapter;
import com.omenacle.agency.DataClasses.Route;

import java.util.ArrayList;

public class VIPRouteActivity extends AppCompatActivity {

    String agencyKey, branch, branchLocation, branchKey;
    FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    ArrayList<Route> routeList = new ArrayList<>();
    RecyclerView recyclerView;
    TextView noRouteView;
    RouteAdapter adapter;
    @Override
    protected void onStart() {
        super.onStart();
        if(branch.contains(agencyKey)){
            branchLocation = branch.replace(agencyKey, "");
            System.out.print(branchLocation);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            branchKey = currentUser.getUid();
        }
        Intent intent = getIntent();
        agencyKey = intent.getStringExtra("AGENCY_KEY");
        branch = intent.getStringExtra("BRANCH");
        setContentView(R.layout.activity_route);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        retrieveRoutes();
        recyclerView = findViewById(R.id.routeRecyclerview);
        noRouteView = findViewById(R.id.no_routeTextView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RouteAdapter(recyclerView, routeList, mDatabase.child("vr"));
        recyclerView.setAdapter(adapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(VIPRouteActivity.this);
                final View v = getLayoutInflater().inflate(R.layout.add_route_dialog, null);

                //Initialiase Views here
                final TextInputEditText destEditText = v.findViewById(R.id.destination);
                final TextInputEditText fareEditText = v.findViewById(R.id.price);
                final CheckBox morningCheckBox = v.findViewById(R.id.checkboxMorning);
                final CheckBox afternoonCheckBox = v.findViewById(R.id.checkboxAfternoon);
                final CheckBox nightCheckBox = v.findViewById(R.id.checkboxNight);
                Button addRouteBtn = v.findViewById(R.id.btn_add);

                builder.setView(v);
                final AlertDialog dialog = builder.create();
                dialog.show();

                addRouteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View vw) {
                        StringBuilder result = new StringBuilder();
                        if(!TextUtils.isEmpty(destEditText.getText())){
                            String dest = destEditText.getText().toString().toLowerCase().trim();
                            String route = branchLocation + "->" + dest;
                            if(!TextUtils.isEmpty(fareEditText.getText())){
                                String fare = fareEditText.getText().toString().trim();
                                if(!((!morningCheckBox.isChecked() && !afternoonCheckBox.isChecked()) && !nightCheckBox.isChecked()))
                                {
                                   if(morningCheckBox.isChecked()){
                                       result.append("morning");
                                   }
                                   if(afternoonCheckBox.isChecked()){
                                       result.append("_afternoon");
                                   }
                                   if(nightCheckBox.isChecked()){
                                       result.append("_night");
                                   }

                                   if(result.charAt(0) == '_'){
                                        result.replace(0, 1, "");
                                        Log.d("TravelTime", result.toString());

                                   }
                                   String travel_time = result.toString();
                                   writeRoute(agencyKey, branchKey, fare, route, travel_time);

                                }
                                else
                                {

                                    Snackbar.make(vw, getResources().getString(R.string.travel_time_error), Snackbar.LENGTH_LONG).show();
                                }
                            }
                            else
                            {
                                Snackbar.make(vw, getResources().getString(R.string.fare_error), Snackbar.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
                            Snackbar.make(vw, getResources().getString(R.string.destination_error), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void writeRoute(String agencyKey, String branchKey, String fare, String route, String travel_time) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getResources().getString(R.string.please_wait));
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.show();
        String key = mDatabase.child("vr").push().getKey();
        Route newRoute = new Route(agencyKey, branchKey, key, Long.parseLong(fare), route, travel_time);
        if (key != null) {
            mDatabase.child("vr").child(key).setValue(newRoute).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        pd.dismiss();
                        Toast.makeText(VIPRouteActivity.this, getString(R.string.route_added), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(VIPRouteActivity.this, getString(R.string.route_failed), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            });
        }
    }

    private ArrayList<Route> retrieveRoutes(){
        mDatabase.child("vr").orderByChild("b_k").equalTo(branchKey).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //fetchData(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                removeData(dataSnapshot);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return routeList;
    }

    private void removeData(DataSnapshot dataSnapshot) {
        Route route = dataSnapshot.getValue(Route.class);
        for(int i = 0; i < routeList.size(); i++){
            if(route != null && route.getR_k().equals(routeList.get(i).getR_k()))
            {
                routeList.remove(i);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void fetchData(DataSnapshot dataSnapshot) {
        Route mtRoute = dataSnapshot.getValue(Route.class);
        if (mtRoute != null && mtRoute.getB_k().equals(branchKey)) {
            routeList.add(mtRoute);
            Log.d("Snapshot", String.valueOf(dataSnapshot));
            adapter.notifyDataSetChanged();
        }

        if (!routeList.isEmpty()){
            noRouteView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else{
            noRouteView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}
