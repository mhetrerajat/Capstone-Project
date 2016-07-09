package com.mhetrerajat.backpacker.Events;

/**
 * Created by rajatmhetre on 25/06/16.
 */
public class CurrentPlaceEvent {

    private String mCurrentPlaceId;

    public CurrentPlaceEvent(String mCurrentPlaceId) {
        this.mCurrentPlaceId = mCurrentPlaceId;
    }

    public String getmCurrentPlaceId() {
        return mCurrentPlaceId;
    }
}
