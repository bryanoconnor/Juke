package com.studios.juke.juke;


import java.io.Serializable;

public class Song implements Serializable{

    private String mSongName;
    private String mArtist;
    private String mImageUrl;
    private String mUri;

    public Song(){

    }

    public Song(String songName, String artist, String uri, String imageUrl){
        mSongName = songName;
        mArtist = artist;
        mUri = uri;
        mImageUrl = imageUrl;
    }

    public String getSongName() {
        return mSongName;
    }

    public String getArtist() {
        return mArtist;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getUri(){ return mUri; }

    public void setSongName(String songName) {
        mSongName = songName;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }

   public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public void setUri(String uri) { mUri = uri; }


}
