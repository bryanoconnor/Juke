package com.studios.juke.juke;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SpotifySearchActivity extends MenuBarOptions implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback, LoaderCallbacks<List<Song>> {

    private EditText mEditSong;
    private Toolbar mToolbar;
    public static final String LOG_TAG = "TEST_LOGS";
    private static final int SONG_LOADER_ID = 1;
    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "256fa987714a455687888ed1f07c3630";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "juke-login://callback";
    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;
    public static boolean isLoaded;
    private static boolean runOnce = true;
    //dont leave this static
    public static Player mPlayer;
    private String mAccessToken;
    private String mSearchedSong;
    private String mRefreshToken;
    private ArrayList<Song> mSongs = new ArrayList<>();
    @BindView(R.id.search_button)
    Button mSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_search);
        ButterKnife.bind(this);
        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);

        mEditSong = (EditText) findViewById(R.id.edit_song);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        Log.i("Access Token: ", "BREAK------------------------------------------");

        //mSearchButton = (Button) findViewById(R.id.search_button);


/*        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchedSong = mEditSong.getText().toString();
                //String searched_song = mEditSong.getText().toString();
                //new GetSpotifyTracks(searched_song).execute();
                // Check for network connectivity
                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                if(isConnected) {
                    // Get instance of LoaderManager and start the loader
                    Log.i(LOG_TAG, "initLoader() Called");
                    getLoaderManager().initLoader(SONG_LOADER_ID, null, this);
                }else{
                    //progressBar.setVisibility(View.GONE);
                    //emptyView.setText("No Internet Connection");
                }
            }
        });*/



    }

    @OnClick(R.id.search_button)
    public void search() {
        isLoaded = true;
        mSearchedSong = mEditSong.getText().toString();
        //String searched_song = mEditSong.getText().toString();
        //new GetSpotifyTracks(searched_song).execute();
        // Check for network connectivity
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(isConnected) {
            // Get instance of LoaderManager and start the loader
            Log.i(LOG_TAG, "initLoader() Called");
            if(runOnce){
                getLoaderManager().initLoader(SONG_LOADER_ID, null, this);
                runOnce = false;
            }else{
                getLoaderManager().restartLoader(SONG_LOADER_ID, null, this);
            }

        }else{
            //progressBar.setVisibility(View.GONE);
            //emptyView.setText("No Internet Connection");
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.i("onActivityResult", Integer.toString(requestCode));
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

    @Override
    public Loader<List<Song>> onCreateLoader(int i, Bundle bundle) {

        String url = "https://api.spotify.com/v1/search?q=" + mSearchedSong + "&type=track";
        return new SpotifySearchLoader(this, url,mAccessToken );
    }

    @Override
    public void onLoadFinished(Loader<List<Song>> loader, List<Song> songs) {
        mSongs.clear();

        if (songs != null && !songs.isEmpty() && isLoaded) {
            //progressBar.setVisibility(View.GONE);
            isLoaded = false;
            mSongs.addAll(songs);
            Intent intent = SongListActivity.newIntent(SpotifySearchActivity.this, mSongs);
            startActivity(intent);
        } else {
            //progressBar.setVisibility(View.GONE);
            //Toast.makeText(SpotifySearchActivity.this, "Error Populating List", Toast.LENGTH_LONG).show();
            Log.i(LOG_TAG, "Error populating list");
        }


    }

    @Override
    public void onLoaderReset(Loader<List<Song>> loader) {
        // Clear the adapter of previous song data
        mSongs.clear();
        Log.i("SpotifySearchActivity", "onLoaderReset() Called");
    }

 /*   private class GetSpotifyTracks extends AsyncTask<Void, Song, Void> {

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
        protected Void doInBackground(Void... arg0){
            //start with empty arraylist
            mSongs.clear();
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            // use string builder?? then string
            String url = "https://api.spotify.com/v1/search?q=" + mSearchedSong + "&type=track";

            String jsonStr = null;
            try {
                jsonStr = sh.makeServiceCall(url, mAccessToken);
            } catch (IOException e) {
                e.printStackTrace();
            }

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
    }*/
}