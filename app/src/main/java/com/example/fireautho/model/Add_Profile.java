package com.example.fireautho.model;

public class Add_Profile {
    String mName;
    String mAbout;
    String mProfExperience;
    String mMobno;
    String mAddress;
    String mShopname;
    String id;

    public String getmName() {
        return mName;
    }

    public String getmAbout() {
        return mAbout;
    }

    public String getmProfExperience() {
        return mProfExperience;
    }

    public String getmMobno() {
        return mMobno;
    }

    public String getmAddress() {
        return mAddress;
    }

    public String getId() {
        return id;
    }

    public String getmShopname() {
        return mShopname;
    }

    public Add_Profile(String mName, String mAbout, String mProfExperience, String mMobno, String mAddress, String mShopname, String id) {
        this.mName = mName;
        this.mAbout = mAbout;
        this.mProfExperience = mProfExperience;
        this.mMobno = mMobno;
        this.mAddress = mAddress;
        this.mShopname = mShopname;
        this.id = id;
    }
}
