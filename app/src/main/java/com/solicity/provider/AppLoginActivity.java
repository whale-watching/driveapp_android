package com.solicity.provider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.viewpager.widget.ViewPager;

import com.dialogs.OpenListView;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.InternetConnection;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.LoopingCirclePageIndicator;
import com.view.MTextView;
import com.view.anim.loader.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import ViewPagerCards.CardImagePagerAdapter;
import ViewPagerCards.ShadowTransformer;

public class AppLoginActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    public GeneralFunctions generalFunc;

    MTextView introductondetailstext, languageText, currancyText, loginbtn, registerbtn;

    public LinearLayout languagearea, currencyarea;

    LinearLayout languageCurrancyArea;

    GenerateAlertBox languageListAlertBox;

    String selected_language_code = "";

    ArrayList<HashMap<String, String>> languageDataList = new ArrayList<>();
    ArrayList<HashMap<String, String>> currencyDataList = new ArrayList<>();

    String selected_currency = "";
    String selected_currency_symbol = "";

    GenerateAlertBox currencyListAlertBox;

    String type = "";

    AVLoadingIndicatorView loaderView;
    InternetConnection intCheck;
    LinearLayout btnArea;
    boolean isAnimated = false;
    ViewPager bannerViewPager;
    private CardImagePagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    LoopingCirclePageIndicator bannerCirclePageIndicator;

    ArrayList<HashMap<String, String>> imagesList = new ArrayList<>();
    View bannerArea;


    private SkeletonScreen skeletonScreen;
    ImageView splashlogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_app_login);

        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        isAnimated = getIntent().getBooleanExtra("isAnimated", false);
        languageCurrancyArea = (LinearLayout) findViewById(R.id.languageCurrancyArea);
        splashlogo = findViewById(R.id.splashlogo);

        if (isAnimated) {
            //  shimmerFrameLayout.startShimmerAnimation();
            splashlogo.startAnimation(inFromLeftAnimation());


            Animation anim = AnimationUtils.loadAnimation(getActContext(), R.anim.slide_from_right);
            if (generalFunc.isRTLmode()) {
                anim = AnimationUtils.loadAnimation(getActContext(), R.anim.slide_from_left);
            }
            languageCurrancyArea.setAnimation(anim);
        }


        intCheck = new InternetConnection(getActContext());
        generalFunc.getHasKey(getActContext());
        initview();
        setLabel();
        buildLanguageList();

        manageLaunchImages();

        if (isAnimated) {


            bannerArea = findViewById(R.id.bannerArea);
            skeletonScreen = Skeleton.bind(bannerArea)
                    .load(R.layout.launch_shimmer_view)
                    .duration(1000)
                    .color(R.color.shimmer_color)
                    .angle(0)
                    .show();

            MyHandler myHandler = new MyHandler(this);
            myHandler.sendEmptyMessageDelayed(1, 3000);
        }


    }

    public static class MyHandler extends android.os.Handler {
        private final WeakReference<AppLoginActivity> activityWeakReference;

        MyHandler(AppLoginActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (activityWeakReference.get() != null) {
                activityWeakReference.get().skeletonScreen.hide();
            }
        }
    }


    private Animation inFromLeftAnimation() {

        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, generalFunc.isRTLmode() ? -0.9f : 0.9f,
                Animation.ABSOLUTE, generalFunc.isRTLmode() ? -0.9f : 0.9f,
                Animation.RELATIVE_TO_PARENT, 0.3f,
                Animation.ABSOLUTE, 0.3f);
        animation.setDuration(300);
        animation.setInterpolator(new AccelerateInterpolator());

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                SpringAnimation animation1 = new SpringAnimation(splashlogo, DynamicAnimation.TRANSLATION_Y);
                SpringForce spring = new SpringForce();
                spring.setFinalPosition(Utils.dpToPx(2, getActContext()));
                spring.setStiffness(SpringForce.STIFFNESS_LOW);
                spring.setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);
                animation1.setSpring(spring);
                animation1.start();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return animation;


    }


    private void initview() {

        bannerViewPager = (ViewPager) findViewById(R.id.bannerViewPager);

        bannerCirclePageIndicator = findViewById(R.id.bannerCirclePageIndicator);

        bannerViewPager.addOnPageChangeListener(this);
        btnArea = (LinearLayout) findViewById(R.id.btnArea);
        introductondetailstext = (MTextView) findViewById(R.id.introductondetailstext);
        languageText = (MTextView) findViewById(R.id.languageText);
        currancyText = (MTextView) findViewById(R.id.currancyText);

        languagearea = (LinearLayout) findViewById(R.id.languagearea);
        currencyarea = (LinearLayout) findViewById(R.id.currencyarea);
        loginbtn = (MTextView) findViewById(R.id.loginbtn);
        registerbtn = (MTextView) findViewById(R.id.registerbtn);

        loaderView = (AVLoadingIndicatorView) findViewById(R.id.loaderView);
        loaderView.setVisibility(View.GONE);

        languageCurrancyArea = (LinearLayout) findViewById(R.id.languageCurrancyArea);

        loginbtn.setOnClickListener(new setOnClickAct());
        registerbtn.setOnClickListener(new setOnClickAct());
        languagearea.setOnClickListener(new setOnClickAct());
        currencyarea.setOnClickListener(new setOnClickAct());


    }


    private void setLabel() {
        introductondetailstext.setText(generalFunc.retrieveLangLBl("", "LBL_HOME_DRIVER_INTRO_DETAILS"));
        loginbtn.setText(generalFunc.retrieveLangLBl("", "LBL_LOGIN"));
        registerbtn.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_REGISTER_TXT"));

        if (!Utils.checkText(introductondetailstext.getText().toString())) {
            introductondetailstext.setVisibility(View.GONE);
        }

        languageText.setText(generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));
        currancyText.setText(generalFunc.retrieveValue(Utils.DEFAULT_CURRENCY_VALUE));


    }

    int selCurrancyPosition = -1;
    int selLanguagePosition = -1;

    public void buildLanguageList() {

        JSONArray languageList_arr = generalFunc.getJsonArray(generalFunc.retrieveValue(Utils.LANGUAGE_LIST_KEY));
        languageDataList.clear();
        for (int i = 0; i < languageList_arr.length(); i++) {
            JSONObject obj_temp = generalFunc.getJsonObject(languageList_arr, i);

            HashMap<String, String> mapData = new HashMap<>();
            mapData.put("vTitle", generalFunc.getJsonValueStr("vTitle", obj_temp));
            mapData.put("vCode", generalFunc.getJsonValueStr("vCode", obj_temp));
            mapData.put("vService_TEXT_color", generalFunc.getJsonValueStr("vService_TEXT_color", obj_temp));
            mapData.put("vService_BG_color", generalFunc.getJsonValueStr("vService_BG_color", obj_temp));

            if (Utils.getText(languageText).equalsIgnoreCase(generalFunc.getJsonValueStr("vCode", obj_temp))) {
                selLanguagePosition = i;
                setColorLangCode(generalFunc.getJsonValueStr("vService_TEXT_color", obj_temp), generalFunc.getJsonValueStr("vService_BG_color", obj_temp));
            }

            languageDataList.add(mapData);

            if ((generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY)).equals(generalFunc.getJsonValue("vCode", obj_temp))) {
                selected_language_code = generalFunc.getJsonValueStr("vCode", obj_temp);

            }
        }


        setLanguageArea();

        buildCurrencyList();

    }

    public void setColorLangCode(String textColor, String bgColor) {
        languageText.setTextColor(Color.parseColor(textColor));
        languagearea.getBackground().setTint(Color.parseColor(bgColor));
    }

    public void setColorCurrCode(String textColor, String bgColor) {
        currancyText.setTextColor(Color.parseColor(textColor));
        currencyarea.getBackground().setTint(Color.parseColor(bgColor));
    }

    public void setLanguageArea() {
        if (languageDataList.size() < 2 || generalFunc.retrieveValue("LANGUAGE_OPTIONAL").equalsIgnoreCase("Yes")) {
            languagearea.setVisibility(View.GONE);
        }
    }

    public void buildCurrencyList() {


        JSONArray currencyList_arr = generalFunc.getJsonArray(generalFunc.retrieveValue(Utils.CURRENCY_LIST_KEY));
        currencyDataList.clear();
        for (int i = 0; i < currencyList_arr.length(); i++) {
            JSONObject obj_temp = generalFunc.getJsonObject(currencyList_arr, i);

            HashMap<String, String> mapData = new HashMap<>();
            mapData.put("vName", generalFunc.getJsonValueStr("vName", obj_temp));
            mapData.put("vCode", generalFunc.getJsonValueStr("vSymbol", obj_temp));
            mapData.put("vSymbol", generalFunc.getJsonValueStr("vSymbol", obj_temp));
            mapData.put("vService_BG_color", generalFunc.getJsonValueStr("vService_BG_color", obj_temp));
            mapData.put("vService_TEXT_color", generalFunc.getJsonValueStr("vService_TEXT_color", obj_temp));

            if (Utils.getText(currancyText).equalsIgnoreCase(generalFunc.getJsonValueStr("vName", obj_temp))) {
                selCurrancyPosition = i;
                setColorCurrCode(generalFunc.getJsonValueStr("vService_TEXT_color", obj_temp), generalFunc.getJsonValueStr("vService_BG_color", obj_temp));
            }

            currencyDataList.add(mapData);

        }

        if (currencyDataList.size() < 2 || generalFunc.retrieveValue("CURRENCY_OPTIONAL").equalsIgnoreCase("Yes")) {
            currencyarea.setVisibility(View.GONE);

            setLanguageArea();
        }
    }

    public Context getActContext() {
        return AppLoginActivity.this;
    }


    public String getSelectLangText() {
        return ("" + generalFunc.retrieveLangLBl("Select", "LBL_SELECT_LANGUAGE_HINT_TXT"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void showLanguageList() {

        OpenListView.getInstance(getActContext(), getSelectLangText(), languageDataList, OpenListView.OpenDirection.CENTER, true, position -> {

            currencyarea.setClickable(true);
            selLanguagePosition = position;
            HashMap<String, String> mapData = languageDataList.get(position);

            selected_language_code = mapData.get("vCode");

            if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {
                generalFunc.showGeneralMessage("",
                        generalFunc.retrieveLangLBl("No Internet Connection", "LBL_NO_INTERNET_TXT"));
            } else {
                if (!generalFunc.retrieveValue(Utils.DEFAULT_LANGUAGE_VALUE).equals(mapData.get("vTitle"))) {
                    languageText.setText(mapData.get("vCode"));
                    generalFunc.storeData(Utils.LANGUAGE_CODE_KEY, selected_language_code);
                    generalFunc.storeData(Utils.DEFAULT_LANGUAGE_VALUE, mapData.get("vTitle"));

                    changeLanguagedata(selected_language_code);
                }
            }
        }, true, generalFunc.retrieveLangLBl("", "LBL_LANG_PREFER"), true).show(selLanguagePosition, "vTitle");
    }

    public void showCurrencyList() {
        OpenListView.getInstance(getActContext(), generalFunc.retrieveLangLBl("", "LBL_SELECT_CURRENCY"), currencyDataList, OpenListView.OpenDirection.CENTER, true, position -> {

            currencyarea.setClickable(true);
            selCurrancyPosition = position;
            HashMap<String, String> mapData = currencyDataList.get(position);

            selected_currency_symbol = mapData.get("vSymbol");

            selected_currency = mapData.get("vName");
            currancyText.setText(mapData.get("vName"));

            generalFunc.storeData(Utils.DEFAULT_CURRENCY_VALUE, selected_currency);
        }, true, generalFunc.retrieveLangLBl("", "LBL_CURRENCY_PREFER"), false).show(selCurrancyPosition, "vName");
    }

    public void manageLaunchImages() {


        mCardAdapter = new CardImagePagerAdapter(getActContext());


        JSONArray imagesArray = null;
        try {
            imagesArray = new JSONArray(generalFunc.retrieveValue("APP_LAUNCH_IMAGES").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (imagesArray != null && imagesArray.length() > 0) {

            if (generalFunc.isRTLmode()) {

                for (int x = imagesArray.length() - 1; x >= 0; x--) {
                    JSONObject imageObj = generalFunc.getJsonObject(imagesArray, x);
                    //String imageURL = Utils.getResizeImgURL(getActContext(),  generalFunc.getJsonValue("vImage", imageObj).toString(), Utils.getWidthOfBanner(getActContext(), 0), Utils.getHeightOfBanner(getActContext(), 0, "16:9"));
//                String imageURL = Utils.getResizeImgURL(getActContext(),  generalFunc.getJsonValue("vImage", imageObj).toString(),
//                      bannerWidth,
//                       bannerHeight);
                    String imageURL = generalFunc.getJsonValue("vImage", imageObj).toString();
                    HashMap<String, String> imagMap = new HashMap<>();
                    imagMap.put("vImage", imageURL);
                    imagMap.put("tTitle", generalFunc.getJsonValue("tTitle", imageObj).toString());
                    imagMap.put("tSubtitle", generalFunc.getJsonValue("tSubtitle", imageObj).toString());
                    imagesList.add(imagMap);
                    mCardAdapter.addCardItem(imagMap, getActContext());
                }

            } else {
                for (int i = 0; i < imagesArray.length(); i++) {
                    JSONObject imageObj = generalFunc.getJsonObject(imagesArray, i);
                    //String imageURL = Utils.getResizeImgURL(getActContext(),  generalFunc.getJsonValue("vImage", imageObj).toString(), Utils.getWidthOfBanner(getActContext(), 0), Utils.getHeightOfBanner(getActContext(), 0, "16:9"));
//                String imageURL = Utils.getResizeImgURL(getActContext(),  generalFunc.getJsonValue("vImage", imageObj).toString(),
//                      bannerWidth,
//                       bannerHeight);
                    String imageURL = generalFunc.getJsonValue("vImage", imageObj).toString();
                    HashMap<String, String> imagMap = new HashMap<>();
                    imagMap.put("vImage", imageURL);
                    imagMap.put("tTitle", generalFunc.getJsonValue("tTitle", imageObj).toString());
                    imagMap.put("tSubtitle", generalFunc.getJsonValue("tSubtitle", imageObj).toString());
                    imagesList.add(imagMap);
                    mCardAdapter.addCardItem(imagMap, getActContext());
                }
            }
        }


        mCardShadowTransformer = new ShadowTransformer(bannerViewPager, mCardAdapter);
        bannerViewPager.setAdapter(mCardAdapter);
        bannerViewPager.setPageTransformer(false, mCardShadowTransformer);
        bannerViewPager.setOffscreenPageLimit(3);
        bannerCirclePageIndicator.setDataSize(imagesList.size());
        if (imagesList.size() > 1) {
            bannerCirclePageIndicator.setVisibility(View.VISIBLE);
        } else {
            bannerCirclePageIndicator.setVisibility(View.GONE);
        }
        bannerCirclePageIndicator.setViewPager(bannerViewPager);

        if (generalFunc.isRTLmode()) {
            if (imagesList != null) {
                bannerViewPager.setCurrentItem(imagesList.size());
            }
        }


    }

    public void changeLanguagedata(String langcode) {
        loaderView.setVisibility(View.VISIBLE);
        btnArea.setVisibility(View.GONE);
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "changelanguagelabel");
        parameters.put("vLang", langcode);
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(responseString -> {
            JSONObject responseStringObject = generalFunc.getJsonObject(responseString);

            if (responseStringObject != null && !responseStringObject.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseStringObject);

                if (isDataAvail == true) {


                    generalFunc.storeData(Utils.languageLabelsKey, generalFunc.getJsonValue(Utils.message_str, responseString));
                    generalFunc.storeData(Utils.LANGUAGE_IS_RTL_KEY, generalFunc.getJsonValue("eType", responseString));
                    generalFunc.storeData("APP_LAUNCH_IMAGES", generalFunc.getJsonValue("APP_LAUNCH_IMAGES", responseString));
                    GeneralFunctions.clearAndResetLanguageLabelsData(MyApp.getInstance().getApplicationContext());

                    imagesList.clear();

                    manageLaunchImages();
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            loaderView.setVisibility(View.VISIBLE);
                            btnArea.setVisibility(View.VISIBLE);
                            // generalFunc.restartApp();

                            MyApp.getInstance().refreshWithConfigData();
                            setLabel();
                        }
                    }, 2000);


                } else {
                    loaderView.setVisibility(View.GONE);
                    btnArea.setVisibility(View.VISIBLE);

                }
            } else {
                loaderView.setVisibility(View.GONE);
                btnArea.setVisibility(View.VISIBLE);

            }

        });
        exeWebServer.execute();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class setOnClickAct implements View.OnClickListener {


        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(getActContext());
            if (i == R.id.languagearea) {

                if (loaderView.getVisibility() == View.GONE) {


                    languagearea.setEnabled(false);
                    showLanguageList();
                    manageBtn();
                }

            } else if (i == R.id.currencyarea) {
                if (loaderView.getVisibility() == View.GONE) {
                    currencyarea.setEnabled(false);
                    showCurrencyList();
                    manageBtn();
                }
            } else if (i == R.id.loginbtn) {
                if (loaderView.getVisibility() == View.GONE) {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "login");

                    new StartActProcess(getActContext()).startActWithData(AppLoignRegisterActivity.class, bundle);
                }


            } else if (i == R.id.registerbtn) {
                if (loaderView.getVisibility() == View.GONE) {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "register");
                    new StartActProcess(getActContext()).startActWithData(AppLoignRegisterActivity.class, bundle);
                }

            }
        }


    }

    public void manageBtn() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                languagearea.setEnabled(true);
                currencyarea.setEnabled(true);

            }
        }, 500);
    }

}
