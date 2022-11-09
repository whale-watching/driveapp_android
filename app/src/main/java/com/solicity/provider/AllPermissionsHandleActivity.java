package com.solicity.provider;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.general.files.GeneralFunctions;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import org.json.JSONObject;

import java.util.ArrayList;

public class AllPermissionsHandleActivity extends AppCompatActivity {

    MButton btn_type2;
    int submitBtnId;
    GeneralFunctions generalFunctions;
    ArrayList<String> requestPermissions = new ArrayList<>();
    GenerateAlertBox currentAlertBox;
    boolean isopenAllowAllDialog = false;
    JSONObject userProfileJsonObj;
    ImageView permisssionImg;
    MTextView titleTxt, noteTxt;
    private boolean isOneTime = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_permissions_handle);
        generalFunctions = new GeneralFunctions(this);
        permisssionImg = findViewById(R.id.permisssionImg);


        String userProfileData = generalFunctions.retrieveValue(Utils.USER_PROFILE_JSON);

        userProfileJsonObj = generalFunctions.getJsonObject(userProfileData);

        titleTxt = findViewById(R.id.titleTxt);
        noteTxt = findViewById(R.id.noteTxt);
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setText(generalFunctions.retrieveLangLBl("", "LBL_ALLOW"));

        RelativeLayout.LayoutParams lyParams_permisssionImg = (RelativeLayout.LayoutParams) permisssionImg.getLayoutParams();
        lyParams_permisssionImg.height = (int) ((Utils.getScreenPixelWidth(getActContext()) - getResources().getDimensionPixelSize(R.dimen._50sdp)) / 1.3888);

        permisssionImg.setLayoutParams(lyParams_permisssionImg);

        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);

        if (generalFunctions.getJsonValueStr("RIDE_DRIVER_CALLING_METHOD", userProfileJsonObj).equalsIgnoreCase("Voip")) {

            permisssionImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_permission_all));
            titleTxt.setText(generalFunctions.retrieveLangLBl("Location & Call Permission required", "LBL_LOC_PHONE_PERMISSION"));
            String notLBL = generalFunctions.retrieveLangLBl("", "LBL_LOC_CALL_PERMISSION_NOTE");
            notLBL = notLBL.replace("###", "\n");
            noteTxt.setText(notLBL);

        } else {
            titleTxt.setText(generalFunctions.retrieveLangLBl("Location Permission required", "LBL_LOC_PERMISSION"));
            String notLBL = generalFunctions.retrieveLangLBl("", "LBL_LOC_PERMISSION_NOTE");
            notLBL = notLBL.replace("###", "\n");
            noteTxt.setText(notLBL);
            permisssionImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_permission_location));

        }

        btn_type2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                requestPermissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
                requestPermissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
                if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                    requestPermissions.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                }

                if (generalFunctions.getJsonValueStr("RIDE_DRIVER_CALLING_METHOD", userProfileJsonObj).equalsIgnoreCase("Voip")) {
                    requestPermissions.add(android.Manifest.permission.RECORD_AUDIO);
                    requestPermissions.add(Manifest.permission.READ_PHONE_STATE);
                }

                if (generalFunctions.getJsonValueStr("RIDE_DRIVER_CALLING_METHOD", userProfileJsonObj).equalsIgnoreCase("Voip")) {
                    if (generalFunctions.isCallPermissionGranted(false) || generalFunctions.isAllPermissionGranted(false)) {
                        isopenAllowAllDialog = true;
                    }
                } else {

                    if (generalFunctions.isAllPermissionGranted(false)) {
                        isopenAllowAllDialog = true;
                    }

                }
                if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
                    if (!isOneTime) {
                        requestPermissions.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                        if (generalFunctions.isAllPermissionGranted(true, requestPermissions)) {
                            generalFunctions.restartApp();
                        }
                        return;
                    }
                }

                if (!generalFunctions.isAllPermissionGranted(isopenAllowAllDialog ? false : true, requestPermissions)) {
                    if (isopenAllowAllDialog) {
                        showNoPermission();
                    }
                } else {
                    if (isopenAllowAllDialog) {
                        showNoPermission();
                    }
                }
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (requestPermissions.size() > 0) {
            if (generalFunctions.isAllPermissionGranted(false, requestPermissions)) {
                generalFunctions.restartApp();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (isOneTime) {
                isOneTime = false;
                requestPermissions.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                if (generalFunctions.isAllPermissionGranted(true, requestPermissions)) {
                    generalFunctions.restartApp();
                }
                return;
            }
        }

        if (!generalFunctions.isCallPermissionGranted(false)) {
            isopenAllowAllDialog = true;
        }
        if (!generalFunctions.isAllPermissionGranted(false)) {
            isopenAllowAllDialog = true;
        }
        if (generalFunctions.isAllPermissionGranted(false, requestPermissions)) {
            generalFunctions.restartApp();
        }


    }

    public Context getActContext() {
        return AllPermissionsHandleActivity.this;
    }

    public void showNoPermission() {
        currentAlertBox = generalFunctions.showGeneralMessage("", generalFunctions.retrieveLangLBl("Application requires some permission to be granted to work. Please allow it.",
                "LBL_ALLOW_PERMISSIONS_APP"), generalFunctions.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"), generalFunctions.retrieveLangLBl("Allow All", "LBL_ALLOW_ALL_TXT"), buttonId -> handleBtnClick(buttonId, "NO_PERMISSION"));
    }

    public void handleBtnClick(int buttonId, String alertType) {

        if (buttonId == 0) {
            currentAlertBox.closeAlertBox();

        } else {

            generalFunctions.openSettings();
        }


    }
}