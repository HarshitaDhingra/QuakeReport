package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.android.quakereport.EarthquakeActivity.LOG_TAG;

public final class QueryUtils {
    private QueryUtils() {}
    /** Sample JSON response for a USGS query */
    public static final List<Earthquake> earthquakes = new ArrayList<>();

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<Earthquake> fetchEarthquakeData(String requestUrl) {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(LOG_TAG,"fetchData");
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<Earthquake> earthquakes=extractFeatureFromJson(jsonResponse);
        // Return the {@link Event}
       // return result;
    return earthquakes;
    }
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static List<Earthquake> extractFeatureFromJson(String earthquakeJSON) {
        String []s=new String[2];
        Double magnitude=0.0;
        String dateToDisplay="";
        String timeToDisplay="";
        String u="";

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }
       try {
            JSONObject root=new JSONObject(earthquakeJSON);
            JSONArray features=root.getJSONArray("features");
            for(int i=0;i<features.length();i++)
            {
                JSONObject feat = features.getJSONObject(i);
                JSONObject prop = feat.getJSONObject("properties");
                String place = prop.getString("place");
                magnitude = prop.getDouble("mag");
                long time = prop.getLong("time");
                u = prop.getString("url");

                DecimalFormat formatter = new DecimalFormat("0.00");
                String output = formatter.format(magnitude);

                if (place.contains("of")) {
                    int l = place.indexOf("of");
                    s[0] = place.substring(0, l + 2);
                    s[1] = place.substring(l + 2);
                } else {
                    s[0] = "Near the";
                    s[1] = place;
                }

                long timeInMilliseconds = time;
                Date dateObject = new Date(timeInMilliseconds);
                SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM DD, yyyy");
                dateToDisplay = dateFormatter.format(dateObject);

                Date timeObject = new Date(time);
                SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
                timeToDisplay = timeFormatter.format(timeObject);
                earthquakes.add(new Earthquake(magnitude,s[0],s[1],dateToDisplay,timeToDisplay,u));
            }
            }
        catch (JSONException e) {
               Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        return earthquakes;
        // Return the list of earthquakes
    }
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
