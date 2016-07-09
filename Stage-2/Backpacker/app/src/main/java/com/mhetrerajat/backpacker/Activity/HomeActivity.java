package com.mhetrerajat.backpacker.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mhetrerajat.backpacker.BackpackerApplication;
import com.mhetrerajat.backpacker.Db.CityModel;
import com.mhetrerajat.backpacker.Events.CurrentCityEvent;
import com.mhetrerajat.backpacker.Events.HighlightTypeEvent;
import com.mhetrerajat.backpacker.R;
import com.squareup.picasso.Picasso;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import io.realm.Realm;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static StringBuilder PHOTO_URL;
    private String TAG = HomeActivity.class.getSimpleName();

    @BindString(R.string.REMOTE_API_KEY) String API_KEY;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.home_activity_cl) CoordinatorLayout mHomeActivityCL;
    @BindView(R.id.home_activity_drawer) DrawerLayout mHomeActivityDL;
    @BindView(R.id.home_activity_nv) NavigationView mHomeActivityNV;

    private EventBus mEventBus;
    private Realm mRealm;
    private CityModel mCurrentCityModel;

    private GoogleApiClient mGApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Event Bus
        mEventBus = EventBus.getDefault();

        //Init
        mRealm = Realm.getDefaultInstance();
        mCurrentCityModel = mEventBus.getStickyEvent(CurrentCityEvent.class).getmCurrentCity();

        // Bind Views
        ButterKnife.bind(this);

        // Init Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Discover " + mCurrentCityModel.getName());
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Snackbar.make(mHomeActivityCL, mCurrentCityModel.getFormatted_address(), Snackbar.LENGTH_LONG).show();

        BackpackerApplication mBackpackerApp = (BackpackerApplication) getApplicationContext();
        mGApiClient = mBackpackerApp.mGApiClient;

        // Navigation View
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mHomeActivityDL, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mHomeActivityDL.setDrawerListener(toggle);
        toggle.syncState();
        mHomeActivityNV.setNavigationItemSelectedListener(this);

        View header = mHomeActivityNV.getHeaderView(0);

        TextView mNHCityName = (TextView) header.findViewById(R.id.nav_header_city_name_tv);
        ImageView mNHCityHeaderBg = (ImageView) header.findViewById(R.id.nav_header_bg_iv);

        mNHCityName.setText(mCurrentCityModel.getName());

        if(mCurrentCityModel.getPhoto_reference() != null){
            PHOTO_URL = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?photoreference=")
                    .append(mCurrentCityModel.getPhoto_reference())
                    .append("&maxheight=")
                    .append(300)
                    .append("&key=")
                    .append(API_KEY);
            Picasso.with(header.getContext())
                    .load(PHOTO_URL.toString())
                    .into(mNHCityHeaderBg);
        }



    }


    @Override
    protected void onStart() {
        super.onStart();
        mEventBus.registerSticky(this);
        if (!mGApiClient.isConnected()) {
            mGApiClient.connect();
        }
    }

    @Override
    protected void onDestroy() {
        mEventBus.unregister(this);
        super.onDestroy();
        if (mGApiClient.isConnected()) {
            mGApiClient.disconnect();
        }
    }

    @Override
    protected void onStop() {
        mEventBus.unregister(this);
        super.onStop();
        if (mGApiClient.isConnected()) {
            mGApiClient.disconnect();
        }

    }


    public void onEvent(CurrentCityEvent mEvent){
        mCurrentCityModel = mEvent.getmCurrentCity();
    }

    @OnClick(R.id.tourist_menu_sights_cv)
    void onClickTouristMenuSightsCV(){
        //mEventBus.postSticky(new CurrentCityEvent(mCurrentCityModel));
        //Snackbar.make(mHomeActivityCL, "Sights", Snackbar.LENGTH_LONG).show();

        //startActivity(new Intent(this, SightsListActivity.class));

        mEventBus.postSticky(new HighlightTypeEvent("sights"));
        startActivity(new Intent(this, HighlightsListActivity.class));
    }

    @OnClick(R.id.tourist_menu_restaurants_cv)
    void onClickTouristMenuRestaurantsCV(){
        //mEventBus.postSticky(new CurrentCityEvent(mCurrentCityModel));
        //startActivity(new Intent(this, RestaurantsListActivity.class));
        //Snackbar.make(mHomeActivityCL, "Restaurants", Snackbar.LENGTH_LONG).show();

        mEventBus.postSticky(new HighlightTypeEvent("restaurant"));
        startActivity(new Intent(this, HighlightsListActivity.class));
    }

    @OnClick(R.id.tourist_menu_hotels_cv)
    void onClickTouristMenuHotelsCV(){
        //mEventBus.postSticky(new CurrentCityEvent(mCurrentCityModel));
        //startActivity(new Intent(this, HotelsListActivity.class));
        //Snackbar.make(mHomeActivityCL, "Hotels", Snackbar.LENGTH_LONG).show();

        mEventBus.postSticky(new HighlightTypeEvent("lodging"));
        startActivity(new Intent(this, HighlightsListActivity.class));
    }

    @OnClick(R.id.tourist_menu_drink_cv)
    void onClickTouristMenuDrinkCV(){
        //mEventBus.postSticky(new CurrentCityEvent(mCurrentCityModel));
        //Snackbar.make(mHomeActivityCL, "Drink", Snackbar.LENGTH_LONG).show();
        //startActivity(new Intent(this, DrinkListActivity.class));

        mEventBus.postSticky(new HighlightTypeEvent("bar"));
        startActivity(new Intent(this, HighlightsListActivity.class));
    }

    @OnClick(R.id.tourist_menu_shopping_cv)
    void onClickTouristMenuShoppingCV(){
        //mEventBus.postSticky(new CurrentCityEvent(mCurrentCityModel));
        //Snackbar.make(mHomeActivityCL, "Shopping", Snackbar.LENGTH_LONG).show();

        //startActivity(new Intent(this, StoreListActivity.class));

        mEventBus.postSticky(new HighlightTypeEvent("store"));
        startActivity(new Intent(this, HighlightsListActivity.class));
    }

    @OnClick(R.id.tourist_menu_club_cv)
    void onClickTouristMenuClubCV(){
        //mEventBus.postSticky(new CurrentCityEvent(mCurrentCityModel));
        //Snackbar.make(mHomeActivityCL, "Club", Snackbar.LENGTH_LONG).show();
        //startActivity(new Intent(this, ClubListActivity.class));

        mEventBus.postSticky(new HighlightTypeEvent("night_club"));
        startActivity(new Intent(this, HighlightsListActivity.class));
    }

    @OnClick(R.id.highlights_transit_cv)
    void onClickTransitCV(){
        mEventBus.postSticky(new HighlightTypeEvent("transit_station"));
        startActivity(new Intent(this, HighlightsListActivity.class));
    }

    @OnClick(R.id.highlights_cafe_cv)
    void onClickCafeCV(){
        mEventBus.postSticky(new HighlightTypeEvent("cafe"));
        startActivity(new Intent(this, HighlightsListActivity.class));
    }

    @OnClick(R.id.highlights_aquarium_cv)
    void onClickAquariumCV(){
        mEventBus.postSticky(new HighlightTypeEvent("aquarium"));
        startActivity(new Intent(this, HighlightsListActivity.class));
    }

    @OnClick(R.id.highlights_bakery_cv)
    void onClickBakeryCV(){
        mEventBus.postSticky(new HighlightTypeEvent("bakery"));
        startActivity(new Intent(this, HighlightsListActivity.class));
    }

    @OnClick(R.id.highlights_nature_cv)
    void onClickNatureCV(){
        mEventBus.postSticky(new HighlightTypeEvent("natural_feature"));
        startActivity(new Intent(this, HighlightsListActivity.class));
    }

    @OnClick(R.id.highlights_art_cv)
    void onClickArtCV(){
        mEventBus.postSticky(new HighlightTypeEvent("art_gallery"));
        startActivity(new Intent(this, HighlightsListActivity.class));
    }

    @OnClick(R.id.highlights_subway_cv)
    void onClickSubwayCV(){
        mEventBus.postSticky(new HighlightTypeEvent("subway_station"));
        startActivity(new Intent(this, HighlightsListActivity.class));
    }

    @OnClick(R.id.highlights_taxi_cv)
    void onClickTaxiCV(){
        mEventBus.postSticky(new HighlightTypeEvent("taxi_stand"));
        startActivity(new Intent(this, HighlightsListActivity.class));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.ViewAllCitiesLink){
            startActivity(new Intent(this, CitySelectActivity.class));
        }else if(id == R.id.FavoritesLink){
            startActivity(new Intent(this, FavouriteListActivity.class));
        }else if(id == R.id.LogoutLink){
            if(mGApiClient.isConnected()){
                Auth.GoogleSignInApi.signOut(mGApiClient);
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        mRealm.deleteAll();
                    }
                });
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }


            Log.d(TAG, String.valueOf(mGApiClient.isConnected()));
        }

        mHomeActivityDL.closeDrawer(GravityCompat.START);

        return false;
    }
}
