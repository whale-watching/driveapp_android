package com.countryview.view;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adapter.files.PinnedSectionListAdapter;
import com.countryview.model.Country;
import com.countryview.presenter.CountryPickerContractor;
import com.countryview.presenter.CountryPickerPresenter;

import com.solicity.provider.R;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.GetCountryList;
import com.general.files.KmRecyclerView;
import com.general.files.MyApp;
import com.general.files.OnScrollTouchDelegate;
import com.utils.Utils;
import com.view.MTextView;
import com.view.anim.loader.AVLoadingIndicatorView;
import com.view.pinnedListView.CountryListItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CountryPicker implements CountryPickerContractor.View, PinnedSectionListAdapter.CountryClick, OnScrollTouchDelegate {

    @Override
    public void countryClickList(CountryListItem countryListItem) {

    }

    @Override
    public void onTouchDelegate() {

    }

    public interface OnAutoDetectCountryListener {
        void onCountryDetected(Country country);
    }

    private boolean mShowingFlag;
    private boolean mEnablingSearch;
    private boolean mShowingDialCode;
    private Context mContext;
    private List<Country> mCountries = new ArrayList<>();
    private List<Country> mCountriesName;
    private Locale mLocale;
    private KmRecyclerView mRecyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    private SearchView mSearchViewCountry;
    private int mStyle;
    private DetectionMethod mDetectionMethod;
    private BaseView mBaseView;
    private RecyclerViewAdapter.OnCountryClickListener mOnCountryClickListener;
    private OnAutoDetectCountryListener mOnAutoDetectCountryListener;
    private View mView;
    private CountryPickerContractor.Presenter mPresenter;
    EditText searchTxt;
    ImageView imageCancel;
    MTextView titleTxt;
    AVLoadingIndicatorView loaderView;
    GeneralFunctions generalFunc;
    ArrayList<Country> items_list = new ArrayList<Country>();
    int imagewidth,imageHeight;

    private CountryPicker(Builder builder) {
        initAttributes(builder);

        mPresenter = new CountryPickerPresenter(mCountriesName, this);
        initLocale();
        initView();
        initSearchView();
        initDetectionMethod();
        if (GetCountryList.getInstance().getCountryList() != null) {
            setCountries(GetCountryList.getInstance().getCountryList());
            mCountriesName = GetCountryList.getInstance().getCountryList();
            mPresenter = new CountryPickerPresenter(mCountriesName, this);
            recyclerViewAdapter.notifyDataSetChanged();
            loaderView.setVisibility(View.GONE);
        } else {

            getCountryList();
        }
    }


    private void initAttributes(Builder builder) {

        mCountriesName = builder.mCountries;
        mOnAutoDetectCountryListener = builder.mOnAutoDetectCountryListener;
        mDetectionMethod = builder.mDetectionMethod;
        mShowingFlag = builder.mShowingFlag;

        mEnablingSearch = builder.mEnablingSearch;
        mOnCountryClickListener = builder.mOnCountryClickListener;
        mShowingDialCode = builder.mShowingDialCode;
        mLocale = builder.mLocale;
        mContext = builder.mContext;
        generalFunc = MyApp.getInstance().getGeneralFun(mContext);
        mStyle = builder.mStyle;

        if (mStyle == 0) {
            mStyle = R.style.CountryPickerLightStyle;
        }
        builder.mContext.getTheme().applyStyle(mStyle, true);
    }

    public  static boolean  isShadow=false;
    private void initView() {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        mView = layoutInflater.inflate(R.layout.layout_countrypicker, null);

        if (generalFunc.isRTLmode()) {
            mView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        mRecyclerView = mView.findViewById(R.id.recyclerview_countries);

        //   mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerViewAdapter = new RecyclerViewAdapter(mCountries, "india", mShowingFlag, mShowingDialCode);
        if (mOnCountryClickListener != null)
            recyclerViewAdapter.setListener(country -> {
                mOnCountryClickListener.onCountrySelected(country);
                dismiss();
            });
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(recyclerViewAdapter);
        mRecyclerView.setLayoutManager(layoutManager);
        items_list = new ArrayList<>();
        imagewidth = (int) mContext.getResources().getDimension(R.dimen._30sdp);
        imageHeight = (int) mContext.getResources().getDimension(R.dimen._20sdp);

        ImageView closeImg = (ImageView) mView.findViewById(R.id.closeImg);
        closeImg.setOnClickListener(v -> {
           mBaseView.dismissView();


        });


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!recyclerView.canScrollVertically(-1)) {
                    // we have reached the top of the list
                    isShadow=false;
                   // recyclerViewAdapter.notifyDataSetChanged();
                } else {
                    // we are not at the top yet
                    isShadow=true;
                   // recyclerViewAdapter.notifyDataSetChanged();

                }
            }
        });

    }

    public List<Country> getCountries() {

        return mCountries;
    }

    private void initSearchView() {

        mSearchViewCountry = mView.findViewById(R.id.searchview_country);
        searchTxt = (EditText) mView.findViewById(R.id.searchTxt);
        searchTxt.setHint(generalFunc.retrieveLangLBl("", "LBL_SEARCH_COUNTRY"));
        imageCancel = (ImageView) mView.findViewById(R.id.imageCancel);
        titleTxt = (MTextView) mView.findViewById(R.id.titleTxt);
        loaderView = (AVLoadingIndicatorView) mView.findViewById(R.id.loaderView);
        //loaderView.setVisibility(View.VISIBLE);
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SELECT_CONTRY"));
        imageCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTxt.setText("");
                Utils.hideSoftKeyboard(mContext, mView);
                setCountries(mCountriesName);
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });


        searchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                mPresenter.filterSearch(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (searchTxt.length() == 0) {
                    imageCancel.setVisibility(View.GONE);

                    setCountries(mCountriesName);
                    recyclerViewAdapter.notifyDataSetChanged();
                } else {
                    imageCancel.setVisibility(View.VISIBLE);
                }
            }
        });

        mSearchViewCountry.setOnClickListener(v -> mSearchViewCountry.setIconified(false));

        if (mEnablingSearch) {
            mSearchViewCountry.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    mPresenter.filterSearch(newText);
                    return true;
                }
            });
        } else {
            mSearchViewCountry.setVisibility(View.GONE);
        }
    }

    private void initLocale() {

        if (mLocale != null) {
            Locale.setDefault(mLocale);
            Configuration config = new Configuration();
            config.locale = mLocale;
            config.setLayoutDirection(mLocale);
            mContext.getResources().updateConfiguration(config, mContext.getResources().getDisplayMetrics());
        }
    }

    private void initDetectionMethod() {

        switch (mDetectionMethod) {
            case LOCALE:
                detectByLocale();
                break;
            case SIM:
                detectBySim();
                break;
            case NETWORK:
                detectByNetwork();
                break;
        }
    }

    private void detectByLocale() {

        Locale locale;
        String countryValue;

        if (mLocale != null)
            countryValue = mLocale.getCountry();
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = mContext.getResources().getConfiguration().getLocales().get(0);
            } else {
                locale = mContext.getResources().getConfiguration().locale;
            }
            countryValue = locale.getCountry();
        }
        Country country = getCountryByCode(countryValue);
        mOnAutoDetectCountryListener.onCountryDetected(country);
    }

    private Country getCountryByCode(String countryIso) {

        if (countryIso == null || countryIso.equals(""))
            countryIso = "us";

        for (Country country : mCountries) {
            if (country.getCode().toLowerCase().equals(countryIso.toLowerCase())) {
                return country;
            }
        }
        return mCountries.get(0);
    }


    private void detectBySim() {

        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso();
        Country country = getCountryByCode(countryCode);
        mOnAutoDetectCountryListener.onCountryDetected(country);
    }

    private void detectByNetwork() {

        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getNetworkCountryIso();
        Country country = getCountryByCode(countryCode);
        mOnAutoDetectCountryListener.onCountryDetected(country);

    }

    public void show(Context activity) {

        if (mBaseView == null) {
            mBaseView = new MyBottomSheetDialog(activity);

            mBaseView.setView(mView);
        }
        mBaseView.showView();
    }

    public void dismiss() {

        mBaseView.dismissView();
    }

    @Override
    public void setCountries(List<Country> countries) {
        mCountries = countries;
        ((RecyclerViewAdapter) mRecyclerView.getAdapter()).setCountries(mCountries);
    }

    public static class Builder {

        private boolean mShowingFlag;
        private boolean mEnablingSearch;
        private boolean mShowingDialCode;
        private String mPreSelectedCountry;
        private DetectionMethod mDetectionMethod = DetectionMethod.NONE;
        private Sort mSort = Sort.NONE;
        private RecyclerViewAdapter.OnCountryClickListener mOnCountryClickListener;
        private OnAutoDetectCountryListener mOnAutoDetectCountryListener;
        private List<Country> mCountries;

        private Context mContext;
        private Locale mLocale;
        private List<String> mExceptCountries;
        private int mStyle;
        private ViewType mViewType = ViewType.DIALOG;

        public Builder(Context context) {

            mContext = context;
        }


        public Builder setStyle(int style) {

            mStyle = style;
            return this;
        }

        public Builder showingFlag(boolean showingFlag) {

            mShowingFlag = showingFlag;
            return this;
        }

        public Builder showingDialCode(boolean showingDialCode) {

            mShowingDialCode = showingDialCode;
            return this;
        }

        public Builder enablingSearch(boolean enablingSearch) {

            mEnablingSearch = enablingSearch;
            return this;
        }

        public Builder setCountrySelectionListener(RecyclerViewAdapter.OnCountryClickListener onCountryClickListener) {

            mOnCountryClickListener = onCountryClickListener;
            return this;
        }

        public Builder enableAutoDetectCountry(DetectionMethod detectionMethod, OnAutoDetectCountryListener onAutoDetectCountryListener) {

            mDetectionMethod = detectionMethod;
            mOnAutoDetectCountryListener = onAutoDetectCountryListener;
            return this;

        }

        public Builder setLocale(Locale locale) {

            mLocale = locale;
            return this;
        }

        public Builder setPreSelectedCountry(String preSelectedCountry) {

            mPreSelectedCountry = preSelectedCountry;
            return this;
        }

        public Builder exceptCountriesName(List<String> countriesName) {

            mExceptCountries = countriesName;
            return this;
        }

        public Builder setViewType(ViewType viewType) {

            mViewType = viewType;
            return this;
        }

        public Builder setCountries(List<Country> countries) {
            if (countries == null || countries.size() < 1) {
                return this;
            }
            mCountries = countries;
            return this;
        }

        public CountryPicker build() {

            return new CountryPicker(this);
        }
    }

    public enum Sort {
        NONE,
        COUNTRY,
        CODE,
        DIALCODE
    }

    public enum ViewType {
        DIALOG,
        BOTTOMSHEET;
    }

    public enum DetectionMethod {
        NONE,
        LOCALE,
        SIM,
        NETWORK
    }

    public void getCountryList() {


        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "countryList");


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mContext, parameters);
        exeWebServer.setDataResponseListener(responseString -> {

            loaderView.setVisibility(View.VISIBLE);
            if (responseString != null && !responseString.equals("")) {
                if (generalFunc.checkDataAvail(Utils.action_str, responseString) == true) {
                    loaderView.setVisibility(View.GONE);
                    JSONArray countryArr = generalFunc.getJsonArray("CountryList", responseString);
                    for (int i = 0; i < countryArr.length(); i++) {
                        JSONObject tempJson = generalFunc.getJsonObject(countryArr, i);

                        String key_str = generalFunc.getJsonValueStr("key", tempJson);
                        String count_str = generalFunc.getJsonValueStr("TotalCount", tempJson);


                        JSONArray subListArr = generalFunc.getJsonArray("List", tempJson);
                        Country headersection = new Country(key_str, "", "", "", "Header", null);
                        items_list.add(headersection);
                        for (int j = 0; j < subListArr.length(); j++) {
                            JSONObject subTempJson = generalFunc.getJsonObject(subListArr, j);
                            String vCountryCode = generalFunc.getJsonValueStr("vCountryCode", subTempJson);
                            String vPhoneCode = generalFunc.getJsonValueStr("vPhoneCode", subTempJson);
                            String iCountryId = generalFunc.getJsonValueStr("iCountryId", subTempJson);
                            String vCountry = generalFunc.getJsonValueStr("vCountry", subTempJson);
                            String vRImage = generalFunc.getJsonValueStr("vRImage", subTempJson);
                            String vSImage = generalFunc.getJsonValueStr("vSImage", subTempJson);
                            vSImage = Utils.getResizeImgURL(mContext, vSImage, imagewidth, imageHeight);
                            Country section = new Country(vCountry, vCountryCode, vPhoneCode, vSImage, "List", headersection);
                            items_list.add(section);
                        }

                        setCountries(items_list);
                        mCountriesName = items_list;
                        mCountries = mCountriesName;
                        mPresenter = new CountryPickerPresenter(mCountriesName, this);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                }
            } else {

            }
        });
        exeWebServer.execute();
    }


}