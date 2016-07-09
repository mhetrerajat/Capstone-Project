package com.mhetrerajat.backpacker.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.mhetrerajat.backpacker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rajatmhetre on 25/06/16.
 */
public class PDPhotosViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.place_details_photos_item_image) public ImageView mPDPhotosItemImage;

    public PDPhotosViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
