package com.omenacle.agency;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omenacle.agency.Adapters.ValidatedListAdapter;
import com.omenacle.agency.DataClasses.Ticket;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;


/**
 * A simple {@link Fragment} subclass.
 */
public class ValidatedVIPFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private ImageButton buttonRefresh;
    private RecyclerView ticketRecyclerView;
    static String traveltime, route;
    String time;
    List<String> timeArray;
    Spinner travel_time;
    Context ctx;
    ArrayList<Ticket> allTicket = new ArrayList<>();
    ValidatedListAdapter adapter;
    String today_date;

    private DatabaseReference mDatabase;

    public ValidatedVIPFragment() {
        // Required empty public constructor
    }

    public static ValidatedVIPFragment newInstance(String r, String travel_time) {
        ValidatedVIPFragment fragment = new ValidatedVIPFragment();
        traveltime = travel_time;
        route = r;
        Bundle args = new Bundle();
        args.putString("TRAVEL_TIME", travel_time);
        args.putString("ROUTE", r);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mBundle = this.getArguments();
        if(mBundle != null){
            traveltime = mBundle.getString("TRAVEL_TIME");
            route = mBundle.getString("ROUTE");
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("vt").keepSynced(true);

        Calendar calendar = Calendar.getInstance();


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        today_date = dateFormat.format(calendar.getTime());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ctx = getContext();
        View view = inflater.inflate(R.layout.fragment_validate, container, false);
        timeArray = getTimeArray(traveltime);
        travel_time = view.findViewById(R.id.time_spinner);
        //initialising spinner
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(ctx, R.layout.spinner_row, timeArray);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        travel_time.setOnItemSelectedListener(ValidatedVIPFragment.this);
        //Setting adapter to spinner
        travel_time.setAdapter(timeAdapter);
        ticketRecyclerView = view.findViewById(R.id.ticketRecyclerView);
        ticketRecyclerView.setHasFixedSize(true);
        ticketRecyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        adapter = new ValidatedListAdapter(ticketRecyclerView, allTicket, adapterInterface);
        ticketRecyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecor = new DividerItemDecoration(ctx, VERTICAL);
        ticketRecyclerView.addItemDecoration(itemDecor);

        buttonRefresh = view.findViewById(R.id.refresh_btn);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(ctx);
                progressDialog.setMessage(getResources().getString(R.string.please_wait));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);

                retrieveRoutes(new OnGetFirebaseDataListener() {
                    @Override
                    public void onStart() {
                        progressDialog.show();
                        allTicket.clear();
                    }

                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            if (snapshot != null){
                                Log.d("Tickets", String.valueOf(snapshot));
                                Ticket ticket = snapshot.getValue(Ticket.class);
                                if (ticket != null && ticket.getStatus().equals("u")) {
                                    allTicket.add(ticket);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.d("Branch", errorMessage);
                        progressDialog.dismiss();
                    }
                });
            }
        });
        return view;
    }

    private List<String> getTimeArray(String time){
        List<String> timeArray = new ArrayList<>();
        if(time.contains("_")){
            String s[] = time.split("_");
            if(s.length > 0){
                Log.d("SplitTime1", s[0]);
                Log.d("SplitTime1", s[1]);
                timeArray.addAll(Arrays.asList(s));
            }
        }
        return timeArray;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        time = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    void retrieveRoutes(final OnGetFirebaseDataListener listener){
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
        mDatabase.child("vt").orderByChild("r_time").equalTo(route+"_"+time+"_"+today_date).addListenerForSingleValueEvent(mGetRouteListListener);
    }

   ValidatedListAdapter.InfoAdapterInterface adapterInterface = new ValidatedListAdapter.InfoAdapterInterface() {
        @Override
        public void OnItemClicked(int pos) {

            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            final View v = getLayoutInflater().inflate(R.layout.ticket_dialog, null);
            //Initialise Views
            TextView codeTextView = v.findViewById(R.id.code);
            TextView nameTextView = v.findViewById(R.id.name);
            TextView idTextView = v.findViewById(R.id.id);

            codeTextView.setText("#"+ allTicket.get(pos).getCode());
            nameTextView.setText(allTicket.get(pos).getName());
            idTextView.setText(String.valueOf(allTicket.get(pos).getId()));

            builder.setView(v);
            final AlertDialog dialog = builder.create();
            dialog.show();
        }
    };
}
