package com.mhetrerajat.backpacker.Events;

import android.database.Cursor;

import com.mhetrerajat.backpacker.Db.PlaceDetailsModel;
import com.mhetrerajat.backpacker.Models.Place.Place;

/**
 * Created by rajatmhetre on 25/06/16.
 */
public class PlaceDetailsSuccessEvent {

    //private PlaceDetailsModel mPlaceDetailsModel;
    private Cursor mPDCursor, mPDPCursor, mPDRCursor;

    public PlaceDetailsSuccessEvent(Cursor mPDCursor, Cursor mPDPCursor, Cursor mPDRCursor) {
        this.mPDCursor = mPDCursor;
        this.mPDPCursor = mPDPCursor;
        this.mPDRCursor = mPDRCursor;
    }

    public Cursor getmPDCursor() {
        return mPDCursor;
    }

    public Cursor getmPDPCursor() {
        return mPDPCursor;
    }

    public Cursor getmPDRCursor() {
        return mPDRCursor;
    }
}
