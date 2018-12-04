package com.example.owner.grabalorry;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.core.utilities.Utilities;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class UrlorryActivity extends AppCompatActivity {
    private static final String TAG = "UrlorryActivity";

    private EditText mNoPlat;
    private EditText mWeight;

    private Button mSubmit;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private static Record localRecord;
    //private static boolean doneSearch = false;

    private static String lorryPickArea;
    private static String lorryDestinyArea;

    private static int mOwnLorryWeight;

    private static boolean noWeight = false;
    private static boolean noNumPlat = false;

    private static boolean searchResult = false;

    private static String requesterID="";
    private static String requesterBookID="";
    private static String requesterEmail="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urlorry);

        SwitchCompat mSwitch = (SwitchCompat) findViewById(R.id.lorrySwitch);

        mNoPlat = (EditText) findViewById(R.id.myLorryNoPlatEditText);
        mWeight = (EditText) findViewById(R.id.myLorryWeightEditText);
        //mLorryPickUp = (EditText) findViewById(R.id.myLorryArriveEditText);
        //mLorryDestiny = (EditText) findViewById(R.id.myLorryBackEditText);
        mSubmit = (Button) findViewById(R.id.myLorrySubmitBtn);

        getOwnWeight();

        //start of pickup Area
        PlaceAutocompleteFragment autocompletePickUpAreaFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.myLorryArriveAutoComplete);

        autocompletePickUpAreaFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();


        autocompletePickUpAreaFragment.setFilter(typeFilter);
        autocompletePickUpAreaFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());//get place details here

                //retrieve the position name to latlng and give value to static variable to be accessed to mapsactivity
                //sendPickUpLocation(place.getName().toString());

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        //end of pickup area

        //start of destiny area
        PlaceAutocompleteFragment autocompleteDestinyAreaFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.myLorryBackAutoComplete);

        autocompletePickUpAreaFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                //Log.i(TAG, "Place: " + place.getName());
                lorryPickArea=place.getName().toString();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        autocompleteDestinyAreaFragment.setFilter(typeFilter);
        autocompleteDestinyAreaFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                //Log.i(TAG, "Place: " + place.getName());//get place details here

                lorryDestinyArea=place.getName().toString();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
        //end of destiny area




        mNoPlat.setEnabled(false);
        mWeight.setEnabled(false);
        //mLorryPickUp.setEnabled(false);
        //mLorryDestiny.setEnabled(false);
        mSubmit.setEnabled(false);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    mNoPlat.setEnabled(true);
                    mWeight.setEnabled(true);
                    mSubmit.setEnabled(true);
                }
                else {

                    mNoPlat.setEnabled(false);
                    mWeight.setEnabled(false);
                    mSubmit.setEnabled(false);
                }
            }
        });

        mSubmit = (Button) findViewById(R.id.myLorrySubmitBtn);
        mSubmit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){

                String noPlat = mNoPlat.getText().toString();
                String weight = mWeight.getText().toString();

                if(weight.equals("")){
                    weight=""+0;
                }


                if (lorryValidation(mNoPlat,mWeight,noPlat,parseInt(weight))){
                    String id = NavigationActivity.getStaticUid();

                    updateProfile(noPlat,parseInt(weight),id);

                        searchBooking();



                }


            }

        });




    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //autocompleteFragment.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place:" + place.toString());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                Log.i(TAG,"canceled");
            }
        }
    }


    private boolean lorryValidation(EditText mNoPlat,EditText mWeight, String noPlat, int weight){
        boolean valid=true;

        if(noWeight){
            if(mWeight.getText().length()==0){
                mWeight.setError("Required.");
                valid = false;
            }
            else if(weight<=0){
                mWeight.setError("Invalid input.");
                valid = false;
            }
            else{
                mWeight.setError(null);
            }
        }

        Log.d(TAG,""+noPlat.length());
        if(noNumPlat) {
            if(noPlat.length() == 0) {
                mNoPlat.setError("Required.");
                valid = false;
            }
            else if (noPlat.length() > 7) {
                mNoPlat.setError("Number Plat too long.");
                valid = false;
            } else if (!validateNoPlat(noPlat)) {
                mNoPlat.setError("Number Plat Format Wrong. Use format XXX1234");
                valid = false;
            } else {
                mNoPlat.setError(null);
            }
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

            }
            else if(noMoreLetter){
                count+=1;

                //Toast.makeText(ProfileActivity.this,"2",Toast.LENGTH_SHORT).show();

            }

            if(n<1 && !Character.isLetter(c)){
                valid=false;


            }
            else if(n<3 && !(Character.isDigit(c) || Character.isLetter(c)) ){
                valid=false;

            }
            else if((n==2 && noMoreLetter) && !Character.isDigit(c)){
                valid=false;

            }
            else if(n>=3 && !Character.isDigit(c)){
                valid=false;

            }
            else if(count>=4){
                valid=false;

            }
        }

        return valid;
    }


    private void searchBooking(){

        //final String mEmail = NavigationActivity.getStaticEmail();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult()!=null) {
                            for (QueryDocumentSnapshot firstDocument : task.getResult()) {

                                if(firstDocument.getId().equals(NavigationActivity.getStaticUid())){
                                    mOwnLorryWeight=firstDocument.getLong("userLorryWeight").intValue();
                                }

                                if (!firstDocument.getId().equals(NavigationActivity.getStaticUid())) {
                                    requesterID=firstDocument.getId();
                                    requesterEmail=firstDocument.getString("email").toString();
                                    goInSubCollection(firstDocument.getId());
                                }

                            }
                        }
                        else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                    }

                });

        //RecordLab userLab = RecordLab.get();
        //user = userLab.getUser();

    }

    private void goInSubCollection(final String uid){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(uid).collection("booking")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG,"Fetch "+searchResult);

                                if(searchResult)
                                    break;

                                if (document != null) {

                                    requesterBookID = document.getId();

                                    String status = document.getString("status");
                                    int weightRequired = document.getLong("weightRequired").intValue();

                                    Log.d(TAG,""+mOwnLorryWeight);
                                    Log.d(TAG,"weight compare "+(mOwnLorryWeight >= weightRequired) +""+ status.equals("pending"));

                                    if (mOwnLorryWeight >= weightRequired && status.equals("pending")) {

                                        GeoPoint geoPointPick = document.getGeoPoint("pickUpLocation");
                                        GeoPoint geoPointDestiny = document.getGeoPoint("destinyLocation");

                                        LatLng pickUp = new LatLng(geoPointPick.getLatitude(), geoPointPick.getLongitude());
                                        LatLng destiny = new LatLng(geoPointDestiny.getLatitude(), geoPointDestiny.getLongitude());

                                        String pickUpArea = "";
                                        String destinyArea = "";

                                        Log.d(TAG,"Fetch "+pickUp+" "+ destiny);

                                        try {
                                            Geocoder gcdPickUp = new Geocoder(UrlorryActivity.this, Locale.getDefault());
                                            List<Address> addressesPickUp = gcdPickUp.getFromLocation(geoPointPick.getLatitude(), geoPointPick.getLongitude(), 1);
                                            if (addressesPickUp.size() > 0)
                                                pickUpArea = addressesPickUp.get(0).getLocality();


                                            Geocoder gcdDestiny = new Geocoder(UrlorryActivity.this, Locale.getDefault());
                                            List<Address> addressesDestiny = gcdDestiny.getFromLocation(geoPointDestiny.getLatitude(), geoPointDestiny.getLongitude(), 1);
                                            if (addressesDestiny.size() > 0)
                                                destinyArea = addressesDestiny.get(0).getLocality();

                                            Log.d(TAG,"list of address destiny"+addressesDestiny);

                                            Log.d(TAG," destiny"+lorryDestinyArea.equals(destinyArea)+" pickup "+ lorryPickArea.equals(pickUpArea));

                                            Log.d(TAG,"from input destiny "+lorryDestinyArea);
                                            Log.d(TAG,"from database destiny "+destinyArea);
                                            Log.d(TAG,"from input pickup "+lorryPickArea);
                                            Log.d(TAG,"from database pickup "+pickUpArea);

                                            if (lorryDestinyArea.equals(destinyArea) && lorryPickArea.equals(pickUpArea)) {
                                                searchResult=true;


                                                localRecord = new Record(
                                                        document.getString("lorryServiceType").toString(),
                                                        document.getLong("cost").intValue(),
                                                        document.getDate("date"),
                                                        document.getString("status").toString(),
                                                        pickUp,
                                                        destiny,
                                                        document.getLong("weightRequired").intValue()
                                                );
                                                Log.d(TAG, "inside  " + localRecord.toString());

                                                localRecord.setEmail(requesterEmail);

                                                setSearchedRecord(localRecord);



                                                goConfirm();


                                                //doneSearch = true;

                                                break;
                                            }

                                        }
                                        catch (IOException e) {
                                            Log.e(TAG, "IOException: ", e);
                                        }


                                    }
                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        }
                    }
                });//end of listener inside forloop of first document
    }

    private void updateProfile(String noPlat, int weight, String id) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();
        if(!noPlat.equals(""))
         user.put("noPlat", noPlat);
        if(noWeight)
         user.put("userLorryWeight", weight);

        if(!noPlat.equals("") || noWeight)
         db.collection("users").document(id).update(user);
    }

    private void getOwnWeight(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final String id = NavigationActivity.getStaticUid();

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if (document != null && document.getId().equals(id)) {

                                    if(document.getString("noPlat").equals(""))
                                        noNumPlat=true;
                                    if(document.getLong("userLorryWeight").intValue()==0) {
                                        noWeight = true;
                                    }
                                    else {
                                        mOwnLorryWeight = document.getLong("userLorryWeight").intValue();
                                    }

                                }

                            }

                        }

                    }

                });

    }


    public static Record getSearchedRecord(){return localRecord;}
    private static void setSearchedRecord(Record re){
        localRecord = re;
    }

    public static String getRequesterID(){return requesterID;}
    public static String getRequesterBookID(){return requesterBookID;}

    private void goConfirm(){
        Intent i = new Intent(UrlorryActivity.this, ConfirmBusinessActivity.class);
        startActivity(i);
    }




}
