package ViewPagerCards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;


import com.solicity.provider.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.utils.Logger;
import com.utils.Utils;
import com.view.MTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CardImagePagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<String> mData;
    private float mBaseElevation;
    Context mContext;
    int margin;
    int width;
    int bannerWidth;
    int bannerHeight;
    ArrayList<HashMap<String, String>> imageList;

    public CardImagePagerAdapter(Context context) {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
        imageList = new ArrayList<>();
        this.mContext = context;

        margin = mContext.getResources().getDimensionPixelSize(R.dimen._15sdp);
        width = margin * 2;
       int height=mContext.getResources().getDimensionPixelSize(R.dimen._155sdp);
        bannerWidth = Utils.getWidthOfBanner(mContext, width);
       // bannerHeight = Utils.getHeightOfBanner(mContext, 0, "16:9");
        bannerHeight = Utils.getHeightOfBanner(mContext, 0, bannerWidth/height+"");

    }

    public void addCardItem(HashMap<String, String> item, Context context) {
        mViews.add(null);
        imageList.add(item);
        this.mContext = context;
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.img_adapter, container, false);
        container.addView(view);
        // bind(imageList.get(position), view);

        ImageView imageView = view.findViewById(R.id.imagView);

        MTextView TitleTxt = view.findViewById(R.id.TitleTxt);
        MTextView subTitleTxt = view.findViewById(R.id.subTitleTxt);
        TitleTxt.setText(imageList.get(position).get("tTitle"));
        subTitleTxt.setText(imageList.get(position).get("tSubtitle"));


        position = position % mViews.size();
        Logger.d("BannerImageSize", "::"+ Utils.getResizeImgURL(mContext, imageList.get(position).get("vImage"),
                bannerWidth,
                bannerHeight));

        Picasso.get()
                .load(Utils.getResizeImgURL(mContext, imageList.get(position).get("vImage"),
                                bannerWidth,
                                bannerHeight))
                .fit()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(String item, View view) {

    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

}
