package com.mhetrerajat.backpacker.Adapters;

import android.database.Cursor;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.text.LoginFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mhetrerajat.backpacker.Db.DbHelper;
import com.mhetrerajat.backpacker.Db.PlaceReviewsModel;
import com.mhetrerajat.backpacker.R;
import com.mhetrerajat.backpacker.ViewHolders.PDReviewsViewHolder;

import java.util.List;

/**
 * Created by rajatmhetre on 26/06/16.
 */
public class PDReviewsRVAdapter extends RecyclerView.Adapter<PDReviewsViewHolder> {

    private String TAG = PDReviewsRVAdapter.class.getSimpleName();

    //private List<PlaceReviewsModel> mPlaceReviewsList;
    private CoordinatorLayout mPlaceDetailsCL;
    private Cursor mDataCursor;

    public PDReviewsRVAdapter(Cursor mDataCursor, CoordinatorLayout mPlaceDetailsCL) {
        this.mDataCursor = mDataCursor;
        this.mPlaceDetailsCL = mPlaceDetailsCL;
    }

    @Override
    public PDReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_details_reviews_item_cv, parent, false);
        PDReviewsViewHolder mPDReviewsVH = new PDReviewsViewHolder(mView);
        return mPDReviewsVH;
    }

    @Override
    public void onBindViewHolder(PDReviewsViewHolder holder, int position) {

        //PlaceReviewsModel mCurrentReview = mPlaceReviewsList.get(position);
        mDataCursor.moveToPosition(position);

        holder.mPDReviewsAuthorName.setText(mDataCursor.getString(mDataCursor.getColumnIndex(DbHelper.PDR_AUTHOR_NAME)));
        holder.mPDReviewsText.setText(mDataCursor.getString(mDataCursor.getColumnIndex(DbHelper.PDR_TEXT)));

        //Log.d(TAG, String.valueOf(mCurrentReview.getRating()));

        if(mDataCursor.getInt(mDataCursor.getColumnIndex(DbHelper.PDR_RATING)) == 0){
            holder.mPDReviewsRating.setText("Rating : Not Available");
            holder.mPDReviewsRatingBar.setRating(0);
        }else{
            holder.mPDReviewsRating.setText(String.valueOf(mDataCursor.getInt(mDataCursor.getColumnIndex(DbHelper.PDR_RATING))));
            holder.mPDReviewsRatingBar.setRating(mDataCursor.getInt(mDataCursor.getColumnIndex(DbHelper.PDR_RATING)));
        }
    }

    @Override
    public int getItemCount() {
        return mDataCursor.getCount();
    }
}
