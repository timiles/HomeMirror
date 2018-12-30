package com.morristaedt.mirror.modules;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.morristaedt.mirror.requests.BitstampApiRequest;
import com.morristaedt.mirror.requests.BitstampTickerResponse;
import com.morristaedt.mirror.requests.CoinbaseApiRequest;
import com.morristaedt.mirror.requests.CoinbaseSpotPriceResponse;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class CryptocurrencyModule {

    public static class CryptocurrencyPrice {
        public CryptocurrencyPrice(String cryptocurrency, String fiatCurrencySymbol, float amount) {
            this.cryptocurrency = cryptocurrency;
            this.fiatCurrencySymbol = fiatCurrencySymbol;
            this.amount = amount;
        }

        public String cryptocurrency;
        public String fiatCurrencySymbol;
        public float amount;
    }

    public interface CurrentPriceListener {
        void onPriceUpdated(CryptocurrencyPrice response);
    }

    public static void getPrice(final String cryptocurrency, final CurrentPriceListener listener) {

        new AsyncTask<Void, Void, CryptocurrencyPrice>() {

            @Override
            protected CryptocurrencyPrice doInBackground(Void... params) {
                try {
                    switch (cryptocurrency) {
                        case "XRP": {
                            // Use Bitstamp for XRP
                            // (developers.ripple.com Data API requires issuer to convert to non-XRP?)
                            RestAdapter restAdapter = new RestAdapter.Builder()
                                    .setEndpoint("https://www.bitstamp.net/api/v2")
                                    .build();

                            BitstampApiRequest request = restAdapter.create(BitstampApiRequest.class);
                            BitstampTickerResponse response = request.getTicker("xrp", "usd");
                            return new CryptocurrencyPrice(cryptocurrency, "$", response.last);
                        }
                        default: {
                            // Use Coinbase for everything else
                            RestAdapter restAdapter = new RestAdapter.Builder()
                                    .setEndpoint("https://api.coinbase.com/v2")
                                    .build();

                            CoinbaseApiRequest request = restAdapter.create(CoinbaseApiRequest.class);
                            CoinbaseSpotPriceResponse response =request.getSpotPrice(cryptocurrency, "GBP");
                            return new CryptocurrencyPrice(cryptocurrency, "£", response.data.amount);
                        }
                    }
                } catch (RetrofitError error) {
                    Log.w("CryptocurrencyModule", "Error: " + error.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(@Nullable CryptocurrencyPrice response) {
                listener.onPriceUpdated(response);
            }
        }.execute();

    }
}
