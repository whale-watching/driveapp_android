package com.general.files;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;

import com.solicity.provider.ActiveTripActivity;
import com.solicity.provider.BuildConfig;
import com.solicity.provider.HailActivity;
import com.solicity.provider.MyGalleryActivity;
import com.solicity.provider.MyProfileActivity;
import com.solicity.provider.UploadDocActivity;
import com.solicity.provider.ViewMultiDeliveryDetailsActivity;
import com.solicity.provider.deliverAll.LiveTrackOrderDetail2Activity;
import com.solicity.provider.deliverAll.LiveTrackOrderDetailActivity;
import com.rest.DataRequestProgressBody;
import com.rest.RestClient;
import com.utils.CommonUtilities;
import com.utils.Logger;
import com.utils.ScalingUtilities;
import com.utils.Utils;
import com.view.MyProgressDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.general.files.GeneralFunctions.rotateBitmap;

/**
 * Created by Admin on 08-07-2016.
 */
public class UploadProfileImage implements DataRequestProgressBody.UploadCallbacks /*extends AsyncTask<String, String, String>*/ {

    String selectedImagePath;
    String responseString = "";

    String temp_File_Name = "";
    ArrayList<String[]> paramsList;
    String type = "";
    Context actContext;
    MyProgressDialog myPDialog;
    GeneralFunctions generalFunc;

    boolean isProgressUpdateDialog = false;
    OpenProgressUpdateDialog openProgressDialog;

    Call<Object> call_current;
    boolean isTaskKilled = false;
    boolean isGenie=false;

    public UploadProfileImage(Context actContext, String selectedImagePath, String temp_File_Name, ArrayList<String[]> paramsList, String type) {
        this.selectedImagePath = selectedImagePath;
        this.temp_File_Name = temp_File_Name;
        this.paramsList = paramsList;
        this.type = type;
        this.actContext = actContext;
        this.generalFunc = MyApp.getInstance().getGeneralFun(actContext);

    }
    public UploadProfileImage(Context actContext, String selectedImagePath, String temp_File_Name, ArrayList<String[]> paramsList, String type,boolean isGenie) {
        this.selectedImagePath = selectedImagePath;
        this.temp_File_Name = temp_File_Name;
        this.paramsList = paramsList;
        this.type = type;
        this.actContext = actContext;
        this.generalFunc = MyApp.getInstance().getGeneralFun(actContext);
        this.isGenie=isGenie;
    }


    public UploadProfileImage(Context actContext, String selectedImagePath, String temp_File_Name, ArrayList<String[]> paramsList) {
        this.selectedImagePath = selectedImagePath;
        this.temp_File_Name = temp_File_Name;
        this.paramsList = paramsList;
        this.actContext = actContext;
    }


    public void execute(boolean isProgressUpdateDialog) {
        this.isProgressUpdateDialog = isProgressUpdateDialog;
        execute();
    }

    public void execute() {
        if (generalFunc == null) {
            generalFunc = MyApp.getInstance().getGeneralFun(actContext);
        }

        if (isProgressUpdateDialog) {
            openProgressDialog = new OpenProgressUpdateDialog(actContext, generalFunc, this,isGenie);
            try {
                openProgressDialog.run();
            } catch (Exception e) {

            }

        } else {
            myPDialog = new MyProgressDialog(actContext, false, generalFunc.retrieveLangLBl("Loading", "LBL_LOADING_TXT"));
            try {
                myPDialog.show();
            } catch (Exception e) {

            }
        }

        String filePath = selectedImagePath;
        if (TextUtils.isEmpty(type)) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                String var5 = null;
                Bitmap var6 = null;

                String path = selectedImagePath;
                int DESIREDWIDTH = Utils.ImageUpload_DESIREDWIDTH;
                int DESIREDHEIGHT = Utils.ImageUpload_DESIREDHEIGHT;
                String tempImgName = temp_File_Name;

                try {
                    int var7 = Utils.getExifRotation(path);
                    Bitmap var8 = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.CROP);
                    if (var8.getWidth() <= DESIREDWIDTH && var8.getHeight() <= DESIREDHEIGHT) {
                        if (var8.getWidth() > var8.getHeight()) {
                            var6 = ScalingUtilities.createScaledBitmap(var8, var8.getHeight(), var8.getHeight(), ScalingUtilities.ScalingLogic.CROP);
                        } else {
                            var6 = ScalingUtilities.createScaledBitmap(var8, var8.getWidth(), var8.getWidth(), ScalingUtilities.ScalingLogic.CROP);
                        }
                    } else {
                        var6 = ScalingUtilities.createScaledBitmap(var8, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.CROP);
                    }

                    var6 = rotateBitmap(var6, var7);
                    String var9 = actContext.getExternalCacheDir().toString();
                    File var10 = new File(var9 + "/" + "TempImages");
                    if (!var10.exists()) {
                        var10.mkdir();
                    }

                    File var11 = new File(var10.getAbsolutePath(), tempImgName);
                    var5 = var11.getAbsolutePath();
                    FileOutputStream var12 = null;

                    try {
                        var12 = new FileOutputStream(var11);
                        var6.compress(Bitmap.CompressFormat.JPEG, 60, var12);
                        var12.flush();
                        var12.close();
                    } catch (FileNotFoundException var14) {
                        var14.printStackTrace();
                    } catch (Exception var15) {
                        var15.printStackTrace();
                    }

                    var6.recycle();
                } catch (Throwable var16) {
                    Logger.e("",""+ var16.getMessage());
                }
                filePath = var5 == null ? path : var5;
            } else {
                filePath = generalFunc.decodeFile(selectedImagePath, Utils.ImageUpload_DESIREDWIDTH,
                        Utils.ImageUpload_DESIREDHEIGHT, temp_File_Name);
            }
        }

        if (filePath.equals("")) {
            HashMap<String, String> dataParams = new HashMap<>();
            for (int i = 0; i < paramsList.size(); i++) {
                String[] arrData = paramsList.get(i);

                dataParams.put(arrData[0], arrData[1]);
            }

            if (dataParams != null) {
                dataParams.put("tSessionId", generalFunc.getMemberId().equals("") ? "" : generalFunc.retrieveValue(Utils.SESSION_ID_KEY));
                dataParams.put("deviceHeight", Utils.getScreenPixelHeight(actContext) + "");
                dataParams.put("deviceWidth", Utils.getScreenPixelWidth(actContext) + "");

                dataParams.put("GeneralUserType", Utils.app_type);
                dataParams.put("GeneralMemberId", generalFunc.getMemberId());
                dataParams.put("GeneralDeviceType", "" + Utils.deviceType);
                dataParams.put("GeneralAppVersion", BuildConfig.VERSION_NAME);
                dataParams.put("vTimeZone", generalFunc.getTimezone());
                dataParams.put("vUserDeviceCountry", Utils.getUserDeviceCountryCode(actContext));
                dataParams.put("APP_TYPE", ExecuteWebServerUrl.CUSTOM_APP_TYPE);
            }
            if (call_current != null) {
                call_current.cancel();
                call_current = null;
            }
            Call<Object> call = RestClient.getClient("POST", CommonUtilities.SERVER).getResponse(CommonUtilities.SERVER_WEBSERVICE_PATH, dataParams);
            call_current = call;
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {

                    if (isTaskKilled) {
                        return;
                    }

                    if (response.isSuccessful()) {
                        // request successful (status code 200, 201)
                        responseString = RestClient.getGSONBuilder().toJson(response.body());

                        fireResponse();
                    } else {
                        responseString = "";
                        fireResponse();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {

                    if (isTaskKilled) {
                        return;
                    }

                    Logger.d("DataError", "::" + t.getMessage());
                    responseString = "";
                    fireResponse();
                }

            });

            return;
        }
        File file = new File(filePath);

        MultipartBody.Part filePart = MultipartBody.Part.createFormData(type.equalsIgnoreCase("uploadImageWithMask")?"vFaceMaskVerifyImage":"vImage", temp_File_Name, new DataRequestProgressBody(MediaType.parse("multipart/form-data"), file, this));

        Logger.d("temp_File_Name", "::" + temp_File_Name);


        HashMap<String, RequestBody> dataParams = new HashMap<>();

        for (int i = 0; i < paramsList.size(); i++) {
            String[] arrData = paramsList.get(i);

            dataParams.put(arrData[0], RequestBody.create(MediaType.parse("text/plain"), arrData[1]));
        }

        if (dataParams != null) {
            dataParams.put("tSessionId", RequestBody.create(MediaType.parse("text/plain"), generalFunc.getMemberId().equals("") ? "" : generalFunc.retrieveValue(Utils.SESSION_ID_KEY)));
            dataParams.put("deviceHeight", RequestBody.create(MediaType.parse("text/plain"), Utils.getScreenPixelHeight(actContext) + ""));
            dataParams.put("deviceWidth", RequestBody.create(MediaType.parse("text/plain"), Utils.getScreenPixelWidth(actContext) + ""));

            dataParams.put("GeneralUserType", RequestBody.create(MediaType.parse("text/plain"), Utils.app_type));
            dataParams.put("GeneralMemberId", RequestBody.create(MediaType.parse("text/plain"), generalFunc.getMemberId()));
            dataParams.put("GeneralDeviceType", RequestBody.create(MediaType.parse("text/plain"), "" + Utils.deviceType));
            dataParams.put("GeneralAppVersion", RequestBody.create(MediaType.parse("text/plain"), BuildConfig.VERSION_NAME));
            dataParams.put("vTimeZone", RequestBody.create(MediaType.parse("text/plain"), generalFunc.getTimezone()));
            dataParams.put("vUserDeviceCountry", RequestBody.create(MediaType.parse("text/plain"), Utils.getUserDeviceCountryCode(actContext)));
            dataParams.put("APP_TYPE", RequestBody.create(MediaType.parse("text/plain"), ExecuteWebServerUrl.CUSTOM_APP_TYPE));
        }

        Call<Object> call = RestClient.getClient("POST", CommonUtilities.SERVER).uploadData(CommonUtilities.SERVER_WEBSERVICE_PATH, filePart, dataParams);
        call_current = call;

        call.enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    // request successful (status code 200, 201)

//                    Logger.d("Data", "response = " + new Gson().toJson(response.body()));

                    if (isTaskKilled) {
                        return;
                    }

                    responseString = RestClient.getGSONBuilder().toJson(response.body());

                    fireResponse();
                } else {
                    responseString = "";
                    fireResponse();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

                if (isTaskKilled) {
                    return;
                }
                Logger.d("DataError", "::" + t.getMessage());
                responseString = "";
                fireResponse();
            }

        });

    }

    public void fireResponse() {


        try {
            if (myPDialog != null) {
                myPDialog.close();
            }

            if (openProgressDialog != null) {
                openProgressDialog.dialog_img_update.cancel();
            }
            openProgressDialog = null;
        } catch (Exception e) {

        }


        if (actContext instanceof MyProfileActivity) {
            ((MyProfileActivity) actContext).handleImgUploadResponse(responseString);
        } else if (actContext instanceof ActiveTripActivity) {
            ((ActiveTripActivity) actContext).handleImgUploadResponse(responseString, type);
        } else if (actContext instanceof UploadDocActivity) {
            ((UploadDocActivity) actContext).handleImgUploadResponse(responseString);
        } else if (actContext instanceof ViewMultiDeliveryDetailsActivity) {
            ((ViewMultiDeliveryDetailsActivity) actContext).handleImgUploadResponse(responseString, type);
        } else if (actContext instanceof LiveTrackOrderDetailActivity) {
            ((LiveTrackOrderDetailActivity) actContext).handleImgUploadResponse(responseString, type);
        } else if (actContext instanceof LiveTrackOrderDetail2Activity) {
            ((LiveTrackOrderDetail2Activity) actContext).handleImgUploadResponse(responseString, type);
        } else if (actContext instanceof MyGalleryActivity) {
            ((MyGalleryActivity) actContext).handleImgUploadResponse(responseString, type);
        }else if (actContext instanceof LiveTrackOrderDetail2Activity) {
            ((LiveTrackOrderDetail2Activity) actContext).handleImgUploadResponse(responseString, type);
        }else if (actContext instanceof HailActivity) {
            ((HailActivity) actContext).handleImgUploadResponse(responseString, type);
        }
    }

    @Override
    public void onProgressUpdate(int i, DataRequestProgressBody dataRequestProgressBody) {
//        onProgressUpdate(i);
        if (openProgressDialog != null) {
            openProgressDialog.updateProgress(i);
        }
    }

    @Override
    public void onError(DataRequestProgressBody dataRequestProgressBody) {

    }

    @Override
    public void onFinish(DataRequestProgressBody dataRequestProgressBody) {

    }

    @Override
    public void uploadStart(DataRequestProgressBody dataRequestProgressBody) {

    }

    public void cancel(boolean value) {

        this.isTaskKilled = value;
        if (call_current != null) {
            call_current.cancel();
        }
        try {
            if (myPDialog != null) {
                myPDialog.close();
            }

            if (openProgressDialog != null) {
                openProgressDialog.dialog_img_update.cancel();
            }
            openProgressDialog = null;
        } catch (Exception e) {

        }
    }
}