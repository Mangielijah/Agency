package com.omenacle.agency;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class VIPValidationActivity extends AppCompatActivity {

    String route, timetravel;
    Fragment fragment = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_validate:
                    fragment = ValidateVIPFragment.newInstance(route, timetravel);
                    setFragment(fragment);
                    return true;
                case R.id.navigation_validated:
                    fragment = ValidatedVIPFragment.newInstance(route, timetravel);
                    setFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);

        Intent intent = getIntent();
        route = intent.getStringExtra("ROUTE");
        timetravel = intent.getStringExtra("TIME_TRAVEL");

        fragment = ValidateVIPFragment.newInstance(route, timetravel);
        setFragment(fragment);

        BottomNavigationView navigation = findViewById(R.id.navigation_val);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_validate, fragment);
        fragmentTransaction.commit();
    }
}
