package com.studios.juke.juke;

import java.io.Serializable;

/**
 * Created by Jack on 7/2/2017.
 */

public class Party implements Serializable{

    private String mSongs;
    private String mMembers;
    private String mID;

    public Party(){

    }

    public Party(String id){
        mMembers = "DEFAULT";
        mSongs = "DEFAULT";
        mID = id;
    }

    public Party(String songs, String members, String id){
        mMembers = members;
        mSongs = songs;
        mID = id;
    }

    public String getmSongs() {
        return mSongs;
    }

    public void setmSongs(String mSongs) {
        this.mSongs = mSongs;
    }

    public String getmMembers() {
        return mMembers;
    }

    public void setmMembers(String mMembers) {
        this.mMembers = mMembers;
    }
}
