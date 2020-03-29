package com.morristaedt.mirror.requests;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CoronavirusTrackerResponse {

    public Feature[] features;

    public class Feature {
        public FeatureAttributes attributes;
    }

    public class FeatureAttributes {
        public long DateVal;
        public int TotalUKCases;
        public int NewUKCases;
        public int TotalUKDeaths;
        public int DailyUKDeaths;
    }

    public String getSummary() {
        if (features == null || features.length < 1) {
            return null;
        }

        FeatureAttributes data = features[0].attributes;
        Date date = new Date(data.DateVal);
        DateFormat dayAndMonth = new SimpleDateFormat("MMM dd");

        return String.format("\uD83D\uDE37 %s: %s \uD83E\uDD12 (+%s%%), %s \uD83D\uDC7C (+%s%%)",
                dayAndMonth.format(date),
                data.TotalUKCases,
                (int)Math.floor(100.0 * data.NewUKCases / (data.TotalUKCases - data.NewUKCases)),
                data.TotalUKDeaths,
                (int)Math.floor(100.0 * data.DailyUKDeaths / (data.TotalUKDeaths - data.DailyUKDeaths)));
    }
}
