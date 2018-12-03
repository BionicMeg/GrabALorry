package com.example.owner.grabalorry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

//import static android.support.constraint.Constraints.TAG;

public class LorryFragment extends Fragment {
    private static final String TAG = "FragmentActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private RelativeLayout layoutToAdd;
    private RelativeLayout layoutDelete;

    private static LatLng pickupPosition;
    private static LatLng destinyPosition;

    private static Double value;

    private TextView bookDistance;
    private TextView bookPoint;
    private Button mBtn;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_initial, container, false);


        //EditText mPickUp = (EditText) view.findViewById(R.id.editPickUpLocation);
        //EditText mDestiny = (EditText) view.findViewById(R.id.editDestinyLocation);

        final LayoutInflater in = inflater;
        final ViewGroup con = container;
        final View v = view;




        try {

            ImageView mArrowNav = (ImageView) view.findViewById(R.id.arrow_navi1);
            mArrowNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try{
                        EditText editWeight = (EditText) v.findViewById(R.id.editRequiredWeight);
                        final int mWeight = parseInt(editWeight.getText().toString());

                        //remove layouts
                        ViewGroup vg = (ViewGroup) v.findViewById(R.id.content);

                        layoutDelete = (RelativeLayout) v.findViewById(R.id.initial_relative);
                        layoutDelete.removeAllViewsInLayout();

                        layoutToAdd = (RelativeLayout) v.findViewById(R.id.layoutParent);

                    //.getLayoutParams().height = parseInt("150dp");

                        view = in.inflate(R.layout.fragment_booking,vg);
                        layoutToAdd.addView(view);

                        //get distance
                        value = CalculationByDistance(pickupPosition,destinyPosition);

                        //set distance
                        bookDistance = (TextView) view.findViewById(R.id.bookDistanceTextView);
                        bookDistance.setText(getValue());

                        //set point
                        bookPoint = (TextView) view.findViewById(R.id.bookCostTextView);
                        bookPoint.setText(getValue());

                        mBtn = (Button) view.findViewById(R.id.bookSubmitBtn);
                        mBtn.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {

                                try{
                                    inputBookDetail(mWeight, getPickupGeoPoint(), getDestinyGeoPoint(),(int) parseDouble(getValue()));
                                    backToHome();
                                }
                                catch(Exception e){
                                    Log.e(TAG,"Error: ",e);
                                }

                            }
                        });

                    }
                    catch(Exception e){
                        Log.e(TAG,"Error: ",e);
                    }


                }
            });

        }
        catch(Exception e){
            Log.e(TAG,"Error: ",e);
        }


        PlaceAutocompleteFragment autocompletePickupFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.pickup_autocomplete_fragment);

        PlaceAutocompleteFragment autocompleteDestinyFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.destiny_autocomplete_fragment);



/*
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this.getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
*/

        //PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
        //      getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        /*
         * The following code example shows setting an AutocompleteFilter on a PlaceAutocompleteFragment to
         * set a filter returning only results with a precise address.
         */
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();


        autocompletePickupFragment.setFilter(typeFilter);
        autocompletePickupFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());//get place details here

                //retrieve the position name to latlng and give value to static variable to be accessed to mapsactivity
                sendPickUpLocation(place.getName().toString());

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        autocompleteDestinyFragment.setFilter(typeFilter);
        autocompleteDestinyFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());//get place details here

                sendDestinyLocation(place.getName().toString());

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        return view;
    }

    private void backToHome(){
        Intent i = new Intent(this.getActivity(),NavigationActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void inputBookDetail(final int mWeight, final GeoPoint pickUp, final GeoPoint destiny, final int value){

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

                                        insertCollection(mWeight, pickUp, destiny, value , document.getId());
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

    private void insertCollection(final int mWeight, final GeoPoint pickUp, final GeoPoint destiny, final int value, final String id) {

        Date date = new Date();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> book = new HashMap<>();
        book.put("weightRequired", mWeight);
        book.put("pickUpLocation", pickUp);
        book.put("destinyLocation", destiny);
        book.put("cost", value);
        book.put("status","pending");
        book.put("date",date);
        book.put("lorryServiceType","request");
        book.put("partnerId","");
        book.put("partnerDocId","");

        db.collection("users").document(id).collection("booking")
                .add(book)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public Double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        double kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return kmInDec;
    }


    private void sendPickUpLocation(String address){

        pickupPosition = getLocationFromAddress(this.getActivity(),address);
        //destinyPosition = getLocationFromAddress(this.getActivity(),address)

        //declare latlng
        //LatLng position = getLocationFromAddress(this.getActivity(),address);

        //Bundle args = new Bundle();
        //args.putParcelable("longLat_dataPrivider", position);

        //Intent i = new Intent(this.getActivity(), MapsActivity.class);
        //i.putExtras(args);
        //startActivity(i);

    }

    private void sendDestinyLocation(String address){
        destinyPosition = getLocationFromAddress(this.getActivity(),address);

    }

    private static String getValue(){
        return ""+value;
    }

    public static LatLng getPickUp(){
        return pickupPosition;
    }

    public static LatLng getDestiny(){
        return destinyPosition;
    }

    private GeoPoint getPickupGeoPoint(){

        return (new GeoPoint(getPickUp().latitude,getPickUp().longitude));
    }

    private GeoPoint getDestinyGeoPoint(){

        return (new GeoPoint(getDestiny().latitude,getDestiny().longitude));
    }

  //get location function
    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }


/*
    private void callPlaceAutocompleteActivityIntent() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this.getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            //PLACE_AUTOCOMPLETE_REQUEST_CODE is integer for request code
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }

    }
*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //autocompleteFragment.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this.getActivity(), data);
                Log.i(TAG, "Place:" + place.toString());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this.getActivity(), data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }


}
