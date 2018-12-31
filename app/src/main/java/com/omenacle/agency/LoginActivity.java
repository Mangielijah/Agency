package com.omenacle.agency;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText userEditText, passEditText;
    private FirebaseAuth mAuth;
    public  static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
        userEditText = (EditText) findViewById(R.id.username);
        passEditText = (EditText) findViewById(R.id.password);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

    }

    public void login(View view) {
        String username = userEditText.getText().toString().toLowerCase().trim();
        String password = passEditText.getText().toString().trim();

        if(!username.isEmpty()){
            if(!password.isEmpty())
            {

                String email = username + "@bookaam.com";
                final ProgressDialog progressDialog = new ProgressDialog(this);
                Log.d("Login Activity:", email +" : "+ password);
                progressDialog.setMessage("Login in please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.show();

                mAuth.signInWithEmailAndPassword(email, password)

                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if(user != null){
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Login failed.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });
            }
            else{
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.password_too_short), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {

            Toast.makeText(LoginActivity.this, getResources().getString(R.string.username_empty), Toast.LENGTH_SHORT).show();
        }
    }
}
