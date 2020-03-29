package com.morristaedt.mirror.requests;

import retrofit.http.GET;

public interface CoronavirusTrackerRequest {

    @GET("/0IrmI40n5ZYxTUrV/arcgis/rest/services/DailyIndicators/FeatureServer/0/query?f=json&where=1%3D1&returnGeometry=false&spatialRel=esriSpatialRelIntersects&outFields=*")
    CoronavirusTrackerResponse getData();
}
