package com.morristaedt.mirror.modules;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.morristaedt.mirror.requests.CurrencyConverterApiRequest;
import com.morristaedt.mirror.requests.CurrencyConverterConvertResponse;

import retrofit.RestAdapter;

/**
 * Created by timiles on 13/07/2016.
 */
public class ExchangeRateModule {

    public interface ExchangeRateListener {
        void onNewExchangeRate(Float rate);
    }

    /**
     * Fetch the latest currency exchange rate
     *
     * @param listener
     */
    public static void getExchangeRate(final String fromCurrency, final String toCurrency, final ExchangeRateListener listener) {

        new AsyncTask<Void, Void, Float>() {

            @Override
            protected Float doInBackground(Void... params) {
                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint("https://free.currencyconverterapi.com/api/v5")
                        .build();

                CurrencyConverterApiRequest service = restAdapter.create(CurrencyConverterApiRequest.class);

                try {
                    CurrencyConverterConvertResponse response = service.convert(
                            String.format("%s_%s", fromCurrency, toCurrency));
                    return new Float(response.getValue());
                } catch (Exception error) {
                    Log.w("ExchangeRateModule", "ExchangeRateModule error: " + error.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(@Nullable Float rate) {
                listener.onNewExchangeRate(rate);
            }
        }.execute();
    }
}
