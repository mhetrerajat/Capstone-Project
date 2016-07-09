package com.mhetrerajat.backpacker.Events;

/**
 * Created by rajatmhetre on 25/06/16.
 */
public class PlaceDetailsErrorEvent {

    private String message;

    public PlaceDetailsErrorEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
