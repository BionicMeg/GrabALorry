package com.example.owner.grabalorry;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.lang.Integer.parseInt;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private TextView mHomeName;
    private ImageView mGrabALorry;
    private ImageView mMaLorry;
    private TextView mHomeBalance;

    private static String mStaticEmail;
    private static String mStaticUid;


    private static final String TAG = "NavigationActivity";

    private static List<Record> mRecord;//booking
    private static List<Record> mBusinessRecord;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setTextNavHeader();

        setHome();


        mRecord = new ArrayList<>();
        mBusinessRecord=new ArrayList<>();

        fetchUser();






        //mAuth = FirebaseAuth.getInstance();
        //user = mAuth.getCurrentUser();

/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /*this code make every item in the navigation have a line
        NavigationMenuView navMenuView = (NavigationMenuView) navigationView.getChildAt(0);
        navMenuView.addItemDecoration(new DividerItemDecoration(NavigationActivity.this,DividerItemDecoration.VERTICAL));
        */

        //function set name, and email at the navgation drawer's header




        mGrabALorry = (ImageView) findViewById(R.id.rectshape2);
        mGrabALorry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(NavigationActivity.this,MapsActivity.class);
                startActivity(i);

            }
        });

        mMaLorry = (ImageView) findViewById(R.id.rectshape3);
        mMaLorry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(NavigationActivity.this,UrlorryActivity.class);
                startActivity(i);

            }
        });


    }

    private void setHome(){

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        String e="";

        if(mUser!=null) {
            e = mUser.getEmail();
        }

        final String mEmail = e;
        mStaticEmail = e;

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

                                                if(mUser!=null) {



                                                    String mName = document.getString("name");

                                                    setNavHeaderTextFunction(mEmail, mName);

                                                    mHomeName = (TextView) findViewById(R.id.home_name);
                                                    mHomeName.setText(mName);//set home page name according to database


                                                    int dataPoint = document.getLong("point").intValue();
                                                    String display = ""+dataPoint;

                                                    mHomeBalance = (TextView) findViewById(R.id.balanceValue);
                                                    mHomeBalance.setText(display);

//
                                                //Log.d(TAG, "POINT VALUE:" + dataPoint);
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

    }

    private void setNavHeaderTextFunction(String email, String name){

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);//set navigation view to a variable
        View v= navigationView.inflateHeaderView(R.layout.nav_header_navigation);//inflate the header with the nav_header_navigation xml

        TextView profileName = (TextView) v.findViewById(R.id.profile_name);
        TextView profileEmail = (TextView) v.findViewById(R.id.profile_email);//set the text view's text

        profileEmail.setText(email);//set email to textview
        profileName.setText(name);//set email to textview

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Locale locale = null;
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_En) {
            locale = new Locale("en_US");
        }
        else if(id == R.id.action_Zh) {
            locale = new Locale("zh");
        }

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, NavigationActivity.class);
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(refresh);
        finish();
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if (id == R.id.nav_profile) {

            Intent i = new Intent(NavigationActivity.this,ProfileActivity.class);
            startActivity(i);
            finish();

            // Handle the profile action
        } else if (id == R.id.nav_top_up) {
            Intent i = new Intent(NavigationActivity.this,TopUpActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_history) {
            Intent i = new Intent(NavigationActivity.this,RecordListActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_logout) {
            if (mUser != null) {
                signOut();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser signOutUser= mAuth.getCurrentUser();
        if(signOutUser ==null){
            Toast.makeText(NavigationActivity.this,
                    "Logged Out",
                    Toast.LENGTH_SHORT).show();
        }

        Intent i = new Intent(NavigationActivity.this,LoginActivity.class);
        startActivity(i);

    }

    public static String getStaticEmail(){return mStaticEmail;}
    public static String getStaticUid(){return mStaticUid;}












    public static List<Record> getRecord(){
        return mRecord;
    }
    public static List<Record> getBusinessRecord(){
        return mBusinessRecord;
    }


    private void fetchUser(){

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
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


                                    if (mEmail.equals(email)) {
                                        mStaticUid=document.getId();

                                        fetchRecord(mStaticUid);

                                        fetchBusinessRecord(mStaticUid);

                                    }
                                }

                            }
                        } else {
                            Log.w("RecordLab", "Error getting documents.", task.getException());
                        }
                    }
                });


    }


    private void fetchRecord(String id){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(id).collection("booking")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult()!=null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if(document!=null) {


                                    GeoPoint geoPointPick =  document.getGeoPoint("pickUpLocation");
                                    GeoPoint geoPointDestiny =  document.getGeoPoint("destinyLocation");

                                    LatLng pickUp = new LatLng(geoPointPick.getLatitude(), geoPointPick.getLongitude());
                                    LatLng destiny = new LatLng(geoPointDestiny.getLatitude(), geoPointDestiny.getLongitude());

                                    if(document.getString("partnerId").toString().equals("")) {
                                        Record record = new Record(
                                                document.getString("lorryServiceType").toString(),
                                                document.getLong("cost").intValue(),
                                                document.getDate("date"),
                                                document.getString("status").toString(),
                                                pickUp,
                                                destiny,
                                                document.getLong("weightRequired").intValue()
                                        );


                                        mRecord.add(record);
                                    }
                                    else{
                                        Record record = new Record(
                                                document.getString("lorryServiceType").toString(),
                                                document.getLong("cost").intValue(),
                                                document.getDate("date"),
                                                document.getString("status").toString(),
                                                pickUp,
                                                destiny,
                                                document.getLong("weightRequired").intValue(),
                                                document.getString("partnerId").toString(),
                                                document.getString("partnerDocId").toString()
                                        );

                                        record.setEmail(document.getString("partnerEmail").toString());
                                        mRecord.add(record);
                                    }


                                }

                            }
                            for(int i=0;i<mRecord.size();i++){
                                Log.d("RecordLab","in database "+ mRecord.get(i).toString());
                            }
                            //setRecord(mRecord);
                        } else {
                            Log.w("RecordLab", "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    private void fetchBusinessRecord(String id){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(id).collection("business")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult()!=null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if(document!=null) {


                                    GeoPoint geoPointPick =  document.getGeoPoint("pickUpLocation");
                                    GeoPoint geoPointDestiny =  document.getGeoPoint("destinyLocation");

                                    LatLng pickUp = new LatLng(geoPointPick.getLatitude(), geoPointPick.getLongitude());
                                    LatLng destiny = new LatLng(geoPointDestiny.getLatitude(), geoPointDestiny.getLongitude());

                                    Record record = new Record(
                                            document.getString("lorryServiceType").toString(),
                                            document.getLong("cost").intValue(),
                                            document.getDate("date"),
                                            document.getString("status").toString(),
                                            pickUp,
                                            destiny,
                                            document.getLong("weightRequired").intValue(),
                                            document.getString("partnerId").toString(),
                                            document.getString("partnerDocId").toString()
                                            );
                                    record.setEmail(document.getString("partnerEmail").toString());
                                    mBusinessRecord.add(record);

                                }

                            }
                            for(int i=0;i<mRecord.size();i++){
                                Log.d("RecordLab","in database "+ mRecord.get(i).toString());
                            }
                            //setRecord(mRecord);
                        } else {
                            Log.w("RecordLab", "Error getting documents.", task.getException());
                        }
                    }
                });

    }















}
