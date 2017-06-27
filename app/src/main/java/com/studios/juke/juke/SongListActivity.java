package com.studios.juke.juke;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;


public class SongListActivity extends SingleFragmentActivity {

    private Toolbar mToolbar;

    public static final String EXTRA_SONG_ID = "com.studios.juke.juke.song_list_id";



    public static Intent newIntent(Context packageContext, ArrayList<Song> songs){
        Intent intent = new Intent(packageContext, SongListActivity.class);
        intent.putExtra(EXTRA_SONG_ID, songs);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
    }
    @Override
    protected Fragment createFragment(){
        return new SongListFragment();
    }


}
