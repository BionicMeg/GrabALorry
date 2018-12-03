package com.example.owner.grabalorry;

import android.support.v4.app.Fragment;

public class RecordListActivity extends HistorySingleFragmentActivity {


    @Override
    protected Fragment createFragment(){
        return new RecordListFragment();
    }
}
