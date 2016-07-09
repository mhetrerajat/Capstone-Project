package com.mhetrerajat.backpacker.Db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rajatmhetre on 25/06/16.
 */
public class PlaceReviewsModel extends RealmObject {

    @PrimaryKey
    private String author_name;

    private String place_id, author_url, text;
    private Double rating;
    private long time;

    public PlaceReviewsModel() {
    }

    public String getAuthor_name() {
        return author_name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_url() {
        return author_url;
    }

    public void setAuthor_url(String author_url) {
        this.author_url = author_url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
