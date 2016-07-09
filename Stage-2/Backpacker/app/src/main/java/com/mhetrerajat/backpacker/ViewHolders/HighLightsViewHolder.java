package com.mhetrerajat.backpacker.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mhetrerajat.backpacker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rajatmhetre on 27/06/16.
 */
public class HighLightsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    @BindView(R.id.highlights_item_image)
    public ImageView mHighlightItemImage;

    @BindView(R.id.highlights_item_name)
    public TextView mHighlightItemName;

    @BindView(R.id.highlights_item_store_type)
    public TextView mHighlightItemStoreType;

    @BindView(R.id.highlights_item_vicinity)
    public TextView mHighlightItemVicinity;

    @BindView(R.id.highlights_item_rating)
    public TextView mHighlightItemRating;

    @BindView(R.id.highlights_item_rating_bar)
    public RatingBar mHighlightItemRatingBar;

    private ClickListener clickListener;

    public HighLightsViewHolder(View itemView) {
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
