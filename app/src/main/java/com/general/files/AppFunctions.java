package com.general.files;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;

import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Xml;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;

import com.solicity.provider.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.utils.CommonUtilities;
import com.utils.Logger;
import com.utils.NavigationSensor;
import com.utils.Utils;
import com.view.SelectableRoundedImageView;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import static android.text.TextUtils.isEmpty;

public class AppFunctions {
    Context mContext;
    GeneralFunctions generalFunc;

    public AppFunctions(Context mContext) {
        this.mContext = mContext;
        generalFunc = new GeneralFunctions(mContext);
    }

    public static String substringAfterLast(String str, String separator) {
               if (isEmpty(str)) {
                        return str;
                    }
                if (isEmpty(separator)) {
                        return "";
                    }
               int pos = str.lastIndexOf(separator);
                if (pos == -1 || pos == (str.length() - separator.length())) {
                        return "";
                    }
                return str.substring(pos + separator.length());
            }

    public void checkProfileImage(SelectableRoundedImageView userProfileImgView, String userProfileJson, String imageKey) {
        String vImgName_str = generalFunc.getJsonValue(imageKey, userProfileJson);

        Picasso.get().load(CommonUtilities.PROVIDER_PHOTO_PATH + generalFunc.getMemberId() + "/" + vImgName_str).placeholder(R.mipmap.ic_no_pic_user).error(R.mipmap.ic_no_pic_user).into(userProfileImgView);
    }

    public void checkProfileImage(SelectableRoundedImageView userProfileImgView, JSONObject userProfileJsonObj, String imageKey, ImageView profilebackimage) {
        String vImgName_str = generalFunc.getJsonValueStr(imageKey, userProfileJsonObj);

        Picasso.get().load(CommonUtilities.PROVIDER_PHOTO_PATH + generalFunc.getMemberId() + "/" + vImgName_str).placeholder(R.mipmap.ic_no_pic_user).error(R.mipmap.ic_no_pic_user).into(userProfileImgView);

        Picasso.get().load(CommonUtilities.PROVIDER_PHOTO_PATH + generalFunc.getMemberId() + "/" + vImgName_str).placeholder(R.mipmap.ic_no_pic_user).error(R.mipmap.ic_no_pic_user).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                Utils.setBlurImage(bitmap, profilebackimage);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    public CameraUpdate getCameraPosition(Location location, GoogleMap gMap) {
        //if (isFirst) {
        double ZoomLevel = Utils.defaultZomLevel;
        // }

        float bearing = NavigationSensor.getInstance().getCurrentBearing();

        Logger.e("CAMERA_BEARING", "::"+bearing);
        CameraPosition cameraPosition = null;
        if (gMap == null) {
            cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .bearing(bearing != -1 ? bearing : 0)
                    .zoom((float) ZoomLevel)
                    .build();

        } else {
            cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .bearing(bearing != -1 ? bearing : gMap.getCameraPosition().bearing)
                    .zoom((float) ZoomLevel)
                    .build();
        }

        if (cameraPosition == null) {
            return null;
        }

        return (CameraUpdateFactory.newCameraPosition(cameraPosition));


    }


    public boolean checkSinchInstance(SinchService.SinchServiceInterface sinchServiceInterface) {
        boolean isNull = sinchServiceInterface != null && sinchServiceInterface.getSinchClient() != null;
        Logger.d("call", "Instance" + isNull);
        return isNull;
    }

    public static AttributeSet getXmlResource(Context mContext, int resourceId){
        XmlPullParser parser = mContext.getResources().getXml(resourceId);
        try {
            parser.next();
            parser.nextTag();
        } catch (Exception e) {
            e.printStackTrace();
        }

        AttributeSet attr = Xml.asAttributeSet(parser);

        return attr;

//        int count = attr.getAttributeCount();

//        final XmlResourceParser parser = mContext.getResources().getLayout(resourceId);
//        AttributeSet attr = Xml.asAttributeSet(parser);
        /*try {
            return attr;
        } finally {
            parser.close();
        }*/
    }

    public void setOverflowButtonColor(final Toolbar toolbar, final int color) {
        Drawable drawable = toolbar.getOverflowIcon();
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable.mutate(), color);
            toolbar.setOverflowIcon(drawable);
        }
    }

    public static Spanned fromHtml(String html){
        if(!Utils.checkText(html)){
            // return an empty spannable if the html is null
            return new SpannableString("");
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
            // we are using this flag to give a consistent behaviour
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    public boolean isEmailBlankAndOptional(GeneralFunctions generalFunc, String email)
    {
        return generalFunc.retrieveValue("ENABLE_EMAIL_OPTIONAL").equalsIgnoreCase("Yes") && !Utils.checkText(email);

    }

    public String formatNumAsPerCurrency(GeneralFunctions generalFunc,String formatString,String currencySymbol,boolean addCurrency){

        if (generalFunc.retrieveValue("eReverseformattingEnable").equalsIgnoreCase("Yes")) {
            NumberFormat myFormat = NumberFormat.getInstance();
            myFormat.setGroupingUsed(true); // this will also round numbers, 3
            myFormat.setMinimumFractionDigits(2);
//          myFormat.setRoundingMode(RoundingMode.HALF_UP);
            // decimal places
            double[] numbers = {generalFunc.parseDoubleValue(0, formatString)};
            System.out.println("adding commas to number in Java using NumberFormat class");
            // https://www.java67.com/2015/06/how-to-format-numbers-in-java.html
            for (double d : numbers) {
                formatString = myFormat.format(d);
                System.out.println(myFormat.format(d));
            }

            Logger.d("FORMATE", "formatString >> " + "" + formatString);
            String newStr = formatString.replace(",", "_").replace(".", ",").replace("_", ".");
            Logger.d("FORMATE", "formatString After >> " + "" + formatString);
            formatString = newStr;


        }

        if (addCurrency) {
            if (generalFunc.retrieveValue("eReverseSymbolEnable").equalsIgnoreCase("Yes")) {
                formatString = formatString + " " + currencySymbol;
            } else {
                formatString = currencySymbol + " " + formatString;
            }
        }

        return formatString;
    }

    public String convertOtpToMD5(String otpTxt)
    {
        byte[] data = new byte[0];
        try {
            data = otpTxt.getBytes("UTF-8");
            String base64 = Base64.encodeToString(data, Base64.NO_WRAP);
            String nwData=base64+"@"+otpTxt;
            String base64Nw = Base64.encodeToString(nwData.getBytes("UTF-8"), Base64.NO_WRAP);
            return md5(base64Nw);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String md5(String s) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(s.getBytes("UTF-8"));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            Logger.d("MD5_HASH","Converted  Values is ::"+sb.toString());
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        } catch(UnsupportedEncodingException ex){
        }
        return null;
    }

}
