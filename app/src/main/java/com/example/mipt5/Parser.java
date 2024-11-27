package com.example.mipt5;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    private static final String TAG = "Parser";

    public static List<String> parseXML(InputStream inputStream) {
        List<String> exchangeRates = new ArrayList<>();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            String currency, rate; // Removed redundant initializations
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = parser.getName();
                    if ("Cube".equals(tagName) && parser.getAttributeCount() == 2) {
                        currency = parser.getAttributeValue(null, "currency");
                        rate = parser.getAttributeValue(null, "rate");
                        exchangeRates.add(currency + " - " + rate);
                    }
                }
                eventType = parser.next();
            }
            Log.d(TAG, "Parsed data: " + exchangeRates);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing XML", e);
        }
        return exchangeRates;
    }
}
