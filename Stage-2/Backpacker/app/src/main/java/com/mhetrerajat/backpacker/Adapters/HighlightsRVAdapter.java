package com.mhetrerajat.backpacker.Adapters;

import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mhetrerajat.backpacker.Activity.PlaceDetailsActivity;
import com.mhetrerajat.backpacker.Db.HighlightsModel;
import com.mhetrerajat.backpacker.Db.DbHelper;
import com.mhetrerajat.backpacker.Events.CurrentPlaceEvent;
import com.mhetrerajat.backpacker.R;
import com.mhetrerajat.backpacker.ViewHolders.HighLightsViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by rajatmhetre on 27/06/16.
 */
public class HighlightsRVAdapter extends RecyclerView.Adapter<HighLightsViewHolder> {

    private String TAG = HighlightsRVAdapter.class.getSimpleName();

    private Cursor mDataMatrixCursor;
    private CoordinatorLayout mHighlightCL;
    private StringBuilder PHOTO_URL, RATING;

    public HighlightsRVAdapter(Cursor mDataMatrixCursor, CoordinatorLayout mHighlightCL) {
        this.mDataMatrixCursor = mDataMatrixCursor;
        this.mHighlightCL = mHighlightCL;
    }

    @Override
    public HighLightsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.highlights_list_item_cv, parent, false);
        HighLightsViewHolder mHighlightVH = new HighLightsViewHolder(mView);
        return mHighlightVH;
    }

    @Override
    public void onBindViewHolder(HighLightsViewHolder holder, int position) {
        mDataMatrixCursor.moveToPosition(position);

        if (mDataMatrixCursor.getString(mDataMatrixCursor.getColumnIndex(DbHelper.HIGHLIGHTS_PHOTO_REFERENCE)) != null){
            PHOTO_URL = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?photoreference=")
                    .append(mDataMatrixCursor.getString(mDataMatrixCursor.getColumnIndex(DbHelper.HIGHLIGHTS_PHOTO_REFERENCE)))
                    .append("&maxheight=")
                    .append(256)
                    .append("&key=")
                    .append(mHighlightCL.getResources().getString(R.string.REMOTE_API_KEY));

            Picasso.with(mHighlightCL.getContext())
                    .load(PHOTO_URL.toString())
                    .placeholder(R.drawable.question)
                    .into(holder.mHighlightItemImage);
        }else{
            Picasso.with(mHighlightCL.getContext())
                    .load(R.drawable.question)
                    .into(holder.mHighlightItemImage);
        }

        holder.mHighlightItemName.setText(mDataMatrixCursor.getString(mDataMatrixCursor.getColumnIndex(DbHelper.HIGHLIGHTS_NAME)));
        if(mDataMatrixCursor.getString(mDataMatrixCursor.getColumnIndex(DbHelper.HIGHLIGHTS_STORE_TYPE)) != null){
            holder.mHighlightItemStoreType.setText(mDataMatrixCursor.getString(mDataMatrixCursor.getColumnIndex(DbHelper.HIGHLIGHTS_STORE_TYPE)));
        }
        holder.mHighlightItemVicinity.setText(mDataMatrixCursor.getString(mDataMatrixCursor.getColumnIndex(DbHelper.HIGHLIGHTS_VICINITY)));

        if(mDataMatrixCursor.getString(mDataMatrixCursor.getColumnIndex(DbHelper.HIGHLIGHTS_RATING)) != null){
            RATING = new StringBuilder("Rating")
                    .append(" : ")
                    .append(mDataMatrixCursor.getString(mDataMatrixCursor.getColumnIndex(DbHelper.HIGHLIGHTS_RATING)));
            holder.mHighlightItemRatingBar.setRating(Float.parseFloat(mDataMatrixCursor.getString(mDataMatrixCursor.getColumnIndex(DbHelper.HIGHLIGHTS_RATING)).toString()));
        }else{
            RATING = new StringBuilder("Rating")
                    .append(" : ")
                    .append("Not Available");
        }

        holder.mHighlightItemRating.setText(RATING.toString());
        final String place_id = mDataMatrixCursor.getString(mDataMatrixCursor.getColumnIndex(DbHelper.HIGHLIGHTS_PLACE_ID));

        Log.d(TAG, "onBindViewHolder: " + place_id);
        
        holder.setClickListener(new HighLightsViewHolder.ClickListener() {
            @Override
            public void onClick(View v, int position, boolean isLongClick) {
                EventBus.getDefault().postSticky(new CurrentPlaceEvent(place_id));
                mHighlightCL.getContext().startActivity(new Intent(mHighlightCL.getContext(), PlaceDetailsActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataMatrixCursor.getCount();
    }
}
