package com.studios.juke.juke;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class SpotifySearchLoader extends AsyncTaskLoader<List<Song>> {

    private String mUrl;
    private String mAccessToken;
    public static final String LOG_TAG = "SPOTLOADER";

    public SpotifySearchLoader(Context context, String url, String accessToken) {
        super(context);
        mUrl = url;
        mAccessToken = accessToken;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "onStartLoading() Called");
        forceLoad();
    }

    @Override
    public List<Song> loadInBackground() {
        if(mUrl == null)
            return null;

        List<Song> result = QueryUtils.fetchSongData(mUrl, mAccessToken);
        Log.i(LOG_TAG, "loadInBackground() Finished");
        return result;
    }
}
