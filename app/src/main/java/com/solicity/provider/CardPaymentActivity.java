package com.solicity.provider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.flutterwave.raveandroid.responses.SubAccount;
import com.fragments.AddCardFragment;
import com.fragments.ViewCardFragment;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.utils.CommonUtilities;
import com.utils.Logger;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MTextView;

import org.json.JSONObject;

import java.util.HashMap;

import co.omise.android.ui.CreditCardActivity;

public class CardPaymentActivity extends AppCompatActivity {

    public GeneralFunctions generalFunc;
    public JSONObject userProfileJsonObj;
    MTextView titleTxt;
    ImageView backImgView;
    ViewCardFragment viewCardFrag;
    AddCardFragment addCardFrag;
    public static final int REQ_ADD_CARD_CODE = 101;

    String APP_PAYMENT_METHOD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_card_payment);

        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());

        getUserProfileJson(generalFunc.getJsonObject(generalFunc.retrieveValue(Utils.USER_PROFILE_JSON)));

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);

        setLabels();

        backImgView.setOnClickListener(new setOnClickList());

        openViewCardFrag();


    }

    private void getUserProfileJson(JSONObject object) {
        userProfileJsonObj = object;

        APP_PAYMENT_METHOD = generalFunc.getJsonValueStr("APP_PAYMENT_METHOD", userProfileJsonObj);
    }


    public void setLabels() {
        changePageTitle(generalFunc.retrieveLangLBl("", "LBL_CARD_PAYMENT_DETAILS"));
    }

    public void changePageTitle(String title) {
        titleTxt.setText(title);
    }

    public void changeUserProfileJson(String userProfileJson) {
        getUserProfileJson(generalFunc.getJsonObject(userProfileJson));

        Bundle bn = new Bundle();
        new StartActProcess(getActContext()).setOkResult(bn);

        openViewCardFrag();

        generalFunc.showMessage(getCurrView(), generalFunc.retrieveLangLBl("", "LBL_INFO_UPDATED_TXT"));
    }

    public View getCurrView() {
        return generalFunc.getCurrentView(CardPaymentActivity.this);
    }

    public void openViewCardFrag() {

        if (viewCardFrag != null) {
            viewCardFrag = null;
            addCardFrag = null;
            Utils.runGC();
        }
        viewCardFrag = new ViewCardFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, viewCardFrag).commit();
    }

    public void openAddCardFrag(String mode) {

        if (addCardFrag != null) {
            addCardFrag = null;
            viewCardFrag = null;
            Utils.runGC();
        }
        if (APP_PAYMENT_METHOD.equalsIgnoreCase("Stripe")) {
            Bundle bundle = new Bundle();
            bundle.putString("PAGE_MODE", mode);
            bundle.putString("carno", generalFunc.getJsonValueStr("vCreditCard", userProfileJsonObj));
            addCardFrag = new AddCardFragment();
            addCardFrag.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, addCardFrag).commit();
        }
    }

    public Context getActContext() {
        return CardPaymentActivity.this;
    }

    @Override
    public void onBackPressed() {
        backImgView.performClick();
        return;
    }

   // PaymentMethodNonce mNonce = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_ADD_CARD_CODE) {
//                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
//                mNonce = result.getPaymentMethodNonce();
//                CreateCustomer(mNonce.getNonce() + "", "");

            } else if (requestCode == Utils.REQ_VERIFY_CARD_PIN_CODE) {



                    if (addCardFrag != null) {
                        addCardFrag.setdata(requestCode, resultCode, data);
                    }

            }
        } else if (resultCode == CreditCardActivity.RESULT_OK) {
            String token = data.getStringExtra(CreditCardActivity.EXTRA_TOKEN);
            CreateCustomer("", token);
        }
        /*Flutterwave Payment GateWay*/
        else if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {

            String message = data.getStringExtra("response");

            if (message != null) {
                Log.d("rave response", message);
            }

            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                //  Toast.makeText(this, "SUCCESS " + message, Toast.LENGTH_SHORT).show();
                String dataJson = generalFunc.getJsonValue("data", message);

                UpdateCustomerToken(generalFunc.getJsonValue("txRef", dataJson));
            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                //  Toast.makeText(this, "ERROR " + message, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                // Toast.makeText(this, "CANCELLED " + message, Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void CreateCustomer(String paymentMethodNonce, String OmisepaymentMethodNonce) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "GenerateCustomer");
        parameters.put("iUserId", generalFunc.getMemberId());

        if (!paymentMethodNonce.equalsIgnoreCase("")) {
            parameters.put("paymentMethodNonce", paymentMethodNonce);
        }

        if (!OmisepaymentMethodNonce.equalsIgnoreCase("")) {
            parameters.put("vOmiseToken", OmisepaymentMethodNonce);
            parameters.put("CardNo", "");
        }


        parameters.put("UserType", Utils.app_type);


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {
            JSONObject responseStringObj = generalFunc.getJsonObject(responseString);

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseStringObj);
                String msg_str = generalFunc.getJsonValueStr(Utils.message_str, responseStringObj);

                if (isDataAvail == true) {
                        generalFunc.storeData(Utils.USER_PROFILE_JSON, msg_str);
                        changeUserProfileJson(msg_str);

                } else {
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", msg_str));
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }


    private void UpdateCustomerToken(String txtRef) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "UpdateCustomerToken");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("vPaymayaToken", "");
        parameters.put("UserType", Utils.app_type);



        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {
            JSONObject responseStringObj = generalFunc.getJsonObject(responseString);

            if (responseStringObj != null && !responseStringObj.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseStringObj);

                if (isDataAvail == true) {
                    generalFunc.storeData(Utils.USER_PROFILE_JSON, generalFunc.getJsonValueStr(Utils.message_str, responseStringObj));
                    changeUserProfileJson(generalFunc.getJsonValueStr(Utils.message_str, responseStringObj));
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

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(CardPaymentActivity.this);
            if (i == R.id.backImgView) {

                if (addCardFrag != null && addCardFrag.isInProcessMode) {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("You cannot go back while your transaction is being processed. Please wait for transaction being completed.", "LBL_TRANSACTION_IN_PROCESS_TXT"));
                } else if (addCardFrag == null) {
                    CardPaymentActivity.super.onBackPressed();
                } else {
                    openViewCardFrag();
                }
            }
        }
    }

}
