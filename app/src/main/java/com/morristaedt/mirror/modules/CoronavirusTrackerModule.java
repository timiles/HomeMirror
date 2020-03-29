package com.morristaedt.mirror.modules;

import android.os.AsyncTask;
import android.util.Log;

import com.morristaedt.mirror.requests.CoronavirusTrackerRequest;
import com.morristaedt.mirror.requests.CoronavirusTrackerResponse;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class CoronavirusTrackerModule {

    public interface CoronavirusTrackerListener {
        void onUpdate(String summary);
    }

    public static void getUpdate(final CoronavirusTrackerListener listener) {
        new AsyncTask<Void, Void, CoronavirusTrackerResponse>() {

            @Override
            protected CoronavirusTrackerResponse doInBackground(Void... params) {
                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint("https://services1.arcgis.com")
                        .build();

                CoronavirusTrackerRequest service = restAdapter.create(CoronavirusTrackerRequest.class);

                try {
                    return service.getData();
                } catch (RetrofitError error) {
                    Log.w("CoronavirusTracker", "Error: " + error.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(CoronavirusTrackerResponse response) {
                if (response != null) {
                    listener.onUpdate(response.getSummary());
                }
            }
        }.execute();
    }
}
