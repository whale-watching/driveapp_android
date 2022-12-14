package com.solicity.provider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.adapter.files.VehicleListAdapter;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.view.ErrorView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class ManageVehiclesActivity extends AppCompatActivity implements VehicleListAdapter.OnItemClickList {

    MTextView titleTxt;

    ImageView addImgView;
    ImageView backImgView;
    GeneralFunctions generalFunc;

    ProgressBar loading;
    MTextView noVehiclesTxt;

    RecyclerView vehiclesRecyclerView;
    VehicleListAdapter adapter;
    ErrorView errorView;

    ArrayList<HashMap<String, String>> list;
    LinearLayout nodatarea;
    MTextView noVehiclescardTxt;
    MButton btn_type2;
    int submitBtnId;
    String iDriverVehicleIdVal = "";
    JSONObject userProfileJsonObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_vehicles);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());

        userProfileJsonObj = generalFunc.getJsonObject(generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));

        backImgView = (ImageView) findViewById(R.id.backImgView);

        vehiclesRecyclerView = (RecyclerView) findViewById(R.id.vehiclesRecyclerView);

        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();

        nodatarea = (LinearLayout) findViewById(R.id.nodatarea);
        noVehiclescardTxt = (MTextView) findViewById(R.id.noVehiclescardTxt);

        iDriverVehicleIdVal = getIntent().getStringExtra("iDriverVehicleId");


        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        addImgView = (ImageView) findViewById(R.id.ic_iv_add);
        addImgView.bringToFront();
        addImgView.setClickable(true);
        errorView = (ErrorView) findViewById(R.id.errorView);
        loading = (ProgressBar) findViewById(R.id.loading);
        noVehiclesTxt = (MTextView) findViewById(R.id.noVehiclesTxt);

        backImgView.setImageResource(R.mipmap.ic_back_arrow);
        backImgView.setOnClickListener(new setOnClickList());
        addImgView.setOnClickListener(new setOnClickList());

        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);

        btn_type2.setOnClickListener(new setOnClickList());

        //  findViewById(R.id.rightImgView).setVisibility(View.VISIBLE);
        setLabels();

        list = new ArrayList<>();
        adapter = new VehicleListAdapter(getActContext(), list, generalFunc);
        vehiclesRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickList(this);

        getData();
    }

    public void setLabels() {
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_MANAGE_VEHICLES"));

        String APP_TYPE = generalFunc.getJsonValueStr("APP_TYPE", userProfileJsonObj);

        if (APP_TYPE.equalsIgnoreCase(Utils.CabGeneralType_Ride)) {
            noVehiclescardTxt.setText(generalFunc.retrieveLangLBl("You have not added ant vehicles. Please add your vehicle to continue your account process.", "LBL_ADD_RIDE_VEHICLE_PAGE_GENERAL_NOTE"));
        } else if (APP_TYPE.equalsIgnoreCase(Utils.CabGeneralType_Deliver)) {
            noVehiclescardTxt.setText(generalFunc.retrieveLangLBl("You have not added ant vehicles. Please add your vehicle to continue your account process.", "LBL_ADD_DELIVERY_VEHICLE_PAGE_GENERAL_NOTE"));
        } else {
            noVehiclescardTxt.setText(generalFunc.retrieveLangLBl("You have not added ant vehicles. Please add your vehicle to continue your account process.", "LBL_ADD_VEHICLE_PAGE_GENERAL_NOTE"));
        }

        btn_type2.setText(generalFunc.retrieveLangLBl("Add Vehicle", "LBL_ADD_VEHICLE"));
    }

    public Context getActContext() {
        return ManageVehiclesActivity.this;
    }

    public void getData() {
        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        if (loading.getVisibility() != View.VISIBLE) {
            loading.setVisibility(View.VISIBLE);
        }

        list.clear();
        adapter.notifyDataSetChanged();

        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "displaydrivervehicles");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("MemberType", Utils.app_type);

        noVehiclesTxt.setVisibility(View.GONE);

        final ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(responseString -> {

            noVehiclesTxt.setVisibility(View.GONE);
            JSONObject responseStringObj = generalFunc.getJsonObject(responseString);

            if (responseStringObj != null && !responseStringObj.equals("")) {

                closeLoader();
                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseStringObj)) {

                    JSONArray arr_rides = generalFunc.getJsonArray(Utils.message_str, responseStringObj);

                    if (arr_rides != null && arr_rides.length() > 0) {
                        for (int i = 0; i < arr_rides.length(); i++) {
                            JSONObject obj_temp = generalFunc.getJsonObject(arr_rides, i);

                            HashMap<String, String> map = new HashMap<String, String>();

                            map.put("vLicencePlate", generalFunc.getJsonValueStr("vLicencePlate", obj_temp));
                            map.put("eStatus", generalFunc.getJsonValueStr("eStatus", obj_temp));
                            map.put("vMake", generalFunc.getJsonValueStr("vMake", obj_temp));
                            map.put("iDriverVehicleId", generalFunc.getJsonValueStr("iDriverVehicleId", obj_temp));
                            map.put("vCarType", generalFunc.getJsonValueStr("vCarType", obj_temp));
                            map.put("vRentalCarType", generalFunc.getJsonValueStr("vRentalCarType", obj_temp));
                            map.put("iMakeId", generalFunc.getJsonValueStr("iMakeId", obj_temp));
                            map.put("iYear", generalFunc.getJsonValueStr("iYear", obj_temp));
                            map.put("iModelId", generalFunc.getJsonValueStr("iModelId", obj_temp));
                            map.put("vColour", generalFunc.getJsonValueStr("vColour", obj_temp));
                            map.put("eHandiCapAccessibility", generalFunc.getJsonValueStr("eHandiCapAccessibility", obj_temp));
                            map.put("eChildAccessibility", generalFunc.getJsonValueStr("eChildAccessibility", obj_temp));
                            map.put("eWheelChairAccessibility", generalFunc.getJsonValueStr("eWheelChairAccessibility", obj_temp));


                            map.put("LBL_ACTIVE", generalFunc.retrieveLangLBl("", "LBL_ACTIVE"));
                            map.put("LBL_INACTIVE", generalFunc.retrieveLangLBl("", "LBL_INACTIVE"));
                            map.put("LBL_DELETED", generalFunc.retrieveLangLBl("", "LBL_DELETED"));

                            map.put("JSON", obj_temp.toString());
                            list.add(map);
                        }
                    }
                    if (list.size() == 0) {
                        nodatarea.setVisibility(View.VISIBLE);
                        addImgView.setVisibility(View.GONE);
                    } else {
                        nodatarea.setVisibility(View.GONE);
                        addImgView.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }

                } else {

                    if (loading.getVisibility() == View.VISIBLE) {
                        loading.setVisibility(View.GONE);
                    }
                    nodatarea.setVisibility(View.VISIBLE);
                    addImgView.setVisibility(View.GONE);
                }
            } else {
                noVehiclesTxt.setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseStringObj)));
                noVehiclesTxt.setVisibility(View.VISIBLE);

                if (loading.getVisibility() == View.VISIBLE) {
                    loading.setVisibility(View.GONE);
                }
                generateErrorView();
            }

        });
        exeWebServer.execute();
    }

    public void closeLoader() {
        if (loading.getVisibility() == View.VISIBLE) {
            loading.setVisibility(View.GONE);
        }
    }

    public void generateErrorView() {

        closeLoader();
        generalFunc.generateErrorView(errorView, "LBL_ERROR_TXT", "LBL_NO_INTERNET_TXT");

        if (errorView.getVisibility() != View.VISIBLE) {
            errorView.setVisibility(View.VISIBLE);
        }
        errorView.setOnRetryListener(() -> getData());
    }

    @Override
    public void onItemClick(int position, int viewClickId) {

        HashMap<String, String> data = list.get(position);

        if (viewClickId == 0) {
            Bundle bn = new Bundle();
            bn.putString("PAGE_TYPE", "vehicle");
            bn.putString("vLicencePlate", data.get("vLicencePlate"));
            bn.putString("eStatus", data.get("eStatus"));
            bn.putString("vMake", data.get("vMake"));
            bn.putString("iDriverVehicleId", data.get("iDriverVehicleId"));
            bn.putString("vCarType", data.get("vCarType"));
            bn.putString("vRentalCarType", data.get("vRentalCarType"));
            bn.putString("iMakeId", data.get("iMakeId"));
            bn.putString("iYear", data.get("iYear"));
            bn.putString("iModelId", data.get("iModelId"));
            bn.putString("vColour", data.get("vColour"));
            bn.putString("app_type", Utils.app_type);
            bn.putString("seltype", Utils.app_type);

            new StartActProcess(getActContext()).startActWithData(ListOfDocumentActivity.class, bn);
        } else if (viewClickId == 1) {
            try {
                Bundle bn = new Bundle();
                bn.putString("vLicencePlate", data.get("vLicencePlate"));
                bn.putString("eStatus", data.get("eStatus"));
                bn.putString("vMake", data.get("vMake"));
                bn.putString("iDriverVehicleId", data.get("iDriverVehicleId"));
                bn.putString("vCarType", data.get("vCarType"));
                bn.putString("iMakeId", data.get("iMakeId"));
                bn.putString("iYear", data.get("iYear"));
                bn.putString("iModelId", data.get("iModelId"));
                bn.putString("vRentalCarType", data.get("vRentalCarType"));
                bn.putString("vColour", data.get("vColour"));
                bn.putString("eHandiCapAccessibility", data.get("eHandiCapAccessibility"));
                bn.putString("eChildAccessibility", data.get("eChildAccessibility"));
                bn.putString("eWheelChairAccessibility", data.get("eWheelChairAccessibility"));
                bn.putString("isfrom", "edit");
                bn.putString("app_type", Utils.app_type);

                (new StartActProcess(getActContext())).startActForResult(AddVehicleActivity.class, bn, Utils.ADD_VEHICLE_REQ_CODE);
            } catch (Exception e) {
            }

        } else if (viewClickId == 2) {
            confirmDeleteCar(data.get("iDriverVehicleId"));
        }
    }

    public void confirmDeleteCar(final String iDriverVehicleId) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            generateAlert.closeAlertBox();

            if (btn_id == 1) {
                deleteCar(iDriverVehicleId);
            }
        });
        generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("Do you want to delete this car?", "LBL_DELETE_CAR_SURE"));
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));

        generateAlert.showAlertBox();
    }

    public void deleteCar(String iDriverVehicleId) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "deletedrivervehicle");
        parameters.put("iDriverId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.app_type);
        parameters.put("iDriverVehicleId", iDriverVehicleId);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {
            JSONObject responseStringObj = generalFunc.getJsonObject(responseString);

            if (responseStringObj != null && !responseStringObj.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseStringObj);

                if (isDataAvail) {

                    final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                    generateAlert.setCancelable(false);
                    generateAlert.setBtnClickList(btn_id -> {
                        generateAlert.closeAlertBox();
                        getData();

                    });
                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseStringObj)));
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));

                    generateAlert.showAlertBox();

                } else {
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseStringObj)));
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.ADD_VEHICLE_REQ_CODE && resultCode == RESULT_OK) {
            if (data.getBooleanExtra("isUploadDoc", false)) {
                Bundle bn = new Bundle();
                bn.putString("PAGE_TYPE", "vehicle");
                bn.putString("iDriverVehicleId", data.getStringExtra("iDriverVehicleId"));
                bn.putString("app_type", Utils.app_type);
                bn.putString("seltype", Utils.app_type);

                new StartActProcess(getActContext()).startActWithData(ListOfDocumentActivity.class, bn);
            } else if (data.getBooleanExtra("isContactus", false)) {
                new StartActProcess(getActContext()).startAct(ContactUsActivity.class);
            } else if (data.getBooleanExtra("isListEmpty", false)) {
                new StartActProcess(getActContext()).startAct(ContactUsActivity.class);
            }
            getData();

        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(ManageVehiclesActivity.this);

            if (view.getId() == submitBtnId) {
                Bundle bn = new Bundle();
                bn.putString("app_type", Utils.app_type);
                (new StartActProcess(getActContext())).startActForResult(AddVehicleActivity.class, bn, Utils.ADD_VEHICLE_REQ_CODE);
            } else if (view.getId() == addImgView.getId()) {
                Bundle bn = new Bundle();
                bn.putString("app_type", Utils.app_type);
                (new StartActProcess(getActContext())).startActForResult(AddVehicleActivity.class, bn, Utils.ADD_VEHICLE_REQ_CODE);

            } else if (view.getId() == backImgView.getId()) {
                onBackPressed();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
