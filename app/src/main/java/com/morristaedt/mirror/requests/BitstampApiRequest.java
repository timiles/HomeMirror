package com.morristaedt.mirror.requests;

import retrofit.http.GET;
import retrofit.http.Path;

public interface BitstampApiRequest {

    @GET("/ticker/{base}{currency}")
    BitstampTickerResponse getTicker(
            @Path("base") String base,
            @Path("currency") String currency);
}
