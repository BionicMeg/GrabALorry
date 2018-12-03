package com.example.owner.grabalorry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private static final String TAG = "RegisterActivity";


    private EditText email;
    private EditText pass;
    private EditText confirmPass;
    private EditText username;
    private boolean success;

    //private DatabaseReference rootRef, userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        //rootRef = FirebaseDatabase.getInstance().getReference();


        //button
        Button mRegister = (Button) findViewById(R.id.btnRegistrationUser);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username = (EditText) findViewById(R.id.editUsernameRegister);
                email = (EditText) findViewById(R.id.txtEmailRegistration);
                pass = (EditText) findViewById(R.id.txtPasswordRegistration);
                confirmPass = (EditText) findViewById(R.id.txtConfirmPasswordRegistration);

                //declaration
                final String mEmail = email.getText().toString();
                String mPass = pass.getText().toString();
                String mConfirmPass = confirmPass.getText().toString();
                final String mName = username.getText().toString();


                //validate length
                if(mPass.length() != mConfirmPass.length()){
                    Toast.makeText(RegistrationActivity.this,
                            "Password does not have same length!",
                            Toast.LENGTH_SHORT).show();
                }
                else if(validateEmailPass(email, pass, confirmPass, username, mEmail,mPass,mConfirmPass, mName)){//validation
                    Log.d(TAG, "Registering");



/*
                    //failed**********
                    Intent intent= new Intent(RegistrationActivity.this,LoginActivity.class);
                    intent.putExtra("message",mName);
                    startActivityForResult(intent,1);
                    //setResult(1,intent);
*/

                    mAuth.createUserWithEmailAndPassword(mEmail, mPass)
                            .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    try {

                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "Account Created");

                                            //DocumentReference document = db.collection("users").document();

                                            success=true;
                                            submitData(mName,mEmail);



                                        } else {

                                            Log.d(TAG, "Account Registration Failed");
                                            Toast.makeText(RegistrationActivity.this,
                                                    "Account Registration Failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                    /* catch (FirebaseAuthUserCollisionException e) {
                                        Log.d(TAG, "Register Account Failed", task.getException());

                                    }*//* catch (FirebaseNetworkException e) {
                                        Log.d(TAG, "Register Account Failed", task.getException());

                                    } */
                                    catch (Exception e) {
                                        Log.d(TAG, "Register Account Failed", task.getException());

                                    }

                                }//end of function incomplete

                            });//end of firebase register function

                }
                else{
                    Toast.makeText(RegistrationActivity.this,
                            "Something Went Wrong, Please Try Again.",
                            Toast.LENGTH_SHORT).show();
                }//end of if else and validation for registration

                //start add to database


            }//endof onclick of button


        });


    }

    private void submitData(String mName, String mEmail){
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        Map<String, Object> user = new HashMap<>();
        user.put("name", mName);
        user.put("email", mEmail);
        user.put("point",0);
        user.put("userLorryWeight",0);
        user.put("noPlat","");

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                        Toast.makeText(RegistrationActivity.this,
                                "Account Registration Successful.",
                                Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });


    }



/*read data
    db.collection("users")
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
                                             */
    /*
    private void setUsername(String n){
        Intent data = new Intent();
        data.putExtra(EXTRA_NAME_SHOWN, n);//set the var in quiz activity (mIsCheated)
        setResult(RESULT_OK, data);
    }

    public static Intent newIntent(Context packageContext, String n){
        Intent i = new Intent(packageContext, LoginActivity.class);
        i.putExtra(EXTRA_NAME_TRUE,n);//set the constant string to true or false according to the answer
        return i;
    }

    public static String returnName(Intent result){
        return result.getStringExtra(EXTRA_NAME_SHOWN);//set the constant var to true
    }
    */

    private boolean validateEmailPass(EditText email, EditText pass, EditText confirmPass, EditText username,String e , String p1, String p2, String n) {
        boolean valid = true;

        if (n.length() == 0) {
            username.setError("Required.");
            valid = false;
        }
        else{
            username.setError(null);
        }

        if (e.length() == 0) {
            email.setError("Required.");
            valid = false;
        }
        else if(!e.contains("@")){
            email.setError("Not an email ID.");
            valid = false;
        }
        else{
            email.setError(null);
        }

        if (p1.length() == 0) {
            pass.setError("Password Required.");
            valid = false;
        }
        else if(p1.length() < 6){
            pass.setError("Longer Password Required.");
            valid = false;
        }
        else{
            pass.setError(null);
        }

        if (p2.length() == 0) {
            confirmPass.setError("Password Confirmation Required.");
            valid = false;
        }
        else if(p2.length() < 6){
            confirmPass.setError("Longer Password Required.");
            valid = false;
        }
        else if(!samePass(p1,p2)){
            confirmPass.setError("Password is not same.");
            valid = false;
        }
        else{
            confirmPass.setError(null);
        }

        Log.d(TAG, "Validation");


        return valid;
    }

    private boolean samePass(String p1, String p2){
        boolean samePass=true;

        for(int n=0; n<p1.length();n++){
            if(p1.charAt(n)!= p2.charAt(n)){
                samePass=false;
                break;
            }
        }

        return samePass;
    }


}
