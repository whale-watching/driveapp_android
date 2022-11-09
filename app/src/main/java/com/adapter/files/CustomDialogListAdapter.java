package com.adapter.files;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.solicity.provider.R;
import com.utils.Logger;
import com.view.MTextView;
import com.view.anim.loader.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomDialogListAdapter extends RecyclerView.Adapter<CustomDialogListAdapter.ViewHolder> {

    Context mContext;
    private ArrayList<HashMap<String, String>> listItem;
    OnItemClickList onItemClickList;
    int selectedItemPosition;
    String keyToShowAsTitle;

    int selectedColor;
    int nonSelectedColor;

    boolean onlyStringArrayList;
    private ArrayList<String> stringListItem;
    boolean isImageInList = false;
    int selpos = -1;

    public CustomDialogListAdapter(Context mContext, ArrayList<HashMap<String, String>> listItem, int selectedItemPosition, String keyToShowAsTitle, boolean isImageInList) {
        this.mContext = mContext;
        this.listItem = listItem;
        this.selectedItemPosition = selectedItemPosition;
        this.keyToShowAsTitle = keyToShowAsTitle;

        selectedColor = mContext.getResources().getColor(R.color.appThemeColor_1);
        nonSelectedColor = Color.parseColor("#646464");
        this.isImageInList = isImageInList;
    }

    public CustomDialogListAdapter(Context mContext, ArrayList<String> stringListItem, int selectedItemPosition, boolean onlyStringArrayList, boolean isImageInList) {
        this.mContext = mContext;
        this.stringListItem = stringListItem;
        this.selectedItemPosition = selectedItemPosition;
        this.onlyStringArrayList = onlyStringArrayList;

        selectedColor = mContext.getResources().getColor(R.color.appThemeColor_1);
        nonSelectedColor = Color.parseColor("#646464");
        this.isImageInList = isImageInList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_design, viewGroup, false);
        if (isImageInList) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_image_list_design, viewGroup, false);
        }

        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {


        viewHolder.rowTitleTxtView.setText(onlyStringArrayList ? stringListItem.get(i) : listItem.get(i).get(keyToShowAsTitle));

        if (isImageInList) {
            viewHolder.codeTxt.setText(listItem.get(i).get("vCode"));
            viewHolder.codeArea.getBackground().setTint(Color.parseColor(listItem.get(i).get("vService_BG_color")));
        }

        if (selectedItemPosition == i) {
            viewHolder.rowTitleTxtView.setTextColor(selectedColor);
            viewHolder.rowTitleTxtView.setTypeface(viewHolder.rowTitleTxtView.getTypeface(), Typeface.BOLD);
            if (isImageInList) {
                viewHolder.imgCheck.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
            } else {
                viewHolder.imgCheck.setColorFilter(selectedColor, android.graphics.PorterDuff.Mode.SRC_IN);
            }
        } else {
            viewHolder.rowTitleTxtView.setTextColor(nonSelectedColor);
            viewHolder.rowTitleTxtView.setTypeface(viewHolder.rowTitleTxtView.getTypeface(), Typeface.NORMAL);
            if (isImageInList) {
                viewHolder.imgCheck.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
            } else {
                viewHolder.imgCheck.setColorFilter(nonSelectedColor, android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }

        /*String selectedSortValue = listItem.get(i).get("selectedSortValue");


        if (!selectedSortValue.equals("") && selectedSortValue.equals(listItem.get(i).get("vName"))) {
            viewHolder.rowTitleTxtView.setTextColor(mContext.getResources().getColor(R.color.appThemeColor_1));
            viewHolder.rowTitleTxtView.setTypeface(viewHolder.rowTitleTxtView.getTypeface(), Typeface.BOLD);
            viewHolder.imgCheck.setColorFilter(ContextCompat.getColor(mContext, R.color.appThemeColor_1), android.graphics.PorterDuff.Mode.SRC_IN);
        }*/


        if (isImageInList) {
            if (selpos != -1 && selpos == i) {
                viewHolder.progress.setVisibility(View.VISIBLE);
                viewHolder.imgCheck.setVisibility(View.GONE);
            } else {

                viewHolder.rowTitleTxtView.setTextColor(nonSelectedColor);
                viewHolder.rowTitleTxtView.setTypeface(viewHolder.rowTitleTxtView.getTypeface(), Typeface.NORMAL);
                viewHolder.progress.setVisibility(View.GONE);
                if (isprogressEnable) {
                    viewHolder.imgCheck.setVisibility(View.GONE);
                } else {
                    viewHolder.imgCheck.setVisibility(View.VISIBLE);
                }
            }
        }
        viewHolder.mainArea.setOnClickListener(v -> {
            if (onItemClickList != null) {
                if (i != selectedItemPosition) {
                    onItemClickList.onItemClick(i);
                    if (isImageInList) {
                        Logger.d("CHECKPOS", "::" + i + "::" + selpos + "::" + selectedItemPosition);
                        if (selpos == -1 || selpos == selectedItemPosition) {
                            selpos = i;
                            viewHolder.progress.setVisibility(View.VISIBLE);
                            viewHolder.imgCheck.setVisibility(View.GONE);
                            isprogressEnable = true;
                            notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    boolean isprogressEnable = false;

    @Override
    public int getItemCount() {
        return onlyStringArrayList ? stringListItem.size() : listItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MTextView rowTitleTxtView;
        ImageView imgCheck;
        LinearLayout mainArea;
        MTextView codeTxt;
        LinearLayout codeArea;
        AVLoadingIndicatorView progress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowTitleTxtView = itemView.findViewById(R.id.rowTitleTxtView);
            imgCheck = itemView.findViewById(R.id.imgCheck);
            mainArea = itemView.findViewById(R.id.mainArea);

            if (isImageInList) {
                codeTxt = itemView.findViewById(R.id.codeTxt);
                codeArea = itemView.findViewById(R.id.codeArea);
                progress = itemView.findViewById(R.id.progress);
            }
        }
    }

    public void setOnItemClickList(OnItemClickList onItemClickList) {
        this.onItemClickList = onItemClickList;
    }

    public interface OnItemClickList {
        void onItemClick(int position);
    }
}
