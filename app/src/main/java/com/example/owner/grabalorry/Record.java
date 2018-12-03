package com.example.owner.grabalorry;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Record {
    private String lorryNoPlat;
    private int lorryWeight;
    private String lorryStatus;//lorry status
    private String lorryServiceType;//wot is the type.. request or provide service
    private int cost;
    private Date date;
    private String status;
    private LatLng pickup;
    private LatLng destiny;
    private String email;

    private String partnerId;
    private String partnerDocId;

    //location var


    public Record(int cost, Date date, String status, LatLng pickup, LatLng destiny) {
        this.cost = cost;
        this.date = date;
        this.status = status;
        this.pickup = pickup;
        this.destiny = destiny;
    }

    public Record(String lorryServiceType, int cost, Date date, String status, LatLng pickup, LatLng destiny,int weight) {
        this.lorryServiceType = lorryServiceType;
        this.cost = cost;
        this.date = date;
        this.status = status;
        this.pickup = pickup;
        this.destiny = destiny;
        this.lorryWeight = weight;

    }

    public Record(String lorryServiceType, int cost, Date date, String status, LatLng pickup, LatLng destiny,int weight,String partnerId,String partnerDocId) {
        this.lorryServiceType = lorryServiceType;
        this.cost = cost;
        this.date = date;
        this.status = status;
        this.pickup = pickup;
        this.destiny = destiny;
        this.lorryWeight = weight;
        this.partnerId = partnerId;
        this.partnerDocId = partnerDocId;
    }

    public String toString(){
        return (getLorryWeight()+" "+getCost()+" "+getDate()+" "+getStatus()+" "+getPickup()+" "+getDestiny());
    }

    public String getLorryNoPlat() {
        return lorryNoPlat;
    }

    public void setLorryNoPlat(String lorryNoPlat) {
        this.lorryNoPlat = lorryNoPlat;
    }

    public int getLorryWeight() {
        return lorryWeight;
    }

    public void setLorryWeight(int lorryWeight) {
        this.lorryWeight = lorryWeight;
    }

    public String getLorryStatus() {
        return lorryStatus;
    }

    public void setLorryStatus(String lorryStatus) {
        this.lorryStatus = lorryStatus;
    }

    public String getLorryType() {
        return lorryServiceType;
    }

    public void setLorryType(String lorryType) {
        this.lorryServiceType = lorryType;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LatLng getPickup() {
        return pickup;
    }

    public void setPickup(LatLng pickup) {
        this.pickup = pickup;
    }

    public LatLng getDestiny() {
        return destiny;
    }

    public void setDestiny(LatLng destiny) {
        this.destiny = destiny;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerDocId() {
        return partnerDocId;
    }

    public void setPartnerDocId(String partnerDocId) {
        this.partnerDocId = partnerDocId;
    }
}
