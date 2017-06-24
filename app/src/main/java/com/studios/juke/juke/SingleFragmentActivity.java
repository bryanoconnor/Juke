package com.studios.juke.juke;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;


public abstract class SingleFragmentActivity extends AppCompatActivity {

    //abstract method to create fragment
    protected abstract Fragment createFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        //get the fragment manager
        FragmentManager fm = getSupportFragmentManager();
        //fragment_container is view id in main class activity -> activity_fragment.xml
        //retrieve the CrimeFragment from the FragmentManager
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        //if no fragment available
        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
