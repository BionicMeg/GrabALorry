package com.example.owner.grabalorry;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecordListFragment extends Fragment {
    //nothing yet

    private RecyclerView mRecordRecyclerView;
    private RecyclerView mBusinessRecordRecyclerView;
    private RecordAdapter mAdapter;

    private TextView mBook;
    private TextView mBusiness;

    private FrameLayout mFragmentContainer;

    //private RelativeLayout mBookRelative;
    //private RelativeLayout mBusinessRelative;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_record_list, container, false);
        //view = inflater.inflate(R.layout.fragment_business_record_list,container,false);

        mRecordRecyclerView = (RecyclerView) view.findViewById(R.id.record_recycler_view);
        mRecordRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mBusinessRecordRecyclerView = (RecyclerView) view.findViewById(R.id.business_record_recycler_view);
        mBusinessRecordRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateBookUI();
        updateBusinessUI();


        mBook = (TextView) view.findViewById(R.id.history_book);
        mBusiness = (TextView) view.findViewById(R.id.history_business);

        final LayoutInflater in = inflater;
        final ViewGroup con = container;
        final View v = view;

        mBusinessRecordRecyclerView.setVisibility(View.GONE);



        mBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecordRecyclerView.setVisibility(View.VISIBLE);
                mBusinessRecordRecyclerView.setVisibility(View.GONE);

                mBook.setBackgroundColor(Color.parseColor("#0067e1"));
                mBook.setTextColor(Color.parseColor("#ffffff"));

                mBusiness.setBackgroundColor(Color.parseColor("#DCDCDC"));
                mBusiness.setTextColor(Color.parseColor("#000000"));

            }
        });

        mBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBusinessRecordRecyclerView.setVisibility(View.VISIBLE);
                mRecordRecyclerView.setVisibility(View.GONE);

                mBook.setBackgroundColor(Color.parseColor("#DCDCDC"));
                mBook.setTextColor(Color.parseColor("#000000"));

                mBusiness.setBackgroundColor(Color.parseColor("#0067e1"));
                mBusiness.setTextColor(Color.parseColor("#ffffff"));

            }
        });



        return view;
    }

    private void updateBookUI(){
        //RecordLab recordLab = RecordLab.get(getActivity());
        List<Record> record = NavigationActivity.getRecord();


        mAdapter = new RecordAdapter(record);
        mRecordRecyclerView.setAdapter(mAdapter);
    }

    private void updateBusinessUI(){
        //RecordLab recordLab = RecordLab.get(getActivity());
        List<Record> record = NavigationActivity.getBusinessRecord();


        mAdapter = new RecordAdapter(record);
        mBusinessRecordRecyclerView.setAdapter(mAdapter);
    }

    private class RecordHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        private Record mRecord;

        //public TextView mTitleTextView;

        //private TextView mPickUpTextView;
        //private TextView mDestinyTextView;
        private TextView mDateTextView;
        private TextView mStatusTextView;

        public RecordHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            //mTitleTextView = (TextView) itemView;

            //mPickUpTextView = (TextView) itemView.findViewById(R.id.list_item_record_pickup_text_view);
            //mDestinyTextView = (TextView) itemView.findViewById(R.id.list_item_record_destiny_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_record_date_text_view);
            mStatusTextView = (TextView) itemView.findViewById(R.id.list_item_record_status);
        }

        public void bindRecord(Record record){
            mRecord = record;

            //mPickUpTextView.setText(getCompleteAddressString(mRecord.getPickup().latitude,mRecord.getPickup().longitude));
            //mDestinyTextView.setText(getCompleteAddressString(mRecord.getDestiny().latitude,mRecord.getDestiny().longitude));
            mDateTextView.setText(mRecord.getDate().toString());
            mStatusTextView.setText(mRecord.getStatus());
        }
        // bind title to the title object, bind date to date object and bind checkbox to checkbox object

        @Override
        public void onClick(View v){
            //Toast.makeText(getActivity(),
            //        mRecord.getDate() + " clicked!", Toast.LENGTH_SHORT)
            //        .show();


            // custom dialog
            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.record_dialog);

            ImageView mClose = (ImageView) dialog.findViewById(R.id.dialog_close_btn);
            mClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            // set the custom dialog components - text, image and button
            TextView mDate = (TextView) dialog.findViewById(R.id.dialog_date);
            TextView mEmail = (TextView) dialog.findViewById(R.id.dialog_email);
            TextView mPick = (TextView) dialog.findViewById(R.id.dialog_pickup);
            TextView mDestiny = (TextView) dialog.findViewById(R.id.dialog_destiny);
            TextView mWeight = (TextView) dialog.findViewById(R.id.dialog_weight);
            TextView mFare = (TextView) dialog.findViewById(R.id.dialog_fare);
            TextView mStatus = (TextView) dialog.findViewById(R.id.dialog_status);

            mDate.setText(mRecord.getDate().toString());

            LinearLayout emailLinear = (LinearLayout) dialog.findViewById(R.id.dialog_email_linear);

            if(mRecord.getEmail()==null){
                emailLinear.setVisibility(View.GONE);
            }
            else{
                mEmail.setText(mRecord.getEmail());
                emailLinear.setVisibility(View.VISIBLE);

            }
            //mEmail.setText(mRecord.getEmail());
            mPick.setText(getCompleteAddressString(mRecord.getPickup().latitude,mRecord.getPickup().longitude));
            mDestiny.setText(getCompleteAddressString(mRecord.getDestiny().latitude,mRecord.getDestiny().longitude));
            String w = ""+mRecord.getLorryWeight();
            Log.d("testing",mRecord.toString());
            mWeight.setText(w);
            String f = ""+mRecord.getCost();
            mFare.setText(f);
            mStatus.setText(mRecord.getStatus());

            final Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonPay);


            if(mRecord.getStatus().equals("accepted") && mRecord.getLorryType().equals("business"))
                dialogButton.setText(R.string.dialogComplete);
            else if(mRecord.getStatus().equals("completed") && mRecord.getLorryType().equals("request"))
                dialogButton.setText(R.string.pay);
            else{
                dialogButton.setText(R.string.dialogClose);
            }


            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getActivity(),""+mRecord.getStatus().equals("accepted") +""+ mRecord.getLorryType().equals("business"),Toast.LENGTH_SHORT).show();
                    if(mRecord.getStatus().equals("accepted") && mRecord.getLorryType().equals("business")){
                        goInSubCollection(NavigationActivity.getStaticUid());

                        mRecord.setStatus("completed");
                        mAdapter.notifyDataSetChanged();

                        dialog.dismiss();
                    }
                    else if(mRecord.getStatus().equals("completed") && mRecord.getLorryType().equals("request")){
                        makePayment();

                        mRecord.setStatus("paid");
                        mAdapter.notifyDataSetChanged();

                        dialog.dismiss();
                    }
                    else {
                        dialog.dismiss();
                    }
                }
            });

            dialog.show();
        }



        private void makePayment(){

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    //if user indatabase eh customer doc id is equivelant to record eh customer doc id and date is same
                                    if(document.getId().equals(NavigationActivity.getStaticUid())) {
                                        if (document.getLong("point").intValue()<mRecord.getCost()){
                                            Snackbar.make(getActivity().findViewById(android.R.id.content),"Point is not sufficient, Please top up.", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }
                                        else{
                                            updatePoint();

                                        }

                                    }

                                }
                            }
                        }
                    });

        }

        private void updatePoint(){
            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    //if user indatabase eh customer doc id is equivelant to record eh customer doc id and date is same
                                    if(document.getId().toString().equals(mRecord.getPartnerId())){
                                        int point = document.getLong("point").intValue();
                                        point =point+ mRecord.getCost();

                                        Map<String, Object> p1 = new HashMap<>();
                                        p1.put("point",point);

                                        db.collection("users").document(mRecord.getPartnerId()).update(p1);

                                    }

                                    if(document.getId().toString().equals(NavigationActivity.getStaticUid())){
                                        int point = document.getLong("point").intValue();
                                        point =point - mRecord.getCost();

                                        Map<String, Object> p2 = new HashMap<>();
                                        p2.put("point",point);

                                        db.collection("users").document(NavigationActivity.getStaticUid()).update(p2);

                                        goInOwnSubCollection();

                                        //updatePaidStatus(document.getId());
                                    }



                                }
                            }
                        }
                    });

        }

        private void updatePaidStatus(String id){
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> update = new HashMap<>();
            update.put("status","paid");

            //other user's update
            db.collection("users").document(mRecord.getPartnerId()).collection("business").document(mRecord.getPartnerDocId()).update(update);

            //own user update
            db.collection("users").document(NavigationActivity.getStaticUid()).collection("booking").document(id).update(update);

        }

        private void goInOwnSubCollection(){
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(NavigationActivity.getStaticUid()).collection("booking")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    //if user indatabase eh customer doc id is equivelant to record eh customer doc id and date is same
                                    if((document.getString("partnerDocId").toString().equals(mRecord.getPartnerDocId()))
                                            && document.getDate("date").equals(mRecord.getDate())) {
                                        updatePaidStatus(document.getId());
                                    }

                                }
                            }
                        }
                    });
        }

        private void goInSubCollection(final String uid){
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(uid).collection("business")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    //if user indatabase eh customer doc id is equivelant to record eh customer doc id and date is same
                                    if((document.getString("partnerDocId").toString().equals(mRecord.getPartnerDocId()))
                                            && document.getDate("date").equals(mRecord.getDate())) {
                                        updateStatusComplete(document.getId());
                                    }

                                }
                            }
                        }
                    });
        }


        private void updateStatusComplete(String documentId){
            //String id = UrlorryActivity.getRequesterID();
            //UrlorryActivity.getRequesterBookID()

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> update = new HashMap<>();
            update.put("status","completed");

            //other user's update
            db.collection("users").document(mRecord.getPartnerId()).collection("booking").document(mRecord.getPartnerDocId()).update(update);

            //own user update
            db.collection("users").document(NavigationActivity.getStaticUid()).collection("business").document(documentId).update(update);

        }

        //get address
        private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
            String strAdd = "";
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
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
    //inner class concept for adapter and view holder
    // view holder hold text view


    //adapter will bind a list of crimes
    //adapter keep list of crime here
    private class RecordAdapter extends RecyclerView.Adapter<RecordHolder>{
        private List<Record> mRecord;

        public RecordAdapter(List<Record> record){
            mRecord = record;
        }
        //simple List

        @Override
        public RecordHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            //View view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            View view = layoutInflater.inflate(R.layout.list_item_record, parent, false);
            // changed to refer own layout file
            return new RecordHolder(view);
        }


        @Override
        public void onBindViewHolder(RecordHolder holder, int position){
            Record record = mRecord.get(position);
            //holder.mTitleTextView.setText(crime.getTitle());
            //based on position, set the title of the crime

            holder.bindRecord(record);
        }

        @Override
        public int getItemCount(){
            return mRecord.size();
        }
    }




}
