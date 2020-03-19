package com.morristaedt.mirror.modules;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class OddsModule {
    public interface OddsListener {
        void onNewOdds(String odds);
    }

    public static void getOdds(final OddsListener oddsListener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                oddsListener.onNewOdds(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                try {
                    StringBuilder sb = new StringBuilder();

                    Document politicsDoc = Jsoup.connect("https://m.skybet.com/politics").get();
                    Elements groups = politicsDoc.select("ul.table-group li");

                    for (Element group : groups) {
                        Elements eventLinks = group.select("table.market-table a");

                        // Take top 3 events
                        for (int eventIndex = 0; eventIndex < Math.min(3, eventLinks.size()); eventIndex++) {
                            Element eventLink = eventLinks.get(eventIndex);

                            Document eventDoc = Jsoup.connect(eventLink.attr("abs:href")).get();
                            String title = eventDoc.select("h1.event-title__title").text().toUpperCase();
                            Elements sections = eventDoc.select("li.js-market");

                            // Take top 3 sections
                            int sectionCount = 0;
                            for (Element section : sections) {
                                if (sectionCount >= Math.min(3, sections.size())) {
                                    break;
                                }

                                String splitTitle = section.select(".split__title").text();
                                if (splitTitle.equals("Price Boosts")) {
                                    continue;
                                }

                                Elements rows = section.select("table.market-table tbody tr");

                                sb.append(title);
                                sb.append(": ");
                                if (!splitTitle.equalsIgnoreCase(title)) {
                                    sb.append(splitTitle);
                                    sb.append(": ");
                                }

                                // Top 3 rows
                                for (int rowIndex = 0; rowIndex < Math.min(3, rows.size()); rowIndex++) {
                                    Element row = rows.get(rowIndex);
                                    sb.append(row.child(0).text());
                                    sb.append(" (");

                                    String odds = row.child(1).child(0).text();
                                    sb.append(odds);
                                    try {
                                        String[] parts = odds.split("/");
                                        int a = Integer.parseInt(parts[0]);
                                        int b = Integer.parseInt(parts[1]);
                                        int profit = 100*a/b;
                                        sb.append(" = £");
                                        sb.append(profit);
                                    }
                                    catch (Exception e) {
                                        Log.e("OddsModule", "Error calculating profit.", e);
                                    }

                                    sb.append(") ");
                                }
                                sb.append("        ");
                                sectionCount++;
                            }
                        }
                    }
                    String oddsText = sb.toString();
                    int maxLength = 1000;
                    if (oddsText.length() > maxLength) {
                        oddsText = oddsText.substring(0, maxLength - 1) + "…";
                    }
                    return oddsText;
                } catch (IOException e) {
                    Log.e("OddsModule", "Error parsing doc.", e);
                    return null;
                } catch (Exception e) {
                    Log.e("OddsModule", "Error.", e);
                    return null;
                }
            }
        }.execute();
    }
}
