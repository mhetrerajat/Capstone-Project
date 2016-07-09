package com.mhetrerajat.backpacker.Db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rajatmhetre on 25/06/16.
 */
public class PlacePhotoModel extends RealmObject {

    @PrimaryKey
    private String photo_reference;

    private String place_id;
    private Integer height, width;

    public PlacePhotoModel() {
    }

    public String getPhoto_reference() {
        return photo_reference;
    }

    public void setPhoto_reference(String photo_reference) {
        this.photo_reference = photo_reference;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getwidth() {
        return width;
    }

    public void setwidth(Integer width) {
        this.width = width;
    }
}
