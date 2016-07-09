package com.mhetrerajat.backpacker.Events;

import android.database.Cursor;
import android.database.MatrixCursor;

import com.mhetrerajat.backpacker.Db.CityModel;
import com.mhetrerajat.backpacker.Models.City.Result;

import java.util.List;

/**
 * Created by rajatmhetre on 24/06/16.
 */
public class CitySelectSuccessEvent {

    public Cursor matrixCursor;


    public CitySelectSuccessEvent(Cursor matrixCursor) {
        this.matrixCursor = matrixCursor;
    }

    public Cursor getMatrixCursor() {
        return matrixCursor;
    }
}
