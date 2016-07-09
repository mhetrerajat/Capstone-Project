package com.mhetrerajat.backpacker.Adapters;

import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mhetrerajat.backpacker.Activity.HomeActivity;
import com.mhetrerajat.backpacker.Db.CityModel;
import com.mhetrerajat.backpacker.Db.DbHelper;
import com.mhetrerajat.backpacker.Events.CurrentCityEvent;
import com.mhetrerajat.backpacker.Models.City.Photo;
import com.mhetrerajat.backpacker.Models.City.Result;
import com.mhetrerajat.backpacker.R;
import com.mhetrerajat.backpacker.ViewHolders.CitySelectViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;
import io.realm.Realm;

/**
 * Created by rajatmhetre on 24/06/16.
 */
public class CitySelectRVAdapter extends RecyclerView.Adapter<CitySelectViewHolder>{

    //@BindString(R.string.REMOTE_API_KEY) String REMOTE_API_KEY;

    private String TAG = CitySelectRVAdapter.class.getSimpleName();

    //private List<CityModel> mCitySelectList;
    private CoordinatorLayout mCitySelectCL;
    private StringBuilder PHOTO_URL;
    private Cursor mDataCursor;
    private EventBus mCitySelectEventBus = EventBus.getDefault();

    public CitySelectRVAdapter(Cursor mDataCursor, CoordinatorLayout mCitySelectCL) {
        this.mDataCursor = mDataCursor;
        this.mCitySelectCL = mCitySelectCL;
    }

    @Override
    public CitySelectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_select_item_card, parent, false);
        CitySelectViewHolder mCitySelectVH = new CitySelectViewHolder(mView);
        return mCitySelectVH;
    }

    @Override
    public void onBindViewHolder(CitySelectViewHolder holder, int position) {

        //final CityModel mCurrentCityModel = mCitySelectList.get(position);
        mDataCursor.moveToPosition(position);

        if(mDataCursor.getString(mDataCursor.getColumnIndex(DbHelper.CITY_PHOTO_REFERENCE)) != null){

            PHOTO_URL = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?photoreference=")
                    .append(mDataCursor.getString(mDataCursor.getColumnIndex(DbHelper.CITY_PHOTO_REFERENCE)))
                    .append("&maxheight=")
                    .append(256)
                    .append("&key=")
                    .append(mCitySelectCL.getResources().getString(R.string.REMOTE_API_KEY));

            Log.d(TAG, PHOTO_URL.toString());

            Picasso.with(mCitySelectCL.getContext())
                    .load(PHOTO_URL.toString())
                    .into(holder.mCitySelectImage);
        }

        holder.mCitySelectName.setText(mDataCursor.getString(mDataCursor.getColumnIndex(DbHelper.CITY_NAME)));
        holder.mCitySelectFormattedAddr.setText(mDataCursor.getString(mDataCursor.getColumnIndex(DbHelper.CITY_FORMATTED_ADDRESS)));

        //final String place_id = mDataCursor.getString(mDataCursor.getColumnIndex(DbHelper.CITY_PLACE_ID));

        final CityModel mCurrentCity = new CityModel();
        mCurrentCity.setPlace_id(mDataCursor.getString(mDataCursor.getColumnIndex(DbHelper.CITY_PLACE_ID)));
        mCurrentCity.setName(mDataCursor.getString(mDataCursor.getColumnIndex(DbHelper.CITY_NAME)));
        mCurrentCity.setFormatted_address(mDataCursor.getString(mDataCursor.getColumnIndex(DbHelper.CITY_FORMATTED_ADDRESS)));
        mCurrentCity.setPhoto_reference(mDataCursor.getString(mDataCursor.getColumnIndex(DbHelper.CITY_PHOTO_REFERENCE)));
        mCurrentCity.setReference(mDataCursor.getString(mDataCursor.getColumnIndex(DbHelper.CITY_REFERENCE)));
        mCurrentCity.setLatitude(mDataCursor.getDouble(mDataCursor.getColumnIndex(DbHelper.CITY_LATITUDE)));
        mCurrentCity.setLongitude(mDataCursor.getDouble(mDataCursor.getColumnIndex(DbHelper.CITY_LONGITUDE)));
        mCurrentCity.setAdded_on(mDataCursor.getLong(mDataCursor.getColumnIndex(DbHelper.CITY_ADDED_ON)));

        holder.setClickListener(new CitySelectViewHolder.ClickListener() {
            @Override
            public void onClick(View v, int position, boolean isLongClick) {

                mCitySelectEventBus.postSticky(new CurrentCityEvent(mCurrentCity));
                mCitySelectCL.getContext().startActivity(new Intent(mCitySelectCL.getContext(), HomeActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataCursor.getCount();
    }
}
