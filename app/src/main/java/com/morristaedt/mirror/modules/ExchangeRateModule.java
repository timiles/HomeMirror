package com.morristaedt.mirror.modules;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.morristaedt.mirror.requests.YahooFinanceRequest;
import com.morristaedt.mirror.requests.YahooXchangeResponse;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

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
                        .setEndpoint("http://query.yahooapis.com/v1/public")
                        .build();

                YahooFinanceRequest service = restAdapter.create(YahooFinanceRequest.class);

                String currencyPair = fromCurrency + toCurrency;
                String query = "select * from yahoo.finance.xchange where pair in (\"" + currencyPair + "\")";
                String env = "store://datatables.org/alltableswithkeys";
                String format = "json";
                try {
                    YahooXchangeResponse response = service.getXchangeData(query, env, format);
                    return new Float(response.getRate());
                } catch (Exception error) {
                    Log.w("ExchangeRateModule", "YahooFinance error: " + error.getMessage());
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
