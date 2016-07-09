package com.mhetrerajat.backpacker.Activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mhetrerajat.backpacker.Adapters.CitySelectRVAdapter;
import com.mhetrerajat.backpacker.Db.CityModel;
import com.mhetrerajat.backpacker.Db.DbHelper;
import com.mhetrerajat.backpacker.Events.CitySelectErrorEvent;
import com.mhetrerajat.backpacker.Events.CitySelectSuccessEvent;
import com.mhetrerajat.backpacker.Models.City.City;
import com.mhetrerajat.backpacker.Models.City.Photo;
import com.mhetrerajat.backpacker.Models.City.Result;
import com.mhetrerajat.backpacker.R;
import com.mhetrerajat.backpacker.Service.CityService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.Sort;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class CitySelectActivity extends AppCompatActivity {

    private String TAG = CitySelectActivity.class.getSimpleName();
    private final String LOCAL_DB_URL = "content://com.mhetrerajat.backpacker.Provider.BackpackerContentProvider/city";

    @BindView(R.id.city_select_activity_cl) CoordinatorLayout mCitySelectCL;
    @BindView(R.id.city_search_et) EditText mCitySearchET;
    @BindView(R.id.city_select_rv) RecyclerView mCitySelectRV;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    @BindString(R.string.REMOTE_API_KEY) String REMOTE_API_KEY;
    @BindString(R.string.REMOTE_BASE_URL) String REMOTE_BASE_URL;

    private List<Result> mCityList;
    private EventBus mCitySelectEventBus;
    Realm mRealm;
    private LinearLayoutManager mCitySelectLLM;
    private CitySelectRVAdapter mCitySelectRVAdapter;
    private String QUERY;
    private Cursor mDataMatrixCursor;
    ContentValues[] mCityDataList;
    Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_select);

        // Init
        mCityList = new ArrayList<>();
        //mCityDbFetchedList = new ArrayList<>();
        mRealm = Realm.getDefaultInstance();
        mUri = Uri.parse(LOCAL_DB_URL);
        mCityDataList = new ContentValues[200];


        // Event Bus
        mCitySelectEventBus = EventBus.getDefault();

        // Bind Views
        ButterKnife.bind(this);

        // Init Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Get Started");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Check if db has cities stored
        //mCityDbFetchedList = mRealm.where(CityModel.class).findAllSorted("added_on", Sort.DESCENDING);
        mDataMatrixCursor = getContentResolver().query(mUri, null, null, null, null);


        // Recycler View Init
        mCitySelectLLM = new LinearLayoutManager(this);
        mCitySelectRV.setLayoutManager(mCitySelectLLM);
        mCitySelectRVAdapter = new CitySelectRVAdapter(mDataMatrixCursor, mCitySelectCL);
        mCitySelectRV.setAdapter(mCitySelectRVAdapter);

        // Search By Query
        mCitySearchET.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press

                    QUERY = mCitySearchET.getText().toString();

                    // Fetch data
                    fetchCitiesByQuery(QUERY);

                    return true;
                }
                return false;
            }
        });

    }


    public void fetchCitiesByQuery(final String query){
        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(REMOTE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        CityService mCityService = retrofit.create(CityService.class);

        Map<String, String> params = new HashMap<>();
        params.put("key", REMOTE_API_KEY);
        params.put("query", query);

        final Snackbar mLoadingSnackbar = Snackbar.make(mCitySelectCL, "Fetching data....", Snackbar.LENGTH_LONG);

        mLoadingSnackbar.show();

        final Call<City> mCityCall = mCityService.getCitiesByQuery(params);
        mCityCall.enqueue(new Callback<City>() {
            @Override
            public void onResponse(Response<City> response, Retrofit retrofit) {
                if(response.isSuccess()){
                    mCityList = response.body().getResults();
                    Log.d(TAG, String.valueOf(mCityList.size()));

                    int i=0;
                    for(Result mItem : mCityList){
                        ContentValues mValues = new ContentValues();
                        mValues.put(DbHelper.CITY_PLACE_ID, mItem.getPlaceId());
                        mValues.put(DbHelper.CITY_NAME, mItem.getName());
                        mValues.put(DbHelper.CITY_FORMATTED_ADDRESS, mItem.getFormattedAddress());
                        List<Photo> mCityPhotosList = mItem.getPhotos();
                        if(mCityPhotosList.size() != 0){
                            mValues.put(DbHelper.CITY_PHOTO_REFERENCE, mCityPhotosList.get(0).getPhotoReference());
                            //mCityModel.setPhoto_reference(mCityPhotosList.get(0).getPhotoReference());
                        }
                        mValues.put(DbHelper.CITY_REFERENCE, mItem.getReference());
                        mValues.put(DbHelper.CITY_LATITUDE, mItem.getGeometry().getLocation().getLat());
                        mValues.put(DbHelper.CITY_LONGITUDE, mItem.getGeometry().getLocation().getLng());
                        mValues.put(DbHelper.CITY_ADDED_ON, System.currentTimeMillis());

                        mCityDataList[i] = mValues;
                        i++;
                    }

                    getContentResolver().bulkInsert(mUri, mCityDataList);
                    mDataMatrixCursor = getContentResolver().query(mUri, null, null, null, null);
                    //mCityDbFetchedList = mRealm.where(CityModel.class).findAllSorted("added_on", Sort.DESCENDING);

                    mCitySelectEventBus.post(new CitySelectSuccessEvent(mDataMatrixCursor));

                    mLoadingSnackbar.dismiss();
                }else{
                    // If database has old cities who have been searched before, then show it
                    //mCityDbFetchedList = mRealm.where(CityModel.class).findAllSorted("added_on", Sort.DESCENDING);
                    mDataMatrixCursor = getContentResolver().query(mUri, null, null, null, null);
                    if(mDataMatrixCursor.getCount() != 0){
                        mCitySelectEventBus.post(new CitySelectSuccessEvent(mDataMatrixCursor));
                    }
                    // Error Event
                    mCitySelectEventBus.post(new CitySelectErrorEvent("Oops! Something went wrong."));

                    mLoadingSnackbar.dismiss();
                }
            }

            @Override
            public void onFailure(Throwable t) {

                mLoadingSnackbar.dismiss();

                // If database has old cities who have been searched before, then show it
                //mCityDbFetchedList = mRealm.where(CityModel.class).findAllSorted("added_on", Sort.DESCENDING);
                mDataMatrixCursor = getContentResolver().query(mUri, null, null, null, null);
                if(mDataMatrixCursor.getCount() != 0){
                    mCitySelectEventBus.post(new CitySelectSuccessEvent(mDataMatrixCursor));
                }
                // Error Event
                mCitySelectEventBus.post(new CitySelectErrorEvent("Please check your network connection."));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCitySelectEventBus.register(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCitySelectEventBus.unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCitySelectEventBus.unregister(this);
    }

    public void onEventMainThread(CitySelectSuccessEvent mEvent){
        mDataMatrixCursor = mEvent.getMatrixCursor();
        //Log.d(TAG, "Event : " + mCityList.size());
        mCitySelectRVAdapter = new CitySelectRVAdapter(mDataMatrixCursor, mCitySelectCL);
        mCitySelectRV.setAdapter(mCitySelectRVAdapter);
        mCitySelectRVAdapter.notifyDataSetChanged();
    }

    public void onEventMainThread(CitySelectErrorEvent mEvent){
        Snackbar mSnackbar = Snackbar
                .make(mCitySelectCL, mEvent.getMessage(), Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fetchCitiesByQuery(QUERY);
                        Log.d(TAG, "RETRY");
                    }
                });
        View mSnackbarView = mSnackbar.getView();
        mSnackbarView.setBackgroundColor(Color.RED);
        mSnackbar.setActionTextColor(Color.WHITE);
        mSnackbar.show();
    }
}
