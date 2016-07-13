package com.morristaedt.mirror.utils;

import android.support.v4.util.LongSparseArray;

/**
 * Created by timiles on 12/07/2016.
 */
public class TemporalMeanCalculator {

    private LongSparseArray<Float> dataPoints = new LongSparseArray<>();

    public void addDataPoint(long time, float value) {
        // use append, optimizing for assumption that data is in order
        this.dataPoints.append(time, value);
    }

    public Float calculateMean() {
        int numberOfDataPoints = this.dataPoints.size();
        if (numberOfDataPoints == 0) {
            return null;
        }
        if (numberOfDataPoints == 1) {
            return this.dataPoints.valueAt(0);
        }

        long firstTime = this.dataPoints.keyAt(0);
        long previousTime = firstTime;
        float previousValue = this.dataPoints.valueAt(0);
        float total = 0;

        for (int i = 0; i < numberOfDataPoints; i++) {
            long time = this.dataPoints.keyAt(i);
            float value = this.dataPoints.valueAt(i);

            float averageValue = (previousValue + value) / 2;
            total += averageValue * (time - previousTime);

            previousTime = time;
            previousValue = value;
        }

        // use previousTime as last time value
        return total / (previousTime - firstTime);
    }
}
