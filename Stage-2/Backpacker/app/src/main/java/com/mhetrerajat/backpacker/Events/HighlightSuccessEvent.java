package com.mhetrerajat.backpacker.Events;

import android.database.Cursor;
import android.database.MatrixCursor;

import com.mhetrerajat.backpacker.Db.HighlightsModel;

import java.util.List;

/**
 * Created by rajatmhetre on 27/06/16.
 */
public class HighlightSuccessEvent {

    Cursor matrixCursor;
    String NEXT_PAGE_TOKEN;

    public HighlightSuccessEvent(Cursor matrixCursor, String NEXT_PAGE_TOKEN) {
        this.matrixCursor = matrixCursor;
        this.NEXT_PAGE_TOKEN = NEXT_PAGE_TOKEN;
    }

    public Cursor getMatrixCursor() {
        return matrixCursor;
    }

    public String getNEXT_PAGE_TOKEN() {
        return NEXT_PAGE_TOKEN;
    }
}
