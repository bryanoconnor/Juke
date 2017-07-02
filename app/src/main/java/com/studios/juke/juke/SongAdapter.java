package com.studios.juke.juke;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Jack on 7/1/2017.
 */

public class SongAdapter extends ArrayAdapter<Song> {

    public SongAdapter(Context context, int resource, List<Song> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View songItemView = convertView;
        if(songItemView == null) {
            songItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_song, parent, false);
        }

        ImageView songImageView = (ImageView) songItemView.findViewById(R.id.song_picture);
        TextView songTitle = (TextView) songImageView.findViewById(R.id.song_title);
        TextView songArtist = (TextView)songImageView.findViewById(R.id.song_artist);

        Song song = getItem(position);

        Glide.with(songImageView.getContext()).load(song.getImageUrl()).into(songImageView);
        songTitle.setText(song.getSongName());
        songArtist.setText(song.getArtist());

        return songItemView;
    }
}
