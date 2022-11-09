package com.general.files;

import android.content.Context;

import com.solicity.provider.R;
import com.utils.CommonUtilities;
import com.utils.Logger;
import com.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 02-07-2016.
 */
public class GetAddressFromLocation implements MapDelegate {
    double latitude;
    double longitude;
    Context mContext;
    String serverKey;
    GeneralFunctions generalFunc;

    ExecuteWebServerUrl currentWebTask;

    AddressFound addressFound;

    boolean isLoaderEnable = false;
    boolean isDestinationPointAddress = false;

    public GetAddressFromLocation(Context mContext, GeneralFunctions generalFunc) {
        this.mContext = mContext;
        this.generalFunc = generalFunc;

        serverKey = generalFunc.retrieveValue(Utils.GOOGLE_SERVER_ANDROID_DRIVER_APP_KEY);
    }

    public void setLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setIsDestination(boolean isDestinationPointAddress) {
        this.isDestinationPointAddress = isDestinationPointAddress;
    }

    public void setLoaderEnable(boolean isLoaderEnable) {

        this.isLoaderEnable = isLoaderEnable;
    }

    public void execute() {
        if (currentWebTask != null) {
            currentWebTask.cancel(true);
            currentWebTask = null;
        }

        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("latitude", latitude + "");
        hashMap.put("longitude", longitude + "");


        MapServiceApi.getGeoCodeService(mContext, hashMap, this);

//        String url_str = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=" + serverKey + "&language=" + generalFunc.retrieveValue(Utils.GOOGLE_MAP_LANGUAGE_CODE_KEY) + "&sensor=true";
//        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mContext, url_str, true);
//
//        if (isLoaderEnable == true) {
//            exeWebServer.setLoaderConfig(mContext, true, generalFunc);
//        }
//        this.currentWebTask = exeWebServer;
//        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
//            @Override
//            public void setResponse(String responseString) {
//                JSONObject responseStringObject=generalFunc.getJsonObject(responseString);
//
////                Logger.d("Response", "::" + responseString);
//
//                if (responseString != null && !responseString.equals("")) {
//
//                    String status = generalFunc.getJsonValue("status", responseString);
//
//                    if (status.equals("OK")) {
//                        String address_loc = "";
//
//                        JSONArray arr = generalFunc.getJsonArray("results", responseString);
//
//                        if (arr.length() > 0) {
//
//                            JSONObject obj = generalFunc.getJsonObject(arr, 0);
//
//                            String formatted_address = generalFunc.getJsonValue("formatted_address", obj.toString());
//
////                            Logger.d("formatted_address","::"+formatted_address);
//                            String[] addressArr = formatted_address.split(",");
//
//                            boolean first_input = true;
//                            for (int i = 0; i < addressArr.length; i++) {
//                                if (!addressArr[i].contains("Unnamed") && !addressArr[i].contains("null")) {
//
//                                    if (i == 0 && addressArr[0].matches("^[0-9]+$")) {
//                                        continue;
//                                    }
//                                    if (first_input == true) {
//                                        address_loc = addressArr[i];
//                                        first_input = false;
//                                    } else {
//                                        address_loc = address_loc + "," + addressArr[i];
//                                    }
//
//                                }
//                            }
//
//                            if (address_loc.trim().equalsIgnoreCase("")) {
//                                address_loc = formatted_address;
//                            }
//
//                            if (addressFound != null) {
//                                addressFound.onAddressFound(address_loc, latitude, longitude);
//                            }
//                        }
//
//                    } else {
//                        if (isDestinationPointAddress == true) {
//                            if (addressFound != null) {
//                                addressFound.onAddressFound("", latitude, longitude);
//                            }
//                        }
//                    }
//
//                } else {
//                    if (isDestinationPointAddress == true) {
//                        if (addressFound != null) {
//                            addressFound.onAddressFound("", latitude, longitude);
//                        }
//                    }
//                }
//            }
//        });
//        exeWebServer.execute();
    }

    @Override
    public void searchResult(ArrayList<HashMap<String, String>> placelist, int selectedPos, String input) {

    }

    @Override
    public void resetOrAddDest(int selPos, String address, double latitude, double longitude, String isSkip) {

    }

    @Override
    public void directionResult(HashMap<String, String> directionlist) {

    }

    @Override
    public void geoCodeAddressFound(String address, double latitude, double longitude, String geocodeobject) {
        if (addressFound != null) {
            addressFound.onAddressFound(address, latitude, longitude, geocodeobject);
        }

    }

    public interface AddressFound {
        void onAddressFound(String address, double latitude, double longitude, String geocodeobject);

    }

    public void setAddressList(AddressFound addressFound) {
        this.addressFound = addressFound;
    }


}
