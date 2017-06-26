package com.studios.juke.juke;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.spotify.sdk.android.authentication.LoginActivity.REQUEST_CODE;


public class SongListActivity extends SingleFragmentActivity {

    public static final String EXTRA_SONG_ID = "com.studios.juke.juke.song_list_id";

    public static Intent newIntent(Context packageContext, ArrayList<Song> songs){
        Intent intent = new Intent(packageContext, SongListActivity.class);
        intent.putExtra(EXTRA_SONG_ID, songs);
        return intent;
    }

    @Override
    protected Fragment createFragment(){
        return new SongListFragment();
    }
}
