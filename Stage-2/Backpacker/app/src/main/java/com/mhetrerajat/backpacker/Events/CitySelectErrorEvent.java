package com.mhetrerajat.backpacker.Events;

/**
 * Created by rajatmhetre on 24/06/16.
 */
public class CitySelectErrorEvent {

    String message;

    public CitySelectErrorEvent(String message) {
        this.message = message;
    }


    public String getMessage() {
        return message;
    }
}
