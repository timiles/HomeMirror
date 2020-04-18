package com.morristaedt.mirror.requests;

import retrofit.http.GET;
import retrofit.http.Path;

public interface CoronavirusTrackerRequest {

    @GET("/{fileName}")
    CoronavirusTrackerResponse getData(@Path("fileName") String fileName);
}
