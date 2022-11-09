package com.countryview.model;

import com.google.gson.annotations.SerializedName;

public class Country {

    @SerializedName("name")
    private String mName;

    public String getmViewType() {
        return mViewType;
    }


    @SerializedName("code")
    private String mCode;

    @SerializedName("phone_code")
    private String mDialCode;

    @SerializedName("flag")
    private String mFlag;

    public Country getSetHeaderSection() {
        return setHeaderSection;
    }

    public void setSetHeaderSection(Country setHeaderSection) {
        this.setHeaderSection = setHeaderSection;
    }

    Country setHeaderSection;


    public Country(String mName, String mCode, String mDialCode, String mFlag, String mViewType, Country setHeaderSection) {
        this.mName = mName;
        this.mCode = mCode;
        this.mDialCode = mDialCode;
        this.mFlag = mFlag;
        this.mViewType = mViewType;
        this.setHeaderSection = setHeaderSection;
    }

    @SerializedName("ViewType")
    private String mViewType;

    public Country(String name, String code, String dialCode, String flag) {

        mName = name;
        mCode = code;
        mDialCode = dialCode;
        mFlag = flag;
    }

    public String getName() {

        return mName;
    }

    public String getCode() {

        return mCode;
    }

    public String getDialCode() {

        return mDialCode;
    }

    public String getFlagName() {

        return mFlag;
    }
}
