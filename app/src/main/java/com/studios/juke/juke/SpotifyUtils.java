package com.studios.juke.juke;

import com.spotify.sdk.android.player.Player;

public class SpotifyUtils {

    private static Player mPlayer;
    private static String mAccessToken;
    //private String mRefreshToken;

    public static void setPlayer(Player player){
        mPlayer = player;
    }

    public static void setAccessToken(String accessToken){
        mAccessToken = accessToken;
    }

    public static Player getPlayer(){
        return mPlayer;
    }

    public static String getAccessToken(){
        return mAccessToken;
    }
}
