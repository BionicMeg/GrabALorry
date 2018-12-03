package com.example.owner.grabalorry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;


public abstract class HistorySingleFragmentActivity extends FragmentActivity {
    protected abstract Fragment createFragment();
    //abstract, child must write the function
    //to force child fill tat up

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_record_container);

        if(fragment == null){
            fragment = createFragment();
            //from crime fragment u create an object
            fm.beginTransaction()
                    .add(R.id.fragment_record_container, fragment)//put this fragment into the fragment container
                    .commit();//commit the add from fragment to fragment_container
        }
    }
    //above eh actually in crime activityincluding oncreate and abstract
    // juz public abstract class singleton.... extend... different

    //specific put in child
    //common put in parent

    @Override
    public void onBackPressed() {
        Intent i = new Intent(HistorySingleFragmentActivity.this, NavigationActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

}
