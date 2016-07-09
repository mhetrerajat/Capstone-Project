package com.mhetrerajat.backpacker.Adapters;

import android.database.Cursor;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mhetrerajat.backpacker.Db.DbHelper;
import com.mhetrerajat.backpacker.Db.PlacePhotoModel;
import com.mhetrerajat.backpacker.R;
import com.mhetrerajat.backpacker.ViewHolders.PDPhotosViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by rajatmhetre on 25/06/16.
 */
public class PDPhotosRVAdapter extends RecyclerView.Adapter<PDPhotosViewHolder> {

    private String TAG = PDPhotosRVAdapter.class.getSimpleName();

    //private List<PlacePhotoModel> mPlacePhotosList;
    private CoordinatorLayout mPlaceDetailsCL;
    private StringBuilder PHOTO_URL;
    private Cursor mDataCursor;

    public PDPhotosRVAdapter(Cursor mDataCursor, CoordinatorLayout mPlaceDetailsCL) {
        this.mDataCursor = mDataCursor;
        this.mPlaceDetailsCL = mPlaceDetailsCL;
    }

    @Override
    public PDPhotosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_details_photos_item_cv, parent, false);
        PDPhotosViewHolder mPDPhotosVH = new PDPhotosViewHolder(mView);
        return mPDPhotosVH;
    }

    @Override
    public void onBindViewHolder(PDPhotosViewHolder holder, int position) {

        mDataCursor.moveToPosition(position);

        //PlacePhotoModel mCurrentPDPhotoModel = mPlacePhotosList.get(position);
        PHOTO_URL = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?photoreference=")
                .append(mDataCursor.getString(mDataCursor.getColumnIndex(DbHelper.PDP_PHOTO_REFERENCE)))
                .append("&maxheight=")
                .append(500)
                .append("&key=")
                .append(mPlaceDetailsCL.getResources().getString(R.string.REMOTE_API_KEY));
        Picasso.with(mPlaceDetailsCL.getContext())
                .load(PHOTO_URL.toString())
                .error(R.drawable.error)
                .into(holder.mPDPhotosItemImage);

        Log.d(TAG, PHOTO_URL.toString());
    }

    @Override
    public int getItemCount() {
        return mDataCursor.getCount();
    }
}
