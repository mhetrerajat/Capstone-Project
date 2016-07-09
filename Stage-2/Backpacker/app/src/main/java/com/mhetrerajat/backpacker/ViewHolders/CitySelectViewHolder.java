package com.mhetrerajat.backpacker.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mhetrerajat.backpacker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rajatmhetre on 24/06/16.
 */
public class CitySelectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    @BindView(R.id.city_select_image) public ImageView mCitySelectImage;
    @BindView(R.id.city_select_name) public TextView mCitySelectName;
    @BindView(R.id.city_select_formatted_addr) public TextView mCitySelectFormattedAddr;

    private ClickListener clickListener;

    public CitySelectViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);

        itemView.setOnClickListener(this);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View view) {
        clickListener.onClick(view, getPosition(), false);
    }

    public interface ClickListener {
        public void onClick(View v, int position, boolean isLongClick);

    }
}
