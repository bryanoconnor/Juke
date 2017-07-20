package com.studios.juke.juke;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

public class SpotifyLoginActivity extends MenuBarOptions implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback {

    private Toolbar mToolbar;

    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "256fa987714a455687888ed1f07c3630";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "juke-login://callback";
    // Request code that will be used to verify if the result comes from correct activity
    private static final int REQUEST_CODE = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        Log.i("onActivityResult", Integer.toString(requestCode));
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {

            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            if (response.getType() == AuthenticationResponse.Type.TOKEN) {

                SpotifyUtils.setAccessToken(response.getAccessToken());
                Log.i("Access Token: ", SpotifyUtils.getAccessToken());

                Config playerConfig = new Config(this, SpotifyUtils.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        SpotifyUtils.setPlayer(spotifyPlayer);
                        SpotifyUtils.getPlayer().addConnectionStateCallback(SpotifyLoginActivity.this);
                        SpotifyUtils.getPlayer().addNotificationCallback(SpotifyLoginActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("SpotifySearchActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }

        Intent intent2 = new Intent(SpotifyLoginActivity.this, SpotifyHomeActivity.class);
        startActivity(intent2);
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
}
