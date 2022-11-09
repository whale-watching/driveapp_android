package com.general.files;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.solicity.provider.R;
import com.view.MTextView;

public class TrendyDialog {

    private static TrendyDialog instance = null;
    private Context currentAct;
    GeneralFunctions generalFunc;

    Drawable imageDrawable;
    int imageBackgroundColor;
    int imageDrawableVisibility = View.VISIBLE;

    Drawable dialogBackDrawable;

    String titleText;
    int titleTextColor;
    int titleTextVisibility = View.VISIBLE;

    String descriptionText;
    int descriptionTextColor;
    int descriptionTextVisibility = View.VISIBLE;

    boolean setCancelable;

    MTextView positiveBtn;
    String positiveBtnText;
    int positiveBtnBackTintColor;
    int positiveBtnTextColor;
    Drawable positiveBtnBackground;
    int positiveButtonVisibility = View.VISIBLE;

    MTextView negativeBtn;
    String negativeBtnText;
    int negativeBtnBackTintColor;
    int negativeBtnTextColor;
    Drawable negativeBtnBackground;
    int negativeButtonVisibility = View.INVISIBLE;

    androidx.appcompat.app.AlertDialog trendyDialog;

    public TrendyDialog(Context currentAct) {
        this.currentAct = currentAct;
        generalFunc = new GeneralFunctions(currentAct);

        dialogBackDrawable = currentAct.getResources().getDrawable(R.drawable.all_roundcurve_card);

        imageBackgroundColor = currentAct.getResources().getColor(R.color.appThemeColor_1);

        titleTextColor = currentAct.getResources().getColor(R.color.black);

        descriptionTextColor = currentAct.getResources().getColor(R.color.black);

        positiveBtnText = generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT");
        positiveBtnTextColor = currentAct.getResources().getColor(R.color.white);
        positiveBtnBackTintColor = currentAct.getResources().getColor(R.color.appThemeColor_1);
        positiveBtnBackground = currentAct.getResources().getDrawable(R.drawable.selected_border);

        negativeBtnText = generalFunc.retrieveLangLBl("Skip", "LBL_SKIP_TXT");
        negativeBtnTextColor = currentAct.getResources().getColor(R.color.appThemeColor_1);
        negativeBtnBackTintColor = currentAct.getResources().getColor(R.color.appThemeColor_1);
        negativeBtnBackground = currentAct.getResources().getDrawable(R.drawable.unselected_border);
    }

    private TrendyDialog getInstance() {
        if (instance == null) {
            instance = new TrendyDialog(MyApp.getInstance().getCurrentAct());
            generalFunc = new GeneralFunctions(currentAct);
        }
        return instance;
    }

    public void setDrawable(Drawable drawable) {
        this.imageDrawable = drawable;
    }

    public void setDialogBackGroundDrawable(Drawable backDrawable) {
        this.dialogBackDrawable = backDrawable;
    }

    public void setTitleTxt(String titleText) {
        this.titleText = titleText;
    }

    public void setTitleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
    }

    public void setTitleTextVisibility(int titleTextVisibility) {
        this.titleTextVisibility = titleTextVisibility;
    }

    public void setDescriptionTxt(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public void setDescriptionTextColor(int DescriptionTextColor) {
        this.descriptionTextColor = descriptionTextColor;
    }

    public void setDescriptionTextVisibility(int descriptionTextVisibility) {
        this.descriptionTextVisibility = descriptionTextVisibility;
    }


    public void setImageBackColor(int backgroundColor) {
        this.imageBackgroundColor = backgroundColor;
    }

    public void setImageVisibility(int imageVisibility) {
        this.imageDrawableVisibility = imageVisibility;
    }

    public void setPositiveBtnBackTintColor(int positiveBtnBackTintColor) {
        this.positiveBtnBackTintColor = positiveBtnBackTintColor;
    }

    public void setPositiveBtnTextColor(int positiveBtnTextColor) {
        this.positiveBtnTextColor = positiveBtnTextColor;
    }

    public void setPositiveBtnBackground(Drawable positiveBtnBackground) {
        this.positiveBtnBackground = positiveBtnBackground;
    }

    public void setPositiveButtonVisibility(int positiveButtonVisibility) {
        this.positiveButtonVisibility = positiveButtonVisibility;
    }

    public void setNegativeBtnBackTintColor(int negativeBtnBackTintColor) {
        this.negativeBtnTextColor = negativeBtnTextColor;
    }


    public void setNegativeBtnText(String negativeBtnText) {
        this.negativeBtnText = negativeBtnText;
    }

    public void setNegativeBtnTextColor(int negativeBtnTextColor) {
        this.negativeBtnTextColor = negativeBtnTextColor;
    }

    public void setNegativeBtnBackground(Drawable negativeBtnBackground) {
        this.negativeBtnBackground = negativeBtnBackground;
    }

    public void setNegativeButtonVisibility(int negativeButtonVisibility) {
        this.negativeButtonVisibility = negativeButtonVisibility;
    }
    public void setCancelable(String setCancelable) {
        this.descriptionText = descriptionText;
    }

    public void setDetails(String titleText, String descriptionText, String buttonText, boolean setCancelable, Drawable drawable) {
        this.titleText = titleText;
        this.descriptionText = descriptionText;
        this.imageDrawable = drawable;
        this.positiveBtnText = buttonText;
        this.setCancelable = setCancelable;
    }


    public void showDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(currentAct);
        LayoutInflater inflater = (LayoutInflater) currentAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.desgin_help_dialog, null);
        builder.setView(dialogView);

        final ImageView iv_image = (ImageView) dialogView.findViewById(R.id.iv_image);
        positiveBtn = (MTextView) dialogView.findViewById(R.id.okTxt);
        negativeBtn = (MTextView) dialogView.findViewById(R.id.skipTxt);
        final MTextView titleTxt = (MTextView) dialogView.findViewById(R.id.titleTxt);
        final MTextView msgTxt = (MTextView) dialogView.findViewById(R.id.msgTxt);
        final LinearLayout imageArea = (LinearLayout) dialogView.findViewById(R.id.imageArea);

        imageArea.setBackgroundColor(imageBackgroundColor);
        iv_image.setVisibility(imageDrawableVisibility);
        if (imageDrawable != null) {
            iv_image.setImageDrawable(imageDrawable);
        }

        titleTxt.setText(titleText);
        titleTxt.setTextColor(titleTextColor);
        titleTxt.setVisibility(titleTextVisibility);

        positiveBtn.setBackground(positiveBtnBackground);
        positiveBtn.setText(positiveBtnText);
        positiveBtn.setTextColor(positiveBtnTextColor);
        positiveBtn.setVisibility(positiveButtonVisibility);

        negativeBtn.setBackground(negativeBtnBackground);
        negativeBtn.setText(negativeBtnText);
        negativeBtn.setTextColor(negativeBtnTextColor);
        negativeBtn.setVisibility(negativeButtonVisibility);

        msgTxt.setText(descriptionText);
        msgTxt.setTextColor(descriptionTextColor);
        msgTxt.setMovementMethod(new ScrollingMovementMethod());
        msgTxt.setVisibility(descriptionTextVisibility);

        trendyDialog = builder.create();

        trendyDialog.setCancelable(setCancelable);

        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(trendyDialog);
        }
        trendyDialog.getWindow().setBackgroundDrawable(dialogBackDrawable);
        trendyDialog.show();

    }

    public TrendyDialog setPositiveBtnClick(@Nullable final Closure btnOkPressed) {

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnOkPressed != null) {
                    btnOkPressed.exec();
                }

                trendyDialog.dismiss();
            }
        });


        return this;
    }

    public TrendyDialog setNegativeBtnClick(@Nullable final Closure btnSkipPressed) {

        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnSkipPressed != null) {
                    btnSkipPressed.exec();
                }

                trendyDialog.dismiss();
            }
        });


        return this;
    }
}
