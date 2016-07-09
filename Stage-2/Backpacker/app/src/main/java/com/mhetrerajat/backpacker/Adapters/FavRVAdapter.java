package com.mhetrerajat.backpacker.Adapters;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mhetrerajat.backpacker.Activity.PlaceDetailsActivity;
import com.mhetrerajat.backpacker.Db.DbHelper;
import com.mhetrerajat.backpacker.Db.FavouriteModel;
import com.mhetrerajat.backpacker.Events.CurrentPlaceEvent;
import com.mhetrerajat.backpacker.R;
import com.mhetrerajat.backpacker.ViewHolders.FavViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by rajatmhetre on 27/06/16.
 */
public class FavRVAdapter extends RecyclerView.Adapter<FavViewHolder>{

    private String TAG = FavRVAdapter.class.getSimpleName();

    //private List<FavouriteModel> mFavList;
    private CoordinatorLayout mFavCL;
    private StringBuilder PHOTO_URL;
    private Cursor mDataCursor;

    public FavRVAdapter(Cursor mDataCursor, CoordinatorLayout mFavCL) {
        this.mDataCursor = mDataCursor;
        this.mFavCL = mFavCL;
    }

    @Override
    public FavViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_list_item_cv, parent, false);
        FavViewHolder mFavVH = new FavViewHolder(mView);
        return mFavVH;
    }

    @Override
    public void onBindViewHolder(FavViewHolder holder, int position) {
        //final FavouriteModel mCurrentModel = mFavList.get(position);

        mDataCursor.moveToPosition(position);

        if (mDataCursor.getString(mDataCursor.getColumnIndex(DbHelper.FAV_PHOTO_REFERENCE)) != null){
            PHOTO_URL = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?photoreference=")
                    .append(mDataCursor.getString(mDataCursor.getColumnIndex(DbHelper.FAV_PHOTO_REFERENCE)))
                    .append("&maxheight=")
                    .append(256)
                    .append("&key=")
                    .append(mFavCL.getResources().getString(R.string.REMOTE_API_KEY));

            Picasso.with(mFavCL.getContext())
                    .load(PHOTO_URL.toString())
                    .placeholder(R.drawable.favorite)
                    .into(holder.mFavItemImage);
        }else{
            Picasso.with(mFavCL.getContext())
                    .load(R.drawable.favorite)
                    .into(holder.mFavItemImage);
        }

        holder.mFavItemName.setText(mDataCursor.getString(mDataCursor.getColumnIndex(DbHelper.FAV_NAME)));
        holder.mFavItemVicinity.setText(mDataCursor.getString(mDataCursor.getColumnIndex(DbHelper.FAV_VICINITY)));

        final String place_id = mDataCursor.getString(mDataCursor.getColumnIndex(DbHelper.FAV_PLACE_ID));

        holder.setClickListener(new FavViewHolder.ClickListener() {
            @Override
            public void onClick(View v, int position, boolean isLongClick) {
                EventBus.getDefault().postSticky(new CurrentPlaceEvent(place_id));
                mFavCL.getContext().startActivity(new Intent(mFavCL.getContext(), PlaceDetailsActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataCursor.getCount();
    }
}
