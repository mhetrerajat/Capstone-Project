package com.mhetrerajat.backpacker.Db;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rajatmhetre on 25/06/16.
 */
public class PlaceDetailsModel extends RealmObject {

    @PrimaryKey
    private String place_id;

    private String city_place_id, name, formatted_address, formatted_phone_number, international_phone_number, viciniy, website;
    private Double latitude, longitude;
    private Double rating;
    private RealmList<PlacePhotoModel> mPlacePhotosList = new RealmList<PlacePhotoModel>();
    private RealmList<PlaceReviewsModel> mPlaceReviewsList = new RealmList<PlaceReviewsModel>();


    public PlaceDetailsModel() {
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
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

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getFormatted_phone_number() {
        return formatted_phone_number;
    }

    public void setFormatted_phone_number(String formatted_phone_number) {
        this.formatted_phone_number = formatted_phone_number;
    }

    public String getInternational_phone_number() {
        return international_phone_number;
    }

    public void setInternational_phone_number(String international_phone_number) {
        this.international_phone_number = international_phone_number;
    }

    public String getViciniy() {
        return viciniy;
    }

    public void setViciniy(String viciniy) {
        this.viciniy = viciniy;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public RealmList<PlacePhotoModel> getmPlacePhotosList() {
        return mPlacePhotosList;
    }

    public void setmPlacePhotosList(RealmList<PlacePhotoModel> mPlacePhotosList) {
        this.mPlacePhotosList = mPlacePhotosList;
    }

    public RealmList<PlaceReviewsModel> getmPlaceReviewsList() {
        return mPlaceReviewsList;
    }

    public void setmPlaceReviewsList(RealmList<PlaceReviewsModel> mPlaceReviewsList) {
        this.mPlaceReviewsList = mPlaceReviewsList;
    }
}
