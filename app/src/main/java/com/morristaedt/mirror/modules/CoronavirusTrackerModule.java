package com.morristaedt.mirror.modules;

import android.os.AsyncTask;
import android.util.Log;

import com.morristaedt.mirror.requests.CoronavirusTrackerRequest;
import com.morristaedt.mirror.requests.CoronavirusTrackerResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;

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
                try {
                    Document xmlDoc = Jsoup.connect("https://publicdashacc.blob.core.windows.net/publicdata?restype=container&comp=list").get();
                    Elements dataFiles = xmlDoc.select("Blob Name:containsOwn(data_)");
                    String lastDataFile = ((TextNode)dataFiles.last().childNode(0)).text();

                    RestAdapter restAdapter = new RestAdapter.Builder()
                            .setEndpoint("https://c19pub.azureedge.net")
                            .build();

                    CoronavirusTrackerRequest service = restAdapter.create(CoronavirusTrackerRequest.class);
                    return service.getData(lastDataFile);
                } catch (IOException error) {
                    Log.w("CoronavirusTracker", "Error: " + error.getMessage());
                    return null;
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
