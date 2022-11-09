package com.countryview.view;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.countryview.model.Country;

import com.solicity.provider.R;
import com.general.files.KmStickyListener;
import com.squareup.picasso.Picasso;
import com.utils.Logger;
import com.view.MTextView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements KmStickyListener {




    @Override
    public Integer getHeaderPositionForItem(Integer itemPosition) {
        Integer headerPosition = 0;
        for (Integer i = itemPosition; i > 0; i--) {
            if (isHeader(i)) {
                headerPosition = i;
                return headerPosition;
            }
        }
        return headerPosition;
    }


    @Override
    public Integer getHeaderLayout(Integer headerPosition) {
        return R.layout.country_header_list_item;
    }

    @Override
    public void bindHeaderData(View header, Integer headerPosition) {

        MTextView txt = header.findViewById(R.id.txt);
        txt.setText(mCountryList.get(headerPosition).getName());


    }

    @Override
    public Boolean isHeader(Integer itemPosition) {

        return getItemViewType(itemPosition) == (TYPE_HEADER);
    }

    public interface OnCountryClickListener {
        void onCountrySelected(Country country);
    }

    private List<Country> mCountryList;
    private OnCountryClickListener mOnCountryClickListener;
    private static boolean sShowingFlag;
    private static boolean sShowingDialCode;
    private static String sPreselectCountry;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_ITEM = 2;


    RecyclerViewAdapter(List<Country> countryList, String preselectCountry, boolean showingFlag, boolean showingDialCode) {

        mCountryList = countryList;
        sShowingFlag = showingFlag;
        sShowingDialCode = showingDialCode;
        sPreselectCountry = preselectCountry;
    }

    void setListener(OnCountryClickListener onCountryClickListener) {

        mOnCountryClickListener = onCountryClickListener;

    }

    @Override
    public int getItemViewType(int position) {
        if (mCountryList.get(position).getmViewType().equalsIgnoreCase("Header")) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;
        if (viewType == TYPE_HEADER) {

            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.country_header_list_item, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_country, parent, false);
            return new CountryHolder(itemView, position -> {
                mOnCountryClickListener.onCountrySelected(mCountryList.get(position));
                itemView.setSelected(true);
                sPreselectCountry = mCountryList.get(position).getName();
                notifyDataSetChanged();
            });
        }

        return new HeaderHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CountryHolder) {

            final CountryHolder countryholder = (CountryHolder) holder;
            countryholder.mTextViewName.setText(mCountryList.get(position).getName());
            countryholder.mTextViewCode.setText("+" + mCountryList.get(position).getDialCode());

            Picasso.get().load(mCountryList.get(position).getFlagName()).into(countryholder.mImageViewFlag);
            if (sPreselectCountry != null)
                holder.itemView.setSelected(false);
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = holder.itemView.getContext().getTheme();
            theme.resolveAttribute(R.attr.rowBackgroundColor, typedValue, true);
            @ColorInt int color = typedValue.data;
            holder.itemView.setBackgroundColor(color);
            if (sShowingFlag)
                countryholder.mImageViewFlag.setVisibility(View.VISIBLE);
            else
                countryholder.mImageViewFlag.setVisibility(View.GONE);
        } else {

            final HeaderHolder headerHolder = (HeaderHolder) holder;
            headerHolder.txt.setText(mCountryList.get(position).getName());







        }

    }

//    @Override
//    public void onBindViewHolder(@NonNull CountryHolder holder, int position) {
//
//
//
//        holder.mTextViewName.setText(mCountryList.get(position).getName());
//        holder.mTextViewCode.setText("+"+mCountryList.get(position).getDialCode());
//
//        Picasso.get().load(mCountryList.get(position).getFlagName()).into( holder.mImageViewFlag);
//        if (sPreselectCountry != null)
//                holder.itemView.setSelected(false);
//                TypedValue typedValue = new TypedValue();
//                Resources.Theme theme = holder.itemView.getContext().getTheme();
//                theme.resolveAttribute(R.attr.rowBackgroundColor, typedValue, true);
//                @ColorInt int color = typedValue.data;
//                holder.itemView.setBackgroundColor(color);
//        if (sShowingFlag)
//            holder.mImageViewFlag.setVisibility(View.VISIBLE);
//        else
//            holder.mImageViewFlag.setVisibility(View.GONE);
//    }

    @Override
    public int getItemCount() {

        if (mCountryList == null) {
            return 0;
        }
        return mCountryList.size();
    }

    void setCountries(List<Country> countries) {
        Logger.d("TestCountryList", "::" + countries.size());

        mCountryList = countries;
        notifyDataSetChanged();
    }

    static class CountryHolder extends RecyclerView.ViewHolder {

        private interface OnItemClickListener {
            void onItemSelected(int position);
        }

        private TextView mTextViewName;
        private TextView mTextViewCode;
        private ImageView mImageViewFlag;

        CountryHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {

            super(itemView);

            mTextViewName = itemView.findViewById(R.id.textview_name);
            mTextViewCode = itemView.findViewById(R.id.textview_code);
            mImageViewFlag = itemView.findViewById(R.id.imageview_flag);

            if (sShowingFlag) {
                mImageViewFlag.setVisibility(View.GONE);
            }
            if (!sShowingDialCode) {
                mTextViewCode.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(v -> onItemClickListener.onItemSelected(getAdapterPosition()));
        }
    }

    class HeaderHolder extends RecyclerView.ViewHolder {


        private MTextView txt;
        private CardView cardshadow;

        public HeaderHolder(@NonNull View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.txt);
            cardshadow = itemView.findViewById(R.id.cardshadow);
        }
    }


}
