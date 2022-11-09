package com.solicity.provider;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.view.MTextView;

public class QuestionAnswerActivity extends AppCompatActivity {

    public GeneralFunctions generalFunc;
    MTextView titleTxt;
    MTextView vQuestion,vAnswer;
    ImageView backImgView;
   // List<String> listDataHeader;
   // HashMap<String, List<String>> listDataChild;

   // ExpandableListView expandableList;

   // QuestionAnswerEAdapter adapter;

   // private int lastExpandedPosition = -1;

    String answer="";
    MTextView contact_us_btn;
    MTextView textstillneedhelp;
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_answer);

        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());


        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        contact_us_btn = (MTextView) findViewById(R.id.contact_us_btn);
        textstillneedhelp = (MTextView) findViewById(R.id.textstillneedhelp);

        vQuestion = (MTextView) findViewById(R.id.vQuestion);
        clearCookies(getApplication());
        webView = findViewById(R.id.webView);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.setVerticalScrollBarEnabled(false);
        webView.setLongClickable(false);
        webView.setHapticFeedbackEnabled(false);

        vAnswer = (MTextView) findViewById(R.id.vAnswer);
      /*  expandableList = (ExpandableListView) findViewById(R.id.list);

        expandableList.setDividerHeight(2);
        expandableList.setGroupIndicator(null);
        expandableList.setClickable(true);

        listDataHeader = new ArrayList<String>();*/
       /* answer = getIntent().getStringExtra("ANSWER_MAP");

        String[] answerArrray = answer.split(",");
        vQuestion.setText(answerArrray[0]);
        vAnswer.setText(answerArrray[1]);*/

        if (getIntent().getStringExtra("QUESTION")!=null){
            vQuestion.setText(Html.fromHtml(getIntent().getStringExtra("QUESTION")+""));
//            vAnswer.setText(Html.fromHtml(getIntent().getStringExtra("ANSWER")+""));
            vAnswer.setVisibility(View.GONE);
            webView.loadDataWithBaseURL(null,generalFunc.wrapHtml(webView.getContext(), getIntent().getStringExtra("ANSWER")),
                    "text/html", "UTF-8",null);
        }
        titleTxt.setText(generalFunc.retrieveLangLBl("FAQ","LBL_FAQ_TXT"));
        contact_us_btn.setOnClickListener(new setOnClickList());
      /*  adapter = new QuestionAnswerEAdapter(getActContext(), listDataHeader, listDataChild);

        expandableList.setAdapter(adapter);


        expandableList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expandableList.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });*/

        backImgView.setOnClickListener(new setOnClickList());

        setData();
    }
    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d("Api", "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            Log.d("Api", "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }
    public void setData() {
        contact_us_btn.setText("" + generalFunc.retrieveLangLBl("", "LBL_CONTACT_US_HEADER_TXT"));
        textstillneedhelp.setText("" + generalFunc.retrieveLangLBl("", "LBL_STILL_NEED_HELP"));

      //  titleTxt.setText(generalFunc.getJsonValue("vTitle", getIntent().getStringExtra("QUESTION_LIST")));
     /*   JSONArray obj_ques = generalFunc.getJsonArray("Questions", getIntent().getStringExtra("QUESTION_LIST"));
        for (int i = 0; i < obj_ques.length(); i++) {
            JSONObject obj_temp = generalFunc.getJsonObject(obj_ques, i);


            //listDataHeader.add(generalFunc.getJsonValueStr("vTitle", obj_temp));

            List<String> answer = new ArrayList<String>();
            answer.add(generalFunc.getJsonValueStr("tAnswer", obj_temp));

           // listDataChild.put(listDataHeader.get(i), answer);
        }

       // adapter.notifyDataSetChanged();*/
    }

    public Context getActContext() {
        return QuestionAnswerActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(QuestionAnswerActivity.this);
            switch (view.getId()) {
                case R.id.backImgView:
                    QuestionAnswerActivity.super.onBackPressed();
                    break;
                case R.id.contact_us_btn:
                    new StartActProcess(getActContext()).startAct(ContactUsActivity.class);
                    break;


            }
        }
    }
}
