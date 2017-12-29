package com.morristaedt.mirror.modules;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.morristaedt.mirror.requests.CoinbaseApiRequest;
import com.morristaedt.mirror.requests.CoinbaseSpotPriceResponse;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class CryptocurrencyModule {

    public interface CurrentPriceListener {
        void onPriceUpdated(CoinbaseSpotPriceResponse response);
    }

    public static void getPrice(final String cryptocurrency, final CurrentPriceListener listener) {

        new AsyncTask<Void, Void, CoinbaseSpotPriceResponse>() {

            @Override
            protected CoinbaseSpotPriceResponse doInBackground(Void... params) {
                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint("https://api.coinbase.com/v2")
                        .build();

                CoinbaseApiRequest request = restAdapter.create(CoinbaseApiRequest.class);

                try {
                    return request.getSpotPrice(cryptocurrency, "GBP");
                } catch (RetrofitError error) {
                    Log.w("CryptocurrencyModule", "Error: " + error.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(@Nullable CoinbaseSpotPriceResponse response) {
                listener.onPriceUpdated(response);
            }
        }.execute();

    }
}
