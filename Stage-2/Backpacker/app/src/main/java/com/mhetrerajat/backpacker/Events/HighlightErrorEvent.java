package com.mhetrerajat.backpacker.Events;

/**
 * Created by rajatmhetre on 27/06/16.
 */
public class HighlightErrorEvent {

    String message;

    public HighlightErrorEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
