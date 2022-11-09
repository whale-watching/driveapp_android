package com.solicity.provider.deliverAll;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.adapter.MoreInstructionAdapter;
import com.solicity.provider.R;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.squareup.picasso.Picasso;
import com.utils.Utils;
import com.view.MTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UserPrefrenceActivity extends AppCompatActivity {


    RecyclerView moreInstruction;
    private GeneralFunctions generalFunc;
    MoreInstructionAdapter moreInstructionAdapter;
    LinearLayout moreInstructionLayout;
    ArrayList<HashMap<String, String>> instructionsList;
    MTextView titleTxt;
    boolean isPreference=false;
    ImageView backImgView;
    ImageView iv_preferenceImg;
    CardView preferenceArea;
    MTextView preferenceImageTxt;
    String vImageDeliveryPref="http";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_prefrence);

        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());

        titleTxt = findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }
        iv_preferenceImg = (ImageView) findViewById(R.id.iv_preferenceImg);
        preferenceArea = (CardView) findViewById(R.id.preferenceArea);
        preferenceImageTxt = (MTextView) findViewById(R.id.preferenceImageTxt);
        instructionsList = new ArrayList<>();
        moreInstructionLayout = findViewById(R.id.moreinstructionLyout);
        preferenceImageTxt.setText(generalFunc.retrieveLangLBl("view Preference Image","LBL_VIEW_PREFERENCE_IMAGE"));

        moreInstruction = findViewById(R.id.moreinstuction);
        moreInstructionAdapter = new MoreInstructionAdapter(getActContext(),instructionsList, new MoreInstructionAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(HashMap<String, String> map) {

            }
        });
        moreInstruction.setAdapter(moreInstructionAdapter);

        JSONObject DeliveryPreferences = null;
        try {
            DeliveryPreferences = new JSONObject(getIntent().getStringExtra("DeliveryPreferences"));
            getUserPreference(DeliveryPreferences);
            int imageWidth = (int) this.getResources().getDimension(R.dimen._90sdp);
            Picasso.get().load(Utils.getResizeImgURL(getActContext(), vImageDeliveryPref, imageWidth, imageWidth)).placeholder(R.mipmap.ic_no_icon).error(R.mipmap.ic_no_icon).into(iv_preferenceImg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        preferenceArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new StartActProcess(getActContext()).openURL(vImageDeliveryPref);

            }
        });

        backImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserPrefrenceActivity.super.onBackPressed();
            }
        });

    }



    public void getUserPreference(JSONObject DeliveryPreferences) {
        JSONArray Data = generalFunc.getJsonArray("Data",DeliveryPreferences);
        titleTxt.setText(generalFunc.getJsonValueStr("vTitle",DeliveryPreferences));
        vImageDeliveryPref = generalFunc.getJsonValueStr("vImageDeliveryPref", DeliveryPreferences);
        preferenceArea.setVisibility(Utils.checkText(vImageDeliveryPref)?View.VISIBLE:View.GONE);

        if (Data!=null) {
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
                    instructionsList.add(hashMap);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                moreInstructionAdapter.notifyDataSetChanged();
            }
        }
    }


    public Context getActContext() {
        return UserPrefrenceActivity.this;
    }
}

