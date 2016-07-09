package com.mhetrerajat.backpacker.Provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.mhetrerajat.backpacker.Db.CityModel;
import com.mhetrerajat.backpacker.Db.DbHelper;
import com.mhetrerajat.backpacker.Db.FavouriteModel;
import com.mhetrerajat.backpacker.Db.HighlightsModel;
import com.mhetrerajat.backpacker.Db.PlaceDetailsModel;
import com.mhetrerajat.backpacker.Db.PlacePhotoModel;
import com.mhetrerajat.backpacker.Db.PlaceReviewsModel;
import com.mhetrerajat.backpacker.Models.Place.Photo;
import com.mhetrerajat.backpacker.Models.Place.Review;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by rajatmhetre on 05/07/16.
 */
public class BackpackerContentProvider extends ContentProvider {

    private String TAG = BackpackerContentProvider.class.getSimpleName();

    private DbHelper mDbHelper;
    Realm mRealm;
    private static final String AUTHORITY = "com.mhetrerajat.backpacker.Provider.BackpackerContentProvider";

    private static final int HIGHLIGHTS = 10;
    private static final int CITY = 20;
    private static final int FAV = 30;
    private static final int PD = 40;
    private static final int PDP = 50;
    private static final int PDR = 60;
    private static final int FAV_PLACE = 70;

    static final String[] mColumns = new String[]{
            DbHelper.HIGHLIGHTS_PLACE_ID,
            DbHelper.HIGHLIGHTS_CITY_PLACE_ID,
            DbHelper.HIGHLIGHTS_TYPE,
            DbHelper.HIGHLIGHTS_NAME,
            DbHelper.HIGHLIGHTS_VICINITY,
            DbHelper.HIGHLIGHTS_PHOTO_REFERENCE,
            DbHelper.HIGHLIGHTS_STORE_TYPE,
            DbHelper.HIGHLIGHTS_LATITUDE,
            DbHelper.HIGHLIGHTS_LONGITUDE,
            DbHelper.HIGHLIGHTS_RATING
    };


    static final String[] mCityColumns = new String[]{
            DbHelper.CITY_PLACE_ID,
            DbHelper.CITY_NAME,
            DbHelper.CITY_FORMATTED_ADDRESS,
            DbHelper.CITY_PHOTO_REFERENCE,
            DbHelper.CITY_REFERENCE,
            DbHelper.CITY_LATITUDE,
            DbHelper.CITY_LONGITUDE,
            DbHelper.CITY_ADDED_ON
    };

    static final String[] mFavColumns = new String[]{
            DbHelper.FAV_PLACE_ID,
            DbHelper.FAV_CITY_PLACE_ID,
            DbHelper.FAV_NAME,
            DbHelper.FAV_VICINITY,
            DbHelper.FAV_PHOTO_REFERENCE,
    };

    static final String[] mPDColumns = new String[]{
            DbHelper.PD_PLACE_ID,
            DbHelper.PD_CITY_PLACE_ID,
            DbHelper.PD_NAME,
            DbHelper.PD_FORMATTED_ADDRESS,
            DbHelper.PD_FORMATTED_PHONE_NUMBER,
            DbHelper.PD_INT_PHONE_NUMBER,
            DbHelper.PD_LATITUDE,
            DbHelper.PD_LONGITUDE,
            DbHelper.PD_RATING,
            DbHelper.PD_VICINITY,
            DbHelper.PD_WEBSITE
    };

    static final String[] mPDPColumns = new String[]{
            DbHelper.PDP_PLACE_ID,
            DbHelper.PDP_PHOTO_REFERENCE,
            DbHelper.PDP_HEIGHT,
            DbHelper.PDP_WIDTH
    };

    static final String[] mPDRColumns = new String[]{
            DbHelper.PDR_PLACE_ID,
            DbHelper.PDR_AUTHOR_NAME,
            DbHelper.PDR_AUTHOR_URL,
            DbHelper.PDR_TEXT,
            DbHelper.PDR_RATING,
            DbHelper.PDR_TIME
    };

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, "highlights", HIGHLIGHTS);
        sURIMatcher.addURI(AUTHORITY, "city", CITY);
        sURIMatcher.addURI(AUTHORITY, "fav", FAV);
        sURIMatcher.addURI(AUTHORITY, "favplace", FAV_PLACE);
        sURIMatcher.addURI(AUTHORITY, "PD", PD);
        sURIMatcher.addURI(AUTHORITY, "PDP", PDP);
        sURIMatcher.addURI(AUTHORITY, "PDR", PDR);
    }


    @Override
    public boolean onCreate() {
        //mDbHelper = new DbHelper(getContext());
        RealmConfiguration mRealmConfig = new RealmConfiguration
                .Builder(getContext())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(mRealmConfig);
        mRealm = Realm.getDefaultInstance();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        checkColumns(uri, projection);

        //queryBuilder.setTables(DbHelper.HIGHLIGHTS_TABLE);

        int uriType = sURIMatcher.match(uri);
        Log.d(TAG, String.valueOf(uri));
        Log.d(TAG, "query: " + uriType);
        switch (uriType) {
            case HIGHLIGHTS:
                RealmResults<HighlightsModel> results = mRealm.where(HighlightsModel.class).equalTo("city_place_id", selectionArgs[0]).equalTo("type", selectionArgs[1]).findAll();

                MatrixCursor matrixCursor = new MatrixCursor(mColumns);
                for (HighlightsModel mItem : results){
                    Object[] rowData = new Object[]{
                            mItem.getPlace_id(),
                            mItem.getCity_place_id(),
                            mItem.getType(),
                            mItem.getName(),
                            mItem.getVicinity(),
                            mItem.getPhoto_reference(),
                            mItem.getStore_type(),
                            mItem.getLatitude(),
                            mItem.getLongitude(),
                            mItem.getRating()
                    };
                    matrixCursor.addRow(rowData);
                }
                return matrixCursor;
            case CITY:
                RealmResults<CityModel> city_results = mRealm.where(CityModel.class).findAllSorted("added_on", Sort.DESCENDING);

                MatrixCursor city_cursor = new MatrixCursor(mCityColumns);
                for (CityModel mItem : city_results){
                    Object[] rowData = new Object[]{
                            mItem.getPlace_id(),
                            mItem.getName(),
                            mItem.getFormatted_address(),
                            mItem.getPhoto_reference(),
                            mItem.getReference(),
                            mItem.getLatitude(),
                            mItem.getLongitude(),
                            mItem.getAdded_on()
                    };
                    city_cursor.addRow(rowData);
                }
                return city_cursor;
            case FAV:
                RealmResults<FavouriteModel> fav_results = mRealm.where(FavouriteModel.class).equalTo("city_place_id", selectionArgs[0]).findAll();

                MatrixCursor fav_cursor = new MatrixCursor(mFavColumns);
                for (FavouriteModel mItem : fav_results){
                    Object[] rowData = new Object[]{
                            mItem.getPlace_id(),
                            mItem.getCity_place_id(),
                            mItem.getName(),
                            mItem.getVicinity(),
                            mItem.getPhoto_reference()
                    };
                    fav_cursor.addRow(rowData);
                }
                return fav_cursor;
            case FAV_PLACE:
                RealmResults<FavouriteModel> fav_place_result = mRealm.where(FavouriteModel.class).equalTo("place_id", selectionArgs[0]).findAll();

                MatrixCursor fav_place_cursor = new MatrixCursor(mFavColumns);
                for (FavouriteModel mItem : fav_place_result){
                    Object[] rowData = new Object[]{
                            mItem.getPlace_id(),
                            mItem.getCity_place_id(),
                            mItem.getName(),
                            mItem.getVicinity(),
                            mItem.getPhoto_reference()
                    };
                    fav_place_cursor.addRow(rowData);
                }
                return fav_place_cursor;
            case PD:
                RealmResults<PlaceDetailsModel> pd_results = mRealm.where(PlaceDetailsModel.class).equalTo("place_id", selectionArgs[0]).findAll();
                MatrixCursor pd_cursor = new MatrixCursor(mPDColumns);

                for(PlaceDetailsModel mItem : pd_results){
                    Object[] rowData = new Object[]{
                            mItem.getPlace_id(),
                            mItem.getCity_place_id(),
                            mItem.getName(),
                            mItem.getFormatted_address(),
                            mItem.getFormatted_phone_number(),
                            mItem.getInternational_phone_number(),
                            mItem.getLatitude(),
                            mItem.getLongitude(),
                            mItem.getRating(),
                            mItem.getViciniy(),
                            mItem.getWebsite()
                    };
                    pd_cursor.addRow(rowData);
                }

                return pd_cursor;
            case PDP:
                RealmResults<PlacePhotoModel> pdp_results = mRealm.where(PlacePhotoModel.class).equalTo("place_id", selectionArgs[0]).findAll();

                MatrixCursor pdp_cursor = new MatrixCursor(mPDPColumns);
                for (PlacePhotoModel mItem : pdp_results){
                    Object[] rowData = new Object[]{
                            mItem.getPlace_id(),
                            mItem.getPhoto_reference(),
                            mItem.getHeight(),
                            mItem.getwidth()
                    };
                    pdp_cursor.addRow(rowData);
                }
                return pdp_cursor;
            case PDR:
                RealmResults<PlaceReviewsModel> pdr_results = mRealm.where(PlaceReviewsModel.class).equalTo("place_id", selectionArgs[0]).findAll();

                MatrixCursor pdr_cursor = new MatrixCursor(mPDRColumns);
                for (PlaceReviewsModel mItem : pdr_results){
                    Object[] rowData = new Object[]{
                            mItem.getPlace_id(),
                            mItem.getAuthor_name(),
                            mItem.getAuthor_url(),
                            mItem.getText(),
                            mItem.getRating(),
                            mItem.getTime()
                    };
                    pdr_cursor.addRow(rowData);
                }
                return pdr_cursor;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        /*
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        */
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, final ContentValues contentValues) {
        int uriType = sURIMatcher.match(uri);
        //SQLiteDatabase sqlDB = mDbHelper.getWritableDatabase();
        //long id = 0;
        switch (uriType) {
            case HIGHLIGHTS:
                //id = sqlDB.insert(mDbHelper.HIGHLIGHTS_TABLE, null, contentValues);
                break;
            case FAV:
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                            FavouriteModel mFavModel = new FavouriteModel();
                            mFavModel.setPlace_id(contentValues.getAsString(DbHelper.FAV_PLACE_ID));
                            mFavModel.setCity_place_id(contentValues.getAsString(DbHelper.FAV_CITY_PLACE_ID));
                            mFavModel.setName(contentValues.getAsString(DbHelper.FAV_NAME));
                            mFavModel.setVicinity(contentValues.getAsString(DbHelper.FAV_VICINITY));
                            if(!contentValues.get(DbHelper.FAV_PHOTO_REFERENCE).equals(null)){
                                //mFavModel.setPhoto_reference(mPlaceModel.getmPlacePhotosList().get(0).getPhoto_reference());
                                mFavModel.setPhoto_reference(contentValues.getAsString(DbHelper.FAV_PHOTO_REFERENCE));
                            }

                            mRealm.copyToRealmOrUpdate(mFavModel);
                    }
                });
                return uri;
            case PD :
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        PlaceDetailsModel mPlaceDetailsModel = new PlaceDetailsModel();

                        mPlaceDetailsModel.setCity_place_id(contentValues.getAsString(DbHelper.PD_CITY_PLACE_ID));
                        mPlaceDetailsModel.setPlace_id(contentValues.getAsString(DbHelper.PD_PLACE_ID));
                        mPlaceDetailsModel.setName(contentValues.getAsString(DbHelper.PD_NAME));
                        mPlaceDetailsModel.setLongitude(contentValues.getAsDouble(DbHelper.PD_LONGITUDE));
                        mPlaceDetailsModel.setLatitude(contentValues.getAsDouble(DbHelper.PD_LATITUDE));
                        mPlaceDetailsModel.setFormatted_address(contentValues.getAsString(DbHelper.PD_FORMATTED_ADDRESS));
                        mPlaceDetailsModel.setFormatted_phone_number(contentValues.getAsString(DbHelper.PD_FORMATTED_PHONE_NUMBER));
                        mPlaceDetailsModel.setInternational_phone_number(contentValues.getAsString(DbHelper.PD_INT_PHONE_NUMBER));
                        mPlaceDetailsModel.setViciniy(contentValues.getAsString(DbHelper.PD_VICINITY));
                        mPlaceDetailsModel.setWebsite(contentValues.getAsString(DbHelper.PD_WEBSITE));
                        mPlaceDetailsModel.setRating(contentValues.getAsDouble(DbHelper.PD_RATING));

                        mRealm.copyToRealmOrUpdate(mPlaceDetailsModel);
                    }
                });
                return uri;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        //getContext().getContentResolver().notifyChange(uri, null);
        //return Uri.parse(BASE_PATH + "/" + id);

        return null;
    }


    @Override
    public int bulkInsert(Uri uri, final ContentValues[] values) {
        int uriType = sURIMatcher.match(uri);
        //SQLiteDatabase sqlDB = mDbHelper.getWritableDatabase();
        //long id = 0;
        switch (uriType) {
            case HIGHLIGHTS:
                //id = sqlDB.insert(mDbHelper.HIGHLIGHTS_TABLE, null, contentValues);
                mRealm.executeTransaction(new Realm.Transaction() {

                    List<HighlightsModel> mHighlightModelList = new ArrayList<HighlightsModel>();

                    @Override
                    public void execute(Realm realm) {

                        for (ContentValues mItem : values) {
                            try {
                                HighlightsModel mCurrentModel = new HighlightsModel();
                                mCurrentModel.setPlace_id(mItem.getAsString(mColumns[0]));
                                mCurrentModel.setCity_place_id(mItem.getAsString(mColumns[1]));
                                mCurrentModel.setType(mItem.getAsString(mColumns[2]));
                                mCurrentModel.setName(mItem.getAsString(mColumns[3]));
                                mCurrentModel.setVicinity(mItem.getAsString(mColumns[4]));
                                mCurrentModel.setPhoto_reference(mItem.getAsString(mColumns[5]));
                                mCurrentModel.setStore_type(mItem.getAsString(mColumns[6]));
                                mCurrentModel.setLatitude(mItem.getAsDouble(mColumns[7]));
                                mCurrentModel.setLongitude(mItem.getAsDouble(mColumns[8]));
                                mCurrentModel.setRating(mItem.getAsDouble(mColumns[9]));

                                mHighlightModelList.add(mCurrentModel);

                            } catch (Exception e) {
                                Log.e(TAG, e.getLocalizedMessage());
                            }
                        }

                        realm.copyToRealmOrUpdate(mHighlightModelList);
                    }
                });
                break;
            case CITY:
                mRealm.executeTransaction(new Realm.Transaction() {

                    List<CityModel> mCityModelList = new ArrayList<CityModel>();

                    @Override
                    public void execute(Realm realm) {

                        for (ContentValues mItem : values){
                            try{
                                if(mItem != null){
                                    CityModel mCityModel = new CityModel();
                                    mCityModel.setPlace_id(mItem.getAsString(DbHelper.CITY_PLACE_ID));
                                    mCityModel.setName(mItem.getAsString(DbHelper.CITY_NAME));
                                    mCityModel.setFormatted_address(mItem.getAsString(DbHelper.CITY_FORMATTED_ADDRESS));
                                    mCityModel.setPhoto_reference(mItem.getAsString(DbHelper.CITY_PHOTO_REFERENCE));
                                    mCityModel.setReference(mItem.getAsString(DbHelper.CITY_REFERENCE));
                                    mCityModel.setLatitude(mItem.getAsDouble(DbHelper.CITY_LATITUDE));
                                    mCityModel.setLongitude(mItem.getAsDouble(DbHelper.CITY_LONGITUDE));
                                    mCityModel.setAdded_on(mItem.getAsLong(DbHelper.CITY_ADDED_ON));

                                    mCityModelList.add(mCityModel);
                                }
                            }catch (Exception e){
                                Log.d(TAG, e.getLocalizedMessage());
                            }

                        }

                        realm.copyToRealmOrUpdate(mCityModelList);

                    }
                });
                break;
            case PDP:

                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        List<PlacePhotoModel> mList = new ArrayList<PlacePhotoModel>();

                        try{

                            for (ContentValues mItem : values){

                                PlacePhotoModel mPhotoModel = new PlacePhotoModel();
                                mPhotoModel.setPlace_id(mItem.getAsString(DbHelper.PDP_PLACE_ID));
                                mPhotoModel.setHeight(mItem.getAsInteger(DbHelper.PDP_HEIGHT));
                                mPhotoModel.setwidth(mItem.getAsInteger(DbHelper.PDP_WIDTH));
                                mPhotoModel.setPhoto_reference(mItem.getAsString(DbHelper.PDP_PHOTO_REFERENCE));

                                mList.add(mPhotoModel);
                            }

                        }catch (Exception e){
                            Log.d(TAG ,e.getLocalizedMessage());
                        }

                        mRealm.copyToRealmOrUpdate(mList);
                    }
                });
                break;
            case PDR:

                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        List<PlaceReviewsModel> mList = new ArrayList<PlaceReviewsModel>();

                        try {

                            for (ContentValues mItem : values) {

                                if(mItem != null) {

                                    PlaceReviewsModel mReviewsModel = new PlaceReviewsModel();
                                    mReviewsModel.setPlace_id(mItem.getAsString(DbHelper.PDR_PLACE_ID));
                                    mReviewsModel.setAuthor_name(mItem.getAsString(DbHelper.PDR_AUTHOR_NAME));
                                    mReviewsModel.setAuthor_url(mItem.getAsString(DbHelper.PDR_AUTHOR_URL));
                                    mReviewsModel.setRating(mItem.getAsDouble(DbHelper.PDR_RATING));
                                    mReviewsModel.setText(mItem.getAsString(DbHelper.PDR_TEXT));
                                    mReviewsModel.setTime(mItem.getAsLong(DbHelper.PDR_TIME));

                                    mList.add(mReviewsModel);
                                }
                            }

                        }catch (Exception e) {
                            Log.d(TAG, e.getLocalizedMessage());
                        }

                        mRealm.copyToRealmOrUpdate(mList);

                    }
                });
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        return values.length;
    }

    @Override
    public int delete(Uri uri, String selection, final String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        switch (uriType){
            case FAV:
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        mRealm.where(FavouriteModel.class).contains("place_id", selectionArgs[0]).findAll().deleteAllFromRealm();
                    }
                });
                return 1;
            default :
                Log.d(TAG, "Error Uri : " + uri);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    private void checkColumns(Uri uri, String[] projection) {
        int uriType = sURIMatcher.match(uri);
        String[] available = null;

        switch (uriType){
            case HIGHLIGHTS:
                available = new String[]{
                        DbHelper.HIGHLIGHTS_PLACE_ID,
                        DbHelper.HIGHLIGHTS_CITY_PLACE_ID,
                        DbHelper.HIGHLIGHTS_TYPE,
                        DbHelper.HIGHLIGHTS_NAME,
                        DbHelper.HIGHLIGHTS_VICINITY,
                        DbHelper.HIGHLIGHTS_PHOTO_REFERENCE,
                        DbHelper.HIGHLIGHTS_STORE_TYPE,
                        DbHelper.HIGHLIGHTS_LATITUDE,
                        DbHelper.HIGHLIGHTS_LONGITUDE,
                        DbHelper.HIGHLIGHTS_RATING
                };
                break;
            case CITY:
                available = new String[]{
                        DbHelper.CITY_PLACE_ID,
                        DbHelper.CITY_NAME,
                        DbHelper.CITY_FORMATTED_ADDRESS,
                        DbHelper.CITY_PHOTO_REFERENCE,
                        DbHelper.CITY_REFERENCE,
                        DbHelper.CITY_LATITUDE,
                        DbHelper.CITY_LONGITUDE,
                        DbHelper.CITY_ADDED_ON
                };
                break;
            case FAV :
                available = new String[]{
                        DbHelper.FAV_PLACE_ID,
                        DbHelper.FAV_CITY_PLACE_ID,
                        DbHelper.FAV_NAME,
                        DbHelper.FAV_VICINITY,
                        DbHelper.FAV_PHOTO_REFERENCE
                };
                break;
            case PD :
                available = new String[]{
                        DbHelper.PD_PLACE_ID,
                        DbHelper.PD_CITY_PLACE_ID,
                        DbHelper.PD_NAME,
                        DbHelper.PD_FORMATTED_ADDRESS,
                        DbHelper.PD_FORMATTED_PHONE_NUMBER,
                        DbHelper.PD_INT_PHONE_NUMBER,
                        DbHelper.PD_LATITUDE,
                        DbHelper.PD_LONGITUDE,
                        DbHelper.PD_RATING,
                        DbHelper.PD_VICINITY,
                        DbHelper.PD_WEBSITE

                };
                break;
            case PDP:
                available = new String[]{
                        DbHelper.PDP_PLACE_ID,
                        DbHelper.PDP_PHOTO_REFERENCE,
                        DbHelper.PDP_HEIGHT,
                        DbHelper.PDP_WIDTH
                };
                break;
            case PDR :
                available = new String[]{
                        DbHelper.PDR_PLACE_ID,
                        DbHelper.PDR_AUTHOR_NAME,
                        DbHelper.PDR_AUTHOR_URL,
                        DbHelper.PDR_TEXT,
                        DbHelper.PDR_RATING,
                        DbHelper.PDR_TIME
                };
                break;
            default:
                Log.d(TAG, "Error : Check Columns");
        }



        if (projection != null && available != null) {
            HashSet<String> requestedColumns = new HashSet<String>(
                    Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(
                    Arrays.asList(available));
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException(
                        "Unknown columns in projection");
            }
        }
    }
}
