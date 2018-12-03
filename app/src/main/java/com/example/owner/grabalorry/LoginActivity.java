package com.example.owner.grabalorry;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private static final String TAG = "LoginActivity";

    private EditText email;
    private EditText password;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();

        //set status bar color to avoid all white and causes status cant be seen

        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        //end of status bar color edit

        Button mlogin = (Button) findViewById(R.id.btnUserLogin);
        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = (EditText) findViewById(R.id.txtEmailLogin);
                String mEmail = email.getText().toString();

                password = (EditText) findViewById(R.id.txtPasswordLogin);
                String mPass = password.getText().toString();



                if(validateLoginEmailPass(email,password,mEmail,mPass)){
                    try {
                        performLoginActivity(mEmail, mPass);

                    }
                    catch(Exception e){
                        Log.d(TAG,""+e);
                    }
                }


            }
        });



        TextView mRegister = (TextView) findViewById(R.id.registerText);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent (LoginActivity.this,RegistrationActivity.class);


                //startActivityForResult(i,1);

                startActivity(i);
            }
        });

    }

    private void performLoginActivity(final String email, final String password){
        mAuth.fetchProvidersForEmail(email).addOnCompleteListener(
                this, new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "checking to see if user exists in firebase or not");
                            ProviderQueryResult result = task.getResult();

                            if(result != null && result.getProviders()!= null
                                    && result.getProviders().size() > 0){
                                Log.d(TAG, "User exists, trying to login using entered credentials");

                                Toast.makeText(LoginActivity.this,
                                        "Logging in",
                                        Toast.LENGTH_SHORT).show();

                                performLogin(email, password);
                            }else{
                                Log.d(TAG, "User doesn't exist, create account");
                                //go to register eh
                                //registerAccount(email, password);
                                Toast.makeText(LoginActivity.this,
                                        "No account",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(TAG, "User check failed", task.getException());
                            Toast.makeText(LoginActivity.this,
                                    "There is a problem, please try again later.",
                                    Toast.LENGTH_SHORT).show();

                        }
                        //hide progress dialog
                        //hideProgressDialog();
                        //enable and disable login, logout buttons depending on signin status
                        //showAppropriateOptions();
                    }
                });
    }

    private void performLogin(String email, String password) {


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "login success");

                            Toast.makeText(LoginActivity.this,
                                    "Login successful",
                                    Toast.LENGTH_SHORT).show();

                            Intent i = new Intent (LoginActivity.this,NavigationActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);//make sure after login, back will not goto login page
                            startActivity(i);

                        } else {
                            Log.e(TAG, "Login fail", task.getException());
                            Toast.makeText(LoginActivity.this,
                                    "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        //hide progress dialog
                        //hideProgressDialog();
                        //enable and disable login, logout buttons depending on signin status
                        //showAppropriateOptions();
                    }
                });
    }

    private boolean validateLoginEmailPass(EditText email, EditText pass, String e , String p) {
        boolean valid = true;

        if (e.length() == 0) {
            email.setError("Required.");
            valid = false;
        } else if (!e.contains("@")) {
            email.setError("Not an email ID.");
            valid = false;
        } else {
            email.setError(null);
        }

        if (p.length() == 0) {
            pass.setError("Password Required.");
            valid = false;
        } else if (p.length() < 6) {
            pass.setError("Password Incorrect.");
            valid = false;
        } else {
            pass.setError(null);
        }

        return valid;
    }









/*
// Read from the database
myRef.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // This method is called once with the initial value and again
        // whenever data at this location is updated.
        String value = dataSnapshot.getValue(String.class);
        Log.d(TAG, "Value is: " + value);
    }

    @Override
    public void onCancelled(DatabaseError error) {
        // Failed to read value
        Log.w(TAG, "Failed to read value.", error.toException());
    }
});*/


}
