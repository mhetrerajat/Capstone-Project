package com.mhetrerajat.backpacker.Service;

import com.mhetrerajat.backpacker.Models.Highlight.Highlight;
import com.mhetrerajat.backpacker.Models.Place.Place;

import java.util.Map;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * Created by rajatmhetre on 25/06/16.
 */
public interface PlaceService {


    @GET("details/json?")
    Call<Place> getPlaceDetails(@QueryMap Map<String, String> params);

    @GET("nearbysearch/json?radius=1000")
    Call<Highlight> getNearByHighlights(@QueryMap Map<String, String> params);
}
