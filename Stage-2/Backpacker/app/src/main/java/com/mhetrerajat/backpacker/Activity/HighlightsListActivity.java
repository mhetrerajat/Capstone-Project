package com.mhetrerajat.backpacker.Activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mhetrerajat.backpacker.Adapters.HighlightsRVAdapter;
import com.mhetrerajat.backpacker.Db.CityModel;
import com.mhetrerajat.backpacker.Db.DbHelper;
import com.mhetrerajat.backpacker.Events.CurrentCityEvent;
import com.mhetrerajat.backpacker.Events.HighlightErrorEvent;
import com.mhetrerajat.backpacker.Events.HighlightSuccessEvent;
import com.mhetrerajat.backpacker.Events.HighlightTypeEvent;
import com.mhetrerajat.backpacker.Models.Highlight.Highlight;
import com.mhetrerajat.backpacker.Models.Highlight.Result;
import com.mhetrerajat.backpacker.Models.Highlight.Photo;
import com.mhetrerajat.backpacker.Provider.BackpackerContentProvider;
import com.mhetrerajat.backpacker.R;
import com.mhetrerajat.backpacker.Service.PlaceService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import io.realm.Realm;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class HighlightsListActivity extends AppCompatActivity {

    private String TAG = HighlightsListActivity.class.getSimpleName();
    private String NEXT_PAGE_TOKEN = "";
    private String TYPE;
    private final String SIGHTS_TYPES = "amusement_park|zoo|aquarium|art_gallery|bowling_alley|campground|casino|church|hindu_temple|stadium|shopping_mall|movie_theater|mosque";
    private final String LOCAL_DB_URL = "content://com.mhetrerajat.backpacker.Provider.BackpackerContentProvider/highlights";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.highlights_list_activity_cl)
    CoordinatorLayout mHighlightCL;
    @BindView(R.id.highlights_list_rv)
    RecyclerView mHighlightRV;

    @BindView(R.id.empty_list_error)
    LinearLayout mEmptyListError;

    @BindString(R.string.REMOTE_BASE_URL) String BASE_URL;
    @BindString(R.string.REMOTE_API_KEY) String API_KEY;

    private EventBus mEventBus;
    private CityModel mCurrentCityModel;
    public Realm mRealm;
    private List<Result> mHighlightList;
    //private List<HighlightsModel> mHighlightDbFetchedList;
    private LinearLayoutManager mHighlightLLM;
    private HighlightsRVAdapter mHighlightRVAdapter;
    private Cursor mDataMatrixCursor;
    ContentValues[] mHighlightsDataList;

    private Integer totalItemCount, visibleItemCount, firstVisibleItem, previousTotal = 0, VISIBLE_THRESHOLD = 5;
    private Boolean loading = true;

    private Gson gson;
    private Retrofit retrofit;
    private PlaceService mHighlightService;

    Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highlights_list);

        // Event Bus
        mEventBus = EventBus.getDefault();

        // Bind Views
        ButterKnife.bind(this);

        //  Init
        mRealm = Realm.getDefaultInstance();
        mHighlightList = new ArrayList<>();
        mCurrentCityModel = mEventBus.getStickyEvent(CurrentCityEvent.class).getmCurrentCity();
        mUri = Uri.parse(LOCAL_DB_URL);
        mHighlightsDataList = new ContentValues[20];
        //mHighlightDbFetchedList = mRealm.where(HighlightsModel.class).equalTo("city_place_id", mCurrentCityModel.getPlace_id()).equalTo("type", TYPE).findAll();
        TYPE = mEventBus.getStickyEvent(HighlightTypeEvent.class).getTYPE();
        mDataMatrixCursor = getContentResolver().query(mUri, null, null, new String[]{mCurrentCityModel.getPlace_id(), TYPE}, null);


        // Init Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(prettyType(TYPE) + " in " + mCurrentCityModel.getName());
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Init Retrofit Stuff
        gson = new GsonBuilder().create();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        mHighlightService = retrofit.create(PlaceService.class);

        // Retrofit Job
        fetchNearByHighlights(TYPE);

        // Recycler View Init
        mHighlightLLM = new LinearLayoutManager(this);
        mHighlightRV.setLayoutManager(mHighlightLLM);
        mHighlightRVAdapter = new HighlightsRVAdapter(mDataMatrixCursor, mHighlightCL);

        if (mHighlightRV.getLayoutManager() instanceof LinearLayoutManager) {
            mHighlightRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    totalItemCount = mHighlightLLM.getItemCount();
                    visibleItemCount = mHighlightLLM.getChildCount();
                    firstVisibleItem = mHighlightLLM.findFirstVisibleItemPosition();

                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if (!loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + VISIBLE_THRESHOLD)) {
                        // End has been reached
                        fetchNearByHighlights(TYPE);
                        loading = true;
                    }
                }
            });
        }
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

    public void fetchNearByHighlights(final String TYPE){
        Map<String, String> params = new HashMap<>();
        if (NEXT_PAGE_TOKEN.compareTo("") == 0){
            // Empty
            String LOCATION = new StringBuilder(String.valueOf(mCurrentCityModel.getLatitude()))
                    .append(",")
                    .append(mCurrentCityModel.getLongitude()).toString();

            params.put("key", API_KEY);
            if(TYPE.equals("sights")){
                params.put("types", SIGHTS_TYPES);
            }else{
                params.put("type", TYPE);
            }
            params.put("location", LOCATION);
        }else{
            // Not Empty
            params.put("key", API_KEY);
            params.put("pagetoken", NEXT_PAGE_TOKEN);
        }

        final Snackbar mLoadingSnackbar = Snackbar.make(mHighlightCL, "Fetching data....", Snackbar.LENGTH_LONG);

        mLoadingSnackbar.show();

        Call<Highlight> mCallHighlight = mHighlightService.getNearByHighlights(params);
        mCallHighlight.enqueue(new Callback<Highlight>() {
            @Override
            public void onResponse(Response<Highlight> response, Retrofit retrofit) {
                if(response.isSuccess()){
                    Highlight data =  response.body();
                    mHighlightList = data.getResults();
                    NEXT_PAGE_TOKEN = data.getNextPageToken();
                    if(NEXT_PAGE_TOKEN == null){
                        NEXT_PAGE_TOKEN = "";
                    }

                    int i =0;
                    for(Result mItem : mHighlightList){
                        ContentValues mValues = new ContentValues();
                        mValues.put(DbHelper.HIGHLIGHTS_PLACE_ID, mItem.getPlaceId());
                        mValues.put(DbHelper.HIGHLIGHTS_CITY_PLACE_ID, mCurrentCityModel.getPlace_id());
                        mValues.put(DbHelper.HIGHLIGHTS_TYPE, TYPE);
                        mValues.put(DbHelper.HIGHLIGHTS_NAME, mItem.getName());
                        mValues.put(DbHelper.HIGHLIGHTS_VICINITY, mItem.getVicinity());
                        List<Photo> mHighlightPhotos = mItem.getPhotos();
                        if (mHighlightPhotos.size() != 0){
                            mValues.put(DbHelper.HIGHLIGHTS_PHOTO_REFERENCE, mHighlightPhotos.get(0).getPhotoReference());
                        }else{
                           mValues.put(DbHelper.HIGHLIGHTS_PHOTO_REFERENCE, "");
                        }
                        mValues.put(DbHelper.HIGHLIGHTS_STORE_TYPE, prettyType(mItem.getTypes().get(0)));
                        mValues.put(DbHelper.HIGHLIGHTS_LATITUDE, mItem.getGeometry().getLocation().getLat());
                        mValues.put(DbHelper.HIGHLIGHTS_LONGITUDE, mItem.getGeometry().getLocation().getLng());

                        mHighlightsDataList[i] = mValues;
                        i++;
                    }

                    //mProvider.onCreate();
                    getContentResolver().bulkInsert(mUri, mHighlightsDataList);

                    mDataMatrixCursor = getContentResolver().query(mUri, null, null, new String[]{mCurrentCityModel.getPlace_id(), TYPE}, null);

                    //mHighlightDbFetchedList = mRealm.where(HighlightsModel.class).equalTo("city_place_id", mCurrentCityModel.getPlace_id()).equalTo("type", TYPE).findAll();
                    mEventBus.post(new HighlightSuccessEvent(mDataMatrixCursor, NEXT_PAGE_TOKEN));

                    mLoadingSnackbar.dismiss();

                }else{
                    NEXT_PAGE_TOKEN = "";
                    //mHighlightDbFetchedList = mRealm.where(HighlightsModel.class).equalTo("city_place_id", mCurrentCityModel.getPlace_id()).equalTo("type", TYPE).findAll();
                    mDataMatrixCursor = getContentResolver().query(mUri, null, null, new String[]{mCurrentCityModel.getPlace_id(), TYPE}, null);
                    if(mDataMatrixCursor.getCount() != 0){
                        mEventBus.post(new HighlightSuccessEvent(mDataMatrixCursor, NEXT_PAGE_TOKEN));
                    }
                    mEventBus.post(new HighlightErrorEvent("Oops! Something went wrong."));

                    mLoadingSnackbar.dismiss();
                }
            }

            @Override
            public void onFailure(Throwable t) {

                mLoadingSnackbar.dismiss();

                NEXT_PAGE_TOKEN = "";
                //mHighlightDbFetchedList = mRealm.where(HighlightsModel.class).equalTo("city_place_id", mCurrentCityModel.getPlace_id()).equalTo("type", TYPE).findAll();
                mDataMatrixCursor = getContentResolver().query(mUri, null, null, new String[]{mCurrentCityModel.getPlace_id(), TYPE}, null);
                if(mDataMatrixCursor.getCount() != 0){
                    mEventBus.post(new HighlightSuccessEvent(mDataMatrixCursor, NEXT_PAGE_TOKEN));
                }
                mEventBus.post(new HighlightErrorEvent("Please check your network connection."));
            }
        });
    }

    public void onEvent(CurrentCityEvent mEvent){
        mCurrentCityModel = mEvent.getmCurrentCity();
    }


    public void onEventMainThread(HighlightSuccessEvent mEvent){
        mDataMatrixCursor = mEvent.getMatrixCursor();
        mHighlightRVAdapter = new HighlightsRVAdapter(mDataMatrixCursor, mHighlightCL);
        mHighlightRV.setAdapter(mHighlightRVAdapter);
        mHighlightRVAdapter.notifyItemRangeChanged(0, mDataMatrixCursor.getCount());
        if(mDataMatrixCursor.getCount() == 0){
            mEmptyListError.setVisibility(View.VISIBLE);
        }
        if(firstVisibleItem != null){
            mHighlightRV.scrollToPosition(firstVisibleItem);
        }
    }

    public void onEventMainThread(HighlightErrorEvent mEvent){
        Snackbar mSnackbar = Snackbar
                .make(mHighlightCL, mEvent.getMessage(), Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fetchNearByHighlights(TYPE);
                        Log.d(TAG, "RETRY");
                    }
                });
        View mSnackbarView = mSnackbar.getView();
        mSnackbarView.setBackgroundColor(Color.RED);
        mSnackbar.setActionTextColor(Color.WHITE);
        mSnackbar.show();
    }

    // Customs Methods
    public String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    public String prettyType(String raw){


        if(raw.contains("_")){
            String[] arr = raw.split("_");
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < arr.length; i++) {
                sb.append(Character.toUpperCase(arr[i].charAt(0)))
                        .append(arr[i].substring(1)).append(" ");
            }
            return sb.toString().trim();

        }else{
            return capitalize(raw);
        }
    }

}
