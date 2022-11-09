package com.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.cardview.widget.CardView;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.RecyclerView;

import com.adapter.files.CustomDialogListAdapter;
import com.solicity.provider.R;
import com.general.files.GeneralFunctions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.utils.Utils;
import com.view.MTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class OpenListView {

    public static AppCompatDialog mListDialog = null;

    public enum OpenDirection {CENTER, BOTTOM}

    private Context mContext;
    private String title;
    private String subTitle;
    private OpenDirection openDirection;
    private boolean isCancelable;
    private boolean isStringArrayList = false;
    private ArrayList<HashMap<String, String>> arrayList;
    private ArrayList<String> stringArrayList;
    private OnItemClickList onItemClickList;
    private AppCompatDialog listDialog = null;

    GeneralFunctions generalFunctions;
    boolean isImageinList = false;
    boolean isloading = false;

    private OpenListView(Context mContext, String title, ArrayList<HashMap<String, String>> arrayList, OpenDirection openDirection, boolean isCancelable, OnItemClickList onItemClickList) {
        this.mContext = mContext;
        this.title = title;
        this.openDirection = openDirection;
        this.isCancelable = isCancelable;
        this.arrayList = arrayList;
        this.onItemClickList = onItemClickList;
        generalFunctions = new GeneralFunctions(mContext);
    }

    private OpenListView(Context mContext, String title, ArrayList<HashMap<String, String>> arrayList, OpenDirection openDirection, boolean isCancelable, boolean isImageinList, OnItemClickList onItemClickList, String subTitle, boolean isloading) {
        this.mContext = mContext;
        this.title = title;
        this.openDirection = openDirection;
        this.isCancelable = isCancelable;
        this.arrayList = arrayList;
        this.onItemClickList = onItemClickList;
        generalFunctions = new GeneralFunctions(mContext);
        this.isImageinList = isImageinList;
        this.subTitle = subTitle;
        this.isloading = isloading;
    }

    private OpenListView(Context mContext, String title, ArrayList<String> stringArrayList, OpenDirection openDirection, boolean isCancelable, OnItemClickList onItemClickList, boolean isStringArrayList) {
        this.mContext = mContext;
        this.title = title;
        this.openDirection = openDirection;
        this.isCancelable = isCancelable;
        this.isStringArrayList = isStringArrayList;
        this.stringArrayList = stringArrayList;
        this.onItemClickList = onItemClickList;
        generalFunctions = new GeneralFunctions(mContext);
    }


    public void show(int selectedItemPosition, String keyToShowAsTitle) {
        if (mListDialog != null && mListDialog.isShowing()) {
            return;
        }
        if (openDirection == OpenDirection.BOTTOM) {
            final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
            listDialog = dialog;
        }

        if (openDirection == OpenDirection.CENTER) {
            AppCompatDialog dialog = new AppCompatDialog(mContext);
            Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
            listDialog = dialog;
        }
        mListDialog = listDialog;
        if (isImageinList) {
            showDialogWithImage(selectedItemPosition, keyToShowAsTitle, isStringArrayList);
        } else {
            showDialog(selectedItemPosition, keyToShowAsTitle, isStringArrayList);
        }
    }

    private void showDialogWithImage(int selectedItemPosition, String keyToShowAsTitle, boolean isStringArrayList) {
        if (listDialog == null) {
            return;
        }
        final BottomSheetDialog optionDailog = new BottomSheetDialog(mContext);
        mListDialog = optionDailog;
        View contentView = View.inflate(mContext, R.layout.dialog_img_filter, null);
        optionDailog.setContentView(contentView);
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) contentView.getParent());
        mBehavior.setPeekHeight(Utils.dpToPx(600, mContext));
        optionDailog.setCancelable(true);
        View bottomSheetView = optionDailog.getWindow().getDecorView().findViewById(R.id.design_bottom_sheet);
        bottomSheetView.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));

        CardView cardshadow = bottomSheetView.findViewById(R.id.cardshadow);

        if (generalFunctions.isRTLmode()) {
            contentView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        CustomDialogListAdapter listAdapter;
        if (isStringArrayList) {
            listAdapter = new CustomDialogListAdapter(mContext, stringArrayList, selectedItemPosition, isStringArrayList, isImageinList);
        } else {
            listAdapter = new CustomDialogListAdapter(mContext, arrayList, selectedItemPosition, keyToShowAsTitle, isImageinList);
        }

        ImageView closeImg = (ImageView) bottomSheetView.findViewById(R.id.closeImg);
        MTextView titleTxt = (MTextView) bottomSheetView.findViewById(R.id.TitleTxt);
        MTextView subtitleTxt = (MTextView) bottomSheetView.findViewById(R.id.subTitleTxt);
        titleTxt.setText(title);
        subtitleTxt.setText(subTitle);
        if (!subtitleTxt.equals("")) {
            subtitleTxt.setVisibility(View.VISIBLE);
        }

        SpringAnimation animation = new SpringAnimation(bottomSheetView, DynamicAnimation.TRANSLATION_Y);
        SpringForce spring = new SpringForce();
        spring.setFinalPosition(Utils.dpToPx(15, mContext));
        spring.setStiffness(SpringForce.STIFFNESS_LOW);
        spring.setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);
        // animation.setStartVelocity(1500);
        animation.setSpring(spring);

        closeImg.setOnClickListener(v -> {
            optionDailog.dismiss();
            mListDialog = null;
        });

        RecyclerView mRecyclerView = bottomSheetView.findViewById(R.id.mRecyclerView);
        mRecyclerView.setAdapter(listAdapter);
        mRecyclerView.scrollToPosition(selectedItemPosition);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(-1)) {
                    // we have reached the top of the list
                    cardshadow.setVisibility(View.GONE);
                } else {
                    // we are not at the top yet
                    cardshadow.setVisibility(View.VISIBLE);
                }
            }
        });
        listAdapter.setOnItemClickList(position -> {
            if (!isloading) {
                optionDailog.dismiss();
            }
            if (onItemClickList != null) {
                onItemClickList.onItemClick(position);
            }
            onItemClickList = null;
            closeImg.setClickable(false);
            optionDailog.setCancelable(false);
            mListDialog = null;
        });

        try {
            if (!optionDailog.isShowing()) {
                optionDailog.show();
                animation.start();
            }
        } catch (Exception e) {

        }
    }

    private void showDialog(int selectedItemPosition, String keyToShowAsTitle, boolean isStringArrayList) {
        if (listDialog == null) {
            return;
        }
        View contentView = View.inflate(mContext, R.layout.dialog_filter, null);
        if (generalFunctions.isRTLmode()) {
            contentView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        listDialog.setContentView(contentView);
        listDialog.setCancelable(isCancelable);
        CustomDialogListAdapter listAdapter;
        if (isStringArrayList) {
            listAdapter = new CustomDialogListAdapter(mContext, stringArrayList, selectedItemPosition, isStringArrayList, isImageinList);
        } else {
            listAdapter = new CustomDialogListAdapter(mContext, arrayList, selectedItemPosition, keyToShowAsTitle, isImageinList);
        }


        ImageView closeImg = (ImageView) listDialog.findViewById(R.id.closeImg);
        MTextView titleTxt = (MTextView) listDialog.findViewById(R.id.TitleTxt);
        titleTxt.setText(title);
        closeImg.setOnClickListener(v -> listDialog.dismiss());

        RecyclerView mRecyclerView = listDialog.findViewById(R.id.mRecyclerView);
        mRecyclerView.setAdapter(listAdapter);
        mRecyclerView.scrollToPosition(selectedItemPosition);
        listAdapter.setOnItemClickList(position -> {
            listDialog.dismiss();
            mListDialog = null;
            if (onItemClickList != null) {
                onItemClickList.onItemClick(position);
            }
        });


        try {
            if (!listDialog.isShowing()) {
                listDialog.show();
            }
        } catch (Exception e) {

        }
    }

    public interface OnItemClickList {
        void onItemClick(int position);
    }

    public static OpenListView getInstance(Context mContext, String title, ArrayList<HashMap<String, String>> arrayList, OpenDirection openDirection, boolean isCancelable, OnItemClickList onItemClickList) {
        return new OpenListView(mContext, title, arrayList, openDirection, isCancelable, onItemClickList);
    }

    public static OpenListView getInstance(Context mContext, String title, ArrayList<String> arrayList, OpenDirection openDirection, boolean isCancelable, boolean isStringArrayList, OnItemClickList onItemClickList) {
        return new OpenListView(mContext, title, arrayList, openDirection, isCancelable, onItemClickList, isStringArrayList);
    }

    public static OpenListView getInstance(Context mContext, String title, ArrayList<HashMap<String, String>> arrayList, OpenDirection openDirection, boolean isCancelable, OnItemClickList onItemClickList, boolean isImageinList, String subTitleTxt, boolean isloading) {
        return new OpenListView(mContext, title, arrayList, openDirection, isCancelable, isImageinList, onItemClickList, subTitleTxt, isloading);
    }
}