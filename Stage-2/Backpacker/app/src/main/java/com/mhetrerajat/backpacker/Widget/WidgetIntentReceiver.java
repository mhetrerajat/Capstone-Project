package com.mhetrerajat.backpacker.Widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.mhetrerajat.backpacker.R;

/**
 * Created by rajatmhetre on 02/07/16.
 */
public class WidgetIntentReceiver extends BroadcastReceiver{

    public static int clickCount = 0;
    private String TAG = WidgetIntentReceiver.class.getSimpleName();
    PlaceModel mCurrentPlaceModel;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WidgetUtils.WIDGET_UPDATE_ACTION)) {
            updateWidgetPictureAndButtonListener(context, intent.getBundleExtra("DATA"));
        }
    }

    private void updateWidgetPictureAndButtonListener(Context context, Bundle mBundle) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.backpacker_widget);

        Log.d(TAG, mBundle.getString("LOCATION"));
        Log.d(TAG, mBundle.getParcelable("PLACE").toString());

        mCurrentPlaceModel = mBundle.getParcelable("PLACE");

        // updating view
        remoteViews.setTextViewText(R.id.widget_place_name, mCurrentPlaceModel.getName());
        remoteViews.setTextViewText(R.id.widget_place_vicinity, mCurrentPlaceModel.getVicinity());

        // re-registering for click listener
        remoteViews.setOnClickPendingIntent(R.id.sync_button,
                BackpackerWidget.buildButtonPendingIntent(context));

        BackpackerWidget.pushWidgetUpdate(context.getApplicationContext(),
                    remoteViews);
    }


}
