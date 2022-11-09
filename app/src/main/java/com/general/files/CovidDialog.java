package com.general.files;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.solicity.provider.R;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.anim.loader.AVLoadingIndicatorView;

//import ru.noties.scrollable.CanScrollVerticallyDelegate;
//import ru.noties.scrollable.ScrollableLayout;

public class CovidDialog extends AppCompatActivity {
    private Context mContext;

//    public CovidDialog(Context mContext) {
//        this.mContext = mContext;
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.covid_info);
        mContext=this;

        ImageView backArrowImgView = (ImageView) findViewById(R.id.backArrowImgView);

        backArrowImgView.setOnClickListener(v -> { Intent returnIntent = new Intent();
            returnIntent.putExtra("isOnline",false);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();});
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        WebView mWebView = (WebView) findViewById(R.id.webView);
        AVLoadingIndicatorView loaderView = (AVLoadingIndicatorView) findViewById(R.id.loaderView);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress > 90) {
                    loaderView.setVisibility(View.GONE);
                }
            }
        });

        mWebView.getSettings().setJavaScriptEnabled(true);


        String url = getIntent().getStringExtra("URL");
       // mWebView.loadUrl(url + "&fromapp=yes");



        mWebView.requestFocus();
        mWebView.getSettings().setLightTouchEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setGeolocationEnabled(true);
        mWebView.setSoundEffectsEnabled(true);
        mWebView.loadDataWithBaseURL(null,url,
                "text/html", "UTF-8",null);

        loaderView.setVisibility(View.VISIBLE);
        GeneralFunctions generalFunctions=new GeneralFunctions(mContext);
        MTextView capacityTxt = (MTextView) findViewById(R.id.capacityTxt);
        MButton btn;
        btn = ((MaterialRippleLayout) findViewById(R.id.btn)).getChildView();
        btn.setText(generalFunctions.retrieveLangLBl("Agree and ride","LBL_AGREE"));
        btn.setId(Utils.generateViewId());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("isOnline",true);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        capacityTxt.setText(generalFunctions.retrieveLangLBl("Capacity","LBL_CURRENT_PERSON_LIMIT"));
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("isOnline",false);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @SuppressLint({"ClickableViewAccessibility", "SetJavaScriptEnabled"})
    public void open(String url, String imageURL) {
        final Dialog addAddressDailog = new Dialog(mContext);
        View contentView = View.inflate(mContext, R.layout.covid_info, null);

        addAddressDailog.setContentView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.dpToPx(ViewGroup.LayoutParams.MATCH_PARENT, mContext)));
        addAddressDailog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        addAddressDailog.setCancelable(true);

        int screenHeight = ((int) Utils.getScreenPixelHeight(mContext));
        int peekHeight = 0;
        int bottomMarginForLoader = Utils.dpToPx(50, mContext);
GeneralFunctions generalFunctions=new GeneralFunctions(mContext);

        ImageView backArrowImgView = (ImageView) addAddressDailog.findViewById(R.id.backArrowImgView);

        backArrowImgView.setOnClickListener(v -> addAddressDailog.dismiss());
        RelativeLayout container = (RelativeLayout) addAddressDailog.findViewById(R.id.container);
        WebView mWebView = (WebView) addAddressDailog.findViewById(R.id.webView);
        MTextView capacityTxt = (MTextView) addAddressDailog.findViewById(R.id.capacityTxt);
         MButton btn;
        btn = ((MaterialRippleLayout) addAddressDailog.findViewById(R.id.btn)).getChildView();
        btn.setText(generalFunctions.retrieveLangLBl("Agree and ride","LBL_AGREE_AND_RIDE"));
        btn.setId(Utils.generateViewId());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("isOnline",true);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        capacityTxt.setText(generalFunctions.retrieveLangLBl("Capacity","LBL_CURRENT_PERSON_LIMIT"));
        AVLoadingIndicatorView loaderView = (AVLoadingIndicatorView) addAddressDailog.findViewById(R.id.loaderView);

        RelativeLayout.LayoutParams loaderView_ly_params = (RelativeLayout.LayoutParams) loaderView.getLayoutParams();
        loaderView_ly_params.bottomMargin = screenHeight - peekHeight + bottomMarginForLoader;
        loaderView.setLayoutParams(loaderView_ly_params);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress > 90) {
                    loaderView.setVisibility(View.GONE);
                }
            }
        });

        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.loadData(url,
//                "text/html", "UTF-8");
        mWebView.loadDataWithBaseURL(null,url,
                "text/html", "UTF-8",null);

        loaderView.setVisibility(View.VISIBLE);

        addAddressDailog.show();
    }


}
