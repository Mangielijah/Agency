package com.omenacle.agency;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omenacle.agency.DataClasses.Branch;


/**
 * A simple {@link Fragment} subclass.
 */
public class ClassicFragment extends Fragment implements View.OnClickListener {

    private Context ctx;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Branch mBranch;
    Button btnRoute, btnValidate, btnReminder;

    public ClassicFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        Log.d("LifeCycle", "onCreate");
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ctx = getContext();
        super.onStart(); Log.d("LifeCycle", "onCreateView");
        //Log.d("ClassigFragment", mBranch.getK());
        View layoutView = inflater.inflate(R.layout.fragment_classic, container, false);

        btnRoute = layoutView.findViewById(R.id.btn_route);
        btnValidate = layoutView.findViewById(R.id.btn_validate);
        btnReminder = layoutView.findViewById(R.id.btn_notification);
        btnRoute.setOnClickListener(this);
        btnValidate.setOnClickListener(this);
        btnReminder.setOnClickListener(this);

        return layoutView;
    }

    @Override
    public void onStart() {
        super.onStart(); Log.d("LifeCycle", "onStart");
        // Check if user is signed in (non-null) and update UI accordingly.
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        final ProgressDialog progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        if(currentUser != null){
            getBranchInfo(new OnGetFirebaseDataListener() {
                @Override
                public void onStart() {

                    progressDialog.show();
                }

                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    for(DataSnapshot branchSnapShot : dataSnapshot.getChildren()){

                        Log.d("Branch", String.valueOf(branchSnapShot));
                        Branch branch = branchSnapShot.getValue(Branch.class);
                        if(branch != null && branch.getK().equals(currentUser.getUid())){
                            updateBranch(branch);
                        }
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.d("Branch", errorMessage);
                    progressDialog.dismiss();
                }
            }, currentUser.getUid());
        }else{
            startActivity(new Intent(ctx, LoginActivity.class));
        }
    }

    private void updateBranch(Branch branch) {
        mBranch = branch;
    }

    void getBranchInfo(final OnGetFirebaseDataListener listener, String UID){
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
        mDatabase.child("b").addListenerForSingleValueEvent(mGetRouteListListener);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btn_route:
                Log.d("Branch", mBranch.toString());
                Intent intent = new Intent(ctx, RouteActivity.class);
                intent.putExtra("AGENCY_KEY", mBranch.getA_k());
                intent.putExtra("BRANCH", mBranch.getB());
                startActivity(intent);
                break;
            case R.id.btn_validate:
                Toast.makeText(ctx, "Validate", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_notification:
                Toast.makeText(ctx, "SMS Reminder", Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
