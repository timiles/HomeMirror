package com.morristaedt.mirror.requests;

import retrofit.http.GET;
import retrofit.http.Path;

public interface CoinbaseApiRequest {

    @GET("/prices/{base}-{currency}/spot")
    CoinbaseSpotPriceResponse getSpotPrice(
            @Path("base") String base,
            @Path("currency") String currency);
}
