package com.alixlp.scan.adapter;

import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener {

    private final AdapterView.OnItemClickListener mListener;

    public MyViewHolder(View itemView, AdapterView.OnItemClickListener mListener) {
        super(itemView);
        this.mListener = mListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            int position = getAdapterPosition();
            if (position >= 0) {
                mListener.onItemClick(null, v, position, getItemId());
            }
        }
    }

    private View findViewById(int id) {
        return id == 0 ? itemView : itemView.findViewById(id);
    }

    public MyViewHolder text(int id, CharSequence sequence) {
        View view = findViewById(id);
        if (view instanceof TextView) {
            ((TextView) view).setText(sequence);
        }
        return this;
    }

    public MyViewHolder text(int id, @StringRes int stringRes) {
        View view = findViewById(id);
        if (view instanceof TextView) {
            ((TextView) view).setText(stringRes);
        }
        return this;
    }

    public MyViewHolder textColorId(int id, int colorId) {
        View view = findViewById(id);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(ContextCompat.getColor(view.getContext(), colorId));
        }
        return this;
    }

    /**
     * ImageView 控件
     *
     * @param id
     * @param imageId
     * @return
     */
    public MyViewHolder image(int id, int imageId) {
        View view = findViewById(id);
        if (view instanceof ImageView) {
            ((ImageView) view).setImageResource(imageId);
        }
        return this;
    }
}
