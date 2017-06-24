package com.studios.juke.juke;

import android.support.v4.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class SongListFragment extends Fragment {

    private RecyclerView mSongRecyclerView;
    private ArrayList<Song> mSongs;
    private SongAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //calls getIntent() from parent activity that hosts this fragment
        mSongs = (ArrayList<Song>) getActivity().getIntent().getSerializableExtra(SongListActivity.EXTRA_SONG_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);

        mSongRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mSongRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //update UI on create view
        updateUI();

        return view;
    }

    private void updateUI(){
        mAdapter = new SongAdapter(mSongs);
        mSongRecyclerView.setAdapter(mAdapter);

    }
    //inner class viewholder that will inflate and OWN your layout
    private class SongHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Song mSong;
        private TextView mTitleTextView;
        private TextView mArtistTextView;
        //private ImageView mSolvedImageView;


        public SongHolder(LayoutInflater inflater, ViewGroup parent, int resource){
            super(inflater.inflate(resource, parent, false));
            //set onClickListener for the current object (this)
            itemView.setOnClickListener(this);

            //pull out item text views in this constructor
            mTitleTextView = (TextView) itemView.findViewById(R.id.song_title);
            mArtistTextView = (TextView) itemView.findViewById(R.id.song_artist);
            //mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
        }

        //bind crimes to text views - called each time a new Crime should be displayed (called in CrimeHolder class)
        public void bind(Song song){
            mSong = song;
            mTitleTextView.setText(mSong.getSongName());
            mArtistTextView.setText(mSong.getArtist());
        }

        //overide the onclick method THIS IS WHERE A SONG WILL BE PLAYED
        @Override
        public void onClick(View view){
            //just play song for now
            MainActivity.mPlayer.playUri(null, mSong.getUri(), 0, 0);
        }
    }

    //inner class adapter to hold crimehold views and create the crimeHolders
    private class SongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private List<Song> mSongs;

        public SongAdapter(List<Song> songs) {
            mSongs = songs;
        }

        //onCreateViewHolder must return object 'Crime Holder' because it has to be a HOLDER
        //this is called by the Recycler view when it needs a new ViewHolder to display items with
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //getActivity() returns the activity the fragment is currently associated with
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new SongHolder(layoutInflater, parent, R.layout.list_item_song);
        }

        //position is index position in arrayAdapter basically
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Song song = mSongs.get(position);
            //we defined the bind() method in the crimeHolder class
            //it will bind either normal or serious holder
            ((SongHolder) holder).bind(song);

        }

        @Override
        public int getItemCount() {
            return mSongs.size();
        }
        /*
        @Override
        public int getItemViewType(int position) {

        }*/
    }
}
