package com.morristaedt.mirror.requests;

import retrofit.http.GET;
import retrofit.http.Query;

public interface CurrencyConverterApiRequest {

    @GET("/convert")
    CurrencyConverterConvertResponse convert(@Query("q") String currencyPair);
}
