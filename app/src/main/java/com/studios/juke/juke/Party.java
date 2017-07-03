package com.studios.juke.juke;

/**
 * Created by Jack on 7/2/2017.
 */

public class Party {

    private String mSongs;
    private String mMembers;

    public Party(){

    }

    public Party(String songs, String members){
        mMembers = members;
        mSongs = songs;
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
