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
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SpotifySearchActivity extends MenuBarOptions implements LoaderCallbacks<List<Song>> {

    private EditText mEditSong;
    private Toolbar mToolbar;

    public static final String LOG_TAG = "TEST_LOGS";
    private static final int SONG_LOADER_ID = 1;
    public static boolean isLoaded;
    private static boolean runOnce = true;

    private String mSearchedSong;
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

        if (isConnected) {
            // Get instance of LoaderManager and start the loader
            Log.i(LOG_TAG, "initLoader() Called");
            if (runOnce) {
                getLoaderManager().initLoader(SONG_LOADER_ID, null, this);
                runOnce = false;
            } else {
                getLoaderManager().restartLoader(SONG_LOADER_ID, null, this);
            }

        } else {
            //progressBar.setVisibility(View.GONE);
            //emptyView.setText("No Internet Connection");
        }
    }

    @Override
    public Loader<List<Song>> onCreateLoader(int i, Bundle bundle) {

        String url = "https://api.spotify.com/v1/search?q=" + mSearchedSong + "&type=track";
        return new SpotifySearchLoader(this, url, SpotifyUtils.getAccessToken() );
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
}