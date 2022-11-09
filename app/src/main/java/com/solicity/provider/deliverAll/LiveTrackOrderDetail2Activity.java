package com.solicity.provider.deliverAll;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adapter.MoreInstructionAdapter;
import com.adapter.files.deliverAll.OrderItemListRecycleAdapter;
import com.solicity.provider.BaseActivity;
import com.solicity.provider.BuildConfig;
import com.solicity.provider.CallScreenActivity;
import com.solicity.provider.R;
import com.general.files.AppFunctions;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.GetLocationUpdates;
import com.general.files.ImageFilePath;
import com.general.files.InternetConnection;
import com.general.files.MyApp;
import com.general.files.PreferenceDailogJava;
import com.general.files.RoundCornerDrawable;
import com.general.files.SinchService;
import com.general.files.StartActProcess;
import com.general.files.UploadProfileImage;
import com.model.deliverAll.orderDetailDataModel;
import com.model.deliverAll.orderItemDetailDataModel;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;
import com.sinch.android.rtc.calling.Call;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.utils.CommonUtilities;
import com.utils.Logger;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.ErrorView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.MyProgressDialog;
import com.view.SelectableRoundedImageView;
import com.view.editBox.MaterialEditText;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.utils.Utils.generateImageParams;

/**
 * Created by Admin on 17-04-18.
 */


public class LiveTrackOrderDetail2Activity extends BaseActivity implements OrderItemListRecycleAdapter.OnItemClickListener, GetLocationUpdates.LocationUpdatesListener {
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
    LinearLayout bottomLayout;
    boolean isShow = true;
    Animation slideUpAnimation, slideDownAnimation, slideup, slidedown;
    /*Pagination*/
    boolean mIsLoading = false;
    boolean isNextPageAvailable = false;
    String next_page_str = "";
    ArrayList<orderItemDetailDataModel> subItemList = new ArrayList<orderItemDetailDataModel>();
    String isFrom = "";
    Dialog uploadServicePicAlertBox = null;
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
    private Uri fileUri;
    private String selectedImagePath = "";
    private String pathForCameraImage = "";
    private HashMap<String, String> data_trip;
    private String tripId;
    private Location userLocation;
    private JSONObject userProfileJsonObj;
    LinearLayout callArea, navigateArea;
    MTextView navigateTxt, callTxt, storeTitleTxt, itemTitleTxt, detailsTxt;
    LinearLayout fareDetailDisplayArea;

    ScrollView mainScrollView;
    private boolean isPrefrence;
    private boolean isPreferenceImageUploadRequired;
    private boolean isContactLessDeliverySelected;

    private LinearLayout preferenceArea;
    private MTextView contactLessDeliveryTxt;
    private ImageView PreferenceHelp;
    ArrayList<HashMap<String, String>> instructionslit = new ArrayList<>();
    private String vTitle;
    private AlertDialog preferenceAlertDialog;

    /* RecyclerView moreinstuction;

    MoreInstructionAdapter moreInstructionAdapter;
    LinearLayout moreinstructionLyout;
    ArrayList<HashMap<String, String>> instructionslit;
    MTextView textmoreinstruction;*/

    //manage proof
    String vIdProofImage = "";
    String vIdProofImageNote = "";
    String vIdProofImageUploaded = "";
    String vRandomCode_ = "";
    String eAskCodeToUser = "";
    String vText = "";
    androidx.appcompat.app.AlertDialog ConfirmproofAlert;
    String eForPickDropGenie, eBuyAnyService;
    private String GenieOrderType = "";
    MTextView textVoiceinstruction, voiceTitle;
    ImageView voiceHelp, playBtn;
    SeekBar seekbar;
    MTextView timeTxt;
    LinearLayout Playarea;
    RelativeLayout playTitleArea;
    String voiceDirectionFileUrl = "";
    boolean wasPlaying = false;
    MediaPlayer mediaPlayer;
    boolean isPause = false;
    boolean iscomplete = false;

    private boolean intialStage = true;
    private Dialog dialog_verify_via_otp;
    boolean isOtpVerified = false;
    boolean isOtpVerificationDenied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_track_order_new_detail);
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        userProfileJsonObj = generalFunc.getJsonObject(userProfileJson);

        voiceTitle = findViewById(R.id.voiceTitle);
        textVoiceinstruction = findViewById(R.id.textVoiceinstruction);
        textVoiceinstruction.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_INS"));
        voiceTitle.setText(generalFunc.retrieveLangLBl("", "LBL_VOICE_DIRECTION_TXT" +
                ""));
        voiceHelp = findViewById(R.id.voiceHelp);
        playBtn = findViewById(R.id.playBtn);
        seekbar = findViewById(R.id.seekbar);
        timeTxt = findViewById(R.id.timeTxt);
        Playarea = findViewById(R.id.Playarea);
        playTitleArea = findViewById(R.id.playTitleArea);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    //  clearMediaPlayer();
                    // seekbar.setProgress(0);
                    //  wasPlaying = true;
                    Log.d("wasPlaying", "::00::" + wasPlaying);
                    isPause = true;
                    pauseMediaPlayer();
                    playBtn.setImageDrawable(ContextCompat.getDrawable(LiveTrackOrderDetail2Activity.this, R.drawable.ic_baseline_play_arrow_24));
                    return;
                }

                Log.d("playBtn", "::00::" + isPause + "::" + wasPlaying);
                if (isPause) {

                    Log.d("playBtn", "::00::" + isPause + "::" + mediaPlayer.getDuration());
                    mediaPlayer.start();
                    pauseplay();
                    playBtn.setImageDrawable(ContextCompat.getDrawable(LiveTrackOrderDetail2Activity.this, R.drawable.ic_baseline_pause_24));
                    isPause = false;

                } else if (!wasPlaying) {

                    if (intialStage) {
                        playBtn.setImageDrawable(ContextCompat.getDrawable(LiveTrackOrderDetail2Activity.this, R.drawable.ic_baseline_pause_24));
                        seekbar.setProgress(0);
                        new Player()
                                .execute(voiceDirectionFileUrl);
                    } else {
                        playBtn.setImageDrawable(ContextCompat.getDrawable(LiveTrackOrderDetail2Activity.this, R.drawable.ic_baseline_pause_24));
                        Logger.d("intialStage", "::" + mediaPlayer.getDuration());
                        try {
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            Logger.d("intialStage", "::" + e.toString());
                        }
                        mediaPlayer.start();
                        pauseplay();
                    }


                }
                wasPlaying = false;
                seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {


                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

                        int x = (int) Math.ceil(progress / 1000f);

                        Log.d("Progress", "::" + progress);

                        if (x < 10) {
                            timeTxt.setText("00:0" + x);
                        } else if (x >= 60) {

                            long minutes = x / 60;
                            long seconds = x % 60;
                            NumberFormat f = new DecimalFormat("00");
                            timeTxt.setText(f.format(minutes) + ":" + f.format(seconds));
                        } else {
                            timeTxt.setText("00:" + x);
                        }


                        if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
                            //  clearMediaPlayer();
                            if (!isPause || iscomplete) {
                                iscomplete = false;
                                playBtn.setImageDrawable(ContextCompat.getDrawable(LiveTrackOrderDetail2Activity.this, R.drawable.ic_baseline_play_arrow_24));
                                seekBar.setProgress(0);
                            }


                        }

                        if (progress == mediaPlayer.getDuration()) {
                            playBtn.setImageDrawable(ContextCompat.getDrawable(LiveTrackOrderDetail2Activity.this, R.drawable.ic_baseline_play_arrow_24));
                            seekBar.setProgress(0);
                            isPause = true;


                        }

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {


                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.seekTo(seekBar.getProgress());
                        }
                    }
                });

            }
        });


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

        mainScrollView = findViewById(R.id.mainScrollView);
        callArea = findViewById(R.id.callArea);
        callArea.setOnClickListener(new setOnClickList());
        navigateArea = findViewById(R.id.navigateArea);
        fareDetailDisplayArea = findViewById(R.id.fareDetailDisplayArea);
        navigateArea.setOnClickListener(new setOnClickList());


        int apptheme = getActContext().getResources().getColor(R.color.appThemeColor_1);
        int transpenrent = getActContext().getResources().getColor(R.color.mdtp_transparent_full);
        int white = getActContext().getResources().getColor(R.color.white);
        //  int bordercolor = getActContext().getResources().getColor(R.color.gray_holo_light);
        int cornorRadius = Utils.dipToPixels(getActContext(), 3);
        int strokeWidth = Utils.dipToPixels(getActContext(), 1);

        new CreateRoundedView(transpenrent, cornorRadius, strokeWidth, white, navigateArea);
        new CreateRoundedView(white, cornorRadius, strokeWidth, white, callArea);

        navigateTxt = findViewById(R.id.navigateTxt);
        callTxt = findViewById(R.id.callTxt);
        storeTitleTxt = findViewById(R.id.storeTitleTxt);
        itemTitleTxt = findViewById(R.id.itemTitleTxt);
        detailsTxt = findViewById(R.id.detailsTxt);
        billDetail_ll = (LinearLayout) findViewById(R.id.billDetail_ll);
        footerLayout = (LinearLayout) findViewById(R.id.footerLayout);
        bottomLayout = (LinearLayout) findViewById(R.id.bottomLayout);
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

        setLabels();

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            String restratValue_str = savedInstanceState.getString("RESTART_STATE");

            if (restratValue_str != null && !restratValue_str.equals("") && restratValue_str.trim().equals("true")) {
                generalFunc.restartApp();
            }
        }


        if (isPhotoUploaded.equalsIgnoreCase("NO") && !isDeliver && PickedFromRes.equalsIgnoreCase("Yes")) {
            takeAndUploadPic(getActContext());
//                btn_type2.performClick();
        } else {
            getOrderDetails();
        }


    }

    private void pauseMediaPlayer() {
        if (mediaPlayer != null) {
            isPause = true;
            mediaPlayer.pause();
            //   audioRecording.mMediaPlayer.release();
            playBtn.setImageDrawable(ContextCompat.getDrawable(LiveTrackOrderDetail2Activity.this, R.drawable.ic_baseline_play_arrow_24));

        }
    }

    class Player extends AsyncTask<String, Void, Boolean> {
        private MyProgressDialog progress;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            Boolean prepared;
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(voiceDirectionFileUrl);


                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // TODO Auto-generated method stub
                        //intialStage = true;
                        //  playPause=false;
                        // btn.setBackgroundResource(R.drawable.button_play);
                        Logger.d("onCompletion", "::called");
                        //mediaPlayer.reset();
                        playBtn.setImageDrawable(ContextCompat.getDrawable(LiveTrackOrderDetail2Activity.this, R.drawable.ic_baseline_play_arrow_24));
                        seekbar.resetPivot();
                        isPause = true;
                        iscomplete = true;
                    }
                });
                mediaPlayer.prepare();
                prepared = true;
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                Log.d("IllegarArgument", e.getMessage());
                prepared = false;
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                progress.close();
            } catch (Exception e) {

            }
            Log.d("Prepared", "//" + result);
            play();

            intialStage = false;
        }

        public Player() {
            progress = new MyProgressDialog(getActContext(), false, generalFunc.retrieveLangLBl("Loading", "LBL_LOADING_TXT"));
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            //   this.progress.setMessage("Buffering...");
            this.progress.show();

        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (mediaPlayer != null) {
            //  mediaPlayer.reset();
            //    mediaPlayer.release();
            //  mediaPlayer = null;
            pauseMediaPlayer();
        }
    }

    private void clearMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            //     mediaPlayer.release();
            //  mediaPlayer = null;

            seekbar.setProgress(0);
            wasPlaying = true;
            Log.d("wasPlaying", "::00::" + wasPlaying);
            playBtn.setImageDrawable(ContextCompat.getDrawable(LiveTrackOrderDetail2Activity.this, R.drawable.ic_baseline_play_arrow_24));

        }
    }

    public void pauseplay() {
        if (mediaPlayer != null) {
            seekbar.setMax(mediaPlayer.getDuration());
            new Thread(this::run).start();
        }
    }

    public void play() {
        try {
            Log.d("wasPlaying", "::00::");
//            mediaPlayer.setDataSource(voiceDirectionFileUrl);
//            mediaPlayer.prepare();
//            mediaPlayer.setLooping(false);
            mediaPlayer.start();

            seekbar.setMax(mediaPlayer.getDuration());


            new Thread(this::run).start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {

        int currentPosition = mediaPlayer.getCurrentPosition();
        int total = mediaPlayer.getDuration();


        while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
            try {
                Thread.sleep(1000);
                currentPosition = mediaPlayer.getCurrentPosition();
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                return;
            }


            seekbar.setProgress(currentPosition);

        }
    }

    @Override
    protected void onDestroy() {
        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().stopLocationUpdates(this);
        }

        // clearMediaPlayer();
        pauseMediaPlayer();
        super.onDestroy();

    }

    private void addFareDetailLayout(JSONArray jobjArray) {

        if (fareDetailDisplayArea.getChildCount() > 0) {
            fareDetailDisplayArea.removeAllViewsInLayout();
        }

        for (int i = 0; i < jobjArray.length(); i++) {
            JSONObject jobject = generalFunc.getJsonObject(jobjArray, i);
            try {
                String data = jobject.names().getString(0);
                addFareDetailRow(data, jobject.get(data).toString(), (jobjArray.length() - 1) == i ? true : false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void addFareDetailRow(String row_name, String row_value, boolean isLast) {
        View convertView = null;
        if (row_name.equalsIgnoreCase("eDisplaySeperator")) {
            convertView = new View(getActContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dipToPixels(getActContext(), 1));
            params.setMarginStart(Utils.dipToPixels(getActContext(), 5));
            params.setMarginEnd(Utils.dipToPixels(getActContext(), 5));
            convertView.setBackgroundColor(Color.parseColor("#dedede"));
            convertView.setLayoutParams(params);
        } else {
            LayoutInflater infalInflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.design_fare_deatil_row, null);

            convertView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            convertView.setMinimumHeight(Utils.dipToPixels(getActContext(), 30));

            MTextView titleHTxt = (MTextView) convertView.findViewById(R.id.titleHTxt);
            MTextView titleVTxt = (MTextView) convertView.findViewById(R.id.titleVTxt);

            titleHTxt.setText(generalFunc.convertNumberWithRTL(row_name));
            titleVTxt.setText(generalFunc.convertNumberWithRTL(row_value));

            if (isLast) {
                titleHTxt.setTextColor(getResources().getColor(R.color.black));
                titleHTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Poppins_SemiBold.ttf");
                titleHTxt.setTypeface(face);
                titleVTxt.setTypeface(face);
                titleVTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                titleVTxt.setTextColor(getResources().getColor(R.color.appThemeColor_1));
            }
        }

        if (convertView != null)
            fareDetailDisplayArea.addView(convertView);
    }

    private void initView() {

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


        preferenceArea = (LinearLayout) findViewById(R.id.preferenceArea);
        contactLessDeliveryTxt = (MTextView) findViewById(R.id.contactLessDeliveryTxt);
        contactLessDeliveryTxt.setText(generalFunc.retrieveLangLBl("ContactLessDelivery", "LBL_CONTACT_LESS_DELIVERY_TXT"));
        PreferenceHelp = (ImageView) findViewById(R.id.PreferenceHelp);
        PreferenceHelp.setOnClickListener(new setOnClickList());

      /*  textmoreinstruction = findViewById(R.id.textmoreinstruction);
        instructionslit = new ArrayList<>();
        moreinstructionLyout = findViewById(R.id.moreinstructionLyout);
        moreinstuction = findViewById(R.id.moreinstuction);
        moreInstructionAdapter = new MoreInstructionAdapter(getActContext(),instructionslit, new MoreInstructionAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(HashMap<String, String> map) {

            }

        });
        moreinstuction.setAdapter(moreInstructionAdapter);*/


        // Set Deliver Area

        if (isDeliver) {
            //  call_navigate_Area.setVisibility(View.VISIBLE);
            orderItemListRecyclerView.setVisibility(View.GONE);
            orderDeliverArea.setVisibility(View.VISIBLE);
//            orderHeaderArea.setVisibility(View.GONE);
        } else {
            // call_navigate_Area.setVisibility(View.GONE);
            orderItemListRecyclerView.setVisibility(View.VISIBLE);
//            orderHeaderArea.setVisibility(View.VISIBLE);
            orderDeliverArea.setVisibility(View.GONE);
        }

        orderItemListRecyclerView.setVisibility(View.VISIBLE);
    }

    public void setLabels() {

        setLableAsPerState();

        //orderIdHTxt.setText(generalFunc.retrieveLangLBl("Order Id", "LBL_ORDER_ID_TXT") + " : ");
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
        callTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CALL_TXT"));
        navigateTxt.setText(generalFunc.retrieveLangLBl("", "LBL_NAVIGATE"));
        storeTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RESTAURANT"));
        itemTitleTxt.setText(generalFunc.retrieveLangLBl("Item Details", "LBL_ITEM_DETAILS"));
        detailsTxt.setText(generalFunc.retrieveLangLBl("Item Details", "LBL_CHARGES_TXT"));
    }


    public void setLableAsPerState() {
        titleTxt.setText(generalFunc.retrieveLangLBl("Delivery", "LBL_DELIVERY_TXT"));

        //btn_type2.setText(isDeliver ? generalFunc.retrieveLangLBl("Order Delivered", "LBL_ORDER_DELIVERED") : generalFunc.retrieveLangLBl("Order PickedUp", "LBL_ORDER_PICKDUP"));
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_CONFIRM_TXT"));
        ordertitleTxt.setText(isDeliver ? generalFunc.retrieveLangLBl("Order Delivered", "LBL_ORDER_DELIVERED") : generalFunc.retrieveLangLBl("Order PickedUp", "LBL_ORDER_PICKDUP"));
    }


    private void getOrderDetails() {
        subItemList = new ArrayList<>();

        // if (!isDeliver) {
        orderItemListRecycleAdapter = new OrderItemListRecycleAdapter(getActContext(), subItemList, generalFunc, false, isPhotoUploaded, true);
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

        // }
        getOrderDetailList(false);
    }

    @Override
    public void onItemClickList(int position, String pickedFromRes) {

    }

    @Override
    public void onItemImageUpload(int position) {

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

    private void BuildOrderStatusConfirmation(boolean redirectToPhotoUpload, String eImgSkip) {
        if (Utils.checkText(eAskCodeToUser) && eAskCodeToUser.equalsIgnoreCase("Yes") && !isOtpVerified) {
            if (isOtpVerificationDenied) {
                isOtpVerificationDenied = false;
                return;
            }
            openEnterOtpView(eImgSkip);
        } else {

            final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
            generateAlert.setCancelable(false);
            generateAlert.setBtnClickList(btn_id -> {
                if (btn_id == 0) {
                    generateAlert.closeAlertBox();
                } else if (btn_id == 1) {

                    if (uploadServicePicAlertBox != null && uploadServicePicAlertBox.isShowing()) {
                        ImageView backImgView = (ImageView) uploadServicePicAlertBox.findViewById(R.id.backImgView);
                        backImgView.setVisibility(View.GONE);
                    }
                    orderPickedUpOrDeliver(list.get(0).getTotalAmount(), redirectToPhotoUpload, eImgSkip);
                }
            });
            generateAlert.setContentMessage("", !isDeliver ? generalFunc.retrieveLangLBl("Kindly Confirm to mark order as picked Up ?", "LBL_ORDER_PICKEDUP_CONFIRMATION") : generalFunc.retrieveLangLBl("Kindly Confirm to mark order as delivered ?", "LBL_ORDER_DELIVERED_CONFIRMATION_TXT"));
            generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
            generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));

            generateAlert.showAlertBox();
        }
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

                    voiceDirectionFileUrl = generalFunc.getJsonValueStr("voiceDirectionFileUrl", msg_obj);
                    eForPickDropGenie = generalFunc.getJsonValueStr("eForPickDropGenie", msg_obj);
                    eBuyAnyService = generalFunc.getJsonValueStr("eBuyAnyService", msg_obj);
                    GenieOrderType = generalFunc.getJsonValueStr("GenieOrderType", msg_obj);
                    JSONArray itemList = generalFunc.getJsonArray("itemlist", msg_obj.toString());
                    vIdProofImage = generalFunc.getJsonValueStr("vIdProofImage", msg_obj);
                    vIdProofImageNote = generalFunc.getJsonValueStr("vIdProofImageNote", msg_obj);
                    vIdProofImageUploaded = generalFunc.getJsonValueStr("vIdProofImageUploaded", msg_obj);
                    eAskCodeToUser = generalFunc.getJsonValueStr("eAskCodeToUser", msg_obj);
                    vRandomCode_ = generalFunc.getJsonValueStr("vRandomCode", msg_obj);
                    vText = generalFunc.getJsonValueStr("vText", msg_obj);
                    // Order's Details Add
                    orderDetailDataModel orderDetail = new orderDetailDataModel();
                    orderDetail.setOrderID(generalFunc.getJsonValueStr("iOrderId", msg_obj));
                    orderDetail.setvOrderNo(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("vOrderNo", msg_obj)));
                    orderDetail.setIsPhotoUploaded(generalFunc.getJsonValueStr("isPhotoUploaded", msg_obj));
                    orderDetail.setvVehicleType(generalFunc.getJsonValueStr("vVehicleType", msg_obj));

                    String tOrderRequestDate_Org = generalFunc.getJsonValueStr("tOrderRequestDate_Org", msg_obj);
                    String formattedDate = generalFunc.getDateFormatedType(tOrderRequestDate_Org, Utils.OriginalDateFormate, Utils.getDetailDateFormat(getActContext()));
                    orderDetail.setOrderDate_Time(generalFunc.convertNumberWithRTL(formattedDate));
                    orderDetail.setTotalAmount(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("originalTotal", msg_obj)));
                    orderDetail.setTotalAmount(generalFunc.getJsonValueStr("originalTotal", msg_obj));
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

                    String ePaymentOption = generalFunc.getJsonValueStr("ePaymentOption", msg_obj);
                    if (ePaymentOption.equalsIgnoreCase("Cash")) {
                        collectAmountUserHTxt.setText(generalFunc.retrieveLangLBl("Collect From User", "LBL_COLLECT_FROM_USER_TXT"));
                    }

                    String restAddress = generalFunc.getJsonValueStr("vRestuarantLocation", msg_obj);
                    orderDetail.setRestaurantAddress(Utils.checkText(restAddress) ? WordUtils.capitalize(restAddress) : restAddress);

                    orderDetail.setRestaurantName(generalFunc.getJsonValueStr("vCompany", msg_obj));
                    orderDetail.setRestaurantId(generalFunc.getJsonValueStr("iCompanyId", msg_obj));
                    orderDetail.setRestaurantImage(generalFunc.getJsonValueStr("vRestuarantImage", msg_obj));
                    orderDetail.setRestaurantLattitude(generalFunc.getJsonValueStr("RestuarantLat", msg_obj));
                    orderDetail.setRestaurantLongitude(generalFunc.getJsonValueStr("RestuarantLong", msg_obj));
                    orderDetail.setRestaurantNumber(generalFunc.getJsonValueStr("RestuarantPhone", msg_obj));


                    JSONObject DeliveryPreferences = generalFunc.getJsonObject("DeliveryPreferences", responseStringObject);

                    isPrefrence = generalFunc.getJsonValueStr("Enable", DeliveryPreferences).equalsIgnoreCase("Yes");
                    if (isPrefrence) {
                        isPreferenceImageUploadRequired = generalFunc.getJsonValueStr("isPreferenceImageUploadRequired", DeliveryPreferences).equalsIgnoreCase("Yes");
                        isContactLessDeliverySelected = generalFunc.getJsonValueStr("isContactLessDeliverySelected", DeliveryPreferences).equalsIgnoreCase("Yes");

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

                    if (voiceDirectionFileUrl != null && !voiceDirectionFileUrl.equalsIgnoreCase("")) {
                        playTitleArea.setVisibility(View.VISIBLE);
                        Playarea.setVisibility(View.VISIBLE);
                    }
                    if (eForPickDropGenie != null && eForPickDropGenie.equalsIgnoreCase("Yes")) {
                        storeTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_ADDRESS"));
                    }
                    if (eBuyAnyService != null && eBuyAnyService.equalsIgnoreCase("Yes")) {
                        storeTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_ADDRESS"));
                    }


                    preferenceArea.setVisibility(isContactLessDeliverySelected ? View.VISIBLE : View.GONE);
                    if (isPreferenceImageUploadRequired) {
                        btn_type2.setText(generalFunc.retrieveLangLBl("Next", "LBL_BTN_NEXT_TXT"));
                    }


                    if (itemList != null && itemList.length() > 0) {
                        ArrayList<orderItemDetailDataModel> subItemList = new ArrayList<orderItemDetailDataModel>();

                        for (int i = 0; i < itemList.length(); i++) {
                            orderItemDetailDataModel orderItemList = new orderItemDetailDataModel();

                            JSONObject item_list_detail = generalFunc.getJsonObject(itemList, i);
                            orderItemList.seteDecline(generalFunc.getJsonValueStr("eDecline", item_list_detail));
                            if (eBuyAnyService.equalsIgnoreCase("Yes")) {

                                orderItemList.setvImageUploaded(generalFunc.getJsonValueStr("vImageUploaded", item_list_detail));
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
                            orderItemList.setfTotPriceWithoutSymbol(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("fTotPriceWithoutSymbol", item_list_detail)));
                            orderItemList.setTotalDiscountPrice(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("TotalDiscountPrice", item_list_detail)));

                            subItemList.add(orderItemList);
                        }
                        orderDetail.setorderItemDetailList(subItemList);
                    }

                    list.add(orderDetail);

                    storeNameTxt.setText(orderDetail.getRestaurantName());
                    storeAddressTxt.setText(restAddress);

                    if (!data_trip.get("PPicName").equalsIgnoreCase("")) {
                        vImage = CommonUtilities.USER_PHOTO_PATH + data_trip.get("PassengerId") + "/" + data_trip.get("PPicName");
                    } else {
                        vImage = "temp";
                    }
                    orderDetailDataModel orderDetailDataModel = list.get(0);
                    if (Utils.checkText(orderDetailDataModel.getUserName())) {
                        Picasso.get().load(vImage).placeholder(R.mipmap.ic_no_pic_user).error(R.mipmap.ic_no_pic_user).into((SelectableRoundedImageView) findViewById(R.id.UserImageView));
                    } else {
                        Picasso.get().load(vImage).placeholder(R.mipmap.ic_no_icon).error(R.mipmap.ic_no_icon).into((SelectableRoundedImageView) findViewById(R.id.UserImageView));
                    }


                    if (!orderDetail.getRestaurantImage().equalsIgnoreCase("")) {
                        vImage = CommonUtilities.COMPANY_PHOTO_PATH + orderDetail.getRestaurantId() + "/" + orderDetail.getRestaurantImage();
                    } else {
                        vImage = "temp";
                    }
                    int imagewidth = (int) getResources().getDimension(R.dimen._60sdp);
                    ImageView storeImg = findViewById(R.id.storeImg);
                    if (GenieOrderType != null && GenieOrderType.equalsIgnoreCase("Runner")) {
                        storeImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_locate_place));

                        ((SelectableRoundedImageView) findViewById(R.id.UserImageView)).setVisibility(View.GONE);
                        ((ImageView) findViewById(R.id.runnerImageView)).setImageDrawable(getResources().getDrawable(R.drawable.ic_locate_place));
                        ((ImageView) findViewById(R.id.runnerImageView)).setVisibility(View.VISIBLE);

                        storeTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PICKUP_ADDRESS"));
                    } else {
                        if (GenieOrderType != null && !GenieOrderType.equalsIgnoreCase("Runner")) {
                            storeTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_STORE_ADDRESS"));
                        }
                        Picasso.get()
                                .load(Utils.getResizeImgURL(getActContext(), vImage, imagewidth, imagewidth))
                                .placeholder(R.mipmap.ic_no_icon)
                                .error(R.mipmap.ic_no_icon)
                                .into(storeImg, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        try {
                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                                storeImg.invalidate();
                                                BitmapDrawable drawable = (BitmapDrawable) storeImg.getDrawable();
                                                Bitmap bitmap = drawable.getBitmap();

                                                CardView mCardView = findViewById(R.id.mCardView);
                                                mCardView.setPreventCornerOverlap(false);

                                                RoundCornerDrawable round = new RoundCornerDrawable(bitmap, getResources().getDimension(R.dimen._10sdp), 0);
                                                storeImg.setVisibility(View.GONE);
                                                mCardView.setBackground(round);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                    }


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
                    boolean FareDetailsArrNew = generalFunc.isJSONkeyAvail("FareDetailsNewArr", generalFunc.getJsonValue("message", responseStringObject).toString());
                    JSONArray FareDetailsArrNewObj = null;
                    if (FareDetailsArrNew) {
                        FareDetailsArrNewObj = generalFunc.getJsonArray("FareDetailsNewArr", generalFunc.getJsonValue("message", responseStringObject).toString());
                    }


                    if (FareDetailsArrNewObj != null)
                        addFareDetailLayout(FareDetailsArrNewObj);


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
                collectAmountUserVTxt.setText(" " + orderDetailDataModel.getTotalAmountWithSymbol() /*+ "\n" + generalFunc.retrieveLangLBl("(Paid By User)", "LBL_PAYMENT_DONE_BY_USER")*/);
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
            mainScrollView.setVisibility(View.VISIBLE);
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

    public void showBillDialog(String eImgSkip) {

        if (isDeliver || !isDeliver) {
            orderPickedUpOrDeliver("", true, eImgSkip);
            return;
        }


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
                if (uploadServicePicAlertBox != null && uploadServicePicAlertBox.isShowing()) {
                    ImageView backImgView = (ImageView) uploadServicePicAlertBox.findViewById(R.id.backImgView);
                    backImgView.setVisibility(View.GONE);
                }
                orderPickedUpOrDeliver(Utils.getText(billCollecetdValueEditText).trim(), false, eImgSkip);
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

                if (uploadServicePicAlertBox != null && uploadServicePicAlertBox.isShowing()) {
                    ImageView backImgView = (ImageView) uploadServicePicAlertBox.findViewById(R.id.backImgView);
                    backImgView.setVisibility(View.GONE);
                }
                orderPickedUpOrDeliver(Utils.getText(confirmBillValueEditText).trim(), true, eImgSkip);
                /*Upload Proof Of Arrival*/
//                    takeAndUploadPic(getActContext(), "after", Utils.getText(confirmBillValueEditText).trim());
            }

        });

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (generalFunc.isRTLmode()) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog.show();
    }

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
        uploadServicePicAlertBox.setContentView(R.layout.design_upload_preference_pic);
        uploadServicePicAlertBox.setCancelable(false);

        MTextView titleTxt = (MTextView) uploadServicePicAlertBox.findViewById(R.id.titleTxt);
        MTextView preferenceTitleTxt = (MTextView) uploadServicePicAlertBox.findViewById(R.id.preferenceTitleTxt);
        final MTextView uploadStatusTxt = (MTextView) uploadServicePicAlertBox.findViewById(R.id.uploadStatusTxt);
        MTextView uploadTitleTxt = (MTextView) uploadServicePicAlertBox.findViewById(R.id.uploadTitleTxt);
        ImageView backImgView = (ImageView) uploadServicePicAlertBox.findViewById(R.id.backImgView);
        MTextView skipTxt = (MTextView) uploadServicePicAlertBox.findViewById(R.id.skipTxt);
        final ImageView uploadImgVIew = (ImageView) uploadServicePicAlertBox.findViewById(R.id.uploadImgVIew);
        LinearLayout uploadImgArea = (LinearLayout) uploadServicePicAlertBox.findViewById(R.id.uploadImgArea);
        MButton btn_type2 = ((MaterialRippleLayout) uploadServicePicAlertBox.findViewById(R.id.btn_type2)).getChildView();
        final RecyclerView preferenceList = (RecyclerView) uploadServicePicAlertBox.findViewById(R.id.preferenceList);
        MTextView collectAmountUserHTxt = (MTextView) uploadServicePicAlertBox.findViewById(R.id.collectAmountUserHTxt);
        MTextView collectAmountUserVTxt = (MTextView) uploadServicePicAlertBox.findViewById(R.id.collectAmountUserVTxt);
        ImageView PreferenceHelp = (ImageView) uploadServicePicAlertBox.findViewById(R.id.PreferenceHelp);
        MTextView contactLessDeliveryTxt = (MTextView) uploadServicePicAlertBox.findViewById(R.id.contactLessDeliveryTxt);
        LinearLayout preferenceArea = (LinearLayout) findViewById(R.id.preferenceArea);

        titleTxt.setText(vTitle);
        preferenceTitleTxt.setText(vTitle);
        skipTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SKIP_TXT"));

        contactLessDeliveryTxt.setText(generalFunc.retrieveLangLBl("ContactLessDelivery", "LBL_CONTACT_LESS_DELIVERY_TXT"));

        preferenceArea.setVisibility(isContactLessDeliverySelected ? View.VISIBLE : View.GONE);
        preferenceTitleTxt.setVisibility(View.VISIBLE);

        collectAmountUserHTxt.setText(generalFunc.retrieveLangLBl("Total Bill", "LBL_TOTAL_BILL_TXT"));
        if (isDeliver) {
            collectAmountUserHTxt.setText(generalFunc.retrieveLangLBl("Total Bill", "LBL_TOTAL_BILL_TXT") + " (" + generalFunc.retrieveLangLBl("", "LBL_PAID") + ") ");
        }

        if (list.size() > 0) {

            orderDetailDataModel orderDetailDataModel = list.get(0);

            if (orderDetailDataModel.getePaymentOption().equalsIgnoreCase("Cash")) {
                collectAmountUserVTxt.setText(" " + orderDetailDataModel.getTotalAmountWithSymbol());
            } else {
                collectAmountUserVTxt.setText(" " + orderDetailDataModel.getTotalAmountWithSymbol() /*+ "\n" + generalFunc.retrieveLangLBl("(Paid By User)", "LBL_PAYMENT_DONE_BY_USER")*/);
            }

        }

        uploadTitleTxt.setText(generalFunc.retrieveLangLBl("Click and upload to submit proof of your order delivery task completion to notify user.", "LBL_UPLOAD_ORDER_DELIVER_PREFERENCE_PROOF_MSG_TXT"));
        btn_type2.setText(generalFunc.retrieveLangLBl("Upload Proof And Deliver Order", "LBL_UPLOAD_PREFERENCE_PROOF_ORDER_DELIVER_TXT"));

        MoreInstructionAdapter moreInstructionAdapter = new MoreInstructionAdapter(getActContext(), instructionslit, new MoreInstructionAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(HashMap<String, String> map) {

            }

        });
        preferenceList.setAdapter(moreInstructionAdapter);


        btn_type2.setId(Utils.generateViewId());

        uploadImgArea.setOnClickListener(view -> {

            if (generalFunc.isCameraPermissionGranted()) {
                uploadServicePicAlertBox.findViewById(R.id.uploadStatusTxt).setVisibility(View.GONE);
                new ImageSourceDialog().run();
            } else {
                uploadStatusTxt.setVisibility(View.VISIBLE);
                generalFunc.showMessage(uploadStatusTxt, "Allow this app to use camera.");
            }
        });

        PreferenceHelp.setOnClickListener(view -> {
            showPreferenceHelp();


        });
        btn_type2.setOnClickListener(view -> {

            if (!Utils.checkText(selectedImagePath)) {
                // uploadStatusTxt.setVisibility(View.VISIBLE);
                generalFunc.showMessage(uploadStatusTxt, "Please select image");
            } else {
                uploadStatusTxt.setVisibility(View.GONE);
//                OrderImageUpload("No");


                if (vIdProofImageUploaded != null && vIdProofImageUploaded.equalsIgnoreCase("Yes")) {
                    openproofDailog(true, "");
                } else {
                    confirmBillCleared("No");
                }


            }
        });

        skipTxt.setOnClickListener(view -> {

            isFrom = "";
            selectedImagePath = "";
            uploadImgVIew.setImageURI(null);
            confirmBillCleared("Yes");
//            OrderImageUpload("Yes");

        });
        backImgView.setVisibility(View.VISIBLE);

        backImgView.setOnClickListener(view -> closeuploadServicePicAlertBox());

        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(uploadServicePicAlertBox);
        }

        uploadServicePicAlertBox.show();

    }

    public void confirmBillCleared(String eImgSkip) {
        if (Utils.checkText(eAskCodeToUser) && eAskCodeToUser.equalsIgnoreCase("Yes") && !isOtpVerified) {
            if (isOtpVerificationDenied) {
                isOtpVerificationDenied = false;
                return;
            }
            openEnterOtpView(eImgSkip);
        } else {
            if (list.get(0).getePaid().equalsIgnoreCase("Yes")) {
                BuildOrderStatusConfirmation(false, eImgSkip);
            } else {
                showBillDialog(eImgSkip);
            }
        }
    }

    private void orderPickedUpOrDeliver(String billAmount, boolean b, String eImgSkip) {
        InternetConnection intCheck = new InternetConnection(getActContext());

        if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {
            generalFunc.showGeneralMessage("",
                    generalFunc.retrieveLangLBl("No Internet Connection", "LBL_NO_INTERNET_TXT"));

            return;
        }

        String latitude = "";
        String longitude = "";
        if (userLocation != null) {
            latitude = "" + userLocation.getLatitude();
            longitude = "" + userLocation.getLongitude();
        }
        if (GetLocationUpdates.getInstance().getLastLocation() != null) {
            Location lastLocation = GetLocationUpdates.getInstance().getLastLocation();
            latitude = "" + lastLocation.getLatitude();
            longitude = "" + lastLocation.getLongitude();
        }


        if (!TextUtils.isEmpty(isFrom)) {
            ArrayList<String[]> paramsList = new ArrayList<>();
            paramsList.add(generalFunc.generateImageParams("type", "UpdateOrderStatusDriver"));
            paramsList.add(generalFunc.generateImageParams("iOrderId", iOrderId));
            paramsList.add(generalFunc.generateImageParams("iTripid", tripId));
            paramsList.add(generalFunc.generateImageParams("iDriverId", generalFunc.getMemberId()));
            paramsList.add(generalFunc.generateImageParams("eImgSkip", eImgSkip));
            paramsList.add(generalFunc.generateImageParams("UserType", Utils.app_type));
            paramsList.add(generalFunc.generateImageParams("orderStatus", isDeliver ? "OrderDelivered" : "OrderPickedup"));
            paramsList.add(generalFunc.generateImageParams("billAmount", billAmount));
            paramsList.add(generalFunc.generateImageParams("eSystem", Utils.eSystem_Type));
            paramsList.add(generalFunc.generateImageParams("vLatitude", latitude));
            paramsList.add(generalFunc.generateImageParams("vLongitude", longitude));
            paramsList.add(generateImageParams("iMemberId", generalFunc.getMemberId()));
            paramsList.add(generateImageParams("MemberType", Utils.app_type));
            paramsList.add(generateImageParams("tSessionId", generalFunc.getMemberId().equals("") ? "" : generalFunc.retrieveValue(Utils.SESSION_ID_KEY)));
            paramsList.add(generateImageParams("GeneralUserType", Utils.app_type));
            paramsList.add(generateImageParams("GeneralMemberId", generalFunc.getMemberId()));

            new UploadProfileImage(LiveTrackOrderDetail2Activity.this, selectedImagePath, Utils.TempProfileImageName, paramsList, "").execute();
        } else {
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("type", "UpdateOrderStatusDriver");
            parameters.put("iDriverId", generalFunc.getMemberId());
            parameters.put("orderStatus", isDeliver ? "OrderDelivered" : "OrderPickedup");
            parameters.put("iOrderId", iOrderId);
            parameters.put("iTripid", tripId);
            parameters.put("billAmount", billAmount);
            parameters.put("UserType", Utils.app_type);
            parameters.put("eSystem", Utils.eSystem_Type);
            parameters.put("eImgSkip", eImgSkip);
            parameters.put("vLatitude", latitude);
            parameters.put("vLongitude", longitude);

            ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
            exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
            exeWebServer.setDataResponseListener(responseString -> submitProofResponse(responseString, false));
            exeWebServer.execute();
        }

    }

    private void openEnterOtpView(String eImgSkip) {
        if (dialog_verify_via_otp != null) {
            dialog_verify_via_otp.dismiss();
            dialog_verify_via_otp = null;
        }
        dialog_verify_via_otp = new Dialog(getActContext(), R.style.ImageSourceDialogStyle);
        dialog_verify_via_otp.setContentView(R.layout.verify_with_otp_layout);
        MTextView titleTxt = (MTextView) dialog_verify_via_otp.findViewById(R.id.titleTxt);
        MTextView cancelTxt = (MTextView) dialog_verify_via_otp.findViewById(R.id.cancelTxt);
        MTextView verifyOtpNote = (MTextView) dialog_verify_via_otp.findViewById(R.id.verifyOtpNote);
        MTextView verifyOtpValidationNote = (MTextView) dialog_verify_via_otp.findViewById(R.id.verifyOtpValidationNote);
        LinearLayout OtpAddArea = (LinearLayout) dialog_verify_via_otp.findViewById(R.id.OtpAddArea);
        MaterialEditText otpBox = (MaterialEditText) dialog_verify_via_otp.findViewById(R.id.otpBox);
        OtpView otp_view = (OtpView) dialog_verify_via_otp.findViewById(R.id.otp_verify_view);
        MButton btn_type2 = ((MaterialRippleLayout) dialog_verify_via_otp.findViewById(R.id.btn_type2)).getChildView();

        if (generalFunc.isRTLmode()) {
            otp_view.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }
        int vRandomCode = generalFunc.parseIntegerValue(4, vRandomCode_);
        String LBL_OTP_INVALID_TXT = generalFunc.retrieveLangLBl("", "LBL_OTP_INVALID_TXT");
        if (vRandomCode <= 6) {
            OtpAddArea.setVisibility(View.VISIBLE);
            otpBox.setVisibility(View.GONE);
            verifyOtpValidationNote.setText(LBL_OTP_INVALID_TXT);
            otp_view.setItemCount(generalFunc.parseIntegerValue(4, String.valueOf(vRandomCode)));
        } else {
            otpBox.setBothText("", generalFunc.retrieveLangLBl("OTP", "LBL_ENTER_OTP_TITLE_TXT"));
            OtpAddArea.setVisibility(View.GONE);
            otpBox.setVisibility(View.VISIBLE);
            otpBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_DONE"));

        titleTxt.setText(generalFunc.retrieveLangLBl("Verify OTP", "LBL_OTP_VERIFICATION_TITLE_TXT"));
        verifyOtpNote.setText(generalFunc.retrieveLangLBl("Ask user to provide you an OTP.", "LBL_OTP_VERIFICATION_DESCRIPTION_TXT"));
        btn_type2.setEnabled(false);
//        btn_type2.setBackgroundColor(getResources().getColor(R.color.gray));
        cancelTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));

        cancelTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(getActContext());
                isOtpVerified = false;
                if (dialog_verify_via_otp != null) {
                    dialog_verify_via_otp.dismiss();
                    dialog_verify_via_otp = null;
                }
            }
        });

        Logger.d("MD5_HASH", "Original  Values is ::" + vText);
        btn_type2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(getActContext());
                if (OtpAddArea.getVisibility() == View.VISIBLE) {
                    String finalCode = Utils.getText(otp_view);

                    boolean isCorrectCOde = Utils.checkText(finalCode) &&
                            new AppFunctions(getActContext()).convertOtpToMD5(finalCode).equalsIgnoreCase(vText);
                    boolean isCodeEntered = isCorrectCOde ? true : Utils.setErrorFields(otpBox, LBL_OTP_INVALID_TXT);

                    otp_view.setLineColor(isCorrectCOde ? getActContext().getResources().getColor(R.color.appThemeColor_1) : getActContext().getResources().getColor(R.color.red));

                    if (isCodeEntered) {
                        verifyOtpValidationNote.setVisibility(View.GONE);
                        if (dialog_verify_via_otp != null) {
                            dialog_verify_via_otp.dismiss();
                            dialog_verify_via_otp = null;
                        }

                        isOtpVerified = true;
                        if (list.get(0).getePaid().equalsIgnoreCase("Yes")) {
                            BuildOrderStatusConfirmation(false, eImgSkip);
                        } else {
                            showBillDialog(eImgSkip);
                        }
                    } else {
                        verifyOtpValidationNote.setVisibility(View.VISIBLE);
                    }
                } else {
                    String finalCode = Utils.getText(otpBox);
                    boolean isCorrectCOde = Utils.checkText(finalCode) &&
                            new AppFunctions(getActContext()).convertOtpToMD5(finalCode).equalsIgnoreCase(vText);
                    boolean isCodeEntered = isCorrectCOde ? true : Utils.setErrorFields(otpBox, LBL_OTP_INVALID_TXT);

                    otp_view.setLineColor(isCorrectCOde ? getActContext().getResources().getColor(R.color.appThemeColor_1) : getActContext().getResources().getColor(R.color.red));

                    if (isCodeEntered) {

                        if (dialog_verify_via_otp != null) {
                            dialog_verify_via_otp.dismiss();
                            dialog_verify_via_otp = null;
                        }
                        isOtpVerified = true;
                        if (list.get(0).getePaid().equalsIgnoreCase("Yes")) {
                            BuildOrderStatusConfirmation(false, eImgSkip);
                        } else {
                            showBillDialog(eImgSkip);
                        }
                    }
                }
            }
        });
        otp_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < otp_view.getItemCount()) {
                    btn_type2.setEnabled(false);
                    verifyOtpValidationNote.setVisibility(View.GONE);
                    otp_view.setLineColor(getResources().getColor(R.color.gray));
//                    btn_type2.setBackgroundColor(getResources().getColor(R.color.gray));
                }
            }
        });

        otpBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < vRandomCode) {
                    btn_type2.setEnabled(false);
//                    btn_type2.setBackgroundColor(getResources().getColor(R.color.gray));
                } else {
                    btn_type2.setEnabled(true);
//                    btn_type2.setBackgroundColor(getResources().getColor(R.color.appThemeColor_1));
                }
            }
        });

        otp_view.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                verifyOtpValidationNote.setVisibility(View.GONE);
                otp_view.setLineColor(getResources().getColor(R.color.appThemeColor_1));
                btn_type2.setEnabled(true);
//                btn_type2.setBackgroundColor(getResources().getColor(R.color.appThemeColor_1));
//                btn_type2.performClick();
            }
        });
        otp_view.setCursorVisible(true);
        dialog_verify_via_otp.setCanceledOnTouchOutside(false);
        Window window = dialog_verify_via_otp.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog_verify_via_otp.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        if (generalFunc.isRTLmode()) {
            dialog_verify_via_otp.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog_verify_via_otp.show();
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

            new UploadProfileImage(LiveTrackOrderDetail2Activity.this, selectedImagePath, Utils.TempProfileImageName, paramsList, "").execute();
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


            submitProofResponse(responseString, false);


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
                if (callImageUpload) {
                    takeAndUploadPic(getActContext());
                } else {
                    MyApp.getInstance().restartWithGetDataApp();
                }
            } else {
                String msg_str = generalFunc.getJsonValueStr(Utils.message_str, responseStringObject);
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
                if (fileUri != null && uploadServicePicAlertBox != null) {

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

                        Picasso.get().load(fileUri).resize((int) widthOfImage, Utils.dipToPixels(getActContext(), 200)).into(((ImageView) uploadServicePicAlertBox.findViewById(R.id.uploadImgVIew)));
                    } catch (Exception e) {
                        Picasso.get().load(fileUri).resize(Utils.dipToPixels(getActContext(), 400), Utils.dipToPixels(getActContext(), 200)).into(((ImageView) uploadServicePicAlertBox.findViewById(R.id.uploadImgVIew)));
                    }

                    //  Picasso.get().load(fileUri).into(((ImageView) uploadServicePicAlertBox.findViewById(R.id.uploadImgVIew)));
                    uploadServicePicAlertBox.findViewById(R.id.camImgVIew).setVisibility(View.GONE);
                    uploadServicePicAlertBox.findViewById(R.id.ic_add).setVisibility(View.GONE);
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
                    if (uploadServicePicAlertBox != null) {
                        uploadServicePicAlertBox.dismiss();
                    }
                } catch (Exception e) {

                }
                generalFunc.showMessage(generalFunc.getCurrentView((Activity) getActContext()), generalFunc.retrieveLangLBl("Can't read selected image. Please try again.", "LBL_IMAGE_READ_FAILED"));
                return;
            }

            if (isStoragePermissionAvail) {
                isFrom = "Gallary";
                if (selectedImageUri != null && uploadServicePicAlertBox != null) {
                    //  Picasso.get().load(selectedImageUri).into(((ImageView) uploadServicePicAlertBox.findViewById(R.id.uploadImgVIew)));
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(selectedImagePath, options);

                        int imageHeight = options.outHeight;
                        int imageWidth = options.outWidth;

                        double ratioOfImage = (double) imageWidth / (double) imageHeight;
                        double widthOfImage = ratioOfImage * Utils.dipToPixels(getActContext(), 200);

                        Picasso.get().load(selectedImageUri).resize((int) widthOfImage, Utils.dipToPixels(getActContext(), 200)).into(((ImageView) uploadServicePicAlertBox.findViewById(R.id.uploadImgVIew)));


                    } catch (Exception e) {
                        Picasso.get().load(selectedImageUri).resize(Utils.dipToPixels(getActContext(), 400), Utils.dipToPixels(getActContext(), 200)).into(((ImageView) uploadServicePicAlertBox.findViewById(R.id.uploadImgVIew)));
                    }


//                        Picasso.get().load(selectedImageUri).into(((ImageView) uploadServicePicAlertBox.findViewById(R.id.uploadImgVIew)));

                    uploadServicePicAlertBox.findViewById(R.id.camImgVIew).setVisibility(View.GONE);
                    uploadServicePicAlertBox.findViewById(R.id.ic_add).setVisibility(View.GONE);
                }
            }
        }
    }

    public Context getActContext() {
        return LiveTrackOrderDetail2Activity.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(LiveTrackOrderDetail2Activity.this);
            //  clearMediaPlayer();
            pauseMediaPlayer();
            if (i == R.id.backImgView) {
                LiveTrackOrderDetail2Activity.super.onBackPressed();
            } else if (i == R.id.PreferenceHelp) {
                showPreferenceHelp();
            } else if (i == btn_type2.getId()) {
                // clearMediaPlayer();


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

                if (isDeliver) {

                    if (isPreferenceImageUploadRequired) {
                        takeAndUploadPic(getActContext());
                    } else {

                        if (vIdProofImageUploaded != null && vIdProofImageUploaded.equalsIgnoreCase("Yes")) {
                            openproofDailog(false, "");
                        } else {
                            confirmBillCleared("");
                        }
                    }
                } else {
                    if (orderItemListRecycleAdapter != null && !orderItemListRecycleAdapter.areAllTrue()) {
                        generalFunc.showMessage(findViewById(R.id.mainArea), generalFunc.retrieveLangLBl("Please ensure that you have collected all order items from store.", "LBL_COLLECT_ITEMS_MSG_STORE"));
                        return;
                    }

                    /*Upload Proof Of Arrival If only Photo Upload Pending*/

                    if (list.get(0).getePaid().equalsIgnoreCase("Yes") && PickedFromRes.equalsIgnoreCase("No")) {
                        BuildOrderStatusConfirmation(true, "");
                    } else if (!isDeliver && isPhotoUploaded.equalsIgnoreCase("No") && PickedFromRes.equalsIgnoreCase("Yes")) {
                        takeAndUploadPic(getActContext());
                    } else {
//                        showBillDialog();

                        if (vIdProofImageUploaded != null && vIdProofImageUploaded.equalsIgnoreCase("Yes")) {
                            openproofDailog(false, "");
                        } else {
                            BuildOrderStatusConfirmation(true, "");
                        }

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

            } else if (i == R.id.navigateArea) {
                if (list == null || list.size() == 0) {
                    return;
                }
                Bundle bn = new Bundle();
                bn.putString("type", "trackUser");
                bn.putSerializable("TRIP_DATA", data_trip);
                orderDetailDataModel orderDetailDataModel = list.get(0);

                if (!isDeliver) {
                    bn.putString("vPhoneNo", orderDetailDataModel.getRestaurantNumber());
                } else {
                    bn.putString("vPhoneNo", orderDetailDataModel.getUserPhone());
                }

                bn.putSerializable("currentTaskData", orderDetailDataModel);

                bn.putString("vVehicleType", orderDetailDataModel.getvVehicleType());
                bn.putString("vName", orderDetailDataModel.getUserName());
                if (!data_trip.get("PPicName").equalsIgnoreCase("")) {
                    vImage = CommonUtilities.USER_PHOTO_PATH + data_trip.get("PassengerId") + "/" + data_trip.get("PPicName");
                }
                bn.putString("vImage", vImage);
                bn.putString("callid", data_trip.get("PassengerId"));
                bn.putBoolean("isAudio", true);
                bn.putString("voiceDirectionFileUrl", voiceDirectionFileUrl);
                new StartActProcess(getActContext()).startActWithData(TrackOrderActivity.class, bn);


            } else if (i == R.id.callArea) {
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

    private void showPreferenceHelp() {

        PreferenceDailogJava preferenceDailogJava = new PreferenceDailogJava(getActContext());
        preferenceDailogJava.showPreferenceDialog(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_PREF"), generalFunc.retrieveLangLBl("Customer has selected contactless delivery option. We have introduced this feature to break infectious. To fulfill this requirement you will have to follow below steps:\n- Stay away from customer\n- Put parcel at customer's door.\n- Capture a photo of parcel at customer's door as a proof of delivery\n- Mark order as delivered", "LBL_CONTACTLESS_DELIVERYUSER_NOTE_TXT"), 0, false,
                generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), "", false);
//        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActContext());
//        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View dialogView = inflater.inflate(R.layout.desgin_preference_help, null);
//        builder.setView(dialogView);
//
//
//        final MTextView okTxt = (MTextView) dialogView.findViewById(R.id.okTxt);
//        final MTextView titileTxt = (MTextView) dialogView.findViewById(R.id.titileTxt);
//        WebView msgTxt = dialogView.findViewById(R.id.msgTxt);
//        titileTxt.setText(generalFunc.retrieveLangLBl("Delivery Preferences", "LBL_DELIVERY_PREF"));
//        okTxt.setText(generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT "));
//       /* msgTxt.setText(Html.fromHtml("" + generalFunc.retrieveLangLBl("Customer has selected contactless delivery option. We have introduced this feature to break infectious. To fulfil this requirement you will have to follow below steps:\n" +
//                "<br>" +
//                "- Stay away from customer<br>" +
//                "- Put parcel at customer's door.<br>" +
//                "- Capture a photo of parcel at customer's door as a proof of delivery.<br>" +
//                "- Mark order as delivered", "LBL_DELIVERY_DRIVER_PREFERENCE_NOTE ")));*/
//
//        msgTxt.setText(generalFunc.retrieveLangLBl("Customer has selected contactless delivery option. We have introduced this feature to break infectious. To fulfill this requirement you will have to follow below steps:\n- Stay away from customer\n- Put parcel at customer's door.\n- Capture a photo of parcel at customer's door as a proof of delivery\n- Mark order as delivered", "LBL_CONTACTLESS_DELIVERYUSER_NOTE_TXT"));
//
//        msgTxt.setMovementMethod(new ScrollingMovementMethod());
//
//        okTxt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                preferenceAlertDialog.dismiss();
//            }
//        });
//        preferenceAlertDialog = builder.create();
//        preferenceAlertDialog.setCancelable(true);
//        if (generalFunc.isRTLmode() == true) {
//            generalFunc.forceRTLIfSupported(preferenceAlertDialog);
//        }
//        preferenceAlertDialog.getWindow().setBackgroundDrawable(getActContext().getResources().getDrawable(R.drawable.all_roundcurve_card));
//        preferenceAlertDialog.show();


    }


    private void showproofHelp() {

        PreferenceDailogJava preferenceDailogJava = new PreferenceDailogJava(getActContext());
        preferenceDailogJava.showPreferenceDialog("", generalFunc.retrieveLangLBl("", "LBL_PROOF_DECLINE_NOTE"), R.drawable.ic_caution, false, generalFunc.retrieveLangLBl("", "LBL_CONTACT_US_TXT")
                , generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"), true);

    }

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
                            generalFunc.showMessage(generalFunc.getCurrentView(LiveTrackOrderDetail2Activity.this), generalFunc.retrieveLangLBl("", "LBL_NOT_SUPPORT_CAMERA_TXT"));
                        } else {
                            chooseFromCamera();
                        }
                    }
                } else {
                    if (generalFunc.isCameraStoragePermissionGranted()) {
                        if (!isDeviceSupportCamera()) {
                            generalFunc.showMessage(generalFunc.getCurrentView(LiveTrackOrderDetail2Activity.this), generalFunc.retrieveLangLBl("", "LBL_NOT_SUPPORT_CAMERA_TXT"));
                        } else {
                            chooseFromCamera();
                        }
                    }
                }
                // TODO: 28-04-2021 / Viral | Android 11 (30) Store permission code remove | revert 29
                /*if (generalFunc.isCameraStoragePermissionGranted()) {
                    if (!isDeviceSupportCamera()) {
                        generalFunc.showMessage(generalFunc.getCurrentView(LiveTrackOrderDetail2Activity.this), generalFunc.retrieveLangLBl("", "LBL_NOT_SUPPORT_CAMERA_TXT"));
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

    public void openproofDailog(boolean isproof, String responseString) {


        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActContext());

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.proof_dialog_design, null);


        final MTextView noteTxt = (MTextView) dialogView.findViewById(R.id.noteTxt);
        final MTextView clickToLargeTxt = (MTextView) dialogView.findViewById(R.id.clickToLargeTxt);
        final ImageView itemImg = (ImageView) dialogView.findViewById(R.id.itemImg);
        final ImageView cancelImg = (ImageView) dialogView.findViewById(R.id.cancelImg);


        final MButton btn_confirm = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_confirm)).getChildView();
        final MButton btn_discard = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_discard)).getChildView();

        clickToLargeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CLICK_TO_LARGE"));


        btn_confirm.setOnClickListener(v ->
        {
            ConfirmproofAlert.dismiss();
            if (isproof) {
                confirmBillCleared("No");
            } else {
                BuildOrderStatusConfirmation(true, "");
            }
        });

        btn_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showproofHelp();
                ConfirmproofAlert.dismiss();
            }
        });

        cancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmproofAlert.dismiss();
            }
        });
        builder.setView(dialogView);
        btn_confirm.setText(generalFunc.retrieveLangLBl("Confirm", "LBL_CONFIRM_TXT"));
        btn_discard.setText(generalFunc.retrieveLangLBl("Discard", "LBL_DECLINE_TXT"));
        noteTxt.setText(vIdProofImageNote);
        Picasso.get().load(vIdProofImage).placeholder(R.mipmap.ic_no_icon).into(itemImg);


        ConfirmproofAlert = builder.create();
        if (generalFunc.isRTLmode()) {
            generalFunc.forceRTLIfSupported(ConfirmproofAlert);
        }
        ConfirmproofAlert.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.all_roundcurve_card));
        ConfirmproofAlert.show();
        ConfirmproofAlert.setCancelable(false);
        ConfirmproofAlert.setOnCancelListener(dialogInterface -> Utils.hideKeyboard(getActContext()));

        itemImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new StartActProcess(getActContext()).openURL(vIdProofImage);


            }

        });


    }

}
