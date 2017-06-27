package com.studios.juke.juke;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class SpotifySearchActivity extends MenuBarOptions implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback {

    private EditText mEditSong;
    private Button mSearchButton;
    private Toolbar mToolbar;

    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "256fa987714a455687888ed1f07c3630";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "juke-login://callback";
    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;

    //dont leave this static
    public static Player mPlayer;
    private String mAccessToken;
    private ArrayList<Song> mSongs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_search);

        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);

        mEditSong = (EditText) findViewById(R.id.edit_song);
        mSearchButton = (Button) findViewById(R.id.search_button);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searched_song = mEditSong.getText().toString();
                new GetSpotifyTracks(searched_song).execute();
            }
        });

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                mAccessToken = response.getAccessToken();
                Log.i("Access Token: ", mAccessToken);

                Config playerConfig = new Config(this, mAccessToken, CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(SpotifySearchActivity.this);
                        mPlayer.addNotificationCallback(SpotifySearchActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("SpotifySearchActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        //dispose of the Player when we are done with it (NECESSARY!)
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("SpotifySearchActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("SpotifySearchActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("SpotifySearchActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("SpotifySearchActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error error) {
        Log.d("SpotifySearchActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("SpotifySearchActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("SpotifySearchActivity", "Received connection message: " + message);
    }

    private class GetSpotifyTracks extends AsyncTask<Void, Song, Void> {

        String mSearchedSong;


        GetSpotifyTracks(String searched_song){
            mSearchedSong = searched_song;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(SpotifySearchActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            //start with empty arraylist
            mSongs.clear();
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://api.spotify.com/v1/search?q=" + mSearchedSong + "&type=track";
            String jsonStr = sh.makeServiceCall(url, mAccessToken);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject root = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONObject tracks = root.getJSONObject("tracks");
                    JSONArray items = tracks.getJSONArray("items");

                    for(int i = 0; i < items.length(); ++i) {
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
                        //only publish song if it contains the word you entered
                        if(song.getSongName().toLowerCase().contains(mSearchedSong.toLowerCase())) {
                            publishProgress(song);
                        }
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Song... songs) {
            super.onProgressUpdate(songs);
            mSongs.add(songs[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //After all songs are queried.. THEN start intent!
            Intent intent = SongListActivity.newIntent(SpotifySearchActivity.this, mSongs);
            startActivity(intent);
        }
    }
}