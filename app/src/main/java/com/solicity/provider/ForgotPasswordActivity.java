package com.solicity.provider;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.countryview.view.CountryPicker;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.InternetConnection;
import com.general.files.MyApp;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MTextView;
import com.view.editBox.MaterialEditText;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import android.content.Intent;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.RelativeLayout;
import com.general.files.SetOnTouchList;
import com.general.files.StartActProcess;
import com.squareup.picasso.Picasso;

public class ForgotPasswordActivity extends AppCompatActivity implements GenerateAlertBox.HandleAlertBtnClick {


    MaterialEditText emailBox;
    GeneralFunctions generalFunc;
    public JSONObject userProfileJson;
    String required_str = "";
    String error_email_str = "";
    MTextView forgotpasswordHint, forgotpasswordNote;

    int submitBtnId;
    InternetConnection intCheck;
    LinearLayout imgClose;
    MTextView btnTxt;
    LinearLayout btnArea;
    ImageView btnImg;
    boolean isEmail = true;
    RelativeLayout yearSelectArea;
    static ImageView countryimage;
    ImageView countrydropimage, countrydropimagerror;
    static MaterialEditText countryBox;
    static String vCountryCode = "";
    static String vSImage = "";
    static boolean isCountrySelected = false;
    static String vPhoneCode = "";
    CountryPicker countryPicker;
    Locale locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initView();
        removeInput();
        setLabel();

    }

    public Context getActContext() {
        return ForgotPasswordActivity.this;
    }

    private void initView() {
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        locale = new Locale(generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));
        intCheck = new InternetConnection(this);
        emailBox = (MaterialEditText) findViewById(R.id.emailBox);
        forgotpasswordHint = (MTextView) findViewById(R.id.forgotpasswordHint);
        forgotpasswordNote = (MTextView) findViewById(R.id.forgotpasswordNote);
        btnArea = (LinearLayout) findViewById(R.id.btnArea);

        yearSelectArea = (RelativeLayout) findViewById(R.id.yearSelectArea);
        countryimage = findViewById(R.id.countryimage);
        countryBox = (MaterialEditText) findViewById(R.id.countryBox);

        countrydropimage = (ImageView) findViewById(R.id.countrydropimage);
        countrydropimagerror = (ImageView) findViewById(R.id.countrydropimagerror);
        vCountryCode = generalFunc.retrieveValue(Utils.DefaultCountryCode);
        vPhoneCode = generalFunc.retrieveValue(Utils.DefaultPhoneCode);
        vSImage = generalFunc.retrieveValue(Utils.DefaultCountryImage);

        if (!vSImage.equals("")) {
            Picasso.get().load(vSImage).into(countryimage);
        }
        int paddingValStart = (int) getResources().getDimension(R.dimen._35sdp);
        int paddingValEnd = (int) getResources().getDimension(R.dimen._12sdp);
        if (generalFunc.isRTLmode()) {
            countryBox.setPaddings(paddingValEnd, 0, paddingValStart, 0);

        } else {
            countryBox.setPaddings(paddingValStart, 0, paddingValEnd, 0);

        }

        if (!vPhoneCode.equalsIgnoreCase("")) {
            countryBox.setText("+" + generalFunc.convertNumberWithRTL(vPhoneCode));
            isCountrySelected = true;
        }

        btnTxt = (MTextView) findViewById(R.id.btnTxt);

        imgClose = (LinearLayout) findViewById(R.id.imgClose);
        btnImg = (ImageView) findViewById(R.id.btnImg);
        imgClose.setOnClickListener(new setOnClickList());


        submitBtnId = Utils.generateViewId();


        btnArea.setOnClickListener(new setOnClickList());

        emailBox.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT);
        emailBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        if (generalFunc.isRTLmode()) {
            btnImg.setRotation(180);
            btnArea.setBackground(getActContext().getResources().getDrawable(R.drawable.login_border_rtl));
        }
    }

    public void removeInput() {
        Utils.removeInput(countryBox);

        if (generalFunc.retrieveValue("showCountryList").equalsIgnoreCase("Yes")) {
            // countrydropimage.setVisibility(View.GONE);
            countryBox.setOnTouchListener(new SetOnTouchList());

            countryBox.setOnClickListener(new setOnClickList());
        }
    }

    private void setLabel() {
        emailBox.setBothText(generalFunc.retrieveLangLBl("", generalFunc.retrieveValue("ENABLE_PHONE_LOGIN_VIA_COUNTRY_SELECTION_METHOD").equalsIgnoreCase("Yes")?"LBL_PHONE_EMAIL":"LBL_EMAIL_LBL_TXT"));      //  required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT");
        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD");
     //   required_str = generalFunc.retrieveLangLBl("", "LBL_LOGIN");
        error_email_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_EMAIL_ERROR");
        forgotpasswordHint.setText(generalFunc.retrieveLangLBl("", "LBL_FORGET_YOUR_PASS_TXT"));
        forgotpasswordNote.setText(generalFunc.retrieveLangLBl("", "LBL_FORGET_PASS_NOTE"));
        btnTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SUBMIT_TXT"));

        countryBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_COUNTRY_TXT"));
        emailBox.getLabelFocusAnimator().start();
        countryBox.getLabelFocusAnimator().start();

        if(generalFunc.retrieveValue("ENABLE_PHONE_LOGIN_VIA_COUNTRY_SELECTION_METHOD").equalsIgnoreCase("Yes")){
            emailBox.addTextChangedListener(new TextWatcher() {
                String regexStr = "^[0-9]*$";

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (Utils.checkText(s.toString().trim())&&s.toString().trim().matches(regexStr)) {
                        emailBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_MOBILE_NUMBER_HEADER_TXT"));
                        isEmail = false;
                        yearSelectArea.setVisibility(View.VISIBLE);
                    } else {
                        emailBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_EMAIL_LBL_TXT"));
                        isEmail = true;
                        yearSelectArea.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    @Override
    public void handleBtnClick(int btn_id) {
        Utils.hideKeyboard(getActContext());
        if (btn_id == 1) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.SELECT_COUNTRY_REQ_CODE && data != null) {

            vCountryCode = data.getStringExtra("vCountryCode");
            vPhoneCode = data.getStringExtra("vPhoneCode");
            isCountrySelected = true;
            vSImage = data.getStringExtra("vSImage");


            Picasso.get().load(vSImage).into(countryimage);
            GeneralFunctions generalFunctions = new GeneralFunctions(MyApp.getInstance().getCurrentAct());
            countryBox.setText("+" + generalFunctions.convertNumberWithRTL(vPhoneCode));
        }

    }

    public void checkValues() {
        boolean countryEntered = false;
        isEmail = true;
        boolean emailEntered = Utils.checkText(emailBox.getText().toString().replace("+", "")) ? true
                : Utils.setErrorFields(emailBox, required_str);


        String regexStr = "^[0-9]*$";
        if(generalFunc.retrieveValue("ENABLE_PHONE_LOGIN_VIA_COUNTRY_SELECTION_METHOD").equalsIgnoreCase("Yes") && yearSelectArea.getVisibility()== View.VISIBLE && emailBox.getText().toString().trim().replace("+", "").matches(regexStr)) {

                isEmail = false;
                if (emailEntered) {
                    emailEntered = emailBox.length() >= 3 ? true : Utils.setErrorFields(emailBox, generalFunc.retrieveLangLBl("", "LBL_INVALID_MOBILE_NO"));
                }

                if (generalFunc.retrieveValue("ENABLE_PHONE_LOGIN_VIA_COUNTRY_SELECTION_METHOD").equalsIgnoreCase("Yes") && yearSelectArea.getVisibility() == View.VISIBLE) {
                    countryEntered = isCountrySelected ? true : false;
                    if (countryBox.getText().length() == 0) {
                        countryEntered = false;
                    }

                    if (generalFunc.retrieveValue("showCountryList").equalsIgnoreCase("Yes")) {

                        if (!countryEntered) {
                            Utils.setErrorFields(countryBox, required_str);
                            countrydropimagerror.setVisibility(View.VISIBLE);
                            countrydropimage.setVisibility(View.GONE);
                        } else {
                            countrydropimage.setVisibility(View.VISIBLE);
                            countrydropimagerror.setVisibility(View.GONE);

                        }
                    }
                }

                if (emailEntered == false) {
                    return;
                }
                forgptPasswordCall();

        }else {
            isEmail=true;
            emailEntered = Utils.checkText(emailBox) ?
                    (generalFunc.isEmailValid(Utils.getText(emailBox)) ? true : Utils.setErrorFields(emailBox, error_email_str))
                    : Utils.setErrorFields(emailBox, required_str);

            if (emailEntered == false) {
                return;
            }
            forgptPasswordCall();
        }

    }

    public void forgptPasswordCall() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "requestResetPassword");
        parameters.put("vEmail", Utils.getText(emailBox));
        parameters.put("UserType", Utils.app_type);
        if(generalFunc.retrieveValue("ENABLE_PHONE_LOGIN_VIA_COUNTRY_SELECTION_METHOD").equalsIgnoreCase("Yes")) {
            parameters.put("isEmail", isEmail ? "Yes" : "No");

            if (!isEmail) {
                parameters.put("PhoneCode", vPhoneCode);
                parameters.put("CountryCode", vCountryCode);
            }
        }
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail) {
                    emailBox.setText("");
                    GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));
                    generateAlert.setBtnClickList(ForgotPasswordActivity.this);
                    generateAlert.showAlertBox();
                }else {
                    generalFunc.showGeneralMessage("",generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
                

            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(getActContext());
            int i = view.getId();
            if (i == R.id.countryBox) {
               // new StartActProcess(getActContext()).startActForResult(SelectCountryActivity.class, Utils.SELECT_COUNTRY_REQ_CODE);
                if (countryPicker == null) {
                    countryPicker = new CountryPicker.Builder(getActContext()).showingDialCode(true)
                            .setLocale(locale).showingFlag(true)
                            .enablingSearch(true)
                            //.setCountries(items_list)
                            .setCountrySelectionListener(country -> setData(country.getCode(), country.getDialCode(), country.getFlagName()))
                            .build();
                }
                countryPicker.show(getActContext());
            } else if (i == btnArea.getId()) {

                if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {

                    generalFunc.showMessage(emailBox, generalFunc.retrieveLangLBl("No Internet Connection", "LBL_NO_INTERNET_TXT"));
                } else {
                    checkValues();

                }

            } else if (i == imgClose.getId()) {
                onBackPressed();

            }

        }
    }
    public void setData(String vCountryCode, String vPhoneCode, String vSImage) {
        this.vCountryCode = vCountryCode;
        this.vPhoneCode = vPhoneCode;
        isCountrySelected = true;
        this.vSImage = vSImage;

        Picasso.get().load(vSImage).into(countryimage);

        GeneralFunctions generalFunctions = new GeneralFunctions(MyApp.getInstance().getCurrentAct());
        countryBox.setText("+" + generalFunctions.convertNumberWithRTL(vPhoneCode));
    }

}
