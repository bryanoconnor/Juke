package com.studios.juke.juke;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;


public class HomeActivity extends MenuBarOptions{

    private boolean mInParty;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //ButterKnife.bind(this);

        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);

        // Launching OutPartyFragment
        launchOutParty();


        mInParty = false;
    }

    @Override
    protected void onResume(){
        super.onResume();
    }
    public void onJoinedParty() {

    }

    public void onLeaveParty() {

    }

    public void updateUI(boolean inParty) {
        if (inParty) {

        } else {
            launchOutParty();
        }
    }

    public void launchOutParty(){

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container);

        if(fragment == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new OutPartyFragment())
                    .commit();
        } /*else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new OutPartyFragment())
                    .commit();
        }*/
    }

    public void launchInParty(){

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container);

        if(fragment == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new InPartyFragment())
                    .commit();
        }
    }



}
