package com.morristaedt.mirror.requests;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CoronavirusTrackerResponse {

    public Overview overview;

    public class Overview {
        public Region K02000001;
    }

    public class Region {
        public Value totalCases;
        public Value newCases;
        public Value deaths;
        public DailyData[] dailyDeaths;
    }

    public class Value {
        public int value;
    }

    public class DailyData {
        public String date;
        public int value;
    }

    public String getSummary() {
        try {
            if (overview == null) {
                return null;
            }

            // K02000001 = UK data
            Region data = overview.K02000001;
            DailyData dailyDeaths = data.dailyDeaths[data.dailyDeaths.length - 1];

            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dailyDeaths.date);
            DateFormat dayAndMonth = new SimpleDateFormat("MMM dd");

            return String.format("\uD83D\uDE37 %s: %s \uD83E\uDD12 (+%s%%), %s \uD83D\uDC7C (+%s%%)",
                    dayAndMonth.format(date),
                    data.totalCases.value,
                    (int) Math.floor(100.0 * data.newCases.value / (data.totalCases.value - data.newCases.value)),
                    data.deaths.value,
                    (int) Math.floor(100.0 * dailyDeaths.value / (data.deaths.value - dailyDeaths.value)));
        }
        catch (Exception error) {
            Log.w("CoronavirusTracker", "Error: " + error.getMessage());
            return null;
        }
    }
}
