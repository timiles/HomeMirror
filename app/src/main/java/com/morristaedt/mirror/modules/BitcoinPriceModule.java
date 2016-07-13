package com.morristaedt.mirror.modules;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.morristaedt.mirror.requests.CoinDeskApiRequest;
import com.morristaedt.mirror.requests.CoinDeskCurrentPriceResponse;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by timiles on 10/07/2016.
 */
public class BitcoinPriceModule {

    public interface CurrentPriceListener {
        void onBitcoinPriceUpdated(CoinDeskCurrentPriceResponse response);
    }

    public static void getBitcoinPrice(final CurrentPriceListener listener) {

        new AsyncTask<Void, Void, CoinDeskCurrentPriceResponse>() {

            @Override
            protected CoinDeskCurrentPriceResponse doInBackground(Void... params) {
                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint("https://api.coindesk.com/v1")
                        .build();

                CoinDeskApiRequest request = restAdapter.create(CoinDeskApiRequest.class);

                try {
                    return request.getCurrentPrice();
                } catch (RetrofitError error) {
                    Log.w("BitcoinPriceModule", "CoinDesk error: " + error.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(@Nullable CoinDeskCurrentPriceResponse response) {
                listener.onBitcoinPriceUpdated(response);
            }
        }.execute();

    }
}
