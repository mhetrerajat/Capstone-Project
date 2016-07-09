package com.mhetrerajat.backpacker.Service;

import com.mhetrerajat.backpacker.Models.City.City;

import java.util.Map;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * Created by rajatmhetre on 24/06/16.
 */
public interface CityService {

    @GET("textsearch/json?type=locality")
    Call<City> getCitiesByQuery(@QueryMap Map<String, String> params);
}
