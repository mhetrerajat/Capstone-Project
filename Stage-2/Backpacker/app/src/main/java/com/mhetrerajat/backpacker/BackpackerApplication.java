package com.mhetrerajat.backpacker;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mhetrerajat.backpacker.Activity.MainActivity;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by rajatmhetre on 24/06/16.
 */
public class BackpackerApplication extends Application {

    public GoogleApiClient mGApiClient;
    public static RealmConfiguration mRealmConfig;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialise Realm
        Realm.setDefaultConfiguration(setmRealmConfig(this));
    }

    public static RealmConfiguration setmRealmConfig(Context context){
        mRealmConfig = new RealmConfiguration
                .Builder(context)
                .deleteRealmIfMigrationNeeded()
                .build();
        return mRealmConfig;
    }

}
