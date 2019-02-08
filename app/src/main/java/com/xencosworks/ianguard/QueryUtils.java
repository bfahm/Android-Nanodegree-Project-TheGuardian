package com.xencosworks.ianguard;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Bola on 2/7/2019.
 */

public final class QueryUtils {

    private static final String TAG = "QueryUtils";

    private QueryUtils() {
    }

    public static ArrayList<Article> extractArticles(String incomingUrl) {

        try {
            // Simulate taking long time to load.
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<Article> articles = new ArrayList<>();

        String jsonResponse;
        try {

            try {
                 jsonResponse = makeHttpRequest(createUrl(incomingUrl));
            } catch (IOException e) {
                // if an error occurred while trying to connect, return without attempting to
                // fill the ArrayList.
                e.printStackTrace();
                return null;
            }

            JSONObject baseJsonResponse = new JSONObject(jsonResponse);

            JSONObject serverResponse = baseJsonResponse.getJSONObject("response");
            JSONArray articleArray = serverResponse.getJSONArray("results");

            for (int i = 0; i < articleArray.length(); i++) {

                JSONObject currentArticle = articleArray.getJSONObject(i);

                String title = currentArticle.getString("webTitle");
                String sectionName = currentArticle.getString("sectionName");
                String publicationDate = currentArticle.getString("webPublicationDate");
                String webUrl = currentArticle.getString("webUrl");
                String author;

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                Date date = dateFormat.parse(publicationDate);


                JSONArray tags = currentArticle.getJSONArray("tags");
                if(tags!=null&&tags.length()>0){
                    author = tags.getJSONObject(0).getString("webTitle");
                }else {
                    author = "Unknown Author";
                }

                articles.add(new Article(title, sectionName, date, author, webUrl));
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the article JSON results", e);
        } catch (ParseException e) {
            Log.e("QueryUtils", "Problem parsing the Date", e);
        }
        Log.v("QueryUtils", ""+articles.size());
        return articles;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

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

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "Problem retrieving the Articles JSON results.", e);
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
}
