package com.solicity.provider.deliverAll;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adapter.MoreInstructionAdapter;
import com.adapter.files.deliverAll.OrderItemListRecycleAdapter;
import com.solicity.provider.BaseActivity;
import com.solicity.provider.BuildConfig;
import com.solicity.provider.CallScreenActivity;
import com.solicity.provider.ContactUsActivity;
import com.solicity.provider.R;
import com.general.files.AppFunctions;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.GetLocationUpdates;
import com.general.files.ImageFilePath;
import com.general.files.InternetConnection;
import com.general.files.MyApp;
import com.general.files.PreferenceDailogJava;
import com.general.files.SinchService;
import com.general.files.StartActProcess;
import com.general.files.UploadProfileImage;
import com.kyleduo.switchbutton.SwitchButton;
import com.model.deliverAll.orderDetailDataModel;
import com.model.deliverAll.orderItemDetailDataModel;
import com.sinch.android.rtc.calling.Call;
import com.squareup.picasso.Picasso;
import com.utils.CommonUtilities;
import com.utils.Logger;
import com.utils.Utils;
import com.view.ErrorView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.SelectableRoundedImageView;
import com.view.editBox.MaterialEditText;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.utils.Utils.generateImageParams;

/**
 * Created by Admin on 17-04-18.
 */

public class LiveTrackOrderDetailActivity extends BaseActivity implements OrderItemListRecycleAdapter.OnItemClickListener, GetLocationUpdates.LocationUpdatesListener {
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "Temp";
    private static final int SELECT_PICTURE = 2;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int MANAGE_APP_ALL_FILES_ACCESS_REQUEST_CODE = 2296;
    GeneralFunctions generalFunc;
    MTextView titleTxt;
    MTextView noSItemsTxt, orderIdHTxt, orderIdVTxt, orderStatusTxt, orderDateTxt;
    MTextView orderTotalBillHTxt, orderTotalBillVTxt, collectAmountRestHTxt, collectAmountRestVTxt, collectAmountUserHTxt, collectAmountUserVTxt;
    MTextView userNameVTxt, userAddressTxt, restaurantLocationVTxt, distanceHTxt, distanceVTxt;
    ImageView backImgView, callImgView, iv_arrow_icon;
    MButton btn_type2;
    MTextView ordertitleTxt, storeNameTxt, storeAddressTxt;
    Dialog dialog;
    LinearLayout billDetail_ll;
    LinearLayout footerLayout;
    boolean isShow = true;
    Animation slideUpAnimation, slideDownAnimation, slideup, slidedown;
    /*Pagination*/
    boolean mIsLoading = false;
    boolean isNextPageAvailable = false;
    String next_page_str = "";
    ArrayList<orderItemDetailDataModel> subItemList = new ArrayList<orderItemDetailDataModel>();
    String isFrom = "";
    Dialog uploadServicePicAlertBox = null;
    Dialog showDeliveryPreferenceAlertBox = null;
    String vImage, vName;
    String userProfileJson;
    private RecyclerView orderItemListRecyclerView;
    private LinearLayout orderDeliverArea, call_navigate_Area, trackUserLocationArea, callUserArea;
    private RelativeLayout orderHeaderArea;
    private MTextView noItemsTxt;
    private ProgressBar loading_order_item_list;
    private ErrorView errorView;
    // if more than 1 state required
    private ArrayList<orderDetailDataModel> list = new ArrayList<>();
    private OrderItemListRecycleAdapter orderItemListRecycleAdapter;
    private String iOrderId;
    private boolean isDeliver = false;
    private String isPhotoUploaded = "";
    private String PickedFromRes = "";
    private String eBuyAnyService = "";
    private String eAutoaccept = "";
    private String vInstruction = "";
    private String GenieOrderType = "";
    private Uri fileUri;
    private String selectedImagePath = "";
    private String pathForCameraImage = "";
    private HashMap<String, String> data_trip;
    private String tripId;
    private Location userLocation;
    private JSONObject userProfileJsonObj;
    boolean isPrefrence = false;
    ArrayList<HashMap<String, String>> instructionslit;
    boolean isPrefrenceuploaded = false;
    private String vTitle;

    ScrollView main_layout;
    LinearLayout bottomLayout;
    LinearLayout bottomGenieLayout;
    MTextView genieTitletxt;
    MButton btn_type_refresh;
    boolean isImgUploaded = false;
    ProgressBar mProgressBar;
    String ePaymentOption;
    Toolbar toolbar;
//    ImageView helpmenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_track_order_detail);
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        userProfileJsonObj = generalFunc.getJsonObject(userProfileJson);


        HashMap<String, String> data = (HashMap<String, String>) getIntent().getSerializableExtra("TRIP_DATA");
        this.data_trip = data;

        iOrderId = data_trip.get("iOrderId");
        tripId = data_trip.get("iTripId");


        if (getIntent().hasExtra("isDeliver") && getIntent().getStringExtra("isDeliver").equalsIgnoreCase("Yes")) {
            isDeliver = true;
        } else if (getIntent().hasExtra("isPhotoUploaded")) {
            isPhotoUploaded = getIntent().getStringExtra("isPhotoUploaded");
        }
        if (getIntent().hasExtra("PickedFromRes")) {
            PickedFromRes = getIntent().getStringExtra("PickedFromRes");
        }

        GetLocationUpdates.getInstance().startLocationUpdates(this, this);

        initView();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomLayout = (LinearLayout) findViewById(R.id.bottomLayout);
        main_layout = (ScrollView) findViewById(R.id.main_layout);
        billDetail_ll = (LinearLayout) findViewById(R.id.billDetail_ll);
        footerLayout = (LinearLayout) findViewById(R.id.footerLayout);
        iv_arrow_icon = (ImageView) findViewById(R.id.iv_arrow_icon);
        callImgView = (ImageView) findViewById(R.id.callImgView);

        callImgView.setVisibility(View.GONE);


        btn_type2.setId(Utils.generateViewId());
        btn_type2.setOnClickListener(new setOnClickList());
        backImgView.setOnClickListener(new setOnClickList());
        trackUserLocationArea.setOnClickListener(new setOnClickList());
        callUserArea.setOnClickListener(new setOnClickList());
        /*Set actions on view tap*/

//      billDetail_ll.setOnClickListener(new setOnClickList());
//      footerLayout.setOnClickListener(new setOnClickList());
        callImgView.setOnClickListener(new setOnClickList());

        slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up_animation);
        slideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down_animation);

        slideup = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slideup);
        slidedown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slidedown);

        bottomGenieLayout = (LinearLayout) findViewById(R.id.bottomGenieLayout);
        mProgressBar = (ProgressBar) findViewById(R.id.mProgressBar);
        mProgressBar.setIndeterminate(true);
        genieTitletxt = (MTextView) findViewById(R.id.genieTitletxt);
        btn_type_refresh = ((MaterialRippleLayout) findViewById(R.id.btn_type_refresh)).getChildView();
        btn_type_refresh.setOnClickListener(new setOnClickList());

        setLabels();

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            String restratValue_str = savedInstanceState.getString("RESTART_STATE");

            if (restratValue_str != null && !restratValue_str.equals("") && restratValue_str.trim().equals("true")) {
                generalFunc.restartApp();
            }
        }


        if (isPhotoUploaded.equalsIgnoreCase("NO") && !isDeliver && PickedFromRes.equalsIgnoreCase("Yes") && !isGenie) {
            takeAndUploadPic(getActContext());
//                btn_type2.performClick();
        } else {
            getOrderDetails();
        }


    }

    AlertDialog dialog_declineOrder;
    String titleDailog = "";

    public void showDeclineReasonsAlert() {

        titleDailog = generalFunc.retrieveLangLBl("", "LBL_CANCEL_ORDER");


        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActContext());
        // builder.setTitle(titleDailog);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.decline_order_dialog, null);
        builder.setView(dialogView);

        MaterialEditText reasonBox = (MaterialEditText) dialogView.findViewById(R.id.contentBox);


        reasonBox.setHideUnderline(true);
        reasonBox.setSingleLine(false);

        reasonBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        reasonBox.setGravity(Gravity.TOP);
        if (generalFunc.isRTLmode()) {
            reasonBox.setPaddings(0, 0, (int) getResources().getDimension(R.dimen._10sdp), 0);
        } else {
            reasonBox.setPaddings((int) getResources().getDimension(R.dimen._10sdp), 0, 0, 0);
        }


        reasonBox.setBothText("", generalFunc.retrieveLangLBl("", "LBL_ENTER_REASON"));


        MTextView cancelTxt = (MTextView) dialogView.findViewById(R.id.cancelTxt);
        MTextView submitTxt = (MTextView) dialogView.findViewById(R.id.submitTxt);
        MTextView subTitleTxt = (MTextView) dialogView.findViewById(R.id.subTitleTxt);
        ImageView cancelImg = (ImageView) dialogView.findViewById(R.id.cancelImg);
        subTitleTxt.setText(titleDailog);

        submitTxt.setText(generalFunc.retrieveLangLBl("", "LBL_YES"));
        cancelTxt.setText(generalFunc.retrieveLangLBl("", "LBL_NO"));


        submitTxt.setOnClickListener(v -> {


            if (!Utils.checkText(reasonBox)) {
                reasonBox.setError(generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT"));
                return;
            }


            cancelOrder(Utils.getText(reasonBox));

            dialog_declineOrder.dismiss();

        });
        cancelTxt.setOnClickListener(v -> {
            Utils.hideKeyboard(getActContext());
            dialog_declineOrder.dismiss();
        });

        cancelImg.setOnClickListener(v -> {
            Utils.hideKeyboard(getActContext());
            dialog_declineOrder.dismiss();
        });


        dialog_declineOrder = builder.create();
        dialog_declineOrder.setCancelable(false);
        dialog_declineOrder.getWindow().setBackgroundDrawable(getActContext().getResources().getDrawable(R.drawable.all_roundcurve_card));
        dialog_declineOrder.show();


    }


    private void cancelOrder(String reason) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "cancelDriverOrder");
        parameters.put("UserType", Utils.app_type);
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("iOrderId", iOrderId);
        parameters.put("iTripId", tripId);
        parameters.put("vCancelReason", reason);
        parameters.put("eSystem", Utils.eSystem_Type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {
                    generalFunc.storeData(Utils.DRIVER_ONLINE_KEY, "true");
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)), "", generalFunc.retrieveLangLBl("", "LBL_OK"), buttonId -> generalFunc.restartApp());

                } else {
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }

            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();

    }


    String msg = "";

    public void pubnubmsg(String msg) {
        if (this.msg.equalsIgnoreCase(msg)) {
            return;
        }
        this.msg = msg;


        bottomLayout.setVisibility(View.GONE);
        footerLayout.setVisibility(View.GONE);


        generalFunc.showGeneralMessage("", msg, "", generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), button_Id -> {
            loading_order_item_list.setVisibility(View.VISIBLE);
            getOrderDetailList(false);


        });

    }


    @Override
    protected void onDestroy() {
        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().stopLocationUpdates(this);
        }
        super.onDestroy();

    }

    private void initView() {
        instructionslit = new ArrayList<>();
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        orderIdHTxt = (MTextView) findViewById(R.id.orderIdHTxt);
        noSItemsTxt = (MTextView) findViewById(R.id.noSItemsTxt);
        orderIdVTxt = (MTextView) findViewById(R.id.orderIdVTxt);
        orderDateTxt = (MTextView) findViewById(R.id.orderDateTxt);
        orderStatusTxt = (MTextView) findViewById(R.id.orderStatusTxt);
        orderTotalBillHTxt = (MTextView) findViewById(R.id.orderTotalBillHTxt);
        orderTotalBillVTxt = (MTextView) findViewById(R.id.orderTotalBillVTxt);
        collectAmountRestHTxt = (MTextView) findViewById(R.id.collectAmountRestHTxt);
        collectAmountRestVTxt = (MTextView) findViewById(R.id.collectAmountRestVTxt);
        collectAmountUserHTxt = (MTextView) findViewById(R.id.collectAmountUserHTxt);
        collectAmountUserVTxt = (MTextView) findViewById(R.id.collectAmountUserVTxt);


        backImgView = (ImageView) findViewById(R.id.backImgView);
        ordertitleTxt = (MTextView) findViewById(R.id.orderinfoTxt);
        storeNameTxt = (MTextView) findViewById(R.id.storeNameTxt);
        storeAddressTxt = (MTextView) findViewById(R.id.storeAddressTxt);
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();

        orderDeliverArea = (LinearLayout) findViewById(R.id.orderDeliverArea);
        orderHeaderArea = (RelativeLayout) findViewById(R.id.orderHeaderArea);
        trackUserLocationArea = (LinearLayout) findViewById(R.id.trackUserLocationArea);
        callUserArea = (LinearLayout) findViewById(R.id.callUserArea);
        call_navigate_Area = (LinearLayout) findViewById(R.id.call_navigate_Area);

        restaurantLocationVTxt = (MTextView) findViewById(R.id.restaurantLocationVTxt);
        userNameVTxt = (MTextView) findViewById(R.id.userNameVTxt);
        userAddressTxt = (MTextView) findViewById(R.id.userAddressTxt);
        distanceVTxt = (MTextView) findViewById(R.id.distanceVTxt);
        distanceHTxt = (MTextView) findViewById(R.id.distanceHTxt);


        orderItemListRecyclerView = (RecyclerView) findViewById(R.id.orderItemListRecyclerView);
        noItemsTxt = (MTextView) findViewById(R.id.noItemsTxt);
        loading_order_item_list = (ProgressBar) findViewById(R.id.loading_order_item_list);
        errorView = (ErrorView) findViewById(R.id.errorView);

        // Set Deliver Area

        if (isDeliver) {
            call_navigate_Area.setVisibility(View.VISIBLE);
            orderItemListRecyclerView.setVisibility(View.GONE);
            orderDeliverArea.setVisibility(View.VISIBLE);
//            orderHeaderArea.setVisibility(View.GONE);
        } else {
            call_navigate_Area.setVisibility(View.GONE);
            orderItemListRecyclerView.setVisibility(View.VISIBLE);
//            orderHeaderArea.setVisibility(View.VISIBLE);
            orderDeliverArea.setVisibility(View.GONE);
        }
    }

    public void setLabels() {

        setLableAsPerState();

        //orderIdHTxt.setText(generalFunc.retrieveLangLBl("Order Id", "LBL_ORDER_ID_TXT") + " : ");
        genieTitletxt.setText(generalFunc.retrieveLangLBl("", "LBL_GENIE_USER_APPROVAL"));
        btn_type_refresh.setText(generalFunc.retrieveLangLBl("", "LBL_REFRESH"));
        orderIdHTxt.setText(generalFunc.retrieveLangLBl("Order Id", "LBL_ORDER") + " #");
        noSItemsTxt.setText(generalFunc.retrieveLangLBl("Item(s)", "LBL_ITEM_DETAIL_TXT"));
        orderStatusTxt.setText(generalFunc.retrieveLangLBl("Order is Placed", "LBL_ORDER_PLACED_TXT"));
        orderTotalBillHTxt.setText(generalFunc.retrieveLangLBl("Total Bill", "LBL_TOTAL_BILL_TXT"));
        collectAmountRestHTxt.setText(generalFunc.retrieveLangLBl("Pay", "LBL_BTN_PAYMENT_TXT"));
//        collectAmountUserHTxt.setText(generalFunc.retrieveLangLBl("Collect From User", "LBL_COLLECT_FROM_USER_TXT"));

        collectAmountUserHTxt.setText(generalFunc.retrieveLangLBl("Total Bill", "LBL_TOTAL_BILL_TXT"));
        if (isDeliver) {
            collectAmountUserHTxt.setText(generalFunc.retrieveLangLBl("Total Bill", "LBL_TOTAL_BILL_TXT") + " (" + generalFunc.retrieveLangLBl("", "LBL_PAID") + ") ");
        }

        distanceHTxt.setText(generalFunc.retrieveLangLBl("Distance from Store", "LBL_DISTANCE_FROM_STORE"));
    }


    public void setLableAsPerState() {
        titleTxt.setText(generalFunc.retrieveLangLBl("Delivery", "LBL_DELIVERY_TXT"));
        if (generalFunc.isRTLmode()) {
            titleTxt.setPadding(0, 0, 150, 0);
        } else {

            titleTxt.setPadding(150, 0, 0, 0);
        }


        //btn_type2.setText(isDeliver ? generalFunc.retrieveLangLBl("Order Delivered", "LBL_ORDER_DELIVERED") : generalFunc.retrieveLangLBl("Order PickedUp", "LBL_ORDER_PICKDUP"));
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_CONFIRM_TXT"));
        ordertitleTxt.setText(isDeliver ? generalFunc.retrieveLangLBl("Order Delivered", "LBL_ORDER_DELIVERED") : generalFunc.retrieveLangLBl("Order PickedUp", "LBL_ORDER_PICKDUP"));
    }


    private void getOrderDetails() {
        subItemList = new ArrayList<>();

        if (!isDeliver) {
            //set true for genie
            orderItemListRecycleAdapter = new OrderItemListRecycleAdapter(getActContext(), subItemList, generalFunc, false, isPhotoUploaded, false);
            orderItemListRecyclerView.setAdapter(orderItemListRecycleAdapter);
            orderItemListRecycleAdapter.setSubItemList(subItemList, isPhotoUploaded);
            orderItemListRecycleAdapter.notifyDataSetChanged();
            orderItemListRecycleAdapter.setOnItemClickListener(this);

            orderItemListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    int visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                    int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                    int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                    int lastInScreen = firstVisibleItemPosition + visibleItemCount;
                    if ((lastInScreen == totalItemCount) && !(mIsLoading) && isNextPageAvailable) {

                        mIsLoading = true;
                        orderItemListRecycleAdapter.addFooterView();

                        getOrderDetailList(true);

                    } else if (!isNextPageAvailable) {
                        orderItemListRecycleAdapter.removeFooterView();
                    }
                }
            });

        }
        getOrderDetailList(false);
    }

    @Override
    public void onItemClickList(int position, String pickedFromRes) {
        // showPreferenceHelp(subItemList.get(position).getItemName(),position);
    }

    androidx.appcompat.app.AlertDialog uploadImgAlertDialog;
    ImageView clearImg;
    LinearLayout uploadImgArea;
    MaterialEditText priceBox;
    CardView mCardView;
    Boolean isitemSwitch = true;

    private void showPreferenceHelp(String name, String qty, int pos) {
        if (uploadImgAlertDialog != null && uploadImgAlertDialog.isShowing()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActContext());
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.desgin_genie_upload_image, null);
        builder.setView(dialogView);


        final ImageView iamage_source = (ImageView) dialogView.findViewById(R.id.iamage_source);
        iamage_source.setImageResource(R.drawable.ic_pencil_genie);

        final ImageView cancelImg = (ImageView) dialogView.findViewById(R.id.cancelImg);
        final MTextView msgTxt = (MTextView) dialogView.findViewById(R.id.msgTxt);
        final MTextView payNotTxt = (MTextView) dialogView.findViewById(R.id.payNotTxt);
        final ImageView payinfo = (ImageView) dialogView.findViewById(R.id.payinfo);
        final LinearLayout priceBoxArea = (LinearLayout) dialogView.findViewById(R.id.priceBoxArea);
        final MTextView itemAvailTxt = (MTextView) dialogView.findViewById(R.id.itemAvailTxt);
        final ImageView iteminfo = (ImageView) dialogView.findViewById(R.id.iteminfo);
        SwitchButton itemSwitch = (SwitchButton) dialogView.findViewById(R.id.itemSwitch);
        iteminfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GenieOrderType != null && GenieOrderType.equalsIgnoreCase("Runner")) {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_NOTE_ITEM_UNAVAILABLE_RUNNER"));
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_NOTE_ITEM_UNAVAILABLE_GENIE"));
                }

            }
        });

        msgTxt.setVisibility(View.GONE);
        final MTextView titileTxt = (MTextView) dialogView.findViewById(R.id.titileTxt);
        mCardView = (CardView) dialogView.findViewById(R.id.mCardView);


        final MTextView uploadTitleTxt = (MTextView) dialogView.findViewById(R.id.uploadTitleTxt);
        uploadImgArea = (LinearLayout) dialogView.findViewById(R.id.uploadImgArea);
        final LinearLayout uploadArea = (LinearLayout) dialogView.findViewById(R.id.uploadArea);
        clearImg = (ImageView) dialogView.findViewById(R.id.clearImg);
        clearImg.setVisibility(View.GONE);
        priceBox = (MaterialEditText) dialogView.findViewById(R.id.priceBox);
        payNotTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PAYMENT_NOT_REQUIRED"));
        payinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PAYMENT_NOT_REQUIRED_HELP_INFO"));
            }
        });

        if (subItemList.get(pos).getExtrapayment() != null && subItemList.get(pos).getExtrapayment().equalsIgnoreCase("No")) {
            payNotTxt.setVisibility(View.VISIBLE);
            payinfo.setVisibility(View.VISIBLE);
            priceBoxArea.setVisibility(View.GONE);

        }


        itemSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            isitemSwitch = b;
            if (b == true) {
                itemSwitch.setBackColorRes(R.color.Green);
                mCardView.setVisibility(View.VISIBLE);
                priceBox.setVisibility(View.VISIBLE);
            } else {
                itemSwitch.setBackColorRes(android.R.color.holo_red_dark);

                mCardView.setVisibility(View.GONE);
                priceBox.setVisibility(View.GONE);


            }


        });
        if (subItemList.get(pos).geteItemAvailable() != null && subItemList.get(pos).geteItemAvailable().equalsIgnoreCase("Yes")) {
            itemSwitch.setChecked(true);
            itemSwitch.setBackColorRes(R.color.Green);
            mCardView.setVisibility(View.VISIBLE);
            priceBox.setVisibility(View.VISIBLE);

        } else {
            Logger.d("itemSwitch", "::" + false);
            itemSwitch.setChecked(false);
            itemSwitch.setBackColorRes(android.R.color.holo_red_dark);
            mCardView.setVisibility(View.GONE);
            priceBox.setVisibility(View.GONE);

        }


        priceBox.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        MButton btn_type2 = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_SUBMIT_TXT"));
        btn_type2.setId(Utils.generateViewId());
        priceBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_ENTER_ITEM_AMOUNT"));
        uploadArea.setVisibility(View.VISIBLE);
        titileTxt.setText(name + " x " + qty);
        itemAvailTxt.setText(generalFunc.retrieveLangLBl("Item Available ?", "LBL_ITEM_AVAILABLE"));
        if (subItemList.get(pos).getItemPrice() != null && GeneralFunctions.parseDoubleValue(0, subItemList.get(pos).getfTotPriceWithoutSymbol()) > 0) {

            priceBox.setText(subItemList.get(pos).getfTotPriceWithoutSymbol());
        }
        if (subItemList.get(pos).getvImageUploaded() != null && subItemList.get(pos).getvImageUploaded().equalsIgnoreCase("Yes")) {
            isImgUploaded = true;
            selectedImagePath = subItemList.get(pos).getItemDetailsUpdated();
            clearImg.setVisibility(View.VISIBLE);
            uploadImgArea.setClickable(false);

            dialogView.findViewById(R.id.camImgVIew).setVisibility(View.GONE);
            dialogView.findViewById(R.id.ic_add).setVisibility(View.GONE);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(subItemList.get(pos).getvImage(), options);

            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;

            double ratioOfImage = (double) imageWidth / (double) imageHeight;
            double widthOfImage = ratioOfImage * Utils.dipToPixels(getActContext(), 200);
            Picasso.get().load(subItemList.get(pos).getvImage()).resize((int) widthOfImage, Utils.dipToPixels(getActContext(), 200)).placeholder(R.mipmap.ic_no_icon).into(((ImageView) dialogView.findViewById(R.id.uploadImgVIew)));


        }


        uploadTitleTxt.setText(generalFunc.retrieveLangLBl("Upload Item image", "LBL_UPLOAD_ITEM_PHOTO"));

        cancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImgAlertDialog.dismiss();
                uploadImgAlertDialog = null;

            }
        });

        uploadImgArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (generalFunc.isCameraPermissionGranted()) {
                    isGenie = true;
                    new ImageSourceDialog().run();
                } else {
                    generalFunc.showMessage(btn_type2, "Allow this app to use camera.");
                }
            }
        });

        btn_type2.setOnClickListener(view -> {
            if (mCardView.getVisibility() == View.VISIBLE) {
                boolean isPriceEnter = Utils.checkText(priceBox);
                boolean isImageSelect = Utils.checkText(selectedImagePath);
                if (!isPriceEnter && priceBoxArea.getVisibility() == View.VISIBLE) {
                    priceBox.setError(generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD"));


                }
                if (!isImageSelect) {
                    mCardView.setBackgroundResource(R.drawable.error_border);

                }

                if ((!isPriceEnter && priceBoxArea.getVisibility() == View.VISIBLE) || (!isImageSelect && !isImgUploaded)) {
                    return;
                }
            }

//            else {
            // uploadStatusTxt.setVisibility(View.GONE);
//            subItemList.get(pos).setItemImg(selectedImagePath);
//            subItemList.get(pos).setItemPrice(Utils.getText(priceBox));
//            subItemList.get(pos).setItemTotalPrice(Utils.getText(priceBox) + list.get(0).getCurrencySymbol());
            //orderItemListRecycleAdapter.notifyDataSetChanged();
            // uploadImgAlertDialog.dismiss();
            // }
            checkItemUploadData();
        });
        uploadImgAlertDialog = builder.create();
        uploadImgAlertDialog.setCancelable(false);
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(uploadImgAlertDialog);
        }
        uploadImgAlertDialog.getWindow().setBackgroundDrawable(getActContext().getResources().getDrawable(R.drawable.all_roundcurve_card));
        uploadImgAlertDialog.show();


    }

    @Override
    public void onItemImageUpload(int position) {
        selpos = position;
        isImgUploaded = false;
        selectedImagePath = "";
        showPreferenceHelp(subItemList.get(position).getItemName(), subItemList.get(position).getItemQuantity(), position);

    }

    public void sinchCall(boolean isStore) {
        if (isStore) {
            if (!list.get(0).getRestaurantImage().equals("")) {
                vImage = CommonUtilities.COMPANY_PHOTO_PATH + list.get(0).getRestaurantId() + "/"
                        + list.get(0).getRestaurantImage();
            }
            vName = list.get(0).getRestaurantName();


        } else {
            if (!data_trip.get("PPicName").equals("")) {
                vImage = CommonUtilities.USER_PHOTO_PATH + data_trip.get("PassengerId") + "/" + data_trip.get("PPicName");
            }
            vName = data_trip.get("PName");
        }

        if (!generalFunc.isCallPermissionGranted(false)) {
            generalFunc.isCallPermissionGranted(true);
        } else {
            if (new AppFunctions(getActContext()).checkSinchInstance(getSinchServiceInterface())) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("Id", generalFunc.getMemberId());
                hashMap.put("Name", generalFunc.getJsonValueStr("vName", userProfileJsonObj));
                hashMap.put("PImage", generalFunc.getJsonValueStr("vImage", userProfileJsonObj));
                hashMap.put("type", Utils.userType);
                getSinchServiceInterface().getSinchClient().setPushNotificationDisplayName(generalFunc.retrieveLangLBl("", "LBL_INCOMING_CALL"));
                if (isStore) {
                    Call call = getSinchServiceInterface().callUser(Utils.CALLTOSTORE + "_" + list.get(0).getRestaurantId(), hashMap);
                    String callId = call.getCallId();
                    Intent callScreen = new Intent(getActContext(), CallScreenActivity.class);
                    callScreen.putExtra(SinchService.CALL_ID, callId);
                    callScreen.putExtra("vImage", vImage);
                    callScreen.putExtra("vName", vName);

                    startActivity(callScreen);

                } else {
                    Call call = getSinchServiceInterface().callUser(Utils.CALLTOPASSENGER + "_" + data_trip.get("PassengerId"), hashMap);
                    String callId = call.getCallId();
                    Intent callScreen = new Intent(getActContext(), CallScreenActivity.class);
                    callScreen.putExtra(SinchService.CALL_ID, callId);
                    callScreen.putExtra("vImage", vImage);
                    callScreen.putExtra("vName", vName);
                    startActivity(callScreen);

                }
            }
        }
    }

    public void onLocationUpdate(Location location) {
        this.userLocation = location;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case GeneralFunctions.MY_PERMISSIONS_REQUEST: {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ArrayList<String> requestPermissions = new ArrayList<>();
                    requestPermissions.add(android.Manifest.permission.CAMERA);
                    requestPermissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (generalFunc.isAllPermissionGranted(true,requestPermissions)) {
                        btn_type2.performClick();
                    }
                } else {
                    if (generalFunc.isPermisionGranted()) {
                        btn_type2.performClick();
                    }
                }
                // TODO: 28-04-2021 / Viral | Android 11 (30) Store permission code remove | revert 29
                /*if (generalFunc.isPermisionGranted()) {
                    btn_type2.performClick();
                }*/
                break;

            }
        }
    }


    private void GenieBuildOrderStatusConfirmation(boolean redirectToPhotoUpload) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            if (btn_id == 0) {
                generateAlert.closeAlertBox();
            } else if (btn_id == 1) {
                orderPickedUpOrDeliver(list.get(0).getTotalAmount(), redirectToPhotoUpload);
            }
        });
        generateAlert.setContentMessage("", !isDeliver ? generalFunc.retrieveLangLBl("", "LBL_GENIE_ITEM_EDITED") : generalFunc.retrieveLangLBl("Kindly Confirm to mark order as delivered ?", "LBL_ORDER_DELIVERED_CONFIRMATION_TXT"));
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));

        generateAlert.showAlertBox();
    }

    private void BuildOrderStatusConfirmation(boolean redirectToPhotoUpload) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            if (btn_id == 0) {
                generateAlert.closeAlertBox();
            } else if (btn_id == 1) {
                orderPickedUpOrDeliver(list.get(0).getTotalAmount(), redirectToPhotoUpload);
            }
        });
        generateAlert.setContentMessage("", !isDeliver ? generalFunc.retrieveLangLBl("", "LBL_ORDER_PICKEDUP_CONFIRMATION") : generalFunc.retrieveLangLBl("Kindly Confirm to mark order as delivered ?", "LBL_ORDER_DELIVERED_CONFIRMATION_TXT"));
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));

        generateAlert.showAlertBox();
    }

    public void getMaskNumber(String number) {
        if (generalFunc.getJsonValueStr("CALLMASKING_ENABLED", userProfileJsonObj).equalsIgnoreCase("Yes")) {

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("type", "getCallMaskNumber");
            parameters.put("iOrderId", iOrderId);
            parameters.put("iTripid", data_trip.get("iTripId"));
            parameters.put("UserType", Utils.userType);
            parameters.put("iMemberId", generalFunc.getMemberId());
            parameters.put("eSystem", Utils.eSystem_Type);
            ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
            exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);

            exeWebServer.setDataResponseListener(responseString -> {
                JSONObject responseStringObject = generalFunc.getJsonObject(responseString);

                if (responseStringObject != null && !responseStringObject.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseStringObject);

                    if (isDataAvail) {
                        String message = generalFunc.getJsonValueStr(Utils.message_str, responseStringObject);
                        call(generalFunc.getJsonValue("PhoneNo", message));
                    } else {
                        call(number);
                    }

                } else {
                    generalFunc.showError();
                }
            });
            exeWebServer.execute();
        } else {
            call(number);
        }
    }

    public void call(String phoneNumber) {
        try {

            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void getOrderDetailList(final boolean isLoadMore) {
        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }

        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "GetOrderDetailsRestaurant");
        parameters.put("iOrderId", iOrderId);
        parameters.put("UserType", Utils.app_type);
        if (isLoadMore) {
            parameters.put("page", next_page_str);
        }
        parameters.put("eSystem", Utils.eSystem_Type);

        noItemsTxt.setVisibility(View.GONE);
        list.clear();
        subItemList.clear();

        if (orderItemListRecycleAdapter != null) {
            orderItemListRecycleAdapter.notifyDataSetChanged();

        }

        final ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(responseString -> {
            JSONObject responseStringObject = generalFunc.getJsonObject(responseString);

            noItemsTxt.setVisibility(View.GONE);
            closeLoader();
            if (responseStringObject != null && !responseStringObject.equals("")) {


                if (generalFunc.checkDataAvail(Utils.action_str, responseStringObject)) {
                    list = new ArrayList<>();
                    String nextPage = generalFunc.getJsonValueStr("NextPage", responseStringObject);

                    JSONObject msg_obj = generalFunc.getJsonObject("message", responseStringObject);
                    JSONArray itemList = generalFunc.getJsonArray("itemlist", msg_obj.toString());

                    // Order's Details Add
                    orderDetailDataModel orderDetail = new orderDetailDataModel();
                    orderDetail.setOrderID(generalFunc.getJsonValueStr("iOrderId", msg_obj));
                    orderDetail.setvOrderNo(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("vOrderNo", msg_obj)));
                    orderDetail.setIsPhotoUploaded(generalFunc.getJsonValueStr("isPhotoUploaded", msg_obj));
                    orderDetail.setvVehicleType(generalFunc.getJsonValueStr("vVehicleType", msg_obj));

                    orderDetail.setOrderDate_Time(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("tOrderRequestDate", msg_obj)));
                    String tOrderRequestDate_Org = generalFunc.getJsonValueStr("tOrderRequestDate_Org", msg_obj);
                    String formattedDate = generalFunc.getDateFormatedType(tOrderRequestDate_Org, Utils.OriginalDateFormate, Utils.getDetailDateFormat(getActContext()));
                    orderDetail.setOrderDate_Time(generalFunc.convertNumberWithRTL(formattedDate));
                    orderDetail.setTotalAmount(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("originalTotal", msg_obj)));
                    orderDetail.setCurrencySymbol(generalFunc.getJsonValueStr("vSymbol", msg_obj));
                    orderDetail.setTotalAmountWithSymbol(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("SubTotal", msg_obj)));
                    orderDetail.setTotalItems(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("TotalItems", msg_obj)));
                    orderDetail.setUserPhone(generalFunc.getJsonValueStr("UserPhone", msg_obj));
                    orderDetail.setUserName(generalFunc.getJsonValueStr("UserName", msg_obj));
                    orderDetail.setUserDistance(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("UserDistance", msg_obj)));
//                    orderDetail.seteConfirm(generalFunc.getJsonValueStr("UserDistance", msg_obj));
                    String userAddress = generalFunc.getJsonValueStr("UserAddress", msg_obj);
                    orderDetail.setUserAddress(Utils.checkText(userAddress) ? WordUtils.capitalize(userAddress) : userAddress);
                    orderDetail.setUserLatitude(generalFunc.getJsonValueStr("UserLatitude", msg_obj));
                    orderDetail.setUserLongitude(generalFunc.getJsonValueStr("UserLongitude", msg_obj));

                    orderDetail.setePaid(generalFunc.getJsonValueStr("ePaid", msg_obj));

                    orderDetail.setePaymentOption(generalFunc.getJsonValueStr("ePaymentOption", msg_obj));

                    ePaymentOption = generalFunc.getJsonValueStr("ePaymentOption", msg_obj);


                    String restAddress = generalFunc.getJsonValueStr("vRestuarantLocation", msg_obj);
                    orderDetail.setRestaurantAddress(Utils.checkText(restAddress) ? WordUtils.capitalize(restAddress) : restAddress);

                    orderDetail.setRestaurantName(generalFunc.getJsonValueStr("vCompany", msg_obj));
                    orderDetail.setRestaurantId(generalFunc.getJsonValueStr("iCompanyId", msg_obj));
                    orderDetail.setRestaurantImage(generalFunc.getJsonValueStr("vRestuarantImage", msg_obj));
                    orderDetail.setRestaurantLattitude(generalFunc.getJsonValueStr("RestuarantLat", msg_obj));
                    orderDetail.setRestaurantLongitude(generalFunc.getJsonValueStr("RestuarantLong", msg_obj));
                    orderDetail.setRestaurantNumber(generalFunc.getJsonValueStr("RestuarantPhone", msg_obj));


                    JSONObject DeliveryPreferences = generalFunc.getJsonObject("DeliveryPreferences", responseStringObject);

                    isPrefrence = generalFunc.getJsonValueStr("Enable", DeliveryPreferences).equalsIgnoreCase("Yes") ? true : false;
                    if (isPrefrence) {

                        btn_type2.setText(generalFunc.retrieveLangLBl("Next", "LBL_NEXT"));
                        vTitle = generalFunc.getJsonValueStr("vTitle", DeliveryPreferences);
                        JSONArray Data = generalFunc.getJsonArray("Data", DeliveryPreferences);
                        if (Data != null) {
                            for (int i = 0; i < Data.length(); i++) {
                                try {
                                    JSONObject jsonObject = (JSONObject) Data.get(i);
                                    String tTitle = generalFunc.getJsonValueStr("tTitle", jsonObject);
                                    String tDescription = generalFunc.getJsonValueStr("tDescription", jsonObject);
                                    String ePreferenceFor = generalFunc.getJsonValueStr("ePreferenceFor", jsonObject);
                                    String eImageUpload = generalFunc.getJsonValueStr("eImageUpload", jsonObject);
                                    String iDisplayOrder = generalFunc.getJsonValueStr("iDisplayOrder", jsonObject);
                                    String eContactLess = generalFunc.getJsonValueStr("eContactLess", jsonObject);
                                    String eStatus = generalFunc.getJsonValueStr("eStatus", jsonObject);
                                    String iPreferenceId = generalFunc.getJsonValueStr("iPreferenceId", jsonObject);
                                    HashMap<String, String> hashMap = new HashMap<>();

                                    hashMap.put("tTitle", tTitle);
                                    hashMap.put("tDescription", tDescription);
                                    hashMap.put("ePreferenceFor", ePreferenceFor);
                                    hashMap.put("eImageUpload", eImageUpload);
                                    hashMap.put("iDisplayOrder", iDisplayOrder);
                                    hashMap.put("eContactLess", eContactLess);
                                    hashMap.put("eStatus", eStatus);
                                    hashMap.put("iPreferenceId", iPreferenceId);
                                    instructionslit.add(hashMap);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                        //  moreInstructionAdapter.notifyDataSetChanged();

                    }
                    eBuyAnyService = generalFunc.getJsonValueStr("eBuyAnyService", msg_obj);
                    eAutoaccept = generalFunc.getJsonValueStr("eAutoaccept", msg_obj);
                    vInstruction = generalFunc.getJsonValueStr("vInstruction", msg_obj);
                    GenieOrderType = generalFunc.getJsonValueStr("GenieOrderType", msg_obj);

                    if (ePaymentOption.equalsIgnoreCase("Cash")) {
                        collectAmountUserHTxt.setText(generalFunc.retrieveLangLBl("Collect From User", "LBL_COLLECT_FROM_USER_TXT"));
                    }
                    if (eBuyAnyService.equalsIgnoreCase("Yes")) {
                        collectAmountUserHTxt.setText(generalFunc.retrieveLangLBl("Collect From User", "LBL_TOTAL_BILL_TXT"));
                        onCreateOptionsMenu(menu);
                    }

                    if (itemList != null && itemList.length() > 0) {
                        ArrayList<orderItemDetailDataModel> subItemList = new ArrayList<orderItemDetailDataModel>();

                        for (int i = 0; i < itemList.length(); i++) {
                            orderItemDetailDataModel orderItemList = new orderItemDetailDataModel();

                            JSONObject item_list_detail = generalFunc.getJsonObject(itemList, i);
                            if (eBuyAnyService.equalsIgnoreCase("Yes")) {
                                orderItemList.setIsGenie("Yes");
                                orderItemList.setvImageUploaded(generalFunc.getJsonValueStr("vImageUploaded", item_list_detail));
                                orderItemList.seteDecline(generalFunc.getJsonValueStr("eDecline", item_list_detail));
                                orderItemList.setItemDetailsUpdated(generalFunc.getJsonValueStr("itemDetailsUpdated", item_list_detail));
                            } else {
                                orderItemList.setIsGenie("No");
                            }

                            orderItemList.seteItemAvailable(generalFunc.getJsonValueStr("eItemAvailable", item_list_detail));
                            orderItemList.setExtrapayment(generalFunc.getJsonValueStr("eExtraPayment", item_list_detail));
                            orderItemList.setItemName(generalFunc.getJsonValueStr("MenuItem", item_list_detail));
                            orderItemList.setItemQuantity(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("iQty", item_list_detail)));
                            orderItemList.setItemPrice(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("fTotPrice", item_list_detail)));
                            orderItemList.setSubItemName(generalFunc.getJsonValueStr("SubTitle", item_list_detail));
                            orderItemList.seteAvailable(generalFunc.getJsonValueStr("eAvailable", item_list_detail));
                            orderItemList.setvImage(generalFunc.getJsonValueStr("vImage", item_list_detail));
                            orderItemList.setiOrderDetailId(generalFunc.getJsonValueStr("iOrderDetailId", item_list_detail));
                            orderItemList.setItemTotalPrice(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("fTotPrice", item_list_detail)));
                            orderItemList.setfTotPriceWithoutSymbol(generalFunc.getJsonValueStr("fTotPriceWithoutSymbol", item_list_detail));
                            orderItemList.setTotalDiscountPrice(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("TotalDiscountPrice", item_list_detail)));
                            subItemList.add(orderItemList);
                        }
                        orderDetail.setorderItemDetailList(subItemList);
                    }

                    list.add(orderDetail);
                    if (isFirst) {

                        if (!vInstruction.equalsIgnoreCase("") && eAutoaccept.equalsIgnoreCase("Yes")) {
                            PreferenceDailogJava preferenceDailogJava = new PreferenceDailogJava(getActContext());
                            preferenceDailogJava.showPreferenceDialog(generalFunc.retrieveLangLBl("", "LBL_VIEW_SPEC_INS_FOR_STORE"), vInstruction, R.drawable.ic_notes, false,
                                    generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), "", false);

                            menu.findItem(R.id.intruction_store).setVisible(true);

                        }
                    }

                    storeNameTxt.setText(orderDetail.getRestaurantName());
                    storeAddressTxt.setText(restAddress);

                    if (!orderDetail.getRestaurantImage().equals("")) {
                        vImage = CommonUtilities.COMPANY_PHOTO_PATH + orderDetail.getRestaurantId() + "/"
                                + orderDetail.getRestaurantImage();
                    } else {
                        vImage = "temp";
                    }
//                    if (GenieOrderType != null && GenieOrderType.equalsIgnoreCase("Runner")) {
//
//                        ((SelectableRoundedImageView) findViewById(R.id.storeImageView)).setVisibility(View.GONE);
//                        ((ImageView) findViewById(R.id.runnerImageView)).setVisibility(View.VISIBLE);
//                        ((ImageView) findViewById(R.id.runnerImageView)).setImageDrawable(getResources().getDrawable(R.drawable.ic_locate_place));
//                    } else {
                    Picasso.get().load(vImage).placeholder(R.mipmap.ic_no_icon).error(R.mipmap.ic_no_icon).into((SelectableRoundedImageView) findViewById(R.id.storeImageView));
                    //  }


                    setOrderDetails();

                    if (orderItemListRecycleAdapter != null) {
                        orderItemListRecycleAdapter.notifyDataSetChanged();
                    }

                    if (!nextPage.equals("") && !nextPage.equals("0")) {
                        next_page_str = nextPage;
                        isNextPageAvailable = true;
                    } else {
                        removeNextPageConfig();
                    }

                    if (eBuyAnyService.equalsIgnoreCase("Yes")) {
                        if (generalFunc.getJsonValueStr("genieWaitingForUserApproval", msg_obj).equalsIgnoreCase("Yes") &&
                                generalFunc.getJsonValueStr("genieUserApproved", msg_obj).equalsIgnoreCase("No")) {
                            manageGenieRefreshView();
                            orderItemListRecycleAdapter.setEditable(false);

                        }
                        if (!generalFunc.getJsonValueStr("ePaid", msg_obj).equalsIgnoreCase("Yes")) {
                            if (generalFunc.getJsonValueStr("genieWaitingForUserApproval", msg_obj).equalsIgnoreCase("Yes") &&
                                    generalFunc.getJsonValueStr("genieUserApproved", msg_obj).equalsIgnoreCase("Yes")) {
                                orderItemListRecycleAdapter.setEditable(false);
                                if (ePaymentOption.equalsIgnoreCase("Cash")) {
//                                    MyApp.getInstance().restartWithGetDataApp();
//                                    return;

                                    bottomGenieLayout.setVisibility(View.GONE);
                                    bottomLayout.setVisibility(View.VISIBLE);
                                    footerLayout.setVisibility(View.VISIBLE);
                                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    params.addRule(RelativeLayout.ABOVE, R.id.footerLayout);
                                    params.addRule(RelativeLayout.BELOW, R.id.toolbar_include);
                                    main_layout.setLayoutParams(params);

                                } else {
                                    manageGenieRefreshView();
                                }
                                genieTitletxt.setText(generalFunc.retrieveLangLBl("Waiting for user to make payment", "LBL_GENIE_PAYMENT_WAITING"));
                                //manageGenieRefreshView();


                            }
                        }
                        if (generalFunc.getJsonValueStr("ePaid", msg_obj).equalsIgnoreCase("Yes")) {
                            orderItemListRecycleAdapter.setEditable(false);
                            bottomGenieLayout.setVisibility(View.GONE);
                            bottomLayout.setVisibility(View.VISIBLE);
                            footerLayout.setVisibility(View.VISIBLE);

                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.addRule(RelativeLayout.ABOVE, R.id.footerLayout);
                            params.addRule(RelativeLayout.BELOW, R.id.toolbar_include);
                            main_layout.setLayoutParams(params);
                            orderItemListRecycleAdapter.setEditable(false);
//                            MyApp.getInstance().restartWithGetDataApp();
//                            return;

                        }

                    }


                } else {
                    if (list.size() == 0) {
                        removeNextPageConfig();
                        noItemsTxt.setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseStringObject)));
                        noItemsTxt.setVisibility(View.VISIBLE);
                    }
                }

                if (orderItemListRecycleAdapter != null) {
                    orderItemListRecycleAdapter.notifyDataSetChanged();

                }


            } else {
                if (isLoadMore == false) {
                    removeNextPageConfig();
                    generateErrorView();
                }

            }

            mIsLoading = false;
        });

        if (!isLoadMore) {
            if (list.size() == 0) {
                exeWebServer.execute();
            }

        } else {
            exeWebServer.execute();
        }

    }

    int selpos = 0;

    public void checkItemUploadData() {


        ArrayList<String[]> paramsList = new ArrayList<>();
        paramsList.add(Utils.generateImageParams("type", "UpdateOrderReviewItemDetails"));
        paramsList.add(Utils.generateImageParams("MemberType", Utils.app_type));
        paramsList.add(Utils.generateImageParams("iOrderDetailId", subItemList.get(selpos).getiOrderDetailId()));
        paramsList.add(Utils.generateImageParams("iOrderId", iOrderId));
        paramsList.add(Utils.generateImageParams("iItemPrice", Utils.getText(priceBox)));

        paramsList.add(Utils.generateImageParams("vImage", selectedImagePath));
        paramsList.add(Utils.generateImageParams("eItemAvailable", isitemSwitch ? "Yes" : "No"));

        paramsList.add(Utils.generateImageParams("eSystem", Utils.eSystem_Type));


        //  new UploadProfileImage(LiveTrackOrderDetailActivity.this, selectedImagePath, Utils.TempProfileImageName, paramsList, "").execute();

        UploadProfileImage uploadProfileImage = new UploadProfileImage(LiveTrackOrderDetailActivity.this, isImgUploaded ? "" : selectedImagePath, Utils.TempProfileImageName, paramsList, "", true);


        if (isImgUploaded || priceBox.getVisibility() == View.GONE) {
            uploadProfileImage.execute(false);
        } else {
            uploadProfileImage.execute(true);
        }

    }

    private void setOrderDetails() {
        if (list.size() > 0) {

            orderDetailDataModel orderDetailDataModel = list.get(0);

            subItemList.clear();
            subItemList.addAll(orderDetailDataModel.getorderItemDetailList());
            if (orderItemListRecycleAdapter != null) {
                orderItemListRecycleAdapter.setSubItemList(subItemList, isPhotoUploaded);
            }
            collectAmountRestHTxt.setText(generalFunc.retrieveLangLBl("Pay", "LBL_BTN_PAYMENT_TXT") + " " + orderDetailDataModel.getRestaurantName());
            orderIdVTxt.setText("" + orderDetailDataModel.getvOrderNo());
            orderTotalBillVTxt.setText(" " + orderDetailDataModel.getTotalAmountWithSymbol());
            collectAmountRestVTxt.setText(" " + orderDetailDataModel.getResturantPayAmount());

            if (Utils.checkText(orderDetailDataModel.getUserName())) {
                userNameVTxt.setText(" " + WordUtils.capitalize(orderDetailDataModel.getUserName()));
            }

            if (Utils.checkText(orderDetailDataModel.getUserAddress())) {
                userAddressTxt.setText(" " + orderDetailDataModel.getUserAddress());
            }

            restaurantLocationVTxt.setText(orderDetailDataModel.getRestaurantName() + "\n" + orderDetailDataModel.getRestaurantAddress());
            distanceVTxt.setText(orderDetailDataModel.getUserDistance());

            if (orderDetailDataModel.getePaymentOption().equalsIgnoreCase("Cash")) {
                collectAmountUserVTxt.setText(" " + orderDetailDataModel.getTotalAmountWithSymbol());
            } else {
                collectAmountUserVTxt.setText(Html.fromHtml(" " + orderDetailDataModel.getTotalAmountWithSymbol() + "<br><font color='#434343'>" + generalFunc.retrieveLangLBl("(Paid By User)", "LBL_PAYMENT_DONE_BY_USER") + "</font>"));
            }

            orderDateTxt.setText(" " + orderDetailDataModel.getOrderDate_Time());
            noSItemsTxt.setText(orderDetailDataModel.getTotalItems() + " " + generalFunc.retrieveLangLBl("Item(s)", "LBL_ITEM_DETAIL_TXT"));

        }
    }

    public void generateErrorView() {

        closeLoader();

        generalFunc.generateErrorView(errorView, "LBL_ERROR_TXT", "LBL_NO_INTERNET_TXT");

        if (errorView.getVisibility() != View.VISIBLE) {
            errorView.setVisibility(View.VISIBLE);
        }


        errorView.setOnRetryListener(() -> getOrderDetailList(false));
    }

    public void removeNextPageConfig() {
        next_page_str = "";
        isNextPageAvailable = false;
        mIsLoading = false;
        if (orderItemListRecycleAdapter != null) {
            orderItemListRecycleAdapter.removeFooterView();
        }
    }

    public void closeLoader() {
        if (loading_order_item_list.getVisibility() == View.VISIBLE) {
            loading_order_item_list.setVisibility(View.GONE);
            main_layout.setVisibility(View.VISIBLE);
            footerLayout.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.VISIBLE);
        }
    }

    public void showBill() {
        if (isShow) {
            footerLayout.startAnimation(slidedown);
            billDetail_ll.startAnimation(slideDownAnimation);

            slidedown.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    billDetail_ll.setVisibility(View.GONE);
                    isShow = false;
                    iv_arrow_icon.setImageResource(R.mipmap.ic_arrow_up);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });


        } else {
            isShow = true;
            footerLayout.startAnimation(slideUpAnimation);
            billDetail_ll.setVisibility(View.VISIBLE);
            billDetail_ll.startAnimation(slideUpAnimation);


            slideUpAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    iv_arrow_icon.setImageResource(R.mipmap.ic_arrow_down);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }
    }

    public void showBillDialog() {

        dialog = new Dialog(getActContext(), R.style.My_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.design_bill_dialog);

        MTextView submitDetailHTxt = (MTextView) dialog.findViewById(R.id.submitDetailHTxt);
        MTextView billValueHTxt = (MTextView) dialog.findViewById(R.id.billValueHTxt);
        MTextView billValueCTxt = (MTextView) dialog.findViewById(R.id.billValueCTxt);
        MTextView confirmBillHTxt = (MTextView) dialog.findViewById(R.id.confirmBillHTxt);
        MTextView confirmBillCTxt = (MTextView) dialog.findViewById(R.id.confirmBillCTxt);
        MTextView billCollectedHTxt = (MTextView) dialog.findViewById(R.id.billCollectedHTxt);
        MTextView billCollectedCTxt = (MTextView) dialog.findViewById(R.id.billCollectedCTxt);
        MTextView paidHTxtView = (MTextView) dialog.findViewById(R.id.paidHTxtView);
        MTextView paidBillCTxt = (MTextView) dialog.findViewById(R.id.paidBillCTxt);
        MTextView cancelHTxt = (MTextView) dialog.findViewById(R.id.cancelHTxt);
        MTextView confirmHTxt = (MTextView) dialog.findViewById(R.id.confirmHTxt);

        if (Utils.checkText(list.get(0).getCurrencySymbol())) {
            billValueCTxt.setText("" + list.get(0).getCurrencySymbol());
            confirmBillCTxt.setText("" + list.get(0).getCurrencySymbol());
            billCollectedCTxt.setText("" + list.get(0).getCurrencySymbol());
            paidBillCTxt.setText("" + list.get(0).getCurrencySymbol());
        }

        LinearLayout ll_order_collect_Area = (LinearLayout) dialog.findViewById(R.id.ll_order_collect_Area);
        LinearLayout ll_order_deliver_Area = (LinearLayout) dialog.findViewById(R.id.ll_order_deliver_Area);
        final String required_str = generalFunc.retrieveLangLBl("Required", "LBL_FEILD_REQUIRD");

        submitDetailHTxt.setText(generalFunc.retrieveLangLBl("Submit Detail", "LBL_SUBMIT_DETAILS"));
        billValueHTxt.setText(generalFunc.retrieveLangLBl("Bill Value", "LBL_BILL_VALUE_TXT"));
        confirmBillHTxt.setText(generalFunc.retrieveLangLBl("Confirm Bill Value", "LBL_CONFIRM_BILL_VALUE_TXT"));
        billCollectedHTxt.setText(generalFunc.retrieveLangLBl("Collected", "LBL_COLLECTED_TXT"));

        cancelHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
        confirmHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CONFIRM_TXT"));

        MaterialEditText billValueEditText = (MaterialEditText) dialog.findViewById(R.id.billValueEditText);
        MaterialEditText confirmBillValueEditText = (MaterialEditText) dialog.findViewById(R.id.confirmBillValueEditText);
        MaterialEditText paidValueEditText = (MaterialEditText) dialog.findViewById(R.id.paidValueEditText);
        MaterialEditText billCollecetdValueEditText = (MaterialEditText) dialog.findViewById(R.id.billCollecetdValueEditText);

        billValueEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        confirmBillValueEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        paidValueEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        billCollecetdValueEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        if (isDeliver) {
            ll_order_collect_Area.setVisibility(View.VISIBLE);
            ll_order_deliver_Area.setVisibility(View.GONE);
        }

        cancelHTxt.setOnClickListener(view -> dialog.dismiss());

        confirmHTxt.setOnClickListener(view -> {

            if (isDeliver) {
                double enteredValue = generalFunc.parseDoubleValue(0.00, Utils.getText(billCollecetdValueEditText));
                boolean isBillAmountCollectedEntered = Utils.checkText(billCollecetdValueEditText) && enteredValue > 0 ? true : Utils.setErrorFields(billCollecetdValueEditText, required_str);

                if (isBillAmountCollectedEntered == false) {
                    return;
                }

                orderPickedUpOrDeliver(Utils.getText(billCollecetdValueEditText).trim(), false);
            } else {
                double enteredValue = generalFunc.parseDoubleValue(0.00, Utils.getText(billValueEditText));
                double reEnteredValue = generalFunc.parseDoubleValue(0.00, Utils.getText(confirmBillValueEditText));

//                    double finalTotalValue = generalFunc.parseDoubleValue(0.00, list.get(0).getTotalAmount().trim());

                /*Check fist entered amount not blank or Zero */
                boolean isBillAmountEntered = Utils.checkText(billValueEditText) && enteredValue > 0 ? true : Utils.setErrorFields(billValueEditText, required_str);

                if (isBillAmountEntered == false) {
                    return;
                }

                /*Check Confirmed Second entered amount not blank or Zero */

                boolean isReBillAmountEnter = Utils.checkText(confirmBillValueEditText) && reEnteredValue > 0 ? true : Utils.setErrorFields(confirmBillValueEditText, required_str);


                if (isReBillAmountEnter == false) {
                    return;
                }
                /*Check Confirmed Second entered amount match with first entered amout which is same as final total */

                if (reEnteredValue != enteredValue) {
                    Utils.setErrorFields(confirmBillValueEditText, generalFunc.retrieveLangLBl("Bill value is not same.", "LBL_VERIFY_BILL_VALUE_ERROR_TXT"));
                    return;
                }

                orderPickedUpOrDeliver(Utils.getText(confirmBillValueEditText).trim(), true);
                /*Upload Proof Of Arrival*/
//                    takeAndUploadPic(getActContext(), "after", Utils.getText(confirmBillValueEditText).trim());
            }

        });

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    boolean clickable = true;

    public void takeAndUploadPic(final Context mContext) {
        boolean isStoragePermissionAvail = false;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ArrayList<String> requestPermissions = new ArrayList<>();
            requestPermissions.add(android.Manifest.permission.CAMERA);
            requestPermissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            if (generalFunc.isAllPermissionGranted(true,requestPermissions)) {
                isStoragePermissionAvail = true;
            }
        } else {
            isStoragePermissionAvail = generalFunc.isCameraStoragePermissionGranted();
        }
        // TODO: 28-04-2021 / Viral | Android 11 (30) Store permission code remove | revert 29
        //boolean isStoragePermissionAvail = generalFunc.isCameraStoragePermissionGranted();
        if (!isStoragePermissionAvail) {
            return;
        }
        isFrom = "";
        selectedImagePath = "";

        uploadServicePicAlertBox = new Dialog(mContext, R.style.Theme_Dialog);
        uploadServicePicAlertBox.requestWindowFeature(Window.FEATURE_NO_TITLE);

        uploadServicePicAlertBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        uploadServicePicAlertBox.setContentView(R.layout.design_upload_service_pic);
        uploadServicePicAlertBox.setCancelable(false);

        MTextView titleTxt = (MTextView) uploadServicePicAlertBox.findViewById(R.id.titleTxt);
        final MTextView uploadStatusTxt = (MTextView) uploadServicePicAlertBox.findViewById(R.id.uploadStatusTxt);
        MTextView uploadTitleTxt = (MTextView) uploadServicePicAlertBox.findViewById(R.id.uploadTitleTxt);
        ImageView backImgView = (ImageView) uploadServicePicAlertBox.findViewById(R.id.backImgView);
        MTextView skipTxt = (MTextView) uploadServicePicAlertBox.findViewById(R.id.skipTxt);
        final ImageView uploadImgVIew = (ImageView) uploadServicePicAlertBox.findViewById(R.id.uploadImgVIew);
        LinearLayout uploadImgArea = (LinearLayout) uploadServicePicAlertBox.findViewById(R.id.uploadImgArea);
        MButton btn_type2 = ((MaterialRippleLayout) uploadServicePicAlertBox.findViewById(R.id.btn_type2)).getChildView();

        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_UPLOAD_IMAGE_SERVICE"));
        skipTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SKIP_TXT"));


        uploadTitleTxt.setText(generalFunc.retrieveLangLBl("Click and upload to submit proof of your arrival to the restaurant.Like restaurant's pic OR it's menu or order bill OR anything which shows you are at restaurant .", "LBL_UPLOAD_ORDER_PICKUP_PROOF_MSG_TXT"));
        btn_type2.setText(generalFunc.retrieveLangLBl("Save Proof And Picked Up Order", "LBL_SAVE_PROOF_ORDER_PICKUP_TXT"));


        btn_type2.setId(Utils.generateViewId());


        uploadImgArea.setOnClickListener(view -> {

            if (generalFunc.isCameraPermissionGranted()) {
                uploadServicePicAlertBox.findViewById(R.id.uploadStatusTxt).setVisibility(View.GONE);

                if (clickable) {

                    new ImageSourceDialog().run();
                    clickable = false;
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clickable = true;
                    }
                }, 1000);
            } else {
                uploadStatusTxt.setVisibility(View.VISIBLE);
                generalFunc.showMessage(uploadStatusTxt, "Allow this app to use camera.");
            }
        });
        btn_type2.setOnClickListener(view -> {
            if (!Utils.checkText(selectedImagePath)) {
                uploadStatusTxt.setVisibility(View.VISIBLE);
                generalFunc.showMessage(uploadStatusTxt, "Please select image");
            } else {
                uploadStatusTxt.setVisibility(View.GONE);
                OrderImageUpload("No");
            }
        });

        skipTxt.setOnClickListener(view -> {

            isFrom = "";
            selectedImagePath = "";
            uploadImgVIew.setImageURI(null);
            OrderImageUpload("Yes");


        });
        backImgView.setVisibility(View.GONE);

        backImgView.setOnClickListener(view -> closeuploadServicePicAlertBox());

        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(uploadServicePicAlertBox);
        }

        uploadServicePicAlertBox.show();


    }

    public void removeImage(View v) {
        isImgUploaded = false;
        isFrom = "";
        selectedImagePath = "";
        ((ImageView) uploadImgAlertDialog.findViewById(R.id.uploadImgVIew)).setImageDrawable(null);
        // ((ImageView) uploadImgAlertDialog.findViewById(R.id.uploadImgVIew)).setVisibility(View.GONE);
        uploadImgAlertDialog.findViewById(R.id.camImgVIew).setVisibility(View.VISIBLE);
        uploadImgAlertDialog.findViewById(R.id.ic_add).setVisibility(View.VISIBLE);
//        ((CardView) uploadImgAlertDialog.findViewById(R.id.mCardView)) .setBackgroundColor(Color.parseColor("#F1F5F8"));
//        ((CardView) uploadImgAlertDialog.findViewById(R.id.mCardView)) .setRadius(R.dimen._12sdp);


        uploadImgArea.setClickable(true);
        clearImg.setVisibility(View.GONE);
    }


    public void showDeliveryPreferences(final Context mContext) {

        showDeliveryPreferenceAlertBox = new Dialog(mContext, R.style.Theme_Dialog);
        showDeliveryPreferenceAlertBox.requestWindowFeature(Window.FEATURE_NO_TITLE);

        showDeliveryPreferenceAlertBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        showDeliveryPreferenceAlertBox.setContentView(R.layout.design_delivery_preferences);
        showDeliveryPreferenceAlertBox.setCancelable(false);

        MTextView titleTxt = (MTextView) showDeliveryPreferenceAlertBox.findViewById(R.id.titleTxt);
        final RelativeLayout backArea = (RelativeLayout) showDeliveryPreferenceAlertBox.findViewById(R.id.backArea);
        final RecyclerView preferenceList = (RecyclerView) showDeliveryPreferenceAlertBox.findViewById(R.id.preferenceList);
        MButton btn_type2 = ((MaterialRippleLayout) showDeliveryPreferenceAlertBox.findViewById(R.id.btn_type2)).getChildView();

        titleTxt.setText(vTitle);
        backArea.setVisibility(View.GONE);

        MoreInstructionAdapter moreInstructionAdapter = new MoreInstructionAdapter(getActContext(), instructionslit, new MoreInstructionAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(HashMap<String, String> map) {

            }

        });
        preferenceList.setAdapter(moreInstructionAdapter);


        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_OK"));
        btn_type2.setId(Utils.generateViewId());

        btn_type2.setOnClickListener(view -> {
            MyApp.getInstance().restartWithGetDataApp();
        });

        backArea.setVisibility(View.GONE);

        backArea.setOnClickListener(view -> closeShowDeliveryPreferenceAlertBox());

        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(showDeliveryPreferenceAlertBox);
        }

        showDeliveryPreferenceAlertBox.show();

    }

    private void orderPickedUpOrDeliver(String billAmount, boolean b) {

        InternetConnection intCheck = new InternetConnection(getActContext());

        if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {
            generalFunc.showGeneralMessage("",
                    generalFunc.retrieveLangLBl("No Internet Connection", "LBL_NO_INTERNET_TXT"));
            return;
        }

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "UpdateOrderStatusDriver");
        parameters.put("iDriverId", generalFunc.getMemberId());
        parameters.put("orderStatus", isDeliver ? "OrderDelivered" : "OrderPickedup");
        parameters.put("iOrderId", iOrderId);
        parameters.put("iTripid", tripId);
        parameters.put("billAmount", billAmount);
        parameters.put("UserType", Utils.app_type);
        parameters.put("eSystem", Utils.eSystem_Type);
        if (eBuyAnyService.equalsIgnoreCase("Yes")) {
            parameters.put("genieWaitingForUserApproval", "Yes");
        }
        parameters.put("eSystem", Utils.eSystem_Type);

        if (userLocation != null) {
            parameters.put("vLatitude", "" + userLocation.getLatitude());
            parameters.put("vLongitude", "" + userLocation.getLongitude());
        }
        if (GetLocationUpdates.getInstance().getLastLocation() != null) {
            Location lastLocation = GetLocationUpdates.getInstance().getLastLocation();

            parameters.put("vLatitude", "" + lastLocation.getLatitude());
            parameters.put("vLongitude", "" + lastLocation.getLongitude());
        }


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> submitProofResponse(responseString, b));
        exeWebServer.execute();

    }

    private void OrderImageUpload(String eImgSkip) {
        InternetConnection intCheck = new InternetConnection(getActContext());

        if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {
            generalFunc.showGeneralMessage("",
                    generalFunc.retrieveLangLBl("No Internet Connection", "LBL_NO_INTERNET_TXT"));
            return;
        }
        if (!TextUtils.isEmpty(isFrom)) {
            ArrayList<String[]> paramsList = new ArrayList<>();
            paramsList.add(generalFunc.generateImageParams("type", "OrderImageUpload"));
            paramsList.add(generalFunc.generateImageParams("iOrderId", iOrderId));
            paramsList.add(generalFunc.generateImageParams("iTripid", tripId));
            paramsList.add(generalFunc.generateImageParams("eImgSkip", eImgSkip));
            paramsList.add(generalFunc.generateImageParams("UserType", Utils.app_type));
            paramsList.add(generalFunc.generateImageParams("eSystem", Utils.eSystem_Type));
            paramsList.add(generateImageParams("iMemberId", generalFunc.getMemberId()));
            paramsList.add(generateImageParams("MemberType", Utils.app_type));
            paramsList.add(generateImageParams("tSessionId", generalFunc.getMemberId().equals("") ? "" : generalFunc.retrieveValue(Utils.SESSION_ID_KEY)));
            paramsList.add(generateImageParams("GeneralUserType", Utils.app_type));
            paramsList.add(generateImageParams("GeneralMemberId", generalFunc.getMemberId()));

            new UploadProfileImage(LiveTrackOrderDetailActivity.this, selectedImagePath, Utils.TempProfileImageName, paramsList, "", true).execute();
        } else {
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("type", "OrderImageUpload");
            parameters.put("iOrderId", iOrderId);
            parameters.put("iTripid", tripId);
            parameters.put("UserType", Utils.app_type);
            parameters.put("eImgSkip", eImgSkip);
            parameters.put("eSystem", Utils.eSystem_Type);

            ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
            exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
            exeWebServer.setDataResponseListener(responseString -> submitProofResponse(responseString, false));
            exeWebServer.execute();
        }
    }

    public void handleImgUploadResponse(String responseString, String imageUploadedType) {

        if (responseString != null && !responseString.equals("")) {
            if (eBuyAnyService.equalsIgnoreCase("Yes")) {

                String msgTxt = "";

                msgTxt = generalFunc.retrieveLangLBl("Your document is uploaded successfully", "LBL_UPLOAD_DOC_SUCCESS");
                if (isGenie || mCardView.getVisibility() == View.GONE) {
                    msgTxt = generalFunc.retrieveLangLBl("Your document is uploaded successfully", "LBL_ITEM_INFO_UPDATE");

                }

                final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                generateAlert.setCancelable(false);
                generateAlert.setBtnClickList(btn_id -> {
                    generateAlert.closeAlertBox();

                    if (uploadImgAlertDialog != null) {
                        uploadImgAlertDialog.dismiss();
                        uploadImgAlertDialog = null;
                    }
                    loading_order_item_list.setVisibility(View.VISIBLE);
                    getOrderDetailList(false);
                });
                generateAlert.setContentMessage("", msgTxt);
                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));

                generateAlert.showAlertBox();

            } else {
                submitProofResponse(responseString, false);
            }
        } else {
            generalFunc.showError();
        }
    }

    private void submitProofResponse(String responseString, boolean callImageUpload) {
        JSONObject responseStringObject = generalFunc.getJsonObject(responseString);

        if (responseStringObject != null && !responseStringObject.equals("")) {

            boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseStringObject);

            if (isDataAvail) {
                /*Release Dialog instances*/
                if (dialog != null) {
                    dialog.dismiss();
                }

                if (eBuyAnyService.equalsIgnoreCase("Yes")) {
                    if (generalFunc.getJsonValueStr("genieWaitingForUserApproval", responseStringObject).equalsIgnoreCase("Yes") &&
                            generalFunc.getJsonValueStr("genieUserApproved", responseStringObject).equalsIgnoreCase("No")) {
                        manageGenieRefreshView();
                        orderItemListRecycleAdapter.setEditable(false);

                        orderItemListRecycleAdapter.notifyDataSetChanged();
                        return;
                    }

                    if (!generalFunc.getJsonValueStr("ePaid", responseStringObject).equalsIgnoreCase("Yes")) {
                        if (generalFunc.getJsonValueStr("genieWaitingForUserApproval", responseStringObject).equalsIgnoreCase("Yes") &&
                                generalFunc.getJsonValueStr("genieUserApproved", responseStringObject).equalsIgnoreCase("Yes")) {
                            genieTitletxt.setText(generalFunc.retrieveLangLBl("Waiting for user to make payment", "LBL_GENIE_PAYMENT_WAITING"));
                            orderItemListRecycleAdapter.setEditable(false);
                            manageGenieRefreshView();
                            return;
                        }
                    }
                }


                if (callImageUpload) {
                    takeAndUploadPic(getActContext());
                } else if (isPrefrence) {
                    showDeliveryPreferences(this);
                } else {
                    MyApp.getInstance().restartWithGetDataApp();
                }
            } else {
                String msg_str = generalFunc.getJsonValueStr(Utils.message_str, responseStringObject);

                if (generalFunc.getJsonValueStr("itemsAvailability", responseStringObject) != null && generalFunc.getJsonValueStr("itemsAvailability", responseStringObject).equalsIgnoreCase("No")) {

                    PreferenceDailogJava preferenceDailogJava = new PreferenceDailogJava(getActContext());
                    preferenceDailogJava.showPreferenceDialog("", generalFunc.retrieveLangLBl("", "LBL_ITEM_NOT_AVAILABLE_MARKED"), R.drawable.ic_caution, false, generalFunc.retrieveLangLBl("", "LBL_CONTACT_US_TXT")
                            , generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"), true);
                    return;
                }

                if (msg_str.equals(Utils.GCM_FAILED_KEY) || msg_str.equals(Utils.APNS_FAILED_KEY) || msg_str.equals("LBL_SERVER_COMM_ERROR")) {
                    generalFunc.restartApp();
                } else {

                    GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                    generateAlert.setContentMessage("",
                            generalFunc.retrieveLangLBl("", msg_str));
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));
                    generateAlert.showAlertBox();

                    generateAlert.setCancelable(false);
                    generateAlert.setBtnClickList(btn_id -> {

                        generateAlert.closeAlertBox();
                        if (generalFunc.getJsonValueStr("DO_RESTART", responseStringObject).equalsIgnoreCase("Yes")) {
                            MyApp.getInstance().restartWithGetDataApp();
                        }

                    });
//                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", msg_str));
                }

            }
        } else {
            generalFunc.showError();
        }
    }

    public void manageGenieRefreshView() {
        bottomGenieLayout.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.GONE);
        footerLayout.setVisibility(View.GONE);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ABOVE, R.id.bottomGenieLayout);
        params.addRule(RelativeLayout.BELOW, R.id.toolbar_include);
        main_layout.setLayoutParams(params);
    }

    public void chooseFromCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public Uri getOutputMediaFileUri(int type) {
//        return Uri.fromFile(getOutputMediaFile(type));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_" + timeStamp + ".jpg");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        } else {
            return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(type));
        }
    }

//    OVER UPLOAD SERVICE PIC AREA

    private File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            pathForCameraImage = mediaFile.getAbsolutePath();
        } else {
            return null;
        }

        return mediaFile;
    }

    public void chooseFromGallery() {
        // System.out.println("Gallery pressed");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void closeuploadServicePicAlertBox() {
        if (uploadServicePicAlertBox != null) {
            uploadServicePicAlertBox.dismiss();
        }
    }

    public void closeShowDeliveryPreferenceAlertBox() {
        if (showDeliveryPreferenceAlertBox != null) {
            showDeliveryPreferenceAlertBox.dismiss();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString("RESTART_STATE", "true");
        outState.putParcelable("file_uri", fileUri);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean isStoragePermissionAvail = false;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ArrayList<String> requestPermissions = new ArrayList<>();
            requestPermissions.add(android.Manifest.permission.CAMERA);
            requestPermissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            if (generalFunc.isAllPermissionGranted(true,requestPermissions)) {
                isStoragePermissionAvail = true;
            }
        } else {
            isStoragePermissionAvail = generalFunc.isCameraStoragePermissionGranted();
        }
        // TODO: 28-04-2021 / Viral | Android 11 (30) Store permission code remove | revert 29
        //boolean isStoragePermissionAvail = generalFunc.isCameraStoragePermissionGranted();
        if (!isStoragePermissionAvail) {
            return;
        }

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {

            // successfully captured the image
            // display it in image view

            ArrayList<String[]> paramsList = new ArrayList<>();
            paramsList.add(generalFunc.generateImageParams("iMemberId", generalFunc.getMemberId()));
            paramsList.add(generalFunc.generateImageParams("MemberType", Utils.app_type));
            paramsList.add(generalFunc.generateImageParams("type", "uploadImage"));

            //  selectedImagePath = ImageFilePath.getPath(getApplicationContext(), fileUri);

            if (isStoragePermissionAvail) {

                isFrom = "Camera";


                if (fileUri != null) {
                    if (isGenie && uploadImgAlertDialog == null) {
                        return;
                    }
                    if (!isGenie && uploadServicePicAlertBox == null) {
                        return;
                    }

                    if (pathForCameraImage.equalsIgnoreCase("")) {
                        selectedImagePath = new ImageFilePath().getPath(getActContext(), fileUri);
                    } else {
                        selectedImagePath = pathForCameraImage;
                    }

                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(selectedImagePath, options);

                        int imageHeight = options.outHeight;
                        int imageWidth = options.outWidth;

                        double ratioOfImage = (double) imageWidth / (double) imageHeight;
                        double widthOfImage = ratioOfImage * Utils.dipToPixels(getActContext(), 200);

                        if (isGenie) {
                            clearImg.setVisibility(View.VISIBLE);
                            uploadImgArea.setClickable(false);
                            //   selectedImagePath=fileUri.toString();
                            Picasso.get().load(fileUri).resize((int) widthOfImage, Utils.dipToPixels(getActContext(), 200)).into(((ImageView) uploadImgAlertDialog.findViewById(R.id.uploadImgVIew)));
                            ((CardView) uploadImgAlertDialog.findViewById(R.id.mCardView)).setBackgroundResource(R.drawable.update_border);
                        } else {
                            Picasso.get().load(fileUri).resize((int) widthOfImage, Utils.dipToPixels(getActContext(), 200)).into(((ImageView) uploadServicePicAlertBox.findViewById(R.id.uploadImgVIew)));
                        }
                    } catch (Exception e) {
                        Logger.d("isGenie", "::" + isGenie);
                        if (isGenie) {
                            clearImg.setVisibility(View.VISIBLE);
                            uploadImgArea.setClickable(false);
                            //selectedImagePath=fileUri.toString();
                            Picasso.get().load(fileUri).resize(Utils.dipToPixels(getActContext(), 400), Utils.dipToPixels(getActContext(), 200)).into(((ImageView) uploadImgAlertDialog.findViewById(R.id.uploadImgVIew)));
                            ((CardView) uploadImgAlertDialog.findViewById(R.id.mCardView)).setBackgroundResource(R.drawable.update_border);
                        } else {
                            Picasso.get().load(fileUri).resize(Utils.dipToPixels(getActContext(), 400), Utils.dipToPixels(getActContext(), 200)).into(((ImageView) uploadServicePicAlertBox.findViewById(R.id.uploadImgVIew)));

                        }
                    }

                    //  Picasso.get().load(fileUri).into(((ImageView) uploadServicePicAlertBox.findViewById(R.id.uploadImgVIew)));
                    if (isGenie) {

                        uploadImgAlertDialog.findViewById(R.id.camImgVIew).setVisibility(View.GONE);
                        uploadImgAlertDialog.findViewById(R.id.ic_add).setVisibility(View.GONE);
                    } else {
                        uploadServicePicAlertBox.findViewById(R.id.camImgVIew).setVisibility(View.GONE);
                        uploadServicePicAlertBox.findViewById(R.id.ic_add).setVisibility(View.GONE);
                    }
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
        } else if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {


            ArrayList<String[]> paramsList = new ArrayList<>();
            paramsList.add(generalFunc.generateImageParams("iMemberId", generalFunc.getMemberId()));
            paramsList.add(generalFunc.generateImageParams("type", "uploadImage"));
            paramsList.add(generalFunc.generateImageParams("MemberType", Utils.app_type));

            Uri selectedImageUri = data.getData();

            selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedImageUri);
            if (selectedImagePath == null || selectedImagePath.equalsIgnoreCase("")) {
                selectedImagePath = "";
                try {
                    if (isGenie) {
//                        if (uploadImgAlertDialog != null) {
//                            uploadImgAlertDialog.dismiss();
//                        }aa


                    } else {
                        if (uploadServicePicAlertBox != null) {
                            uploadServicePicAlertBox.dismiss();
                        }
                    }
                } catch (Exception e) {

                }
                generalFunc.showMessage(generalFunc.getCurrentView((Activity) getActContext()), generalFunc.retrieveLangLBl("Can't read selected image. Please try again.", "LBL_IMAGE_READ_FAILED"));
                return;
            }

            if (isStoragePermissionAvail) {
                isFrom = "Gallary";
                if (selectedImageUri != null) {
                    if (isGenie) {
//                        if (uploadImgAlertDialog != null) {
//                            uploadImgAlertDialog.dismiss();
//                        }

                        clearImg.setVisibility(View.VISIBLE);
                        uploadImgArea.setClickable(false);

                    }
                    //  Picasso.get().load(selectedImageUri).into(((ImageView) uploadServicePicAlertBox.findViewById(R.id.uploadImgVIew)));
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(selectedImagePath, options);

                        int imageHeight = options.outHeight;
                        int imageWidth = options.outWidth;

                        double ratioOfImage = (double) imageWidth / (double) imageHeight;
                        double widthOfImage = ratioOfImage * Utils.dipToPixels(getActContext(), 200);

                        if (isGenie) {
                            //selectedImagePath=selectedImageUri.toString();
                            Picasso.get().load(selectedImageUri).resize((int) widthOfImage, Utils.dipToPixels(getActContext(), 200)).into(((ImageView) uploadImgAlertDialog.findViewById(R.id.uploadImgVIew)));
                            ((CardView) uploadImgAlertDialog.findViewById(R.id.mCardView)).setBackgroundResource(R.drawable.update_border);

                        } else {
                            Picasso.get().load(selectedImageUri).resize((int) widthOfImage, Utils.dipToPixels(getActContext(), 200)).into(((ImageView) uploadServicePicAlertBox.findViewById(R.id.uploadImgVIew)));

                        }


                    } catch (Exception e) {
                        if (isGenie) {
                            Logger.d("getItemImg", "::" + selectedImageUri);
                            // selectedImagePath=selectedImageUri.toString();
                            Picasso.get().load(selectedImageUri).resize(Utils.dipToPixels(getActContext(), 400), Utils.dipToPixels(getActContext(), 200)).into(((ImageView) uploadImgAlertDialog.findViewById(R.id.uploadImgVIew)));
                            ((CardView) uploadImgAlertDialog.findViewById(R.id.mCardView)).setBackgroundResource(R.drawable.update_border);

                        } else {

                            Picasso.get().load(selectedImageUri).resize(Utils.dipToPixels(getActContext(), 400), Utils.dipToPixels(getActContext(), 200)).into(((ImageView) uploadServicePicAlertBox.findViewById(R.id.uploadImgVIew)));
                        }
                    }


//                        Picasso.get().load(selectedImageUri).into(((ImageView) uploadServicePicAlertBox.findViewById(R.id.uploadImgVIew)));

                    if (isGenie) {
                        uploadImgAlertDialog.findViewById(R.id.camImgVIew).setVisibility(View.GONE);
                        uploadImgAlertDialog.findViewById(R.id.ic_add).setVisibility(View.GONE);
                    } else {
                        uploadServicePicAlertBox.findViewById(R.id.camImgVIew).setVisibility(View.GONE);
                        uploadServicePicAlertBox.findViewById(R.id.ic_add).setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    public Context getActContext() {
        return LiveTrackOrderDetailActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(LiveTrackOrderDetailActivity.this);
            if (i == R.id.backImgView) {
                LiveTrackOrderDetailActivity.super.onBackPressed();
            } else if (i == btn_type_refresh.getId()) {
                loading_order_item_list.setVisibility(View.VISIBLE);
                getOrderDetailList(false);
                //  orderPickedUpOrDeliver(list.get(0).getTotalAmount(), false);

            } else if (i == btn_type2.getId()) {

                if (list == null || list.size() == 0) {
                    return;
                }

                if (!list.get(0).getePaid().equalsIgnoreCase("Yes") && !isDeliver) {
                    boolean isStoragePermissionAvail = false;
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ArrayList<String> requestPermissions = new ArrayList<>();
                        requestPermissions.add(android.Manifest.permission.CAMERA);
                        requestPermissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (generalFunc.isAllPermissionGranted(true,requestPermissions)) {
                            isStoragePermissionAvail = true;
                        }
                    } else {
                        isStoragePermissionAvail = generalFunc.isCameraStoragePermissionGranted();
                    }
                    // TODO: 28-04-2021 / Viral | Android 11 (30) Store permission code remove | revert 29
                    //boolean isStoragePermissionAvail = generalFunc.isCameraStoragePermissionGranted();
                    if (!isStoragePermissionAvail) {
                        return;
                    }
                }


                if (eBuyAnyService.equalsIgnoreCase("Yes")) {
                    if (!checkItemPriceEntered()) {
                        generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_GENIE_CHECK_ITEM_DETAILS"));
                        return;
                    }
                }
                if (isDeliver) {
                    if (list.get(0).getePaid().equalsIgnoreCase("Yes")) {
                        BuildOrderStatusConfirmation(false);
                    } else {
                        showBillDialog();
                    }
                } else {
                    if (orderItemListRecycleAdapter != null && !orderItemListRecycleAdapter.areAllTrue()) {
                        generalFunc.showMessage(findViewById(R.id.mainArea), generalFunc.retrieveLangLBl("Please ensure that you have collected all order items from store.", "LBL_COLLECT_ITEMS_MSG_STORE"));
                        return;
                    }

                    /*Upload Proof Of Arrival If only Photo Upload Pending*/
                    if (eBuyAnyService.equalsIgnoreCase("") || eBuyAnyService.equalsIgnoreCase("No")) {

                        if (list.get(0).getePaid().equalsIgnoreCase("Yes") && PickedFromRes.equalsIgnoreCase("No")) {

                            BuildOrderStatusConfirmation(true);

                        } else if (!isDeliver && isPhotoUploaded.equalsIgnoreCase("No") && PickedFromRes.equalsIgnoreCase("Yes")) {
                            takeAndUploadPic(getActContext());
                        } else {
//                        showBillDialog();
                            BuildOrderStatusConfirmation(true);
                        }
                    } else {
                        GenieBuildOrderStatusConfirmation(false);
                        //orderPickedUpOrDeliver(list.get(0).getTotalAmount(), false);
                    }
                }
            } else if (i == R.id.footerLayout) {
                showBill();
            } else if (i == R.id.callImgView) {
//                getMaskNumber(0);
                if (list == null || list.size() == 0) {
                    return;
                }
                orderDetailDataModel orderDetailDataModel = list.get(0);
                if (!isDeliver) {
                    //  call(orderDetailDataModel.getRestaurantNumber());

                    if (generalFunc.getJsonValueStr("RIDE_DRIVER_CALLING_METHOD", userProfileJsonObj).equals("Voip")) {
                        sinchCall(true);
                    } else {
                        getMaskNumber(orderDetailDataModel.getRestaurantNumber());
                    }
                } else {
                    //  call(orderDetailDataModel.getUserPhone());
                    if (generalFunc.getJsonValueStr("RIDE_DRIVER_CALLING_METHOD", userProfileJsonObj).equals("Voip")) {
                        sinchCall(false);
                    } else {
                        getMaskNumber(orderDetailDataModel.getUserPhone());
                    }
                }

            } else if (i == R.id.trackUserLocationArea) {
                if (list == null || list.size() == 0) {
                    return;
                }
                Bundle bn = new Bundle();
                bn.putString("type", "trackUser");
                bn.putSerializable("TRIP_DATA", data_trip);

                orderDetailDataModel orderDetailDataModel = list.get(0);
                bn.putString("vLattitude", orderDetailDataModel.getUserLatitude());
                bn.putString("vLongitude", orderDetailDataModel.getUserLongitude());
                bn.putString("vAddress", orderDetailDataModel.getUserAddress());
                bn.putString("sourceLatitude", orderDetailDataModel.getRestaurantLattitude());
                bn.putString("sourceLongitude", orderDetailDataModel.getRestaurantLongitude());

                if (!isDeliver) {
                    bn.putString("vPhoneNo", orderDetailDataModel.getRestaurantNumber());
                } else {
                    bn.putString("vPhoneNo", orderDetailDataModel.getUserPhone());
                }

                bn.putString("vVehicleType", orderDetailDataModel.getvVehicleType());
                bn.putString("vName", orderDetailDataModel.getUserName());
                if (!data_trip.get("PPicName").equals("")) {
                    vImage = CommonUtilities.USER_PHOTO_PATH + data_trip.get("PassengerId") + "/" + data_trip.get("PPicName");
                }
                bn.putString("vImage", vImage);
                bn.putString("callid", data_trip.get("PassengerId"));
                new StartActProcess(getActContext()).startActWithData(TrackOrderActivity.class, bn);


            } else if (i == R.id.callUserArea) {
                if (list == null || list.size() == 0) {
                    return;
                }
                orderDetailDataModel orderDetailDataModel = list.get(0);
                if (!isDeliver) {
                    //call(orderDetailDataModel.getRestaurantNumber());
                    if (generalFunc.getJsonValueStr("RIDE_DRIVER_CALLING_METHOD", userProfileJsonObj).equals("Voip")) {
                        sinchCall(true);
                    } else {
                        getMaskNumber(orderDetailDataModel.getRestaurantNumber());
                    }


                } else {
                    if (generalFunc.getJsonValueStr("RIDE_DRIVER_CALLING_METHOD", userProfileJsonObj).equals("Voip")) {
                        sinchCall(false);
                    } else {
                        getMaskNumber(orderDetailDataModel.getUserPhone());
                    }
                }
//                getMaskNumber(0);
            }
        }
    }

    boolean checkItemPriceEntered() {
        boolean isEntered = true;
        for (int i = 0; i < subItemList.size(); i++) {
            if (subItemList.get(i).getItemDetailsUpdated().equalsIgnoreCase("No")) {
                isEntered = false;
                break;

            }
        }
        return isEntered;
    }

    public boolean isGenie = false;

    class ImageSourceDialog implements Runnable {


        @Override
        public void run() {


            final Dialog dialog_img_update = new Dialog(getActContext(), R.style.ImageSourceDialogStyle);

            dialog_img_update.setContentView(R.layout.design_image_source_select);

            MTextView chooseImgHTxt = (MTextView) dialog_img_update.findViewById(R.id.chooseImgHTxt);
            chooseImgHTxt.setText(generalFunc.retrieveLangLBl("Choose option", "LBL_CHOOSE_OPTION"));
            LinearLayout cameraView = (LinearLayout) dialog_img_update.findViewById(R.id.cameraView);
            LinearLayout galleryView = (LinearLayout) dialog_img_update.findViewById(R.id.galleryView);
            MTextView cameraTxt = (MTextView) dialog_img_update.findViewById(R.id.cameraTxt);
            MTextView galleryTxt = (MTextView) dialog_img_update.findViewById(R.id.galleryTxt);

            MButton btn_type2 = ((MaterialRippleLayout) dialog_img_update.findViewById(R.id.btn_type2)).getChildView();
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
            btn_type2.setOnClickListener(view -> dialog_img_update.dismiss());

            cameraTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CAMERA"));
            galleryTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GALLERY"));


            // SelectableRoundedImageView cameraIconImgView = (SelectableRoundedImageView) dialog_img_update.findViewById(R.id.cameraIconImgView);
            //  SelectableRoundedImageView galleryIconImgView = (SelectableRoundedImageView) dialog_img_update.findViewById(R.id.galleryIconImgView);

            ImageView closeDialogImgView = (ImageView) dialog_img_update.findViewById(R.id.closeDialogImgView);

            btn_type2.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    if (dialog_img_update != null) {
                        dialog_img_update.cancel();
                    }
                }
            });


            cameraView.setOnClickListener(v -> {

                if (dialog_img_update != null) {
                    dialog_img_update.cancel();
                }
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ArrayList<String> requestPermissions = new ArrayList<>();
                    requestPermissions.add(android.Manifest.permission.CAMERA);
                    requestPermissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (generalFunc.isAllPermissionGranted(true,requestPermissions)) {
                        if (!isDeviceSupportCamera()) {
                            generalFunc.showMessage(generalFunc.getCurrentView(LiveTrackOrderDetailActivity.this), generalFunc.retrieveLangLBl("", "LBL_NOT_SUPPORT_CAMERA_TXT"));
                        } else {
                            chooseFromCamera();
                        }
                    }
                } else {
                    if (generalFunc.isCameraStoragePermissionGranted()) {
                        if (!isDeviceSupportCamera()) {
                            generalFunc.showMessage(generalFunc.getCurrentView(LiveTrackOrderDetailActivity.this), generalFunc.retrieveLangLBl("", "LBL_NOT_SUPPORT_CAMERA_TXT"));
                        } else {
                            chooseFromCamera();
                        }
                    }
                }
                // TODO: 28-04-2021 / Viral | Android 11 (30) Store permission code remove | revert 29
                /*if (generalFunc.isCameraStoragePermissionGranted()) {
                    if (!isDeviceSupportCamera()) {
                        generalFunc.showMessage(generalFunc.getCurrentView(LiveTrackOrderDetailActivity.this), generalFunc.retrieveLangLBl("", "LBL_NOT_SUPPORT_CAMERA_TXT"));
                    } else {
                        chooseFromCamera();
                    }
                }*/
            });

            galleryView.setOnClickListener(v -> {

                if (dialog_img_update != null) {
                    dialog_img_update.cancel();
                }
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ArrayList<String> requestPermissions = new ArrayList<>();
                    requestPermissions.add(android.Manifest.permission.CAMERA);
                    requestPermissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (generalFunc.isAllPermissionGranted(true,requestPermissions)) {
                        chooseFromGallery();
                    }
                } else {
                    if (generalFunc.isCameraStoragePermissionGranted()) {
                        chooseFromGallery();
                    }
                }
                // TODO: 28-04-2021 / Viral | Android 11 (30) Store permission code remove | revert 29
                /*if (generalFunc.isCameraStoragePermissionGranted()) {
                    chooseFromGallery();
                }*/
            });

            dialog_img_update.setCanceledOnTouchOutside(true);

            Window window = dialog_img_update.getWindow();
            window.setGravity(Gravity.BOTTOM);

            window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            dialog_img_update.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            if (generalFunc.isRTLmode()) {
                dialog_img_update.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            dialog_img_update.show();

        }

    }

    boolean isFirst = false;
    Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        if (!isFirst) {
            isFirst = true;
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.live_task_activity, menu);
        }


        menu.findItem(R.id.menu_user_call).setTitle(generalFunc.retrieveLangLBl("", "LBL_CALL_TO_USER"));
        menu.findItem(R.id.cancel_order).setTitle(generalFunc.retrieveLangLBl("", "LBL_CANCEL_ORDER"));
        menu.findItem(R.id.contact_us).setTitle(generalFunc.retrieveLangLBl("", "LBL_CONTACT_US_TXT"));
        menu.findItem(R.id.intruction_store).setTitle(generalFunc.retrieveLangLBl("", "LBL_VIEW_SPEC_INS_FOR_STORE"));

        if (!vInstruction.equalsIgnoreCase("") && eAutoaccept.equalsIgnoreCase("Yes")) {
            menu.findItem(R.id.intruction_store).setVisible(true);
        } else {
            menu.findItem(R.id.intruction_store).setVisible(false);

        }

        if (eBuyAnyService.equalsIgnoreCase("") || eBuyAnyService.equalsIgnoreCase("No")) {
            menu.findItem(R.id.menu_user_call).setVisible(false);
            String cancel_Order = generalFunc.getJsonValueStr("ENABLE_CANCEL_DRIVER_ORDER", userProfileJsonObj);
            if (cancel_Order != null && cancel_Order.equalsIgnoreCase("Yes")) {

                menu.findItem(R.id.cancel_order).setVisible(true);
            } else {
                menu.findItem(R.id.cancel_order).setVisible(false);
            }
        } else {
            menu.findItem(R.id.menu_user_call).setVisible(true);

            if (generalFunc.getJsonValueStr("ENABLE_CANCEL_DRIVER_ORDER", userProfileJsonObj) != null && generalFunc.getJsonValueStr("ENABLE_CANCEL_DRIVER_ORDER", userProfileJsonObj).equalsIgnoreCase("Yes")) {
                menu.findItem(R.id.cancel_order).setVisible(true);
            } else {
                menu.findItem(R.id.cancel_order).setVisible(false);
            }


            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            if (generalFunc.isRTLmode()) {
                buttonLayoutParams.setMargins(0, 0, 100, 0);
            } else {
                buttonLayoutParams.setMargins(100, 0, 0, 0);
            }
            titleTxt.setLayoutParams(buttonLayoutParams);
        }


        Utils.setMenuTextColor(menu.findItem(R.id.menu_user_call), getResources().getColor(R.color.black));
        Utils.setMenuTextColor(menu.findItem(R.id.cancel_order), getResources().getColor(R.color.black));
        Utils.setMenuTextColor(menu.findItem(R.id.contact_us), getResources().getColor(R.color.black));
        Utils.setMenuTextColor(menu.findItem(R.id.intruction_store), getResources().getColor(R.color.black));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_user_call:
                managecall();
                return true;
            case R.id.cancel_order:
                showDeclineReasonsAlert();
                return true;
            case R.id.contact_us:
                new StartActProcess(getActContext()).startAct(ContactUsActivity.class);

                return true;
            case R.id.intruction_store:
                PreferenceDailogJava preferenceDailogJava = new PreferenceDailogJava(getActContext());
                preferenceDailogJava.showPreferenceDialog(generalFunc.retrieveLangLBl("", "LBL_VIEW_SPEC_INS_FOR_STORE"), vInstruction, R.drawable.ic_notes, false,
                        generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), "", false);
                return true;
        }


        return true;
    }

    public void managecall() {
        if (generalFunc.getJsonValueStr("RIDE_DRIVER_CALLING_METHOD", userProfileJsonObj).equals("Voip")) {
            sinchCall(false);
        } else {
            orderDetailDataModel orderDetailDataModel = list.get(0);
            getMaskNumber(orderDetailDataModel.getUserPhone());
        }
    }

}
