package com.studios.juke.juke;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

/**
 * Created by bdo04 on 6/26/2017.
 */

public class SpotifyHomeActivity extends MenuBarOptions{

    private Toolbar mToolbar;
    private Button mCreateButton;
    private Button mJoinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_home);

        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);

        mCreateButton = (Button) findViewById(R.id.create_party);
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SpotifyHomeActivity.this, CreateParty.class);

                startActivity(intent);
            }
        });

        mJoinButton = (Button) findViewById(R.id.join_party);
    }
}
