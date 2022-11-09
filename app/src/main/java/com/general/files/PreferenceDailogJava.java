package com.general.files;

import android.app.Dialog;
import android.content.Context;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.solicity.provider.ContactUsActivity;
import com.solicity.provider.R;
import com.view.MTextView;

public class PreferenceDailogJava {

    AlertDialog hotoUseDialog;
    GeneralFunctions generalFunc;
    Context actContext;

    public PreferenceDailogJava(Context actContext) {
        this.actContext = actContext;
        generalFunc = new GeneralFunctions(actContext);

    }

    public void showPreferenceDialog(String title, String Msg, int img, Boolean isUpload, String posBtn, String negBtn,boolean iscontactUs) {
        AlertDialog.Builder builder = new AlertDialog.Builder(actContext);

        LayoutInflater inflater = (LayoutInflater) actContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.desgin_preference_help, null);
        builder.setView(dialogView);
        ImageView iamage_source = dialogView.findViewById(R.id.iamage_source);
        LinearLayout uploadArea = dialogView.findViewById(R.id.uploadArea);
        LinearLayout uploadImgArea = dialogView.findViewById(R.id.uploadImgArea);
        MTextView uploadTitleTxt = dialogView.findViewById(R.id.uploadTitleTxt);

        uploadTitleTxt.setText(generalFunc.retrieveLangLBl("Upload item Photo", "LBL_UPLOAD_ITEM_PHOTO"));

        if (img != 0) {
            iamage_source.setImageResource(img);
        }
        if (isUpload) {
            uploadArea.setVisibility(View.VISIBLE);
        }


        MTextView okTxt = dialogView.findViewById(R.id.okTxt);
        MTextView skipTxtArea = dialogView.findViewById(R.id.skipTxtArea);
        MTextView titileTxt = dialogView.findViewById(R.id.titileTxt);
        WebView msgTxt = dialogView.findViewById(R.id.msgTxt);
        if (!title.equalsIgnoreCase("")) {
            titileTxt.setText(title);
            titileTxt.setVisibility(View.VISIBLE);
        } else {
            titileTxt.setVisibility(View.GONE);
        }

        if (!posBtn.equals("")) {
            okTxt.setText(posBtn);
            okTxt.setVisibility(View.VISIBLE);
        } else {
            okTxt.setVisibility(View.GONE);
        }
        if (!negBtn.equals("")) {
            skipTxtArea.setText(negBtn);
            skipTxtArea.setVisibility(View.VISIBLE);
        } else {
            skipTxtArea.setVisibility(View.INVISIBLE);

        }
        if(!Msg.equalsIgnoreCase(""))
        {
            msgTxt.getSettings().setJavaScriptEnabled(true);
            msgTxt.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            msgTxt.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);


            msgTxt.loadDataWithBaseURL(null, generalFunc.wrapHtml(msgTxt.getContext(), Msg), "text/html", "UTF-8", null);
        }
        else
        {
            msgTxt.setVisibility(View.GONE);
        }

      //  msgTxt.setMovementMethod(ScrollingMovementMethod.getInstance());
        okTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hotoUseDialog.dismiss();
                if(iscontactUs)
                {
                    new StartActProcess(actContext).startAct(ContactUsActivity.class);
                }
            }
        });
        skipTxtArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hotoUseDialog.dismiss();
            }
        });
        hotoUseDialog = builder.create();
        hotoUseDialog.setCancelable(true);
        if (generalFunc.isRTLmode()) {
            generalFunc.forceRTLIfSupported(hotoUseDialog);
        }
        hotoUseDialog.getWindow().setBackgroundDrawable(actContext.getResources().getDrawable(R.drawable.all_roundcurve_card));
        hotoUseDialog.show();

    }

}
