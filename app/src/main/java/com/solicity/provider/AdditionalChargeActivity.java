package com.solicity.provider;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.general.files.AppFunctions;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.GetLocationUpdates;
import com.general.files.MyApp;
import com.general.files.UpdateFrequentTask;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;
import com.utils.Logger;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AdditionalChargeActivity extends BaseActivity implements UpdateFrequentTask.OnTaskRunCalled {

    MTextView titleTxt;
    ImageView backImgView;
    MTextView additionalchargeHTxt, matrialfeeHTxt, miscfeeHTxt, discountHTxt, matrialfeeSTxt, miscfeeSTxt, discountSTxt;
    MTextView finalvalTxt, finalHTxt, currentchargeHTxt, currentchargeVTxt, noteLbl, noteTxt;
    MaterialEditText timatrialfeeVTxt, miscfeeVTxt, discountVTxt;
    MTextView matrialfeeCurrancyTxt, miscfeeCurrancyTxt, discountCurrancyTxt;
    MTextView matrialfeeCurrancyTxt1, miscfeeCurrancyTxt1, discountCurrancyTxt1;
    LinearLayout materialFeelayout, miscFeelayout, providediscFeelayout, fareArea, tollFeelayout, otherFeelayout;

    double matrialfee = 0.00;
    double miscfee = 0.00;
    double discount = 0.00;
    double tollAmount = 0.00;
    double otherChargeAmount = 0.00;
    double finaltotal = 0.00;
    ArrayList<Double> additonallist = new ArrayList<>();
    boolean isDiscountCalc = true;
    GeneralFunctions generalFunc;
    String currencetprice = "0.00";
    MButton submitBtn;
    View midSeparatorLine;
    MButton skipBtn;
    LinearLayout skipBtnArea;
    HashMap<String, String> data;
    String CurrencySymbol;
    boolean isReverseCurrencyEnabled;
    private String vConfirmationCode = "";
    private String required_str = "";
    private Dialog dialog_verify;
    private String error_verification_code = "";
    String eApproveRequestSentByDriver = "";
    UpdateFrequentTask updateApprovalStatusTask;
    int APPROVAL_STATUS_UPDATE_INTERVAL = 4000;

    boolean isTollEnable = false;
    private MTextView tollfeeHTxt, tollfeeSTxt, tollfeeCurrancyTxt, tollfeeCurrancyTxt1;
    private MaterialEditText tollfeeVTxt;
    private MTextView otherfeeHTxt, otherfeeSTxt, otherfeeCurrancyTxt, otherfeeCurrancyTxt1;
    private MaterialEditText otherfeeVTxt;

    private ImageView tollfeeditImgView, otherfeeditImgView;
    boolean isOtherChargesEnable = false;
    private JSONObject userProfileJsonObj;
    boolean isUFX = false;
    int resendSecAfter;
    int resendSecInMilliseconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_charge);
        data = (HashMap<String, String>) getIntent().getSerializableExtra("TRIP_DATA");
        initViews();
        error_verification_code = generalFunc.retrieveLangLBl("", "LBL_VERIFY_CHARGES_CODE_INVALID");
        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD");
        eApproveRequestSentByDriver = data.get("eApproveRequestSentByDriver");
        if (Utils.checkText(eApproveRequestSentByDriver) && eApproveRequestSentByDriver.equalsIgnoreCase("Yes")) {
            if (data.containsKey("vChargesDetailDataAvailable") && data.get("vChargesDetailDataAvailable").equalsIgnoreCase("Yes")) {
                vConfirmationCode = data.get("vConfirmationCode");

                matrialfee = generalFunc.parseDoubleValue(0, data.get("fMaterialFee"));
                additonallist.remove(0);
                additonallist.add(0, matrialfee);
                vConfirmationCode = data.get("vConfirmationCode");
                if (isUFX) {
                    matrialfee = generalFunc.parseDoubleValue(0, data.get("fMaterialFee"));
                    additonallist.remove(0);
                    additonallist.add(0, matrialfee);

                    miscfee = generalFunc.parseDoubleValue(0, data.get("fMiscFee"));
                    additonallist.remove(1);
                    additonallist.add(1, miscfee);


                    discount = generalFunc.parseDoubleValue(0, data.get("fDriverDiscount"));
                    additonallist.remove(2);
                    additonallist.add(2, discount);

                    miscfeeVTxt.setText("" + miscfee);
                    timatrialfeeVTxt.setText("" + matrialfee);
                    discountVTxt.setText("" + discount);
                } else {
                    tollAmount = generalFunc.parseDoubleValue(0, data.get("fTollPrice"));
                    additonallist.remove(0);
                    additonallist.add(0, tollAmount);


                    otherChargeAmount = generalFunc.parseDoubleValue(0, data.get("fOtherCharges"));
                    additonallist.remove(1);
                    additonallist.add(1, otherChargeAmount);

                    discount = generalFunc.parseDoubleValue(0, data.get("fDriverDiscount"));
                    additonallist.remove(2);
                    additonallist.add(2, discount);


                    tollfeeVTxt.setText("" + tollAmount);
                    otherfeeVTxt.setText("" + otherChargeAmount);
                    discountVTxt.setText("" + discount);
                }

                calculateData("", finalvalTxt);

                if (updateApprovalStatusTask == null) {
                    updateApprovalStatusTask = new UpdateFrequentTask(APPROVAL_STATUS_UPDATE_INTERVAL);
                    updateApprovalStatusTask.setTaskRunListener(AdditionalChargeActivity.this);
                    updateApprovalStatusTask.startRepeatingTask();
                }

                openVerificationDialog();
            }

        }
    }

    public Context getActContext() {
        return AdditionalChargeActivity.this;
    }


    public void initViews() {
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());

        userProfileJsonObj = generalFunc.getJsonObject(generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));

        isTollEnable = generalFunc.getJsonValueStr("ENABLE_MANUAL_TOLL_FEATURE", userProfileJsonObj).equalsIgnoreCase("Yes") ? true : false;
        isOtherChargesEnable = generalFunc.getJsonValueStr("ENABLE_OTHER_CHARGES_FEATURE", userProfileJsonObj).equalsIgnoreCase("Yes") ? true : false;

        if (getIntent().getExtras().getString("eType") != null && getIntent().getExtras().getString("eType") != "") {
            isUFX = getIntent().getExtras().getString("eType").equalsIgnoreCase("UberX") ? true : false;
        }


        resendSecAfter = generalFunc.parseIntegerValue(30, generalFunc.getJsonValueStr(Utils.VERIFICATION_CODE_RESEND_TIME_IN_SECONDS_KEY, userProfileJsonObj));
        resendSecInMilliseconds = resendSecAfter * 1 * 1000;


        backImgView = (ImageView) findViewById(R.id.backImgView);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView.setVisibility(View.GONE);

        additionalchargeHTxt = (MTextView) findViewById(R.id.additionalchargeHTxt);
        matrialfeeHTxt = (MTextView) findViewById(R.id.matrialfeeHTxt);
        miscfeeHTxt = (MTextView) findViewById(R.id.miscfeeHTxt);
        discountHTxt = (MTextView) findViewById(R.id.discountHTxt);

        matrialfeeSTxt = (MTextView) findViewById(R.id.matrialfeeSTxt);
        miscfeeSTxt = (MTextView) findViewById(R.id.miscfeeSTxt);
        discountSTxt = (MTextView) findViewById(R.id.discountSTxt);

        finalvalTxt = (MTextView) findViewById(R.id.finalvalTxt);
        finalHTxt = (MTextView) findViewById(R.id.finalHTxt);

        currentchargeHTxt = (MTextView) findViewById(R.id.currentchargeHTxt);
        currentchargeVTxt = (MTextView) findViewById(R.id.currentchargeVTxt);


        tollfeeHTxt = (MTextView) findViewById(R.id.tollfeeHTxt);
        tollfeeVTxt = (MaterialEditText) findViewById(R.id.tollfeeVTxt);
        tollfeeSTxt = (MTextView) findViewById(R.id.tollfeeSTxt);
        tollfeeCurrancyTxt = (MTextView) findViewById(R.id.tollfeeCurrancyTxt);
        tollfeeCurrancyTxt1 = (MTextView) findViewById(R.id.tollfeeCurrancyTxt1);
        tollfeeditImgView = (ImageView) findViewById(R.id.tollfeeditImgView);

        otherfeeHTxt = (MTextView) findViewById(R.id.otherfeeHTxt);
        otherfeeSTxt = (MTextView) findViewById(R.id.otherfeeSTxt);
        otherfeeCurrancyTxt = (MTextView) findViewById(R.id.otherfeeCurrancyTxt);
        otherfeeCurrancyTxt1 = (MTextView) findViewById(R.id.otherfeeCurrancyTxt1);
        otherfeeVTxt = (MaterialEditText) findViewById(R.id.otherfeeVTxt);
        otherfeeditImgView = (ImageView) findViewById(R.id.otherfeeditImgView);

        tollfeeHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TOLL_CHARGES"));
        otherfeeHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_OTHER_CHARGES"));

        noteLbl = (MTextView) findViewById(R.id.noteLbl);
        noteTxt = (MTextView) findViewById(R.id.noteTxt);

        matrialfeeCurrancyTxt = (MTextView) findViewById(R.id.matrialfeeCurrancyTxt);
        miscfeeCurrancyTxt = (MTextView) findViewById(R.id.miscfeeCurrancyTxt);
        discountCurrancyTxt = (MTextView) findViewById(R.id.discountCurrancyTxt);


        matrialfeeCurrancyTxt1 = (MTextView) findViewById(R.id.matrialfeeCurrancyTxt1);
        miscfeeCurrancyTxt1 = (MTextView) findViewById(R.id.miscfeeCurrancyTxt1);
        discountCurrancyTxt1 = (MTextView) findViewById(R.id.discountCurrancyTxt1);

        materialFeelayout = (LinearLayout) findViewById(R.id.materialFeelayout);
        miscFeelayout = (LinearLayout) findViewById(R.id.miscFeelayout);
        providediscFeelayout = (LinearLayout) findViewById(R.id.providediscFeelayout);
        tollFeelayout = (LinearLayout) findViewById(R.id.tollFeelayout);
        fareArea = (LinearLayout) findViewById(R.id.fareArea);
        otherFeelayout = (LinearLayout) findViewById(R.id.otherFeelayout);

        if ((isTollEnable || isOtherChargesEnable) && !isUFX) {
            materialFeelayout.setVisibility(View.GONE);
            miscFeelayout.setVisibility(View.GONE);
            fareArea.setVisibility(View.GONE);
            providediscFeelayout.setVisibility(View.GONE);
            if (!isOtherChargesEnable) {
                otherFeelayout.setVisibility(View.GONE);
            }
            if (!isTollEnable) {
                tollFeelayout.setVisibility(View.GONE);
            }
            //  getTollcostValue();
        } else {
            tollFeelayout.setVisibility(View.GONE);
            otherFeelayout.setVisibility(View.GONE);
            fareArea.setVisibility(View.VISIBLE);
        }
        midSeparatorLine = (View) findViewById(R.id.midSeparatorLine);
        submitBtn = ((MaterialRippleLayout) findViewById(R.id.submitBtn)).getChildView();
        skipBtn = ((MaterialRippleLayout) findViewById(R.id.skipBtn)).getChildView();
        skipBtnArea = (LinearLayout) findViewById(R.id.skipBtnArea);
        skipBtnArea.setVisibility(isUFX ? View.GONE : View.VISIBLE);
        midSeparatorLine.setVisibility(isUFX ? View.GONE : View.VISIBLE);
        skipBtn.setOnClickListener(isUFX ? null : new setOnClickList());

        submitBtn.setId(Utils.generateViewId());
        skipBtn.setId(Utils.generateViewId());
        submitBtn.setOnClickListener(new setOnClickList());


        timatrialfeeVTxt = (MaterialEditText) findViewById(R.id.timatrialfeeVTxt);
        miscfeeVTxt = (MaterialEditText) findViewById(R.id.miscfeeVTxt);
        discountVTxt = (MaterialEditText) findViewById(R.id.discountVTxt);

        int maxLength = 10;
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        timatrialfeeVTxt.setFilters(FilterArray);
        miscfeeVTxt.setFilters(FilterArray);
        discountVTxt.setFilters(FilterArray);
        tollfeeVTxt.setFilters(FilterArray);
        otherfeeVTxt.setFilters(FilterArray);


        timatrialfeeVTxt.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        miscfeeVTxt.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        discountVTxt.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        tollfeeVTxt.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        otherfeeVTxt.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        discountVTxt.setShowClearButton(false);
        miscfeeVTxt.setShowClearButton(false);
        timatrialfeeVTxt.setShowClearButton(false);
        tollfeeVTxt.setShowClearButton(false);
        otherfeeVTxt.setShowClearButton(false);

        String data = generalFunc.convertNumberWithRTL("0.00");
        miscfeeVTxt.setHint(data);
        timatrialfeeVTxt.setHint(data);
        discountVTxt.setHint(data);
        tollfeeVTxt.setHint(data);
        otherfeeVTxt.setHint(data);

        setLabel();
        defaultAddtionalprice();
        calculateData("", finalvalTxt);

        timatrialfeeVTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String val = timatrialfeeVTxt.getText().toString();
                if (val.startsWith(".") || val.equals(".")) {
                    timatrialfeeVTxt.setText("0.");
                    timatrialfeeVTxt.setSelection(2);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0) {
                    matrialfee = 0.00;
                    try {
                        matrialfee = generalFunc.parseDoubleValue(0, s.toString());
                        additonallist.remove(0);
                        additonallist.add(0, matrialfee);
                        calculateData(s.toString(), finalvalTxt);
                    } catch (Exception e) {

                    }
                } else {
                    additonallist.remove(0);
                    additonallist.add(0, 0.00);
                    calculateData(s.toString(), finalvalTxt);
                }

            }
        });

        miscfeeVTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String val = miscfeeVTxt.getText().toString();
                if (val.startsWith(".") || val.equals(".")) {
                    miscfeeVTxt.setText("0.");
                    miscfeeVTxt.setSelection(2);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    miscfee = 0.00;
                    try {
                        miscfee = generalFunc.parseDoubleValue(0, s.toString());
                        additonallist.remove(1);
                        additonallist.add(1, miscfee);
                        calculateData(s.toString(), finalvalTxt);
                    } catch (Exception e) {

                    }
                } else {
                    additonallist.remove(1);
                    additonallist.add(1, 0.00);
                    calculateData(s.toString(), finalvalTxt);
                }

            }
        });

//        discountVTxt.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                String val = discountVTxt.getText().toString();
//                if (val.startsWith(".") || val.equals(".")) {
//                    discountVTxt.setText("0.");
//                    discountVTxt.setSelection(2);
//                }
//
//                if (isDiscountCalc == false) {
//                    isDiscountCalc = true;
//                    return;
//                }
//                double discountValue = GeneralFunctions.parseLongValue(0, "" + s);
//                if (discount > 0) {
//                    finaltotal = finaltotal + discount;
//                }
//                Logger.d("CheckVal", "::" + (finaltotal - discountValue));
//                if ((finaltotal - discountValue) < 0) {
//                    try {
//
//                        isDiscountCalc = false;
//                        discountVTxt.setText(finaltotal + "");
//                        discountVTxt.setSelection(("" + finaltotal).length());
//                        discountValue = finaltotal;
//                    } catch (Exception e) {
////                        finaltotal = finaltotal - discountValue;
////                        discount = discountValue;
////
////                        additonallist.remove(2);
////                        additonallist.add(2, discount);
//                        discountVTxt.setText(GeneralFunctions.convertDecimalPlaceDisplay(finaltotal) + "");
//                        calculateData(Utils.getText(discountVTxt), finalvalTxt);
//                        return;
////                        isDiscountCalc = false;
////                        Logger.e("Exception", "::" + e.toString());
////                        discountVTxt.setText(GeneralFunctions.convertDecimalPlaceDisplay(finaltotal) + "");
//
//                    }
//                }
//
//
//                finaltotal = finaltotal - discountValue;
//                discount = discountValue;
//
//                additonallist.remove(2);
//                additonallist.add(2, discount);
//                calculateData(s.toString(), finalvalTxt);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        otherfeeVTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String val = otherfeeVTxt.getText().toString();
                if (val.startsWith(".") || val.equals(".")) {
                    otherfeeVTxt.setText("0.");
                    otherfeeVTxt.setSelection(2);
                }

                if (isDiscountCalc == false) {
                    isDiscountCalc = true;
                    return;
                }
                double discountValue = GeneralFunctions.parseDoubleValue(0.0, "" + s);
                if (otherChargeAmount > 0) {
                    finaltotal = finaltotal + otherChargeAmount;
                }
               /* Logger.d("CheckVal", "::" + (finaltotal - discountValue));
                if ((finaltotal - discountValue) < 0) {
                    try {

                        isDiscountCalc = false;
                        otherfeeVTxt.setText(finaltotal + "");
                        otherfeeVTxt.setSelection(("" + finaltotal).length());
                        discountValue = finaltotal;
                    } catch (Exception e) {
//                        finaltotal = finaltotal - discountValue;
//                        discount = discountValue;
//
//                        additonallist.remove(2);
//                        additonallist.add(2, discount);
                        otherfeeVTxt.setText(GeneralFunctions.convertDecimalPlaceDisplay(finaltotal) + "");
                        calculateData(Utils.getText(otherfeeVTxt), finalvalTxt);
                        return;
//                        isDiscountCalc = false;
//                        Logger.e("Exception", "::" + e.toString());
//                        discountVTxt.setText(GeneralFunctions.convertDecimalPlaceDisplay(finaltotal) + "");

                    }
                }


                finaltotal = finaltotal - discountValue;*/
                otherChargeAmount = discountValue;

                additonallist.remove(1);
                additonallist.add(1, otherChargeAmount);
                calculateData(s.toString(), finalvalTxt);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        tollfeeVTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String val = tollfeeVTxt.getText().toString();
                if (val.startsWith(".") || val.equals(".")) {
                    tollfeeVTxt.setText("0.");
                    tollfeeVTxt.setSelection(2);
                }

                if (isDiscountCalc == false) {
                    isDiscountCalc = true;
                    return;
                }
                double discountValue = GeneralFunctions.parseDoubleValue(0.0, "" + s);
                if (tollAmount > 0) {
                    finaltotal = finaltotal + tollAmount;
                }
               /* Logger.d("CheckVal", "::" + (finaltotal - discountValue));
                if ((finaltotal - discountValue) < 0) {
                    try {

                        isDiscountCalc = false;
                        tollfeeVTxt.setText(finaltotal + "");
                        tollfeeVTxt.setSelection(("" + finaltotal).length());
                        discountValue = finaltotal;
                    } catch (Exception e) {
//                        finaltotal = finaltotal - discountValue;
//                        discount = discountValue;
//
//                        additonallist.remove(2);
//                        additonallist.add(2, discount);
                        tollfeeVTxt.setText(GeneralFunctions.convertDecimalPlaceDisplay(finaltotal) + "");
                        calculateData(Utils.getText(tollfeeVTxt), finalvalTxt);
                        return;
//                        isDiscountCalc = false;
//                        Logger.e("Exception", "::" + e.toString());
//                        discountVTxt.setText(GeneralFunctions.convertDecimalPlaceDisplay(finaltotal) + "");

                    }
                }


                finaltotal = finaltotal - discountValue;*/
                tollAmount = discountValue;

                additonallist.remove(0);
                additonallist.add(0, tollAmount);
                calculateData(s.toString(), finalvalTxt);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        discountVTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String val = discountVTxt.getText().toString();
                if (val.startsWith(".") || val.equals(".")) {
                    discountVTxt.setText("0.");
                    discountVTxt.setSelection(2);
                }

                if (isDiscountCalc == false) {
                    isDiscountCalc = true;
                    return;
                }
                double discountValue = GeneralFunctions.parseDoubleValue(0.0, "" + s);
                if (discount > 0) {
                    finaltotal = finaltotal + discount;
                }
                Logger.d("CheckVal", "::" + (finaltotal - discountValue));

                if ((finaltotal < discountValue) && ((finaltotal - discountValue) < 0)) {
                    try {

                        isDiscountCalc = false;
                        discountVTxt.setText(finaltotal + "");
                        discountVTxt.setSelection(("" + finaltotal).length());
                        discountValue = finaltotal;
                    } catch (Exception e) {
//                        finaltotal = finaltotal - discountValue;
//                        discount = discountValue;
//
//                        additonallist.remove(2);
//                        additonallist.add(2, discount);
                        discountVTxt.setText(GeneralFunctions.convertDecimalPlaceDisplay(finaltotal) + "");
                        calculateData(Utils.getText(discountVTxt), finalvalTxt);
                        return;
//                        isDiscountCalc = false;
//                        Logger.e("Exception", "::" + e.toString());
//                        discountVTxt.setText(GeneralFunctions.convertDecimalPlaceDisplay(finaltotal) + "");

                    }
                } else if ((discountValue > finaltotal)) {
                    isDiscountCalc = false;
                    discountVTxt.setText(finaltotal + "");
                    discountVTxt.setSelection(("" + finaltotal).length());


                }


                finaltotal = finaltotal - discountValue;

                discount = discountValue;

                additonallist.remove(2);

                additonallist.add(2, discount);
                calculateData(s.toString(), finalvalTxt);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void defaultAddtionalprice() {
        additonallist.add(0, 0.00);
        additonallist.add(1, 0.00);
        additonallist.add(2, 0.00);
    }


    public void setLabel() {
        matrialfeeHTxt.setText(generalFunc.retrieveLangLBl("Material fee", "LBL_MATERIAL_FEE"));
        miscfeeHTxt.setText(generalFunc.retrieveLangLBl("Misc fee", "LBL_MISC_FEE"));
        discountHTxt.setText(generalFunc.retrieveLangLBl("Provider Discount", "LBL_PROVIDER_DISCOUNT"));
        finalHTxt.setText(isUFX ? generalFunc.retrieveLangLBl("FINAL TOTAL", "LBL_FINAL_TOTAL_HINT") : generalFunc.retrieveLangLBl("Charges ", "LBL_TOTAL_EXTRA_CHARGES_TXT"));

        if (!isUFX) {
            currentchargeHTxt.setText(generalFunc.retrieveLangLBl("Trip Fare", "LBL_TRIP_FARE_TXT"));
            if (isTollEnable) {
                noteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TOLL_CHARGE_NOTE"));
            }
            if (isOtherChargesEnable) {
                noteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_OTHER_CHARGE_NOTE"));
            }
            if (isOtherChargesEnable && isTollEnable) {
                noteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TOLL_CHARGE_NOTE") + " " + generalFunc.retrieveLangLBl("", "LBL_AND_TXT") + " " + generalFunc.retrieveLangLBl("", "LBL_OTHER_CHARGE_NOTE"));
            }
        } else {
            noteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADDITIONAL_CHARGE_NOTE"));
            currentchargeHTxt.setText(generalFunc.retrieveLangLBl("Service Cost", "LBL_SERVICE_COST"));
        }
//        currentchargeHTxt.setText(generalFunc.retrieveLangLBl("Service Cost", "LBL_SERVICE_COST"));
        noteLbl.setText(generalFunc.retrieveLangLBl("", "LBL_NOTE") + ":-");
//        noteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADDITIONAL_CHARGE_NOTE"));
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADDITIONAL_CHARGES"));
        submitBtn.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_SUBMIT_TXT"));
        skipBtn.setText(generalFunc.retrieveLangLBl("", "LBL_SKIP_TXT"));

        CurrencySymbol = generalFunc.getJsonValue("CurrencySymbol", generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));
        isReverseCurrencyEnabled = generalFunc.getJsonValue("eReverseSymbolEnable", generalFunc.retrieveValue(Utils.USER_PROFILE_JSON)).equalsIgnoreCase("Yes");
        matrialfeeCurrancyTxt.setText(CurrencySymbol);
        matrialfeeCurrancyTxt1.setText(CurrencySymbol);
        miscfeeCurrancyTxt.setText(CurrencySymbol);
        miscfeeCurrancyTxt1.setText(CurrencySymbol);
        discountCurrancyTxt.setText(CurrencySymbol);
        discountCurrancyTxt1.setText(CurrencySymbol);
        tollfeeCurrancyTxt.setText(CurrencySymbol);
        tollfeeCurrancyTxt1.setText(CurrencySymbol);
        otherfeeCurrancyTxt.setText(CurrencySymbol);
        otherfeeCurrancyTxt1.setText(CurrencySymbol);

        if (isReverseCurrencyEnabled) {
            matrialfeeCurrancyTxt.setVisibility(View.GONE);
            miscfeeCurrancyTxt.setVisibility(View.GONE);
            discountCurrancyTxt.setVisibility(View.GONE);
            tollfeeCurrancyTxt.setVisibility(View.GONE);
            otherfeeCurrancyTxt.setVisibility(View.GONE);

            matrialfeeCurrancyTxt1.setVisibility(View.VISIBLE);
            miscfeeCurrancyTxt1.setVisibility(View.VISIBLE);
            discountCurrancyTxt1.setVisibility(View.VISIBLE);
            tollfeeCurrancyTxt1.setVisibility(View.VISIBLE);
            otherfeeCurrancyTxt1.setVisibility(View.VISIBLE);
        } else {
            matrialfeeCurrancyTxt.setVisibility(View.VISIBLE);
            miscfeeCurrancyTxt.setVisibility(View.VISIBLE);
            discountCurrancyTxt.setVisibility(View.VISIBLE);
            tollfeeCurrancyTxt.setVisibility(View.VISIBLE);
            otherfeeCurrancyTxt.setVisibility(View.VISIBLE);

            matrialfeeCurrancyTxt1.setVisibility(View.GONE);
            miscfeeCurrancyTxt1.setVisibility(View.GONE);
            discountCurrancyTxt1.setVisibility(View.GONE);
            tollfeeCurrancyTxt1.setVisibility(View.GONE);
            otherfeeCurrancyTxt1.setVisibility(View.GONE);
        }

        currencetprice = data.get("TotalFareUberXValue");
        currentchargeVTxt.setText(generalFunc.convertNumberWithRTL(data.get("TotalFareUberX")));
    }


    private void calculateData(String s, MTextView finalvalTxt) {
        try {
            finaltotal = 0.00;
            finaltotal = GeneralFunctions.parseDoubleValue(0, currencetprice) + GeneralFunctions.parseDoubleValue(0, additonallist.get(0).toString()) + GeneralFunctions.parseDoubleValue(0, additonallist.get(1).toString()) - GeneralFunctions.parseDoubleValue(0, additonallist.get(2).toString());
//            if (finaltotal < 0) {
//                finalvalTxt.setText(CurrencySymbol + " " +Utils.getText(discountVTxt));
//                return;
//            }
//            finalvalTxt.setText((isReverseCurrencyEnabled?"":CurrencySymbol) + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(finaltotal))+(isReverseCurrencyEnabled?CurrencySymbol:"'"));


            finalvalTxt.setText(new AppFunctions(getActContext()).formatNumAsPerCurrency(generalFunc, " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(finaltotal)), CurrencySymbol, true));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setTripEnd(boolean isSkip, String finalCode) {


        if (isSkip) {
            callTripEnd(isSkip, finalCode);
        } else if (additonallist.get(0).equals(0.0) &&
                additonallist.get(1).equals(0.0) &&
                additonallist.get(2).equals(0.0)) {
            validationCheck(isSkip, finalCode);
        } else {
            callTripEnd(isSkip, finalCode);
        }
    }

    BottomSheetDialog enterOTpdialog;
    String OTP = "";
    MTextView retryTxtArea;

    public void setCancelable(Dialog dialogview, boolean cancelable) {
        final Dialog dialog = dialogview;
        View touchOutsideView = dialog.getWindow().getDecorView().findViewById(R.id.touch_outside);
        View bottomSheetView = dialog.getWindow().getDecorView().findViewById(R.id.design_bottom_sheet);

        if (cancelable) {
            touchOutsideView.setOnClickListener(v -> {
                if (dialog.isShowing()) {
                    dialog.cancel();
                }
            });
            BottomSheetBehavior.from(bottomSheetView).setHideable(true);
        } else {
            touchOutsideView.setOnClickListener(null);
            BottomSheetBehavior.from(bottomSheetView).setHideable(false);
        }
    }


    private void validationCheck(boolean isSkip, String finalCode) {

        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            generateAlert.closeAlertBox();
            if (btn_id == 1) {
                callTripEnd(isSkip, finalCode);
            } else {
                generateAlert.closeAlertBox();
            }
        });
        generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.retrieveLangLBl("", "LBL_SURE_ADDITIONAL_CHARGES_EMPTY_TXT")));
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("Ok", "LBL_BTN_YES_TXT"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("cANCEL", "LBL_CANCEL_TXT"));
        generateAlert.showAlertBox();


    }

    public void callTripEnd(boolean isSkip, String finalCode) {


        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "ProcessEndTrip");
        parameters.put("TripId", data.get("iTripId"));
        parameters.put("DriverId", generalFunc.getMemberId());
        parameters.put("isUpdateAddress", "No");
        if (isUFX) {
            parameters.put("fMaterialFee", isSkip ? "" : additonallist.get(0).toString());
            parameters.put("fMiscFee", isSkip ? "" : additonallist.get(1).toString());
            parameters.put("fDriverDiscount", isSkip ? "" : additonallist.get(2).toString());
        } else if (!isUFX) {
            parameters.put("fTollPrice", isSkip ? "" : additonallist.get(0).toString());
            parameters.put("fOtherCharges", isSkip ? "" : additonallist.get(1).toString());
            parameters.put("fDriverDiscount", isSkip ? "" : additonallist.get(2).toString());
        }
        parameters.put("PassengerId", data.get("PassengerId"));
        parameters.put("iTripTimeId", data.get("iTripTimeId"));
        parameters.put("isUpdateAddress", "No");

        if (GetLocationUpdates.getInstance() != null) {
            Location lastLocation = GetLocationUpdates.getInstance().getLastLocation();
            if (lastLocation != null) {
                parameters.put("dest_lat", "" + lastLocation.getLatitude());
                parameters.put("dest_lon", "" + lastLocation.getLongitude());
            }
        }

        if (Utils.checkText(finalCode)) {
            parameters.put("vConfirmationCode", finalCode);
        }
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> endTripResponse(responseString));
        exeWebServer.execute();
    }


    private void endTripResponse(String responseString) {

        if (responseString != null && !responseString.equals("")) {

            boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

            if (isDataAvail == true) {
                MyApp.getInstance().restartWithGetDataApp();

            } else {
                generalFunc.showGeneralMessage("",
                        generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
            }
        }
    }

    @Override
    public void onTaskRun() {
        getApprovalStatus();
    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == skipBtn.getId()) {
                setTripEnd(i == skipBtn.getId(), "");
            } else if (i == submitBtn.getId()) {
                GenerateAlertBox alertBox = new GenerateAlertBox(getActContext());
                alertBox.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_ARE_YOU_SURE_YOU_WANT_TO_PROCEED_WITH_ADDITIONAL_CHARGES"));
                alertBox.setPositiveBtn(generalFunc.retrieveLangLBl("Submit", "LBL_BTN_SUBMIT_TXT"));
                alertBox.setNegativeBtn(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"));
                alertBox.setCancelable(false);
                alertBox.setBtnClickList(btn_id -> {
                    alertBox.closeAlertBox();
                    if (btn_id == 1) {
                        if (generalFunc.retrieveValue("ENABLE_VERIFY_ADDITIONAL_CHARGE_FLOW").equalsIgnoreCase("Yes") && isUFX) {

                            if (generalFunc.retrieveValue("ADDITIONAL_CHARGE_VERIFICATION_METHOD").equalsIgnoreCase("Approval")) {
                                openVerificationDialog();
                                sendChargeVerificationCode("Yes");
                            } else {
                                sendChargeVerificationCode("");
                            }

                        } else if (generalFunc.retrieveValue("ENABLE_MANUAL_TOLL_FEATURE").equalsIgnoreCase("Yes") || generalFunc.retrieveValue("ENABLE_OTHER_CHARGES_FEATURE").equalsIgnoreCase("Yes") && !isUFX) {

                            if (generalFunc.retrieveValue("ENABLE_MANUAL_TOLL_VERIFICATION_METHOD").equalsIgnoreCase("Approval")) {
                                openVerificationDialog();
                                sendChargeVerificationCode("Yes");
                            } else {
                                sendChargeVerificationCode("");
                            }

                        } else {
                            setTripEnd(i == skipBtn.getId(), "");
                        }
                    }
                });
                alertBox.showAlertBox();
            }
        }


    }

    private void endTollResponse(String responseString) {

        if (responseString != null && !responseString.equals("")) {

            boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

            if (isDataAvail == true) {

                if (enterOTpdialog == null || (enterOTpdialog != null && !enterOTpdialog.isShowing())) {
//                    showOTPdialog(getActContext());

                }
                showTimer();

            } else {
                generalFunc.showGeneralMessage("",
                        generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
            }
        }
    }

    CountDownTimer countDnTimer;
    boolean isProcessRunning = false;

    public void showTimer() {
        countDnTimer = new CountDownTimer(resendSecInMilliseconds, 1000) {
            @Override
            public void onTick(long milliseconds) {
                isProcessRunning = true;
                setTime(milliseconds);
            }

            @Override
            public void onFinish() {
                isProcessRunning = false;
                // this function will be called when the timecount is finished
                retryTxtArea.setText(generalFunc.retrieveLangLBl("", "LBL_RESEND_OTP_TXT"));
                removecountDownTimer();
            }
        }.start();

    }

    private void setTime(long milliseconds) {
        int minutes = (int) (milliseconds / 1000) / 60;
        int seconds = (int) (milliseconds / 1000) % 60;

        String formattedTxt = String.format("%02d:%02d", minutes, seconds);
        retryTxtArea.setText(formattedTxt);

    }

    private void removecountDownTimer() {

        if (countDnTimer != null) {
            countDnTimer.cancel();
            countDnTimer = null;
            isProcessRunning = false;
        }

    }

    public void openVerificationDialog() {
        if (dialog_verify != null) {
            dialog_verify.dismiss();
            dialog_verify = null;
        }
        dialog_verify = new Dialog(getActContext(), R.style.ImageSourceDialogStyle);
        dialog_verify.setContentView(R.layout.verify_charges_layout);
        MTextView titleTxt = (MTextView) dialog_verify.findViewById(R.id.titleTxt);
        MTextView cancelTxt = (MTextView) dialog_verify.findViewById(R.id.cancelTxt);
        MTextView verifyChargesNote = (MTextView) dialog_verify.findViewById(R.id.verifyChargesNote);
        OtpView otp_view = (OtpView) dialog_verify.findViewById(R.id.otp_view);
        ProgressBar LoadingProgressBar = (ProgressBar) dialog_verify.findViewById(R.id.LoadingProgressBar);
        MButton btn_type2 = ((MaterialRippleLayout) dialog_verify.findViewById(R.id.btn_type2)).getChildView();

        if (generalFunc.isRTLmode()) {
            otp_view.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }

        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_DONE"));

        if ((generalFunc.retrieveValue("ADDITIONAL_CHARGE_VERIFICATION_METHOD").equalsIgnoreCase("Approval") && isUFX) || (generalFunc.retrieveValue("ENABLE_MANUAL_TOLL_VERIFICATION_METHOD").equalsIgnoreCase("Approval") && !isUFX)) {

            titleTxt.setText(generalFunc.retrieveLangLBl("", isUFX ? "LBL_VERIFY_CHARGES_TITLE_TXT" : "LBL_VERIFICATION"));
            verifyChargesNote.setText(generalFunc.retrieveLangLBl("", /*isUFX?*/"LBL_VERIFY_CHARGES_NOTE_TXT"/*:"LBL_CHARGES_VERIFY_NOTE"*/));
            btn_type2.setVisibility(View.GONE);
            otp_view.setVisibility(View.GONE);
            LoadingProgressBar.setVisibility(View.VISIBLE);

        } else {

            titleTxt.setText(generalFunc.retrieveLangLBl("", isUFX ? "LBL_VERIFY_CHARGES_TITLE_TXT" : "LBL_VERIFICATION"));
            verifyChargesNote.setText(generalFunc.retrieveLangLBl("", isUFX ? "LBL_VERIFY_CHARGES_BY_USER_NOTE_TXT" : "LBL_CHARGES_VERIFY_NOTE"));
            btn_type2.setVisibility(View.VISIBLE);
            LoadingProgressBar.setVisibility(View.GONE);
        }
        cancelTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));

        cancelTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*  if ((generalFunc.retrieveValue("ADDITIONAL_CHARGE_VERIFICATION_METHOD").equalsIgnoreCase("Approval") && isUFX) || (generalFunc.retrieveValue("ENABLE_MANUAL_TOLL_VERIFICATION_METHOD").equalsIgnoreCase("Approval")&& !isUFX) || (generalFunc.retrieveValue("ENABLE_MANUAL_TOLL_VERIFICATION_METHOD").equalsIgnoreCase("Verification")&& !isUFX)) {*/
                sendChargeVerificationCode("No");
               /* } else {
                    dialog_verify.dismiss();
                }*/
            }
        });

        btn_type2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String finalCode = Utils.getText(otp_view);
               /* boolean isCodeEntered = Utils.checkText(finalCode) ?
                        ((vConfirmationCode.equalsIgnoreCase(finalCode)) ? true
                                : Utils.setErrorFields(otp_view, error_verification_code)) : Utils.setErrorFields(otp_view, required_str);*/
                boolean isCodeEntered = Utils.checkText(finalCode) ?
                        ((vConfirmationCode.equalsIgnoreCase(finalCode)) ? true
                                : false) : false;

                if (isCodeEntered) {
                    setTripEnd(false, finalCode);
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_VERIFICATION_CODE_INVALID"));
                }
            }
        });

        otp_view.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                btn_type2.performClick();
            }
        });
        otp_view.setCursorVisible(true);
        dialog_verify.setCanceledOnTouchOutside((generalFunc.retrieveValue("ADDITIONAL_CHARGE_VERIFICATION_METHOD").equalsIgnoreCase("Approval") && isUFX) || (generalFunc.retrieveValue("ENABLE_MANUAL_TOLL_VERIFICATION_METHOD").equalsIgnoreCase("Approval") && !isUFX) ? true : false);
        Window window = dialog_verify.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog_verify.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        if (generalFunc.isRTLmode()) {
            dialog_verify.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog_verify.show();
    }

    private void sendChargeVerificationCode(String eApproveRequestSentByDriver) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "sendChargeVerificationCode");
        parameters.put("TripId", data.get("iTripId"));


        if (/*(isTollEnable || isOtherChargesEnable) && */!isUFX) {
            parameters.put("fTollPrice", isTollEnable ? additonallist.get(0).toString() : "");
            parameters.put("fOtherCharges", isOtherChargesEnable ? additonallist.get(1).toString() : "");
            parameters.put("fDriverDiscount", additonallist.get(2).toString());
            parameters.put("eType", Utils.CabGeneralType_Ride);

        } else {
            parameters.put("fMaterialFee", additonallist.get(0).toString());
            parameters.put("fMiscFee", additonallist.get(1).toString());
            parameters.put("fDriverDiscount", additonallist.get(2).toString());
        }


        parameters.put("DriverId", generalFunc.getMemberId());
        parameters.put("PassengerId", data.get("PassengerId"));
        parameters.put("iTripTimeId", data.get("iTripTimeId"));
        parameters.put("UserType", Utils.app_type);
        if (Utils.checkText(eApproveRequestSentByDriver)) {
            parameters.put("eApproveRequestSentByDriver", eApproveRequestSentByDriver);
        }
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), eApproveRequestSentByDriver.equalsIgnoreCase("Yes") ? false : true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> verificationResponse(responseString));
        exeWebServer.execute();
    }


    private void verificationResponse(String responseString) {

        if (responseString != null && !responseString.equals("")) {

            boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

            String action = generalFunc.getJsonValue(Utils.action_str, responseString);
            String message_str = generalFunc.getJsonValue(Utils.message_str, responseString);

            if (action.equals("1")) {

                if (message_str.equalsIgnoreCase("LBL_NO_FURTHER_PROCESS")) {
                    removeBackTaskAndDismissDialog();
                    setTripEnd(false, "");
                    return;
                }
                vConfirmationCode = generalFunc.getJsonValue("vConfirmationCode", responseString);
                eApproveRequestSentByDriver = generalFunc.getJsonValue("eApproveRequestSentByDriver", responseString);

                if ((generalFunc.retrieveValue("ADDITIONAL_CHARGE_VERIFICATION_METHOD").equalsIgnoreCase("Approval") && isUFX) || (generalFunc.retrieveValue("ENABLE_MANUAL_TOLL_VERIFICATION_METHOD").equalsIgnoreCase("Approval") && !isUFX)) {
                    if (eApproveRequestSentByDriver.equalsIgnoreCase("No")) {

                        removeBackTaskAndDismissDialog();
                    } else {
                        if (updateApprovalStatusTask == null) {
                            updateApprovalStatusTask = new UpdateFrequentTask(APPROVAL_STATUS_UPDATE_INTERVAL);
                            updateApprovalStatusTask.setTaskRunListener(AdditionalChargeActivity.this);
                            updateApprovalStatusTask.startRepeatingTask();
                        }

                    }

                    generalFunc.showMessage(findViewById(R.id.dataArea),
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));

                } else if (((generalFunc.retrieveValue("ADDITIONAL_CHARGE_VERIFICATION_METHOD").equalsIgnoreCase("Verification") && isUFX) || (generalFunc.retrieveValue("ENABLE_MANUAL_TOLL_VERIFICATION_METHOD").equalsIgnoreCase("Verification") && !isUFX)) && eApproveRequestSentByDriver.equalsIgnoreCase("No")) {
                    removeBackTaskAndDismissDialog();
                    generalFunc.showMessage(findViewById(R.id.dataArea),
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                } else {

                   /* if((isTollEnable || isOtherChargesEnable) && !isUFX)
                    {

                        if (enterOTpdialog==null || (enterOTpdialog!=null && !enterOTpdialog.isShowing())){
                            showOTPdialog(eApproveRequestSentByDriver);

                        }
                        showTimer();

                    }else
                    {*/
                    openVerificationDialog();
                    // }
                }

            } else if (action.equals("2")) {
                removeBackTaskAndDismissDialog();

                final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                generateAlert.setCancelable(false);
                generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                    @Override
                    public void handleBtnClick(int btn_id) {
                        generateAlert.closeAlertBox();
                        setTripEnd(false, "");
                    }
                });
                generateAlert.setContentMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));

                generateAlert.showAlertBox();
            } else {
                removeBackTaskAndDismissDialog();
                generalFunc.showGeneralMessage("",
                        generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));

            }
        }
    }

    public void removeBackTaskAndDismissDialog() {
        if (updateApprovalStatusTask != null) {
            updateApprovalStatusTask.stopRepeatingTask();
            updateApprovalStatusTask = null;
        }

        if (dialog_verify != null) {
            dialog_verify.dismiss();
            dialog_verify = null;
        }

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }


    public void getApprovalStatus() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getApprovalStatus");
        parameters.put("TripId", data.get("iTripId"));
        parameters.put("DriverId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.app_type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), false, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {
            JSONObject responseStringObject = generalFunc.getJsonObject(responseString);

            if (responseStringObject != null && !responseStringObject.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseStringObject);

                if (isDataAvail == true) {
                    String eApproved = generalFunc.getJsonValue("eApproved", responseString);

                    if (Utils.checkText(eApproved)) {
                        if (updateApprovalStatusTask != null) {
                            updateApprovalStatusTask.stopRepeatingTask();
                            updateApprovalStatusTask = null;
                        }


                        if (eApproved.equalsIgnoreCase("Yes")) {
                            generalFunc.showMessage(findViewById(R.id.dataArea),
                                    generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));

                            setTripEnd(false, "");
                        } else {
                            final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                            generateAlert.setCancelable(false);
                            generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                                @Override
                                public void handleBtnClick(int btn_id) {
                                    generateAlert.closeAlertBox();
                                    if (dialog_verify != null) {
                                        dialog_verify.dismiss();
                                        dialog_verify = null;
                                    }
                                }
                            });
                            generateAlert.setContentMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
                            generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));

                            generateAlert.showAlertBox();
                        }

                    }

                }
            } else {
//                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }
}
