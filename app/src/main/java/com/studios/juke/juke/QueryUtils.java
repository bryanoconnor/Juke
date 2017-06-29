package com.studios.juke.juke;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack on 6/29/2017.
 */

public class QueryUtils {
    public static final String LOG_TAG = "QUERYUTILS";

    private QueryUtils(){

    }
    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url, String accessToken) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            Log.i(LOG_TAG, Integer.toString(urlConnection.getResponseCode()));
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the song JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }


    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
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

    /**
     * Return a list of {@link Song} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Song> extractFeatureFromJson(String songsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(songsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding songs to
        List<Song> songs = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject root = new JSONObject(songsJSON);
            JSONObject tracks = root.getJSONObject("tracks");
            JSONArray items = tracks.getJSONArray("items");

            // For each song in the items, create an {@link Song} object
            for (int i = 0; i < items.length(); i++) {

                JSONObject track = items.getJSONObject(i);
                String song_name = track.getString("name");

                JSONArray artists = track.getJSONArray("artists");
                JSONObject main_artist = artists.getJSONObject(0);
                String artist = main_artist.getString("name");

                String uri = track.getString("uri");

                JSONObject album = track.getJSONObject("album");
                JSONArray images = album.getJSONArray("images");
                String imageUrl = images.getJSONObject(0).getString("url");

                Song song = new Song(song_name, artist, uri, imageUrl);

                // Add the new {@link Song} to the list of songs.
                songs.add(song);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of songs
        return songs;
    }

    /**
     * Query the Spotify dataset and return a list of {@link Song} objects.
     */
    public static List<Song> fetchSongData(String requestUrl, String accessToken) {
        // Two Second Pause to Test Loading Bar
/*        try{
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }*/

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url, accessToken);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Song}s
        List<Song> songs = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Song}s
        Log.i(LOG_TAG, "fetchSongData() Called");
        return songs;
    }

}
