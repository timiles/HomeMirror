package com.morristaedt.mirror.requests;

import com.morristaedt.mirror.utils.TemporalMeanCalculator;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by HannahMitt on 8/23/15.
 */
public class ForecastResponse {

    float latitude;
    float longitude;

    public ForecastCurrently currently;
    public ForecastHourly hourly;

    public String getNextDaytimeSummary() {
        if (hourly == null) {
            return null;
        }

        return hourly.summary;
    }

    public String getNextDaytimeData() {
        if (hourly == null || hourly.data == null) {
            return null;
        }

        int day = -1;
        float minTemp = 999, maxTemp = -999;
        boolean mightRain = false;

        TemporalMeanCalculator temporalMeanCalculator = new TemporalMeanCalculator();

        for (ForecastResponse.Hour hour : hourly.data) {
            Calendar hourCalendar = hour.getCalendar();

            // if we haven't started counting yet, or if it's the day we're counting:
            if (day == -1 || day == hourCalendar.get(Calendar.DAY_OF_WEEK)) {
                int hourOfDay = hourCalendar.get(Calendar.HOUR_OF_DAY);
                if (hourOfDay >= 8 && hourOfDay <= 19) {
                    day = hourCalendar.get(Calendar.DAY_OF_WEEK);

                    if (hourOfDay >= 17 && hour.precipProbability >= 0.3) {
                        mightRain = true;
                    }
                    if (hour.temperature < minTemp) {
                        minTemp = hour.temperature;
                    }
                    if (hour.temperature > maxTemp) {
                        maxTemp = hour.temperature;
                    }
                    temporalMeanCalculator.addDataPoint(hour.time, hour.temperature);
                }
            }
        }

        String[] dayNames = new DateFormatSymbols().getShortWeekdays();
        Float meanTemp = temporalMeanCalculator.calculateMean();

        return String.format("%s: %s-%s%s°%s",
                dayNames[day],
                Math.round(minTemp),
                meanTemp != null ? Math.round(meanTemp) + "-" : "",
                Math.round(maxTemp),
                mightRain ? " ☔" : "");
    }

    public class ForecastCurrently {
        public String summary;
        public float temperature;

        public String getDisplayTemperature() {
            return String.valueOf(Math.round(temperature)) + (char) 0x00B0;
        }
    }

    public class ForecastHourly {
        public String summary;
        public ArrayList<Hour> data;
    }

    public class Hour {
        public long time; // in seconds
        public String summary;
        public String precipType;
        public float precipProbability;
        public float temperature;

        public Calendar getCalendar() {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time * 1000);
            return calendar;
        }
    }
}
