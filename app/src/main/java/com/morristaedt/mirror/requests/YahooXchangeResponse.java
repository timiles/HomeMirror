package com.morristaedt.mirror.requests;

/**
 * Created by timiles on 13/07/2016.
 */
public class YahooXchangeResponse {

    private YahooQueryResponse query;

    private class YahooQueryResponse {
        public YahooResultsResponse results;
    }

    private class YahooResultsResponse {
        // if querying more than one currency pair, this renders as an array
        public YahooRateResponse rate;
    }

    public class YahooRateResponse {
        public String Rate;
    }

    public String getRate() {
        return this.query.results.rate.Rate;
    }
}
