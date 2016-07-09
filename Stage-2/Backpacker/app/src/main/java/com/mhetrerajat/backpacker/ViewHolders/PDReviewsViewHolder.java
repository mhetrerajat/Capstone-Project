package com.mhetrerajat.backpacker.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mhetrerajat.backpacker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rajatmhetre on 26/06/16.
 */
public class PDReviewsViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.place_details_reviews_author_name) public TextView mPDReviewsAuthorName;
    @BindView(R.id.place_details_reviews_text) public TextView mPDReviewsText;
    @BindView(R.id.place_details_reviews_rating) public TextView mPDReviewsRating;
    @BindView(R.id.place_details_reviews_rating_bar) public RatingBar mPDReviewsRatingBar;

    public PDReviewsViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
