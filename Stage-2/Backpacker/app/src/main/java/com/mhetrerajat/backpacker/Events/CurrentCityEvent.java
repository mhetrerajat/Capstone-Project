package com.mhetrerajat.backpacker.Events;

import com.mhetrerajat.backpacker.Db.CityModel;

/**
 * Created by rajatmhetre on 25/06/16.
 */
public class CurrentCityEvent {

    private CityModel mCurrentCity;

    public CurrentCityEvent(CityModel mCurrentCity) {
        this.mCurrentCity = mCurrentCity;
    }

    public CityModel getmCurrentCity() {
        return mCurrentCity;
    }
}
