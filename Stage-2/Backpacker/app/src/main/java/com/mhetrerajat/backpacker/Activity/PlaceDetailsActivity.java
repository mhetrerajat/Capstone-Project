package com.mhetrerajat.backpacker.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mhetrerajat.backpacker.Adapters.PDPhotosRVAdapter;
import com.mhetrerajat.backpacker.Adapters.PDReviewsRVAdapter;
import com.mhetrerajat.backpacker.Db.CityModel;
import com.mhetrerajat.backpacker.Db.DbHelper;
import com.mhetrerajat.backpacker.Db.FavouriteModel;
import com.mhetrerajat.backpacker.Db.PlaceDetailsModel;
import com.mhetrerajat.backpacker.Db.PlacePhotoModel;
import com.mhetrerajat.backpacker.Db.PlaceReviewsModel;
import com.mhetrerajat.backpacker.Events.CurrentCityEvent;
import com.mhetrerajat.backpacker.Events.CurrentPlaceEvent;
import com.mhetrerajat.backpacker.Events.PlaceDetailsErrorEvent;
import com.mhetrerajat.backpacker.Events.PlaceDetailsSuccessEvent;
import com.mhetrerajat.backpacker.Models.Place.Photo;
import com.mhetrerajat.backpacker.Models.Place.Place;
import com.mhetrerajat.backpacker.Models.Place.Result;
import com.mhetrerajat.backpacker.Models.Place.Review;
import com.mhetrerajat.backpacker.R;
import com.mhetrerajat.backpacker.Service.PlaceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmList;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class PlaceDetailsActivity extends AppCompatActivity {

    private String TAG = PlaceDetailsActivity.class.getSimpleName();

    @BindString(R.string.REMOTE_API_KEY) String API_KEY;
    @BindString(R.string.REMOTE_BASE_URL) String BASE_URL;

    @BindView(R.id.place_details_cl)
    CoordinatorLayout mPlaceDetailsCL;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.place_details_name)
    TextView mPlaceDetailsName;

    @BindView(R.id.place_details_vicinity)
    TextView mPlaceDetailsVicinity;

    @BindView(R.id.place_details_phone)
    TextView mPlaceDetailsPhone;

    @BindView(R.id.place_details_rating) TextView mPlaceDetailsRating;
    @BindView(R.id.place_details_rating_bar) RatingBar mPlaceDetailsRatingBar;
    @BindView(R.id.place_details_photos_rv)
    RecyclerView mPDPhotosRV;

    @BindView(R.id.place_details_reviews_rv)
    RecyclerView mPDReviewsRV;

    @BindView(R.id.place_details_website)
    Button mPDWebsiteBtn;

    @BindView(R.id.place_details_favourite)
    Button mPDMarksAsFav;

    @BindView(R.id.place_details_photos_error) TextView mPDPhotosError;
    @BindView(R.id.place_details_reviews_error) TextView mPDReviewsError;

    private long mFavDbCount;

    private EventBus mEventBus;
    private CityModel mCurrentCityModel;
    private String mCurrentPlaceId;

    private Gson gson;
    private Retrofit retrofit;
    private PlaceService mPlaceDetailsService;
    private Realm mRealm;

    private LinearLayoutManager mPDPhotosLLM, mPDReviewsLLM;
    private PDPhotosRVAdapter mPDPhotosRVAdapter;
    private PDReviewsRVAdapter mPDReviewsRVAdapter;

    //private List<PlacePhotoModel> mPhotosList;
    //private List<PlaceReviewsModel> mReviewsList;

    private Cursor mPhotosListCursor;
    private Cursor mReviewsListCursor;
    private Cursor mPDDataCursor;

    ContentValues[] mPhotosListCV;
    ContentValues[] mReviewsListCV;
    ContentValues mPDDataCV;

    private final String LOCAL_DB_FAV = "content://com.mhetrerajat.backpacker.Provider.BackpackerContentProvider/fav";
    private final String LOCAL_DB_FAV_PLACE = "content://com.mhetrerajat.backpacker.Provider.BackpackerContentProvider/favplace";
    private final String LOCAL_DB_PD = "content://com.mhetrerajat.backpacker.Provider.BackpackerContentProvider/PD";
    private final String LOCAL_DB_PDP = "content://com.mhetrerajat.backpacker.Provider.BackpackerContentProvider/PDP";
    private final String LOCAL_DB_PDR = "content://com.mhetrerajat.backpacker.Provider.BackpackerContentProvider/PDR";
    private Uri mUriFav, mUriPD, mUriPDP, mUriPDR, mUriFavPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        // Event Bus
        mEventBus = EventBus.getDefault();

        // Bind Views
        ButterKnife.bind(this);

        // Init
        mCurrentCityModel = mEventBus.getStickyEvent(CurrentCityEvent.class).getmCurrentCity();
        mCurrentPlaceId = mEventBus.getStickyEvent(CurrentPlaceEvent.class).getmCurrentPlaceId();
        mRealm = Realm.getDefaultInstance();
        mUriFav = Uri.parse(LOCAL_DB_FAV);
        mUriFavPlace = Uri.parse(LOCAL_DB_FAV_PLACE);
        mUriPD = Uri.parse(LOCAL_DB_PD);
        mUriPDP = Uri.parse(LOCAL_DB_PDP);
        mUriPDR = Uri.parse(LOCAL_DB_PDR);
        mPhotosListCV = new ContentValues[100];
        mReviewsListCV = new ContentValues[100];
        mPDDataCV = new ContentValues();
        //mPhotosList = mRealm.where(PlacePhotoModel.class).equalTo("place_id", mCurrentPlaceId).findAll();
        //mReviewsList = mRealm.where(PlaceReviewsModel.class).equalTo("place_id", mCurrentPlaceId).findAll();
        //mFavDbCount = mRealm.where(FavouriteModel.class).contains("place_id", mCurrentPlaceId).count();
        mPhotosListCursor = getContentResolver().query(mUriPDP, null, null, new String[]{mCurrentPlaceId}, null);
        mReviewsListCursor = getContentResolver().query(mUriPDR, null, null, new String[]{mCurrentPlaceId}, null);
        mFavDbCount = getContentResolver().query(mUriFavPlace, null, null, new String[]{mCurrentPlaceId}, null).getCount();

        // Set Fav Btn Text
        Log.d(TAG, "FAV : " + mFavDbCount);
        if(mFavDbCount > 0)
            mPDMarksAsFav.setText("Unmark as Favourite");
        else
            mPDMarksAsFav.setText("Mark as Favourite");

        // Toolbat init
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mCurrentPlaceId);
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
        mPlaceDetailsService = retrofit.create(PlaceService.class);

        // Fetch Data
        fetchPlaceDetails();

        // Photos RV
        mPDPhotosLLM = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mPDPhotosRV.setLayoutManager(mPDPhotosLLM);
        mPDPhotosRVAdapter = new PDPhotosRVAdapter(mPhotosListCursor, mPlaceDetailsCL);
        mPDPhotosRV.setAdapter(mPDPhotosRVAdapter);

        // Reviews RV
        mPDReviewsLLM = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mPDReviewsRV.setLayoutManager(mPDReviewsLLM);
        mPDReviewsRVAdapter = new PDReviewsRVAdapter(mReviewsListCursor, mPlaceDetailsCL);
        mPDReviewsRV.setAdapter(mPDReviewsRVAdapter);

    }

    private void fetchPlaceDetails() {

        Map<String, String> params = new HashMap<>();
        params.put("key", API_KEY);
        params.put("placeid", mCurrentPlaceId);

        final Snackbar mLoadingSnackbar = Snackbar.make(mPlaceDetailsCL, "Fetching data....", Snackbar.LENGTH_LONG);

        mLoadingSnackbar.show();

        Call<Place> mCallPlace = mPlaceDetailsService.getPlaceDetails(params);
        mCallPlace.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Response<Place> response, Retrofit retrofit) {
                if(response.isSuccess()){
                    final Result data = response.body().getResult();


                    mPDDataCV.put(DbHelper.PD_PLACE_ID, data.getPlaceId());
                    mPDDataCV.put(DbHelper.PD_CITY_PLACE_ID, mCurrentCityModel.getPlace_id());
                    mPDDataCV.put(DbHelper.PD_NAME, data.getName());
                    mPDDataCV.put(DbHelper.PD_FORMATTED_ADDRESS, data.getFormattedAddress());
                    mPDDataCV.put(DbHelper.PD_FORMATTED_PHONE_NUMBER, data.getFormattedPhoneNumber());
                    mPDDataCV.put(DbHelper.PD_INT_PHONE_NUMBER, data.getInternationalPhoneNumber());
                    mPDDataCV.put(DbHelper.PD_LATITUDE, data.getGeometry().getLocation().getLat());
                    mPDDataCV.put(DbHelper.PD_LONGITUDE, data.getGeometry().getLocation().getLng());
                    mPDDataCV.put(DbHelper.PD_RATING, data.getRating());
                    mPDDataCV.put(DbHelper.PD_VICINITY, data.getVicinity());
                    mPDDataCV.put(DbHelper.PD_WEBSITE, data.getWebsite());

                    getContentResolver().insert(mUriPD, mPDDataCV);

                    if(data.getReviews() != null) {
                        int i = 0;
                        for (Review mItem : data.getReviews()) {

                            ContentValues mValues = new ContentValues();
                            mValues.put(DbHelper.PDR_PLACE_ID, mCurrentPlaceId);
                            mValues.put(DbHelper.PDR_AUTHOR_NAME, mItem.getAuthorName());
                            mValues.put(DbHelper.PDR_AUTHOR_URL, mItem.getAuthorUrl());
                            mValues.put(DbHelper.PDR_TEXT, mItem.getText());
                            mValues.put(DbHelper.PDR_RATING, mItem.getRating());
                            mValues.put(DbHelper.PDR_TIME, mItem.getTime());

                            mReviewsListCV[i] = mValues;
                            i++;
                        }
                    }
                    // Do bulk insert for reviews
                    getContentResolver().bulkInsert(mUriPDR, mReviewsListCV);

                    if(data.getPhotos() != null){
                        int j=0;
                        for (Photo mItem : data.getPhotos()){

                            ContentValues mValues = new ContentValues();
                            mValues.put(DbHelper.PDP_PLACE_ID, mCurrentPlaceId);
                            mValues.put(DbHelper.PDP_PHOTO_REFERENCE, mItem.getPhotoReference());
                            mValues.put(DbHelper.PDP_HEIGHT, mItem.getHeight());
                            mValues.put(DbHelper.PDP_WIDTH, mItem.getWidth());

                            mPhotosListCV[j] = mValues;
                            j++;
                        }
                    }
                    //Do bulk insert for photos
                    getContentResolver().bulkInsert(mUriPDP, mPhotosListCV);

                    /*
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            PlaceDetailsModel mPlaceDetailsModel = new PlaceDetailsModel();

                            mPlaceDetailsModel.setCity_place_id(mCurrentCityModel.getPlace_id());
                            mPlaceDetailsModel.setPlace_id(data.getPlaceId());
                            mPlaceDetailsModel.setName(data.getName());
                            mPlaceDetailsModel.setLongitude(data.getGeometry().getLocation().getLng());
                            mPlaceDetailsModel.setLatitude(data.getGeometry().getLocation().getLat());
                            mPlaceDetailsModel.setFormatted_address(data.getFormattedAddress());
                            mPlaceDetailsModel.setFormatted_phone_number(data.getFormattedPhoneNumber());
                            mPlaceDetailsModel.setInternational_phone_number(data.getInternationalPhoneNumber());
                            mPlaceDetailsModel.setViciniy(data.getVicinity());
                            mPlaceDetailsModel.setWebsite(data.getWebsite());
                            mPlaceDetailsModel.setRating(data.getRating());

                            RealmList<PlaceReviewsModel> mReviewsModelList = new RealmList<PlaceReviewsModel>();
                            if(data.getReviews() != null){
                                for (Review currentItem : data.getReviews()){

                                    PlaceReviewsModel mReviewsModel = new PlaceReviewsModel();
                                    mReviewsModel.setPlace_id(mCurrentPlaceId);
                                    mReviewsModel.setAuthor_name(currentItem.getAuthorName());
                                    mReviewsModel.setAuthor_url(currentItem.getAuthorUrl());
                                    mReviewsModel.setRating(currentItem.getRating());
                                    mReviewsModel.setText(currentItem.getText());
                                    mReviewsModel.setTime(currentItem.getTime());

                                    mReviewsModelList.add(mReviewsModel);
                                }
                            }
                            mPlaceDetailsModel.setmPlaceReviewsList(mReviewsModelList);

                            RealmList<PlacePhotoModel> mPhotosModelList = new RealmList<PlacePhotoModel>();
                            if(data.getPhotos() != null){
                                for(Photo currentItem : data.getPhotos()){

                                    PlacePhotoModel mPhotoModel = new PlacePhotoModel();
                                    mPhotoModel.setPlace_id(mCurrentPlaceId);
                                    mPhotoModel.setHeight(currentItem.getHeight());
                                    mPhotoModel.setWeight(currentItem.getWidth());
                                    mPhotoModel.setPhoto_reference(currentItem.getPhotoReference());

                                    mPhotosModelList.add(mPhotoModel);
                                }
                            }
                            mPlaceDetailsModel.setmPlacePhotosList(mPhotosModelList);


                            realm.copyToRealmOrUpdate(mPlaceDetailsModel);
                        }
                    });

                    */

                    //PlaceDetailsModel mPlaceModel = mRealm.where(PlaceDetailsModel.class).equalTo("place_id", mCurrentPlaceId).findFirst();
                    mPDDataCursor = getContentResolver().query(mUriPD, null, null, new String[]{mCurrentPlaceId}, null);
                    mPhotosListCursor = getContentResolver().query(mUriPDP, null, null, new String[]{mCurrentPlaceId}, null);
                    mReviewsListCursor = getContentResolver().query(mUriPDR, null, null, new String[]{mCurrentPlaceId}, null);

                    mEventBus.post(new PlaceDetailsSuccessEvent(mPDDataCursor, mPhotosListCursor, mReviewsListCursor));

                    mLoadingSnackbar.dismiss();

                }else{

                    mLoadingSnackbar.dismiss();
                    //PlaceDetailsModel mPlaceModel = mRealm.where(PlaceDetailsModel.class).equalTo("place_id", mCurrentPlaceId).findFirst();

                    mPDDataCursor = getContentResolver().query(mUriPD, null, null, new String[]{mCurrentPlaceId}, null);
                    mPhotosListCursor = getContentResolver().query(mUriPDP, null, null, new String[]{mCurrentPlaceId}, null);
                    mReviewsListCursor = getContentResolver().query(mUriPDR, null, null, new String[]{mCurrentPlaceId}, null);

                    if(mPDDataCursor.getCount() != 0){
                        mEventBus.post(new PlaceDetailsSuccessEvent(mPDDataCursor, mPhotosListCursor, mReviewsListCursor));
                    }
                    mEventBus.post(new PlaceDetailsErrorEvent("Oops! Something went wrong."));
                }
            }

            @Override
            public void onFailure(Throwable t) {

                mLoadingSnackbar.dismiss();
                //PlaceDetailsModel mPlaceModel = mRealm.where(PlaceDetailsModel.class).equalTo("place_id", mCurrentPlaceId).findFirst();
                mPDDataCursor = getContentResolver().query(mUriPD, null, null, new String[]{mCurrentPlaceId}, null);
                mPhotosListCursor = getContentResolver().query(mUriPDP, null, null, new String[]{mCurrentPlaceId}, null);
                mReviewsListCursor = getContentResolver().query(mUriPDR, null, null, new String[]{mCurrentPlaceId}, null);

                if(mPDDataCursor.getCount() != 0){
                    mEventBus.post(new PlaceDetailsSuccessEvent(mPDDataCursor, mPhotosListCursor, mReviewsListCursor));
                }
                mEventBus.post(new PlaceDetailsErrorEvent("Please check your network connection."));
            }
        });

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
    public void onEvent(CurrentPlaceEvent mEvent){
        mCurrentPlaceId = mEvent.getmCurrentPlaceId();
    }


    public void onEventMainThread(PlaceDetailsSuccessEvent mEvent){
        //final PlaceDetailsModel mPlaceModel = mEvent.getmPlaceDetailsModel();
        mPDDataCursor = mEvent.getmPDCursor();
        mPhotosListCursor = mEvent.getmPDPCursor();
        mReviewsListCursor = mEvent.getmPDRCursor();

        mPDDataCursor.moveToPosition(0);

        getSupportActionBar().setTitle(mPDDataCursor.getString(mPDDataCursor.getColumnIndex(DbHelper.PD_NAME)));
        mPlaceDetailsName.setText(mPDDataCursor.getString(mPDDataCursor.getColumnIndex(DbHelper.PD_NAME)));
        mPlaceDetailsVicinity.setText(mPDDataCursor.getString(mPDDataCursor.getColumnIndex(DbHelper.PD_VICINITY)));

        if(mPDDataCursor.getString(mPDDataCursor.getColumnIndex(DbHelper.PD_INT_PHONE_NUMBER)) != null){
            mPlaceDetailsPhone.setText("Phone : " + mPDDataCursor.getString(mPDDataCursor.getColumnIndex(DbHelper.PD_INT_PHONE_NUMBER)));
        }else{
            mPlaceDetailsPhone.setText("Phone : Not Available");
        }

        if(mPDDataCursor.getString(mPDDataCursor.getColumnIndex(DbHelper.PD_RATING)) != null){
            mPlaceDetailsRating.setText(mPDDataCursor.getString(mPDDataCursor.getColumnIndex(DbHelper.PD_RATING)));
            mPlaceDetailsRatingBar.setRating(Float.parseFloat(mPDDataCursor.getString(mPDDataCursor.getColumnIndex(DbHelper.PD_RATING))));
        }else{
            mPlaceDetailsRating.setText("Rating : Not Available");
        }


        // Photos RV
        //mPhotosList = mPlaceModel.getmPlacePhotosList();
        if(mPhotosListCursor.getCount() != 0){
            mPDPhotosRVAdapter = new PDPhotosRVAdapter(mPhotosListCursor, mPlaceDetailsCL);
            mPDPhotosRV.setAdapter(mPDPhotosRVAdapter);
            mPDPhotosRVAdapter.notifyDataSetChanged();
        }else{
            mPDPhotosError.setVisibility(View.VISIBLE);
        }


        // Reviews RV
        //mReviewsList = mPlaceModel.getmPlaceReviewsList();
        if(mReviewsListCursor.getCount() != 0){
            mPDReviewsRVAdapter = new PDReviewsRVAdapter(mReviewsListCursor, mPlaceDetailsCL);
            mPDReviewsRV.setAdapter(mPDReviewsRVAdapter);
            mPDReviewsRVAdapter.notifyDataSetChanged();
        }else {
            mPDReviewsError.setVisibility(View.VISIBLE);
        }

        if(mPDDataCursor.getString(mPDDataCursor.getColumnIndex(DbHelper.PD_WEBSITE)) != null){
            // Visit Website onClick
            mPDWebsiteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(mPDDataCursor.getString(mPDDataCursor.getColumnIndex(DbHelper.PD_WEBSITE)))));
                }
            });
        }else{
            mPDWebsiteBtn.setVisibility(View.GONE);
        }

        mPDMarksAsFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mFavDbCount == 0){
                    //insert
                    ContentValues mValues = new ContentValues();
                    mValues.put(DbHelper.FAV_PLACE_ID, mPDDataCursor.getString(mPDDataCursor.getColumnIndex(DbHelper.PD_PLACE_ID)));
                    mValues.put(DbHelper.FAV_CITY_PLACE_ID, mCurrentCityModel.getPlace_id());
                    mValues.put(DbHelper.FAV_NAME, mPDDataCursor.getString(mPDDataCursor.getColumnIndex(DbHelper.PD_NAME)));
                    mValues.put(DbHelper.FAV_VICINITY, mPDDataCursor.getString(mPDDataCursor.getColumnIndex(DbHelper.PD_VICINITY)));
                    if(mPhotosListCursor.getCount() != 0){
                        //mFavModel.setPhoto_reference(mPlaceModel.getmPlacePhotosList().get(0).getPhoto_reference());
                        mPhotosListCursor.moveToFirst();
                        mValues.put(DbHelper.FAV_PHOTO_REFERENCE, mPhotosListCursor.getString(mPhotosListCursor.getColumnIndex(DbHelper.PDP_PHOTO_REFERENCE)));
                    }
                    getContentResolver().insert(mUriFav, mValues);
                    mPDMarksAsFav.setText("Marked as Favourite");
                    Snackbar.make(mPlaceDetailsCL, "Marked as Favourite Successfully.", Snackbar.LENGTH_LONG).show();
                }else{
                    //delete
                    //mRealm.where(FavouriteModel.class).contains("place_id", mCurrentPlaceId).findAll().deleteAllFromRealm();
                    getContentResolver().delete(mUriFav,null, new String[]{mCurrentPlaceId});
                    mPDMarksAsFav.setText("Unmarked as Favourite");
                    Snackbar.make(mPlaceDetailsCL, "Unmarked as Favourite Successfully.", Snackbar.LENGTH_LONG).show();
                }

                /*
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if(mFavDbCount == 0){
                            FavouriteModel mFavModel = new FavouriteModel();
                            mFavModel.setPlace_id(mPlaceModel.getPlace_id());
                            mFavModel.setCity_place_id(mCurrentCityModel.getPlace_id());
                            mFavModel.setName(mPlaceModel.getName());
                            mFavModel.setVicinity(mPlaceModel.getViciniy());
                            if(mPlaceModel.getmPlacePhotosList().size() != 0){
                                mFavModel.setPhoto_reference(mPlaceModel.getmPlacePhotosList().get(0).getPhoto_reference());
                            }

                            mRealm.copyToRealmOrUpdate(mFavModel);

                            mPDMarksAsFav.setText("Marked as Favourite");
                            Snackbar.make(mPlaceDetailsCL, "Marked as Favourite Successfully.", Snackbar.LENGTH_LONG).show();
                        }else{
                            mRealm.where(FavouriteModel.class).contains("place_id", mCurrentPlaceId).findAll().deleteAllFromRealm();
                            mPDMarksAsFav.setText("Unmarked as Favourite");
                            Snackbar.make(mPlaceDetailsCL, "Unmarked as Favourite Successfully.", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });

                */
            }
        });
    }

    public void onEventMainThread(PlaceDetailsErrorEvent mEvent){
        Snackbar mSnackbar = Snackbar
                .make(mPlaceDetailsCL, mEvent.getMessage(), Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fetchPlaceDetails();
                        Log.d(TAG, "RETRY");
                    }
                });
        View mSnackbarView = mSnackbar.getView();
        mSnackbarView.setBackgroundColor(Color.RED);
        mSnackbar.setActionTextColor(Color.WHITE);
        mSnackbar.show();
    }
}
