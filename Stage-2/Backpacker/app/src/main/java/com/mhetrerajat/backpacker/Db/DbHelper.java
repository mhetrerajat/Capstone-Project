package com.mhetrerajat.backpacker.Db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by rajatmhetre on 06/07/16.
 */
public class DbHelper {

    private String TAG = DbHelper.class.getSimpleName();

    public static final String HIGHLIGHTS_TABLE = "highlights";
    public static final String HIGHLIGHTS_PLACE_ID = "place_id";
    public static final String HIGHLIGHTS_CITY_PLACE_ID = "city_place_id";
    public static final String HIGHLIGHTS_TYPE = "type";
    public static final String HIGHLIGHTS_NAME = "name";
    public static final String HIGHLIGHTS_VICINITY = "vicinity";
    public static final String HIGHLIGHTS_PHOTO_REFERENCE = "photo_reference";
    public static final String HIGHLIGHTS_STORE_TYPE = "store_type";
    public static final String HIGHLIGHTS_LATITUDE = "latitude";
    public static final String HIGHLIGHTS_LONGITUDE = "longitude";
    public static final String HIGHLIGHTS_RATING = "rating";


    public static final String CITY_PLACE_ID = "place_id";
    public static final String CITY_NAME = "name";
    public static final String CITY_FORMATTED_ADDRESS = "formatted_address";
    public static final String CITY_PHOTO_REFERENCE = "photo_reference";
    public static final String CITY_REFERENCE = "reference";
    public static final String CITY_LATITUDE = "latitude";
    public static final String CITY_LONGITUDE = "longitude";
    public static final String CITY_ADDED_ON = "added_on";


    public static final String FAV_PLACE_ID = "place_id";
    public static final String FAV_CITY_PLACE_ID = "city_place_id";
    public static final String FAV_NAME = "name";
    public static final String FAV_VICINITY = "vicinity";
    public static final String FAV_PHOTO_REFERENCE = "photo_reference";


    public static final String PD_PLACE_ID = "place_id";
    public static final String PD_CITY_PLACE_ID = "city_place_id";
    public static final String PD_NAME = "name";
    public static final String PD_FORMATTED_ADDRESS = "formatted_address";
    public static final String PD_FORMATTED_PHONE_NUMBER = "formatted_phone_number";
    public static final String PD_INT_PHONE_NUMBER = "international_phone_number";
    public static final String PD_LATITUDE = "latitude";
    public static final String PD_LONGITUDE = "longitude";
    public static final String PD_RATING = "rating";
    public static final String PD_WEBSITE = "website";
    public static final String PD_VICINITY = "vicinity";



    public static final String PDP_PLACE_ID = "place_id";
    public static final String PDP_PHOTO_REFERENCE = "photo_reference";
    public static final String PDP_HEIGHT = "height";
    public static final String PDP_WIDTH = "width";


    public static final String PDR_PLACE_ID = "place_id";
    public static final String PDR_AUTHOR_NAME = "author_name";
    public static final String PDR_AUTHOR_URL = "author_url";
    public static final String PDR_TEXT = "text";
    public static final String PDR_RATING = "rating";
    public static final String PDR_TIME = "time";


    private static final int DATABASE_VERSION = 1;




}
