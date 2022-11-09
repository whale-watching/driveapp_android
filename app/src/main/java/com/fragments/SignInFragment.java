package com.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.countryview.view.CountryPicker;
import com.solicity.provider.AppLoignRegisterActivity;
import com.solicity.provider.ContactUsActivity;
import com.solicity.provider.ForgotPasswordActivity;
import com.solicity.provider.R;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.OpenMainProfile;
import com.general.files.PasswordViewHideManager;
import com.general.files.SetUserData;
import com.general.files.StartActProcess;
import com.general.files.MyApp;
import com.solicity.provider.SelectCountryActivity;
import android.widget.RelativeLayout;
import android.util.Log;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.Editable;
import com.squareup.picasso.Picasso;
import com.general.files.SetOnTouchList;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.editBox.MaterialEditText;

import java.util.HashMap;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {


    MaterialEditText emailBox;
    MaterialEditText passwordBox;
    MButton btn_type2;

    AppLoignRegisterActivity appLoginAct;
    GeneralFunctions generalFunc;

    int submitBtnId;
    MTextView forgetPassTxt;

    View view;

    String required_str = "";
    String error_email_str = "";


    MTextView btnTxt, titleTxt;
    LinearLayout btnArea;
    ImageView btnImg;
    ImageView imgClose;

    MTextView signbootomHint;
    boolean isPassShow = false;

    boolean isBack = false;

    static MaterialEditText countryBox;
    static String vCountryCode = "";
    static String vPhoneCode = "";
    static String vSImage = "";
    static boolean isCountrySelected = false;
    static ImageView countryimage;
    RelativeLayout countryArea;
    ImageView countrydropimage, countrydropimagerror;
    CountryPicker countryPicker;
    Locale locale;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        appLoginAct = (AppLoignRegisterActivity) getActivity();
        generalFunc = appLoginAct.generalFunc;
        locale = new Locale(generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));
        countryArea = (RelativeLayout) view.findViewById(R.id.countryArea);
        countryBox = (MaterialEditText) view.findViewById(R.id.countryBox);
        countryimage = view.findViewById(R.id.countryimage);

        emailBox = (MaterialEditText) view.findViewById(R.id.emailBox);
        emailBox.setHelperTextAlwaysShown(true);
        passwordBox = (MaterialEditText) view.findViewById(R.id.passwordBox);
        forgetPassTxt = (MTextView) view.findViewById(R.id.forgetPassTxt);

        signbootomHint = (MTextView) view.findViewById(R.id.signbootomHint);

        passwordBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordBox.setTypeface(generalFunc.getDefaultFont(getActContext()));
        emailBox.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT);
        emailBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        passwordBox.setImeOptions(EditorInfo.IME_ACTION_DONE);
        new PasswordViewHideManager(getActContext(), passwordBox, generalFunc);

        btnArea = (LinearLayout) view.findViewById(R.id.btnArea);
        btnTxt = (MTextView) view.findViewById(R.id.btnTxt);
        titleTxt = (MTextView) view.findViewById(R.id.titleTxt);
        countrydropimage = (ImageView) view.findViewById(R.id.countrydropimage);
        countrydropimagerror = (ImageView) view.findViewById(R.id.countrydropimagerror);

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

        countryBox.setShowClearButton(false);


        btnImg = (ImageView) view.findViewById(R.id.btnImg);
        imgClose = (ImageView) view.findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new setOnClickList());
        if (generalFunc.isRTLmode()) {
            btnImg.setRotation(180);
            btnArea.setBackground(getActContext().getResources().getDrawable(R.drawable.login_border_rtl));
        }


        btnArea.setOnClickListener(new setOnClickList());
        forgetPassTxt.setOnClickListener(new setOnClickList());
        setLabels();
        if (generalFunc.retrieveValue("ENABLE_PHONE_LOGIN_VIA_COUNTRY_SELECTION_METHOD").equalsIgnoreCase("Yes")) {
            removeInput();
            emailBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (charSequence.length() > 3 && TextUtils.isDigitsOnly(emailBox.getText().toString().trim())) {
                        countryArea.setVisibility(View.VISIBLE);
                    } else {
                        countryArea.setVisibility(View.GONE);
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
        return view;
    }

    public void removeInput() {
        Utils.removeInput(countryBox);

        if (generalFunc.retrieveValue("showCountryList").equalsIgnoreCase("Yes")) {
            // countrydropimage.setVisibility(View.GONE);
            countryBox.setOnTouchListener(new SetOnTouchList());

            countryBox.setOnClickListener(new setOnClickList());
        }
    }

    public void setLabels() {

        emailBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_PHONE_EMAIL"));
        emailBox.setHelperText(generalFunc.retrieveLangLBl("", "LBL_SIGN_IN_MOBILE_EMAIL_HELPER"));
        passwordBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_PASSWORD_LBL_TXT"));

        countryBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_COUNTRY_TXT"));

        forgetPassTxt.setText(generalFunc.retrieveLangLBl("", "LBL_FORGET_YOUR_PASS_TXT"));
        btnTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SIGN_IN_SIGN_IN_TXT"));
//        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SIGN_IN"));
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HEADER_TOPBAR_SIGN_IN_TXT"));
        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD");
        error_email_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_EMAIL_ERROR");
        signbootomHint.setText(generalFunc.retrieveLangLBl("", "LBL_DONT_HAVE_AN_ACCOUNT"));
        // signbtn.setText(generalFunc.retrieveLangLBl("", "LBL_SIGNUP"));
        //nowTxt.setText(generalFunc.retrieveLangLBl("", "LBL_NOW"));

        String firstVal = generalFunc.retrieveLangLBl("", "LBL_DONT_HAVE_AN_ACCOUNT");
        String secondVal = generalFunc.retrieveLangLBl("", "LBL_SIGNUP");
        String thirdVal = generalFunc.retrieveLangLBl("", "LBL_NOW");
        String finalVal = firstVal + " " + secondVal + " " + thirdVal;
        SpannableString signUpspan = new SpannableString(finalVal);

        //  signUpspan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.appThemeColor_1)),  firstVal.length(),firstVal.length()+ secondVal.length()+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        // Initialize a new ClickableSpan to display appthemecolor background
        ClickableSpan signUpClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {

                appLoginAct.titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SIGN_UP"));

                if (!appLoginAct.isSignUpFirst) {
                    appLoginAct.signUpFragment=new SignUpFragment();
                    appLoginAct.hadnleFragment(appLoginAct.signUpFragment, true, false);
                }else {
                    appLoginAct.signUpFragment=new SignUpFragment();
                    appLoginAct.hadnleFragment(appLoginAct.signUpFragment, false, false);
                }

                appLoginAct.signheaderHint.setText(generalFunc.retrieveLangLBl("", "LBL_SIGN_UP_WITH_SOC_ACC_HINT"));

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.appThemeColor_1));    // you can use custom color
                ds.setUnderlineText(false);    // this remove the underline
            }
        };
        signUpspan.setSpan(
                signUpClickableSpan, // Span to add
                finalVal.indexOf(secondVal), // Start of the span (inclusive)
                finalVal.indexOf(secondVal) + String.valueOf(secondVal).length(), // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
        );
        signbootomHint.setText(signUpspan);
        signbootomHint.setMovementMethod(LinkMovementMethod.getInstance());

    }

    public void checkValues() {
        Utils.hideKeyboard(getActContext());
        String noWhiteSpace = generalFunc.retrieveLangLBl("Password should not contain whitespace.", "LBL_ERROR_NO_SPACE_IN_PASS");
        String pass_length = generalFunc.retrieveLangLBl("Password must be", "LBL_ERROR_PASS_LENGTH_PREFIX")
                + " " + Utils.minPasswordLength + " " + generalFunc.retrieveLangLBl("or more character long.", "LBL_ERROR_PASS_LENGTH_SUFFIX");

        boolean countryEntered = false;

        boolean emailEntered = Utils.checkText(emailBox.getText().toString().replace("+", "")) ? true
                : Utils.setErrorFields(emailBox, required_str);

        boolean passwordEntered = Utils.checkText(passwordBox) ?
                (Utils.getText(passwordBox).contains(" ") ? Utils.setErrorFields(passwordBox, noWhiteSpace)
                        : (Utils.getText(passwordBox).length() >= Utils.minPasswordLength ? true : Utils.setErrorFields(passwordBox, pass_length)))
                : Utils.setErrorFields(passwordBox, required_str);


        String regexStr = "^[0-9]*$";

        if (emailBox.getText().toString().trim().replace("+", "").matches(regexStr)) {
            if (emailEntered) {
                emailEntered = emailBox.length() >= 3 ? true : Utils.setErrorFields(emailBox, generalFunc.retrieveLangLBl("", "LBL_INVALID_MOBILE_NO"));
            }

            if(generalFunc.retrieveValue("ENABLE_PHONE_LOGIN_VIA_COUNTRY_SELECTION_METHOD").equalsIgnoreCase("Yes")) {
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


        } else {
            emailEntered = Utils.checkText(emailBox) ?
                    (generalFunc.isEmailValid(Utils.getText(emailBox)) ? true : Utils.setErrorFields(emailBox, error_email_str))
                    : Utils.setErrorFields(emailBox, required_str);

            if (emailEntered == false) {
                return;
            }
        }

        if (emailEntered == false || passwordEntered == false) {
            return;
        }

        btnArea.setEnabled(false);
        signInUser();
    }

    public void signInUser() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "signIn");
        parameters.put("vEmail", Utils.getText(emailBox));
        parameters.put("vPassword", Utils.getText(passwordBox));
        parameters.put("vDeviceType", Utils.deviceType);
        parameters.put("UserType", Utils.app_type);
        parameters.put("vCurrency", generalFunc.retrieveValue(Utils.DEFAULT_CURRENCY_VALUE));
        parameters.put("vLang", generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));
       if (generalFunc.retrieveValue("ENABLE_PHONE_LOGIN_VIA_COUNTRY_SELECTION_METHOD").equalsIgnoreCase("Yes")) {
           parameters.put("CountryCode", vCountryCode);
           parameters.put("PhoneCode", vPhoneCode);
       }

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken", generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            btnArea.setEnabled(true);
            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {
                    new SetUserData(responseString, generalFunc, getActContext(), true);
                    appLoginAct.manageSinchClient();
                    generalFunc.storeData(Utils.USER_PROFILE_JSON, generalFunc.getJsonValue(Utils.message_str, responseString));
                    new OpenMainProfile(getActContext(),
                            generalFunc.getJsonValue(Utils.message_str, responseString), false, generalFunc).startProcess();

                } else {
                    passwordBox.setText("");
                    if (generalFunc.getJsonValue("eStatus", responseString).equalsIgnoreCase("Deleted")) {
                        openContactUsDialog(responseString);
                    } else if (generalFunc.getJsonValue("eStatus", responseString).equalsIgnoreCase("Inactive")) {
                        openContactUsDialog(responseString);
                    } else {
                        generalFunc.showGeneralMessage("",
                                generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                    }
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    public void openContactUsDialog(String responseString) {
        GenerateAlertBox alertBox = new GenerateAlertBox(getActContext());
        alertBox.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
        alertBox.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        alertBox.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CONTACT_US_TXT"));
        alertBox.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {

                alertBox.closeAlertBox();
                if (btn_id == 0) {
                    new StartActProcess(getActContext()).startAct(ContactUsActivity.class);
                }
            }
        });
        alertBox.showAlertBox();
    }

    public Context getActContext() {
        return appLoginAct.getActContext();
    }
    public static void setdata(int requestCode, int resultCode, Intent data) {

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.SELECT_COUNTRY_REQ_CODE && resultCode == appLoginAct.RESULT_OK && data != null) {

            vCountryCode = data.getStringExtra("vCountryCode");
            vPhoneCode = data.getStringExtra("vPhoneCode");
            vSImage = data.getStringExtra("vSImage");
            Log.d("TestData", "::" + vSImage);
            Picasso.get().load(vSImage).into(countryimage);
            isCountrySelected = true;
            countryBox.setTextColor(getResources().getColor(R.color.black));
            countryBox.setText("+" + generalFunc.convertNumberWithRTL(vPhoneCode));

        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.hideKeyboard(getActivity());
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(getActivity());
            if (i == btnArea.getId()) {
                checkValues();
            } else if (i == forgetPassTxt.getId()) {
//                String link = generalFunc.retrieveValue(Utils.LINK_FORGET_PASS_KEY);
//                new StartActProcess(appLoginAct.getActContext()).openURL(link);

                new StartActProcess(getActContext()).startAct(ForgotPasswordActivity.class);
            } else if (i == R.id.countryBox) {
                // new StartActProcess(getActivity()).startActForResult(SelectCountryActivity.class, Utils.SELECT_COUNTRY_REQ_CODE);
//                Intent intent = new Intent(getActivity(), SelectCountryActivity.class);
//                getActivity().startActivityForResult(intent, Utils.SELECT_COUNTRY_REQ_CODE);

                if (countryPicker == null) {
                    countryPicker = new CountryPicker.Builder(getActContext()).showingDialCode(true)
                            .setLocale(locale).showingFlag(true)
                            .enablingSearch(true)
                            //.setCountries(items_list)
                            .setCountrySelectionListener(country -> setData(country.getCode(), country.getDialCode(), country.getFlagName()))
                            .build();
                }
                countryPicker.show(getActContext());
            }
            else if (i == imgClose.getId()) {
                appLoginAct.onBackPressed();
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
