package com.morristaedt.mirror.requests;

public class CoinbaseSpotPriceResponse {

    public Data data;

    public class Data {
        public String base;
        public String currency;
        public float amount;
    }
}
