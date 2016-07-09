package com.mhetrerajat.backpacker.Events;

/**
 * Created by rajatmhetre on 27/06/16.
 */
public class HighlightTypeEvent {

    String TYPE;

    public HighlightTypeEvent(String TYPE) {
        this.TYPE = TYPE;
    }


    public String getTYPE() {
        return TYPE;
    }
}
