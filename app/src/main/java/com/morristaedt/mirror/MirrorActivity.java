package com.morristaedt.mirror;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.morristaedt.mirror.configuration.ConfigurationSettings;
import com.morristaedt.mirror.modules.BirthdayModule;
import com.morristaedt.mirror.modules.CryptocurrencyModule;
import com.morristaedt.mirror.modules.CalendarModule;
import com.morristaedt.mirror.modules.CountdownModule;
import com.morristaedt.mirror.modules.DayModule;
import com.morristaedt.mirror.modules.ExchangeRateModule;
import com.morristaedt.mirror.modules.ForecastModule;
import com.morristaedt.mirror.modules.MoodModule;
import com.morristaedt.mirror.modules.NewsModule;
import com.morristaedt.mirror.modules.YahooFinanceModule;
import com.morristaedt.mirror.receiver.AlarmReceiver;
import com.morristaedt.mirror.requests.CoinbaseSpotPriceResponse;
import com.morristaedt.mirror.requests.YahooStockResponse;
import com.morristaedt.mirror.utils.WeekUtil;
import com.morristaedt.mirror.views.ScrollTextView;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MirrorActivity extends ActionBarActivity {

    @NonNull
    private ConfigurationSettings mConfigSettings;

    private TextView mBirthdayText;
    private TextView mDayText;
    private TextView mWeatherSummary;
    private TextView mWeatherData;
    private TextView mStockText;
    private TextView mCryptocurrencyPrices;
    private TextView mExchangeRate;
    private TextView mMoodText;
    private MoodModule mMoodModule;
    private ScrollTextView mNewsHeadline;
    private TextView mCalendarTitleText;
    private TextView mCalendarDetailsText;
    private TextView mCountdownText;

    private YahooFinanceModule.StockListener mStockListener = new YahooFinanceModule.StockListener() {
        @Override
        public void onNewStockPrice(YahooStockResponse.YahooQuoteResponse quoteResponse) {
            if (quoteResponse == null) {
                mStockText.setVisibility(View.GONE);
            } else {
                mStockText.setVisibility(View.VISIBLE);
                mStockText.setText("$" + quoteResponse.symbol + " $" + quoteResponse.LastTradePriceOnly);
            }
        }
    };

    private static Map<String, Integer> cryptocurrencyPrices = new LinkedHashMap<>();

    private CryptocurrencyModule.CurrentPriceListener mCryptocurrencyPriceListener = new CryptocurrencyModule.CurrentPriceListener() {
        @Override
        public void onPriceUpdated(CoinbaseSpotPriceResponse response) {
            if (response != null) {
                cryptocurrencyPrices.put(response.data.base, Math.round(response.data.amount));
                mCryptocurrencyPrices.setVisibility(View.VISIBLE);
                List<String> priceStrings = new ArrayList<>();
                for (String key : cryptocurrencyPrices.keySet()) {
                    priceStrings.add(String.format("%s:\u00A0£%s", key, cryptocurrencyPrices.get(key)));
                }
                mCryptocurrencyPrices.setText(TextUtils.join(", ", priceStrings));
            }
        }
    };

    private ExchangeRateModule.ExchangeRateListener mExchangeRateListener = new ExchangeRateModule.ExchangeRateListener() {
        @Override
        public void onNewExchangeRate(Float rate) {
            if (rate == null) {
                mExchangeRate.setVisibility(View.GONE);
            } else {
                mExchangeRate.setVisibility(View.VISIBLE);
                String exchangeRateString = String.format("%s: %s %s",
                        mConfigSettings.getFromCurrency(),
                        rate,
                        mConfigSettings.getToCurrency());
                mExchangeRate.setText(exchangeRateString);
            }
        }
    };

    private ForecastModule.ForecastListener mForecastListener = new ForecastModule.ForecastListener() {
        @Override
        public void onWeatherToday(String summary, String data) {
            if (!TextUtils.isEmpty(summary)) {
                mWeatherSummary.setVisibility(View.VISIBLE);
                mWeatherSummary.setText(summary);
            }
            if (!TextUtils.isEmpty(data)) {
                mWeatherData.setVisibility(View.VISIBLE);
                mWeatherData.setText(data);
            }
        }
    };

    private NewsModule.NewsListener mNewsListener = new NewsModule.NewsListener() {
        @Override
        public void onNewNews(String headline) {
            if (TextUtils.isEmpty(headline)) {
                mNewsHeadline.setVisibility(View.GONE);
            } else {
                mNewsHeadline.setVisibility(View.VISIBLE);
                mNewsHeadline.setText(headline);
                mNewsHeadline.setSelected(true);
                mNewsHeadline.startScroll();
            }
        }
    };

    private MoodModule.MoodListener mMoodListener = new MoodModule.MoodListener() {
        @Override
        public void onShouldGivePositiveAffirmation(final String affirmation) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMoodText.setVisibility(affirmation == null ? View.GONE : View.VISIBLE);
                    mMoodText.setText(affirmation);
                }
            });
        }
    };

    private CalendarModule.CalendarListener mCalendarListener = new CalendarModule.CalendarListener() {
        @Override
        public void onCalendarUpdate(String title, String details) {
            mCalendarTitleText.setVisibility(title != null ? View.VISIBLE : View.GONE);
            mCalendarTitleText.setText(title);
            mCalendarDetailsText.setVisibility(details != null ? View.VISIBLE : View.GONE);
            mCalendarDetailsText.setText(details);

            //Make marquee effect work for long text
            mCalendarTitleText.setSelected(true);
            mCalendarDetailsText.setSelected(true);
        }
    };

    private CountdownModule.CountdownListener mCountdownListener = new CountdownModule.CountdownListener() {
        @Override
        public void onCountdownUpdate(final String timeLeft) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCountdownText.setVisibility(View.VISIBLE);
                    mCountdownText.setText(timeLeft);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mirror);
        mConfigSettings = new ConfigurationSettings(this);
        AlarmReceiver.startMirrorUpdates(this);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_IMMERSIVE;
            decorView.setSystemUiVisibility(uiOptions);
            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mBirthdayText = (TextView) findViewById(R.id.birthday_text);
        mDayText = (TextView) findViewById(R.id.day_text);
        mWeatherSummary = (TextView) findViewById(R.id.weather_summary);
        mWeatherData = (TextView) findViewById(R.id.weather_data);
        mStockText = (TextView) findViewById(R.id.stock_text);
        mCryptocurrencyPrices = (TextView) findViewById(R.id.cryptocurrency_prices);
        mExchangeRate = (TextView) findViewById(R.id.exchange_rate);
        mMoodText = (TextView) findViewById(R.id.mood_text);
        mNewsHeadline = (ScrollTextView) findViewById(R.id.news_headline);
        mCalendarTitleText = (TextView) findViewById(R.id.calendar_title);
        mCalendarDetailsText = (TextView) findViewById(R.id.calendar_details);
        mCountdownText = (TextView) findViewById(R.id.countdown_text);

        setViewState();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mMoodModule != null) {
            mMoodModule.release();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setViewState();
    }

    private void colorTextViews(ViewGroup mview){
        for (int i = 0; i < mview.getChildCount(); i++) {
            View view = mview.getChildAt(i);
            if (view instanceof ViewGroup)
                colorTextViews((ViewGroup) view);
            else if (view instanceof TextView) {
                ((TextView) view).setTextColor(mConfigSettings.getTextColor());
            }
        }
    }

    private void setViewState() {
        colorTextViews((ViewGroup) findViewById(R.id.main_layout));

        String birthday = BirthdayModule.getBirthday();
        if (TextUtils.isEmpty(birthday)) {
            mBirthdayText.setVisibility(View.GONE);
        } else {
            mBirthdayText.setVisibility(View.VISIBLE);
            mBirthdayText.setText(getString(R.string.happy_birthday, birthday));
        }

        mDayText.setText(DayModule.getDay());

        // Get the API key for whichever weather service API key is available
        // These should be declared as a string in xml
        int forecastApiKeyRes = getResources().getIdentifier("dark_sky_api_key", "string", getPackageName());
        int openWeatherApiKeyRes = getResources().getIdentifier("open_weather_api_key", "string", getPackageName());

        if (forecastApiKeyRes != 0) {
            ForecastModule.getForecastIOHourlyForecast(getString(forecastApiKeyRes), mConfigSettings.getForecastUnits(), mConfigSettings.getLatitude(), mConfigSettings.getLongitude(), mForecastListener);
        } else if (openWeatherApiKeyRes != 0) {
            ForecastModule.getOpenWeatherForecast(getString(openWeatherApiKeyRes), mConfigSettings.getForecastUnits(), mConfigSettings.getLatitude(), mConfigSettings.getLongitude(), mForecastListener);
        }

        if (mConfigSettings.showNewsHeadline()) {
            NewsModule.getNewsHeadline(mNewsListener);
        } else {
            mNewsHeadline.setVisibility(View.GONE);
        }

        if (mConfigSettings.showNextCalendarEvent()) {
            CalendarModule.getCalendarEvents(this, mCalendarListener);
        } else {
            mCalendarTitleText.setVisibility(View.GONE);
            mCalendarDetailsText.setVisibility(View.GONE);
        }

        if (mConfigSettings.showStock() && (ConfigurationSettings.isDemoMode() || WeekUtil.isWeekdayAfterFive())) {
            YahooFinanceModule.getStockForToday(mConfigSettings.getStockTickerSymbol(), mStockListener);
        } else {
            mStockText.setVisibility(View.GONE);
        }

        String[] cryptocurrenciesToShow = mConfigSettings.getCryptocurrenciesToShow();
        if (cryptocurrenciesToShow.length > 0) {
            for (String cryptocurrency : cryptocurrenciesToShow) {
                CryptocurrencyModule.getPrice(cryptocurrency, mCryptocurrencyPriceListener);
            }
        } else {
            mCryptocurrencyPrices.setVisibility(View.GONE);
        }

        if (mConfigSettings.showExchangeRate()) {
            ExchangeRateModule.getExchangeRate(
                    mConfigSettings.getFromCurrency(),
                    mConfigSettings.getToCurrency(),
                    mExchangeRateListener);
        } else {
            mExchangeRate.setVisibility(View.GONE);
        }

        if (mConfigSettings.showMoodDetection()) {
            mMoodModule = new MoodModule(new WeakReference<Context>(this));
            mMoodModule.getCurrentMood(mMoodListener);
        } else {
            mMoodText.setVisibility(View.GONE);
        }

        if (mConfigSettings.showCountdown()){
            CountdownModule.getTimeRemaining(mConfigSettings.getCountdownEnd(), mCountdownListener);
        } else {
            mCountdownText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlarmReceiver.stopMirrorUpdates(this);
        Intent intent = new Intent(this, SetUpActivity.class);
        startActivity(intent);
    }
}
