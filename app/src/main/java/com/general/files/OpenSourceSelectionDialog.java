package com.general.files;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.solicity.provider.BuildConfig;
import com.solicity.provider.R;
import com.utils.Logger;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OpenSourceSelectionDialog implements Runnable {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int SELECT_PICTURE = 2;
    public static final int SELECT_FILE_BROWSABLE = 3;
    private static final String IMAGE_DIRECTORY_NAME = "Temp";
    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    private Uri fileUri;
    private String pathForCameraURI = "";

    Context mContext;
    GeneralFunctions generalFunc;

    boolean isShowFileBrowser;

    OnFileUriGenerateListener onFileUriGenerateListener;

    public OpenSourceSelectionDialog(Context mContext, GeneralFunctions generalFunc, boolean isShowFileBrowser) {
        this.mContext = mContext;
        this.generalFunc = generalFunc;
        this.isShowFileBrowser = isShowFileBrowser;
    }

    @Override
    public void run() {
        if (mContext instanceof Activity == false) {
            Logger.e(BuildConfig.APPLICATION_ID, "Context must be instance of Activity OR Fragment");
            return;
        }

        final Dialog dialog_img_update = new Dialog(mContext, R.style.ImageSourceDialogStyle);

        dialog_img_update.setContentView(R.layout.design_image_source_select_doc);

        MTextView chooseImgHTxt = (MTextView) dialog_img_update.findViewById(R.id.chooseImgHTxt);
        chooseImgHTxt.setText(generalFunc.retrieveLangLBl("Choose Category", "LBL_CHOOSE_CATEGORY"));

        LinearLayout cameraIconImgView = (LinearLayout) dialog_img_update.findViewById(R.id.cameraView);
        LinearLayout galleryIconImgView = (LinearLayout) dialog_img_update.findViewById(R.id.galleryView);
        LinearLayout docIconImgView = (LinearLayout) dialog_img_update.findViewById(R.id.docView);

        MButton closeDialogImgView = ((MaterialRippleLayout) dialog_img_update.findViewById(R.id.closeDialogImgView)).getChildView();
        MTextView cameraTxt = (MTextView) dialog_img_update.findViewById(R.id.cameraTxt);
        MTextView galleryTxt = (MTextView) dialog_img_update.findViewById(R.id.galleryTxt);
        MTextView docTxt = (MTextView) dialog_img_update.findViewById(R.id.docTxt);

        cameraTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CAMERA"));
        galleryTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GALLERY"));
        docTxt.setText(generalFunc.retrieveLangLBl("Document", "LBL_DOCUMET"));
        closeDialogImgView.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_CANCEL_TXT"));
        closeDialogImgView.setOnClickListener(v -> {

            if (dialog_img_update != null) {
                dialog_img_update.cancel();
            }
        });

        int strokeColor=Color.parseColor("#00000000");
        int backColor=mContext.getResources().getColor(R.color.appThemeColor_Dark_1);
        int textColor=mContext.getResources().getColor(R.color.appThemeColor_TXT_1);
        int btnRadius=Utils.dipToPixels(mContext, 25);

      //  new CreateRoundedView(backColor, btnRadius, 0, strokeColor, cameraIconImgView);
      //  new CreateRoundedView(backColor, btnRadius, 0, strokeColor, galleryIconImgView);
      //  new CreateRoundedView(backColor, btnRadius, 0, strokeColor, docIconImgView);

       // cameraIconImgView.setColorFilter(textColor);
      //  galleryIconImgView.setColorFilter(textColor);
      //  docIconImgView.setColorFilter(textColor);

        if (isShowFileBrowser) {
            docIconImgView.setVisibility(View.VISIBLE);
        }

        docIconImgView.setOnClickListener(v -> {
            if (dialog_img_update != null) {
                dialog_img_update.cancel();
            }
            openFileBrowser();
        });

        cameraIconImgView.setOnClickListener(v -> {

            if (dialog_img_update != null) {
                dialog_img_update.cancel();
            }

            if (!isDeviceSupportCamera()) {
                if (mContext instanceof Activity) {
                    generalFunc.showMessage(generalFunc.getCurrentView((Activity) mContext), generalFunc.retrieveLangLBl("", "LBL_NOT_SUPPORT_CAMERA_TXT"));
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_NOT_SUPPORT_CAMERA_TXT"));
                }
            } else {
                chooseFromCamera();
            }
        });

        galleryIconImgView.setOnClickListener(v -> {

            if (dialog_img_update != null) {
                dialog_img_update.cancel();
            }

            chooseFromGallery();

        });

        dialog_img_update.setCanceledOnTouchOutside(true);

        Window window = dialog_img_update.getWindow();
        window.setGravity(Gravity.BOTTOM);

        window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        dialog_img_update.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (generalFunc.isRTLmode()) {
            dialog_img_update.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog_img_update.show();
    }

    public void openFileBrowser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        ((Activity) mContext).startActivityForResult(Intent.createChooser(intent, generalFunc.retrieveLangLBl("", "LBL_SELECT_FILE")), SELECT_FILE_BROWSABLE);
    }

    public void chooseFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ((Activity) mContext).startActivityForResult(Intent.createChooser(intent, generalFunc.retrieveLangLBl("", "LBL_SELECT_IMAGE")), SELECT_PICTURE);
    }


    public void chooseFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        if (onFileUriGenerateListener != null) {
            onFileUriGenerateListener.onFileUriGenerated(fileUri, pathForCameraURI);
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        ((Activity) mContext).startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }


    private boolean isDeviceSupportCamera() {
        if (mContext.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public Uri getOutputMediaFileUri(int type) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

            ContentResolver resolver = mContext.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_" + timeStamp + ".jpg");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        } else {
            return FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(type));
        }
    }

    private File getOutputMediaFile(int type) {
        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        pathForCameraURI = mediaFile.getAbsolutePath();

        return mediaFile;
    }

    public interface OnFileUriGenerateListener {
        void onFileUriGenerated(Uri fileUri, String pathForCameraImage);
    }

    public void setOnFileUriGenerateListener(OnFileUriGenerateListener onFileUriGenerateListener) {
        this.onFileUriGenerateListener = onFileUriGenerateListener;
    }
}
