package com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.solicity.provider.R;
import com.view.MTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MoreInstructionAdapter extends RecyclerView.Adapter<MoreInstructionAdapter.ViewHolder> {

    Context mContext;

    ArrayList<HashMap<String, String>> instructionslit;
    View view;
    @NonNull
    private OnItemCheckListener onItemClick;
    public  interface OnItemCheckListener {
        void onItemCheck(HashMap<String, String> item);
    }

    public MoreInstructionAdapter(Context context, ArrayList<HashMap<String, String>> list, @NonNull OnItemCheckListener onItemCheckListener) {
        this.mContext = context;
        this.instructionslit = list;

        this.onItemClick = onItemCheckListener;

    }

    @Override
    public MoreInstructionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(mContext).inflate(R.layout.item_instructions, parent, false);


        return new MoreInstructionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoreInstructionAdapter.ViewHolder holder, final int position) {

        HashMap<String, String> map=instructionslit.get(position);
        String tTitle = map.get("tTitle");
        String tDescription = map.get("tDescription");
        String ePreferenceFor = map.get("ePreferenceFor");
        String eImageUpload = map.get("eImageUpload");
        String iDisplayOrder = map.get("iDisplayOrder");
        String eContactLess = map.get("eContactLess");
        String eStatus = map.get("eStatus");
        String iPreferenceId = map.get("iPreferenceId");

        holder.institle.setText(tTitle);
        holder.inssubtitle.setText(tDescription);

        /*if (eImageUpload.equalsIgnoreCase("Yes")){
            holder.statusArea.setVisibility(View.VISIBLE);
            holder.statusArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }else {
            holder.statusArea.setVisibility(View.VISIBLE);
        }*/
/*
        holder.instructioncb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    if (eContactLess.equalsIgnoreCase("Yes")){
                        onItemClick.onItemCheck(map);
                    }
                } else {

                }
            }
        });*/


    }

    @Override
    public int getItemCount() {
        return instructionslit.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        MTextView institle;
        MTextView inssubtitle;

        LinearLayout instructioncbLayout;
        LinearLayout statusArea;

        public ViewHolder(View itemView) {
            super(itemView);

            institle = (MTextView) itemView.findViewById(R.id.institle);
            inssubtitle = (MTextView) itemView.findViewById(R.id.inssubtitle);

            instructioncbLayout = (LinearLayout) itemView.findViewById(R.id.instructioncbLayout);
            statusArea = (LinearLayout) itemView.findViewById(R.id.statusArea);

        }
    }

}