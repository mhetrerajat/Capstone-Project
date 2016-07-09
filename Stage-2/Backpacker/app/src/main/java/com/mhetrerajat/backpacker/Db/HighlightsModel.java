package com.mhetrerajat.backpacker.Db;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rajatmhetre on 27/06/16.
 */
public class HighlightsModel extends RealmObject {

    @PrimaryKey
    String place_id;

    @Index
    String city_place_id;

    @Index
    String type;

    String name, vicinity, photo_reference, store_type;
    Double longitude, latitude, rating;


    public HighlightsModel() {
    }

    public String getType() {
        return type;
    }

    public String getStore_type() {
        return store_type;
    }

    public void setStore_type(String store_type) {
        this.store_type = store_type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getCity_place_id() {
        return city_place_id;
    }

    public void setCity_place_id(String city_place_id) {
        this.city_place_id = city_place_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicnity) {
        this.vicinity = vicnity;
    }

    public String getPhoto_reference() {
        return photo_reference;
    }

    public void setPhoto_reference(String photo_reference) {
        this.photo_reference = photo_reference;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
