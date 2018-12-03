package com.example.owner.grabalorry;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class TopUpActivity extends AppCompatActivity {
    private static final String TAG = "TopUpActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private EditText mTopUp;
    private Button mTopUpBtn;

    private int value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);




            mTopUpBtn = (Button) findViewById(R.id.topupBtn);
            mTopUpBtn.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     try {

                         mTopUp = (EditText) findViewById(R.id.topupEdit);
                         String mTU = mTopUp.getText().toString();
                         value = parseInt(mTU);

                         setDatabasePoint(value);


                     }
                     catch(Exception e){
                         Log.d(TAG, "top up error: ", e );
                     }
                 }




            });




    }

    private void setDatabasePoint(int v){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        String e="";

        if(mUser!=null) {
            e = mUser.getEmail();
        }

        final String mEmail = e;
        final int value = v;

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if (document != null) {
                                    String email = document.getString("email");
                                    //Log.d(TAG, mEmail + "   " + email);

                                    if (mEmail.equals(email)) {
                                        try {
                                            if (document.get("point") != null) {

                                                int dataPoint = document.getLong("point").intValue();

                                                //Log.d(TAG, "POINT VALUE:" + dataPoint);

                                                dataPoint+=value;

                                                inputProfile(dataPoint,mEmail);
                                            }
                                            else{
                                                inputProfile(value, mEmail);
                                            }
                                        } catch (Exception ex) {

                                            Log.d(TAG, "Error: ", ex);
                                        }

                                    }
                                    else {
                                        Log.w(TAG, "Error getting documents.", task.getException());
                                    }
                                }
                            }
                        }
                    }
                });//end of fetch data


        //inputProfile(value,mEmail);


    }

    private void inputProfile(int number, String e){


        final String mEmail = e;
        final int topupvalue = number;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult()!=null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if(document!=null) {

                                    String email = document.getString("email");

                                    //Log.d(TAG, mEmail + "   " + email);

                                    if (mEmail.equals(email)) {

                                        updateValue(topupvalue, document.getId());

                                    }
                                }

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        //RecordLab userLab = RecordLab.get();
        //user = userLab.getUser();

    }

    private void updateValue(int topUpValue, String id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();
        user.put("point", topUpValue);

        db.collection("users").document(id).update(user);

        Intent i = new Intent(TopUpActivity.this, NavigationActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

}
