package com.morristaedt.mirror.requests;

import retrofit.http.GET;

/**
 * Created by timiles on 10/07/2016.
 */
public interface CoinDeskApiRequest {

    @GET("/bpi/currentprice.json")
    CoinDeskCurrentPriceResponse getCurrentPrice();
}
