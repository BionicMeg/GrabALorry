package com.example.owner.grabalorry;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ConfirmBusinessActivity extends AppCompatActivity {
    private static final String TAG="ConfirmBusiness";

    private Record record;

    private TextView mPickUp;
    private TextView mDestiny;
    private TextView mWeight;
    private TextView mFare;

    private Button mPickUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_business);

        mPickUp=(TextView) findViewById(R.id.businessPickUpValue);
        mDestiny=(TextView) findViewById(R.id.businessDestinyValue);
        mWeight=(TextView) findViewById(R.id.businessRequiredWeightValue);
        mFare=(TextView) findViewById(R.id.businessFareValue);
        mPickUpButton = (Button) findViewById(R.id.myLorryPickUpBtn);


        record = UrlorryActivity.getSearchedRecord();

        try{
            mPickUp.setText(getCompleteAddressString(record.getPickup().latitude, record.getPickup().longitude));
            mDestiny.setText(getCompleteAddressString(record.getDestiny().latitude, record.getDestiny().longitude));
            String w = "" + record.getLorryWeight();
            mWeight.setText(w);
            String c = "" + record.getCost();
            mFare.setText(c);
        }
        catch(Exception e){
            Log.e(TAG,"Error: ",e);
        }


        mPickUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertCollectionBusiness();

                updateStatus();

            }
        });


    }

    private void insertCollectionBusiness() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> business = new HashMap<>();
        business.put("weightRequired", record.getLorryWeight());
        business.put("pickUpLocation", getPickupGeoPoint());
        business.put("destinyLocation", getDestinyGeoPoint());
        business.put("cost", record.getCost());
        business.put("status","accepted");
        business.put("date",record.getDate());
        business.put("lorryServiceType","business");
        business.put("partnerId",UrlorryActivity.getRequesterID());
        business.put("partnerDocId",UrlorryActivity.getRequesterBookID());
        business.put("partnerEmail",record.getEmail());


        db.collection("users").document(NavigationActivity.getStaticUid()).collection("business")
                .add(business)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                        updatePartner(UrlorryActivity.getRequesterID(), UrlorryActivity.getRequesterBookID(), documentReference.getId());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    private void updatePartner(String reId, String reDocId, String partnerId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> book = new HashMap<>();
        book.put("partnerId",NavigationActivity.getStaticUid());
        book.put("partnerDocId",partnerId);
        book.put("partnerEmail",NavigationActivity.getStaticEmail());

        db.collection("users").document(reId).collection("booking").document(reDocId).update(book);

    }


    private void updateStatus(){
        String id = UrlorryActivity.getRequesterID();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> update = new HashMap<>();
        update.put("status","accepted");

        db.collection("users").document(id).collection("booking").document(UrlorryActivity.getRequesterBookID()).update(update);

        gotoHome();
    }

    private void gotoHome(){
        Intent i = new Intent(ConfirmBusinessActivity.this, NavigationActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }


    private GeoPoint getPickupGeoPoint(){

        return (new GeoPoint(record.getPickup().latitude,record.getPickup().longitude));
    }

    private GeoPoint getDestinyGeoPoint(){

        return (new GeoPoint(record.getDestiny().latitude,record.getDestiny().longitude));
    }


    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }
}
