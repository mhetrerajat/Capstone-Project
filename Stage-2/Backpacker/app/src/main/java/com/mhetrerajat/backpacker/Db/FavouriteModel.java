package com.mhetrerajat.backpacker.Db;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rajatmhetre on 27/06/16.
 */
public class FavouriteModel extends RealmObject{

    @PrimaryKey
    String place_id;

    @Index
    String city_place_id;

    String name, vicinity, photo_reference;

    public FavouriteModel() {
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

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
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

    public String getCity_place_id() {
        return city_place_id;
    }

    public void setCity_place_id(String city_place_id) {
        this.city_place_id = city_place_id;
    }
}
