package com.studios.juke.juke;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import java.lang.reflect.Array;
import java.util.ArrayList;


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
