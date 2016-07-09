package com.mhetrerajat.backpacker.Activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.mhetrerajat.backpacker.Adapters.FavRVAdapter;
import com.mhetrerajat.backpacker.Db.CityModel;
import com.mhetrerajat.backpacker.Db.FavouriteModel;
import com.mhetrerajat.backpacker.Events.CurrentCityEvent;
import com.mhetrerajat.backpacker.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import io.realm.Realm;

public class FavouriteListActivity extends AppCompatActivity {

    private String TAG = FavouriteListActivity.class.getSimpleName();
    private final String LOCAL_DB_URL = "content://com.mhetrerajat.backpacker.Provider.BackpackerContentProvider/fav";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.favourite_list_cl)
    CoordinatorLayout mFavCL;
    @BindView(R.id.favourite_list_rv)
    RecyclerView mFavRV;

    @BindView(R.id.empty_list_error)
    LinearLayout mEmptyListError;

    private EventBus mEventBus;
    private CityModel mCurrentCityModel;
    private Realm mRealm;

    //private List<FavouriteModel> mFavDbFetchedList;
    private LinearLayoutManager mFavLLM;
    private FavRVAdapter mFavRVAdapter;
    private Cursor mDataMatrixCursor;
    ContentValues[] mFavDataList;

    Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_list);

        // Event Bus
        mEventBus = EventBus.getDefault();

        // Bind Views
        ButterKnife.bind(this);

        //  Init
        mRealm = Realm.getDefaultInstance();
        mCurrentCityModel = mEventBus.getStickyEvent(CurrentCityEvent.class).getmCurrentCity();
        mUri = Uri.parse(LOCAL_DB_URL);
        //mFavDbFetchedList = mRealm.where(FavouriteModel.class).equalTo("city_place_id", mCurrentCityModel.getPlace_id()).findAll();
        mDataMatrixCursor = getContentResolver().query(mUri, null, null, new String[]{mCurrentCityModel.getPlace_id()}, null);

        if(mDataMatrixCursor.getCount() == 0){
            mEmptyListError.setVisibility(View.VISIBLE);
        }

        // Init Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("My Favourites in " + mCurrentCityModel.getName());
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Recycler View Init
        mFavLLM = new LinearLayoutManager(this);
        mFavRV.setLayoutManager(mFavLLM);
        mFavRVAdapter = new FavRVAdapter(mDataMatrixCursor, mFavCL);
        mFavRV.setAdapter(mFavRVAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mEventBus.registerSticky(this);
    }

    @Override
    protected void onDestroy() {
        mEventBus.unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        mEventBus.unregister(this);
        super.onStop();
    }

    public void onEvent(CurrentCityEvent mEvent){
        mCurrentCityModel = mEvent.getmCurrentCity();
    }

}
