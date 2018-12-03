package com.example.owner.grabalorry;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1beta1.WriteResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private EditText editName;
    private EditText editNoPlat;
    private EditText editWeight;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        submit = (Button) findViewById(R.id.profSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editName = (EditText) findViewById(R.id.profEditName);
                editNoPlat = (EditText) findViewById(R.id.profEditNoPlat);
                editWeight = (EditText) findViewById(R.id.profEditWeight);

                String name = "";
                String noPlat = "";
                String weight = "";

                 name = editName.getText().toString();
                 noPlat = editNoPlat.getText().toString();
                 weight = editWeight.getText().toString();


                if(weight.equals("")){
                    weight=""+0;
                }

                if (profileValidation(editName, editNoPlat, editWeight, name, noPlat, weight)){

                    inputProfile(name, noPlat, weight);

                    Intent i = new Intent(ProfileActivity.this,NavigationActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);

                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this,NavigationActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }


    private void inputProfile(final String name, final String noPlat, final String weight){

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        String e="";

        if(mUser!=null) {
            e = mUser.getEmail();
        }

        final String mEmail = e;

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

                                    updateProfile(name, noPlat, parseInt(weight), document.getId());
                                }
                            }

                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                }
            });



    }

    private void updateProfile(String name, String noPlat, int weight, String id){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();

        if(!name.equals(""))
        user.put("name", name);
        if(!noPlat.equals(""))
        user.put("noPlat", noPlat);
        if(weight!=0)
        user.put("userLorryWeight", weight);

        Toast.makeText(ProfileActivity.this,""+weight,Toast.LENGTH_SHORT).show();

        if(!name.equals("") || !noPlat.equals("") || weight!=0)
        db.collection("users").document(id).update(user);


    }

    private boolean profileValidation(EditText editName, EditText editNoPlat, EditText editWeight, String name, String noPlat, String weight){
        boolean valid = true;


        if(noPlat.length()>7){
            editNoPlat.setError("Number Plat too long.");
            valid = false;
        }
        else if(!validateNoPlat(noPlat)){
            editNoPlat.setError("Number Plat Format Wrong. Use format XXX1234");
            valid = false;
        }
        else{
            editNoPlat.setError(null);
        }



        return valid;


    }

    private boolean validateNoPlat(String np){
        boolean valid = true;

        boolean noMoreLetter=false;
        int count = 0;

        for (int n=0; n<np.length();n++){
            char c = np.charAt(n);

            if(n==1 && (Character.isDigit(c))){//!(Character.isLetter(c)) &&
                noMoreLetter = true;
                count+=1;
                //Toast.makeText(ProfileActivity.this,"1",Toast.LENGTH_SHORT).show();

            }
            else if(noMoreLetter){
                count+=1;

                //Toast.makeText(ProfileActivity.this,"2",Toast.LENGTH_SHORT).show();

            }

            if(n<1 && !Character.isLetter(c)){
                valid=false;

                //Toast.makeText(ProfileActivity.this,"3",Toast.LENGTH_SHORT).show();


            }
            else if(n<3 && !(Character.isDigit(c) || Character.isLetter(c)) ){
                valid=false;
                //Toast.makeText(ProfileActivity.this,"4",Toast.LENGTH_SHORT).show();

            }
            else if((n==2 && noMoreLetter) && !Character.isDigit(c)){
                valid=false;
                //Toast.makeText(ProfileActivity.this,"5",Toast.LENGTH_SHORT).show();

            }
            else if(n>=3 && !Character.isDigit(c)){
                valid=false;
                //Toast.makeText(ProfileActivity.this,"6",Toast.LENGTH_SHORT).show();

            }
            else if(count>=4){
                valid=false;
                //Toast.makeText(ProfileActivity.this,"7",Toast.LENGTH_SHORT).show();

            }
        }

        return valid;
    }
}
