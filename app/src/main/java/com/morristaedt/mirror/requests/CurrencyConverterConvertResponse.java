package com.morristaedt.mirror.requests;

import java.util.Map;

public class CurrencyConverterConvertResponse {

    // e.g. {"query":{"count":1},"results":{"GBP_EUR":{"id":"GBP_EUR","val":1.110992,"to":"EUR","fr":"GBP"}}}

    public Map<String, Result> results;

    class Result {
        public float val;
        public String to;
        public String fr;
    }

    public float getValue() {
        // Assume the first value is what we want.
        return  this.results.entrySet().iterator().next().getValue().val;
    }
}
