package com.solicity.provider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.autofit.et.lib.AutoFitEditText;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.general.files.TrendyDialog;
import com.google.android.material.snackbar.Snackbar;
import com.utils.Utils;
import com.view.ErrorView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import java.util.HashMap;

public class WithdrawBalanceActivity extends AppCompatActivity {

    ErrorView errorView;

    MTextView titleTxt;
    MTextView withdrawTitle;
    RelativeLayout readFAQ;
    RelativeLayout support;
    RelativeLayout buttonlayout;

    ImageView readFAQimage;
    ImageView supportimage;
    ImageView helipnonwithdrawamount;
    ImageView helpwithdrawamount;
    ImageView addaccountImage;
    MTextView accountdetailText;
    ImageView backImgView;
    public GeneralFunctions generalFunc;
    public String userProfileJson;
    private MButton changeAccountDetails;
    private MButton withdrawnow;

    String WITHDRAWABLE_AMOUNT;
    String NON_WITHDRAWABLE_AMOUNT;
    String ACCOUNT_NO;
    String MemberBalance;

    String ORIG_WITHDRAWABLE_AMOUNT = "";
    String ORIG_NON_WITHDRAWABLE_AMOUNT = "";
    String vAccountNumber = "";

    MTextView walletamountTxt, withdrawAmount, nonwithdrawAmount, accountHTxt;
    MTextView needhelptext;
    MTextView withdrawAmountTitle;
    MTextView nonwithdrawAmountTitle;


    AutoFitEditText autofitEditText;
    boolean accountDetailsAdded;
    LinearLayout withdrawBalArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_balance);
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        initView();
        setLables();

    }

    private void initView() {


        errorView = (ErrorView) findViewById(R.id.errorView);
        titleTxt = findViewById(R.id.titleTxt);
        withdrawTitle = findViewById(R.id.withdrawTitle);
        support = findViewById(R.id.support);
        readFAQ = findViewById(R.id.readFAQ);
        withdrawBalArea = findViewById(R.id.withdrawBalArea);
        if (!generalFunc.getJsonValue("ENABLE_WALLET_WITHDRAWAL_REQUEST_RESTRICTION", userProfileJson).equalsIgnoreCase("Yes")) {
            withdrawBalArea.setVisibility(View.GONE);

        }

        supportimage = (ImageView) findViewById(R.id.supportimage);
        readFAQimage = (ImageView) findViewById(R.id.readFAQimage);
        readFAQimage = (ImageView) findViewById(R.id.readFAQimage);
        helipnonwithdrawamount = (ImageView) findViewById(R.id.helipnonwithdrawamount);
        helpwithdrawamount = (ImageView) findViewById(R.id.helpwithdrawamount);
        addaccountImage = (ImageView) findViewById(R.id.addaccountImage);

        backImgView = findViewById(R.id.backImgView);
        nonwithdrawAmountTitle = findViewById(R.id.nonwithdrawAmountTitle);
        withdrawAmountTitle = findViewById(R.id.withdrawAmountTitle);
        accountdetailText = findViewById(R.id.accountdetailText);
        buttonlayout = findViewById(R.id.buttonlayout);


        walletamountTxt = (MTextView) findViewById(R.id.walletamountTxt);
        withdrawAmount = (MTextView) findViewById(R.id.withdrawAmount);
        nonwithdrawAmount = (MTextView) findViewById(R.id.nonwithdrawAmount);
        accountHTxt = (MTextView) findViewById(R.id.accountHTxt);
        needhelptext = (MTextView) findViewById(R.id.needhelptext);


        backImgView.setOnClickListener(new setOnClickList());
        readFAQ.setOnClickListener(new setOnClickList());
        support.setOnClickListener(new setOnClickList());

        // changeAccountDetails = ((MaterialRippleLayout) findViewById(R.id.changeAccountDetails)).getChildView();
        buttonlayout.setId(Utils.generateViewId());
        buttonlayout.setOnClickListener(new setOnClickList());
        withdrawnow = ((MaterialRippleLayout) findViewById(R.id.withdrawnow)).getChildView();
        withdrawnow.setId(Utils.generateViewId());
        withdrawnow.setOnClickListener(new setOnClickList());
        autofitEditText = (AutoFitEditText) findViewById(R.id.autofitEditText);


        WITHDRAWABLE_AMOUNT = getIntent().getStringExtra("WITHDRAWABLE_AMOUNT");
        NON_WITHDRAWABLE_AMOUNT = getIntent().getStringExtra("NON_WITHDRAWABLE_AMOUNT");
        ACCOUNT_NO = getIntent().getStringExtra("ACCOUNT_NO");
        MemberBalance = getIntent().getStringExtra("MemberBalance");

        ORIG_WITHDRAWABLE_AMOUNT = getIntent().getStringExtra("ORIG_WITHDRAWABLE_AMOUNT");
        ORIG_NON_WITHDRAWABLE_AMOUNT = getIntent().getStringExtra("ORIG_NON_WITHDRAWABLE_AMOUNT");
        vAccountNumber = getIntent().getStringExtra("vAccountNumber");


        walletamountTxt.setText(MemberBalance);
        withdrawAmount.setText(WITHDRAWABLE_AMOUNT);
        nonwithdrawAmount.setText(NON_WITHDRAWABLE_AMOUNT);
        accountHTxt.setText(ACCOUNT_NO);


        LBL_RETRY_TXT = generalFunc.retrieveLangLBl("Retry", "LBL_RETRY_TXT");
        LBL_CANCEL_TXT = generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT");
        LBL_BTN_OK_TXT = generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT");
        LBL_TRY_AGAIN_TXT = generalFunc.retrieveLangLBl("Please try again.", "LBL_TRY_AGAIN_TXT");


    }

    private void setLables() {

        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_WITHDRAW_REQUEST"));
        withdrawTitle.setText(generalFunc.retrieveLangLBl("", "LBL_WITHDRAW_AMT"));
        withdrawnow.setText(generalFunc.retrieveLangLBl("", "LBL_SEND_REQUEST"));
        needhelptext.setText(generalFunc.retrieveLangLBl("", "LBL_NEED_HELP"));
        //readFAQ.setText(generalFunc.retrieveLangLBl("", "LBL_READ_FAQ"));
        //support.setText(generalFunc.retrieveLangLBl("", "LBL_SUPPORT"));

        supportimage.setBackground(getRoundBG("#239707", 100, "#CCCACA"));
        readFAQimage.setBackground(getRoundBG("#ffa60a", 100, "#CCCACA"));
        //  helipnonwithdrawamount.setBackground(getRoundBG("#EBEAEA",50));
        autofitEditText.setBackground(getRoundBG("#ffffff", 8, "#CCCACA"));
        autofitEditText.setHint("0.00");
        // buttonlayout.setBackground(getRoundBG(getResources().getColor(R.color.appThemeColor_1),100));

        accountdetailText.setText(generalFunc.retrieveLangLBl("", "LBL_ACCOUNT_NUMBER"));


        withdrawAmountTitle.setText(generalFunc.retrieveLangLBl("", "LBL_WITHDRAWABLE_BAL") + " ");
        ((MTextView) findViewById(R.id.yourBalTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_USER_BALANCE"));

        nonwithdrawAmountTitle.setText(generalFunc.retrieveLangLBl("", "LBL_NON_WITHDRAWABLE_BAL") + " ");

        ImageSpan imageSpan = new ImageSpan(this, (R.drawable.ic_question_circle), ImageSpan.ALIGN_BASELINE); //Find your drawable.
        SpannableString spannableString = new SpannableString(nonwithdrawAmountTitle.getText()); //Set text of SpannableString from TextView
        spannableString.setSpan(imageSpan, nonwithdrawAmountTitle.getText().length() - 1, nonwithdrawAmountTitle.getText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //Add image at end of string


        //nonwithdrawAmountTitle.setOnClickListener(new setOnClickList());
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

                TrendyDialog customDialog = new TrendyDialog(WithdrawBalanceActivity.this);
                customDialog.setDetails("", generalFunc.retrieveLangLBl("", "LBL_NOTE_NON_WITHDRAWABLE_BAL"), LBL_BTN_OK_TXT, true, getActContext().getResources().getDrawable(R.drawable.ic_walletnonwithdraw));
                customDialog.showDialog();
                customDialog.setPositiveBtnClick(new com.general.files.Closure() {
                    @Override
                    public void exec() {

                    }
                });


            }
        }, nonwithdrawAmountTitle.getText().length() - 1, nonwithdrawAmountTitle.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        nonwithdrawAmountTitle.setText(spannableString); //Assign string to TextView (Use TextFormatted for Spannables)

        nonwithdrawAmountTitle.setMovementMethod(LinkMovementMethod.getInstance());


        ImageSpan imageSpan1 = new ImageSpan(this, (R.drawable.ic_question_circle), ImageSpan.ALIGN_BASELINE); //Find your drawable.
        SpannableString spannableString1 = new SpannableString(withdrawAmountTitle.getText()); //Set text of SpannableString from TextView
        spannableString1.setSpan(imageSpan1, withdrawAmountTitle.getText().length() - 1, withdrawAmountTitle.getText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //Add image at end of string


        //nonwithdrawAmountTitle.setOnClickListener(new setOnClickList());
        spannableString1.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

                TrendyDialog customDialog = new TrendyDialog(WithdrawBalanceActivity.this);
                customDialog.setDetails("", generalFunc.retrieveLangLBl("", "LBL_NOTE_WITHDRAWABLE_BAL"), LBL_BTN_OK_TXT, true, getActContext().getResources().getDrawable(R.drawable.ic_walletwithdraw));
                customDialog.showDialog();
                customDialog.setPositiveBtnClick(new com.general.files.Closure() {
                    @Override
                    public void exec() {

                    }
                });

            }
        }, withdrawAmountTitle.getText().length() - 1, withdrawAmountTitle.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        withdrawAmountTitle.setText(spannableString1); //Assign string to TextView (Use TextFormatted for Spannables)

        withdrawAmountTitle.setMovementMethod(LinkMovementMethod.getInstance());

        if (generalFunc.isRTLmode()) {
            withdrawAmountTitle.setTextDirection(View.TEXT_DIRECTION_RTL);
            nonwithdrawAmountTitle.setTextDirection(View.TEXT_DIRECTION_RTL);
        }


        if (vAccountNumber.equalsIgnoreCase("no")) {
            accountDetailsAdded = false;
            //changeAccountDetails.setText(generalFunc.retrieveLangLBl("", "LBL_ACTION_ADD "));
            // changeAccountDetails.setTextSize(getResources().getDimension(R.dimen._4ssp));
            addaccountImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_pic_add));
        } else {
            accountDetailsAdded = true;
            // changeAccountDetails.setText(generalFunc.retrieveLangLBl("", "LBL_CHANGE "));
            // changeAccountDetails.setTextSize(getResources().getDimension(R.dimen._4ssp));
            addaccountImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit));

        }

    }

    private GradientDrawable getRoundBG(String color, int radius, String stroke) {

        int strokeWidth = 2;
        int strokeColor = Color.parseColor(stroke);
        GradientDrawable gD = new GradientDrawable();
        gD.setColor(Color.parseColor(color));
        gD.setShape(GradientDrawable.RECTANGLE);
        gD.setCornerRadius(radius);
        gD.setStroke(strokeWidth, strokeColor);
        return gD;
    }

    public Context getActContext() {
        return WithdrawBalanceActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(WithdrawBalanceActivity.this);
            int i = view.getId();


            if (i == buttonlayout.getId()) {
                Bundle bn = new Bundle();
                bn.putString("from", "walletWithDraw");
                new StartActProcess(getActContext()).startActForResult(BankDetailActivity.class, bn, 123);
            }

            if (i == withdrawnow.getId()) {
                boolean AmountEntered = Utils.checkText(autofitEditText);
                if (AmountEntered) {
                    double amount = Double.parseDouble(Utils.getText(autofitEditText));
                    if (amount > 0) {
                        if (amount > Double.parseDouble(ORIG_WITHDRAWABLE_AMOUNT) && generalFunc.getJsonValue("ENABLE_WALLET_WITHDRAWAL_REQUEST_RESTRICTION", userProfileJson).equalsIgnoreCase("Yes")) {
                            Snackbar.make(autofitEditText, generalFunc.retrieveLangLBl("", "LBL_RESTRICT_WITHDRAW_AMT_NOTE"), Snackbar.LENGTH_SHORT).show();
                            autofitEditText.setBackground(getRoundBG("#ffffff", 8, "#F44336"));
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    autofitEditText.setBackground(getRoundBG("#ffffff", 8, "#CCCACA"));
                                }
                            }, 2000);
                        } else if (!accountDetailsAdded) {
                            generalFunc.showMessage(withdrawnow, generalFunc.retrieveLangLBl("", "LBL_ACCOUNT_NUM_MSG"));
                        } else {
                            withdrawlRequest(Utils.getText(autofitEditText));
                        }
                    } else {
                        Snackbar.make(autofitEditText, generalFunc.retrieveLangLBl("", "LBL_WITHDRAW_AMT_ERROR"), Snackbar.LENGTH_SHORT).show();
                        autofitEditText.setBackground(getRoundBG("#ffffff", 8, "#F44336"));
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                autofitEditText.setBackground(getRoundBG("#ffffff", 8, "#CCCACA"));
                            }
                        }, 2000);

                    }
                } else {
                    Snackbar.make(autofitEditText, generalFunc.retrieveLangLBl("", "LBL_WITHDRAW_AMT_MSG"), Snackbar.LENGTH_SHORT).show();
                    autofitEditText.setBackground(getRoundBG("#ffffff", 8, "#F44336"));
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            autofitEditText.setBackground(getRoundBG("#ffffff", 8, "#CCCACA"));
                        }
                    }, 2000);
                }
            }

            switch (i) {

                case R.id.readFAQ:
                    new StartActProcess(getActContext()).startAct(ContactUsActivity.class);

                    break;
                case R.id.support:
                    new StartActProcess(getActContext()).startAct(HelpActivity.class);

                    break;
                case R.id.backImgView:
                    WithdrawBalanceActivity.super.onBackPressed();
                    break;
                case R.id.nonwithdrawAmountTitle:
                    Toast.makeText(getActContext(), "Clicked", Toast.LENGTH_LONG).show();
                    break;

            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123 && data != null) {
            vAccountNumber = "Yes";
            accountHTxt.setText(data.getStringExtra("vAccountNumber"));
            accountDetailsAdded = true;
            // changeAccountDetails.setText(generalFunc.retrieveLangLBl("", "LBL_CHANGE "));
            // changeAccountDetails.setTextSize(getResources().getDimension(R.dimen._4ssp));
            addaccountImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit));

        }

    }

    GenerateAlertBox currentAlertBox;

    String LBL_BTN_OK_TXT, LBL_CANCEL_TXT, LBL_RETRY_TXT, LBL_TRY_AGAIN_TXT;

    public void withdrawlRequest(String amount) {


        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "createWithdrawlRequest");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.app_type);
        parameters.put("amount", amount);
        parameters.put("iServiceId", "0");


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {


            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                String message = generalFunc.getJsonValue("message", responseString);

                currentAlertBox = generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", message), "", LBL_BTN_OK_TXT, buttonId -> {
                    finish();
                });

            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

}
