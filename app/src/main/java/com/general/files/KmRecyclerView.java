package com.general.files;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class KmRecyclerView extends RecyclerView {
    public KmRecyclerView(@NonNull Context context) {
        super(context);
    }

    public KmRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public KmRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        super.setAdapter(adapter);
        if (getAdapter() instanceof KmStickyListener) {
            setStickyItemDecoration();
        }
    }

    private void setStickyItemDecoration() {
        KmHeaderItemDecoration itemDecoration = new KmHeaderItemDecoration((KmStickyListener) getAdapter());
        this.addItemDecoration(itemDecoration);
    }

}
