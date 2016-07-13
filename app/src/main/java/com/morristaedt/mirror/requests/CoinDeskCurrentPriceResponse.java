package com.morristaedt.mirror.requests;

/**
 * Created by timiles on 10/07/2016.
 */
public class CoinDeskCurrentPriceResponse {

    public BitcoinPriceIndex bpi;

    public class BitcoinPriceIndex {

        public Currency USD;
        public Currency GBP;

        public class Currency {
            public float rate_float;
        }
    }

    public float getUsdRate() {
        return this.bpi.USD.rate_float;
    }

    public float getGbpRate() {
        return this.bpi.GBP.rate_float;
    }
}
