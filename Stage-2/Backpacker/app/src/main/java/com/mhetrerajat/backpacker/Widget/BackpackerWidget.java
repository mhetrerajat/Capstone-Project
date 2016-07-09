package com.mhetrerajat.backpacker.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mhetrerajat.backpacker.Models.Highlight.Highlight;
import com.mhetrerajat.backpacker.Models.Highlight.Result;
import com.mhetrerajat.backpacker.R;
import com.mhetrerajat.backpacker.Service.PlaceService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Implementation of App Widget functionality.
 */
public class BackpackerWidget extends AppWidgetProvider implements LocationListener {

    private static RemoteViews views;
    private static final String TAG = BackpackerWidget.class.getSimpleName();
    GPSTracker gps;
    double LATITUDE, LONGITUDE;
    Context context;
    static String LOCATION = "";
    static PlaceModel mRandomPlace = new PlaceModel();

    static Gson gson;
    static Retrofit retrofit;
    static PlaceService mService;


    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        this.context = context;

        gps = new GPSTracker(context);
        views = new RemoteViews(context.getPackageName(), R.layout.backpacker_widget);

        // check if GPS enabled
        if(gps.canGetLocation()){

             LATITUDE = gps.getLatitude();
             LONGITUDE = gps.getLongitude();

            Log.d(TAG, String.valueOf(LATITUDE));
            Log.d(TAG, String.valueOf(LONGITUDE));

            // \n is for new line
            //views.setTextViewText(R.id.appwidget_text, String.valueOf(latitude));
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }


        LOCATION = new StringBuilder(String.valueOf(LATITUDE))
                .append(",")
                .append(LONGITUDE).toString();


        views.setOnClickPendingIntent(R.id.sync_button,
                buildButtonPendingIntent(context));


        views.setTextViewText(R.id.widget_place_name, getTitle());
        views.setTextViewText(R.id.widget_place_vicinity, getDesc());

        // request for widget update
        pushWidgetUpdate(context, views);




    }


    public static PendingIntent buildButtonPendingIntent(final Context context) {
        ++WidgetIntentReceiver.clickCount;

        //final Result mWidgetData;

        gson = new GsonBuilder().create();
        retrofit = new Retrofit.Builder()
                .baseUrl(context.getResources().getString(R.string.REMOTE_BASE_URL))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        mService = retrofit.create(PlaceService.class);

        Map<String, String> params = new HashMap<>();
        params.put("key", context.getResources().getString(R.string.REMOTE_API_KEY));
        params.put("types", "amusement_park|zoo|aquarium|art_gallery|bowling_alley|campground|casino|church|hindu_temple|stadium|shopping_mall|movie_theater|mosque|restaurant|cafe|bar");
        params.put("location", LOCATION);

        final Random mRandom = new Random();

        final AppWidgetManager manager = AppWidgetManager.getInstance(context);

        Call<Highlight> mCall = mService.getNearByHighlights(params);
        mCall.enqueue(new Callback<Highlight>() {
            @Override
            public void onResponse(Response<Highlight> response, Retrofit retrofit) {
                if(response.isSuccess()){
                    int random = randInt(0, response.body().getResults().size() - 1);
                    Result mWidgetData = response.body().getResults().get(random);

                    mRandomPlace.setName(mWidgetData.getName());
                    mRandomPlace.setVicinity(mWidgetData.getVicinity());


                    Log.d(TAG, mRandomPlace.getName());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, t.getLocalizedMessage());
            }
        });

        Bundle mBundle = new Bundle();
        Log.d(TAG, LOCATION);
        mBundle.putString("LOCATION", LOCATION);
        mBundle.putParcelable("PLACE", mRandomPlace);

        // initiate widget update request
        Intent intent = new Intent();
        intent.setAction(WidgetUtils.WIDGET_UPDATE_ACTION);
        intent.putExtra("DATA", mBundle);
        return PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static CharSequence getDesc() {
        return "Sync to see some nearby locations.";
    }

    private static CharSequence getTitle() {
        return "Nearby Locations";
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
        ComponentName myWidget = new ComponentName(context,
                BackpackerWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }

    public static int randInt(int min, int max) {

        Random rand = new Random();

        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }








    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    // Location Listener

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}

