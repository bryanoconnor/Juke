package com.studios.juke.juke;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SongListFragment extends Fragment {

    private RecyclerView mSongRecyclerView;
    private ArrayList<Song> mSongs;
    private SongAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //calls getIntent() from parent activity that hosts this fragment
        mSongs = (ArrayList<Song>) getActivity().getIntent().getSerializableExtra(SongListActivity.EXTRA_SONG_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);

        mSongRecyclerView = (RecyclerView) view.findViewById(R.id.song_recycler_view);
        mSongRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return view;
    }

    private void updateUI() {
        mAdapter = new SongAdapter(mSongs);
        mSongRecyclerView.setAdapter(mAdapter);
    }

    //inner class viewholder that will inflate and OWN your layout
    private class SongHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Song mSong;
        private TextView mTitleTextView;
        private TextView mArtistTextView;
        private ImageView mPicture;
        private ImageView mPlayImage;


        public SongHolder(LayoutInflater inflater, ViewGroup parent, int resource) {
            super(inflater.inflate(resource, parent, false));
            //set onClickListener for the current object (this)
            itemView.setOnClickListener(this);

            //pull out item text views in this constructor
            mTitleTextView = (TextView) itemView.findViewById(R.id.song_title);
            mArtistTextView = (TextView) itemView.findViewById(R.id.song_artist);
            mPicture = (ImageView) itemView.findViewById(R.id.song_picture);
            mPlayImage = (ImageView) itemView.findViewById(R.id.play_button);
        }

        //bind crimes to text views - called each time a new Crime should be displayed (called in CrimeHolder class)
        public void bind(Song song) {
            mSong = song;
            mTitleTextView.setText(mSong.getSongName());
            mArtistTextView.setText(mSong.getArtist());
            new DownloadImageFromInternet(mPicture).execute(mSong.getImageUrl());
        }

        //overide the onclick method THIS IS WHERE A SONG WILL BE PLAYED
        @Override
        public void onClick(View view) {
            //set tag for play/pause functionality
            //mPlayButton.setTag(1);
            if(SpotifyUtils.getPlayer().getPlaybackState().isPlaying == false) {
                SpotifyUtils.getPlayer().playUri(null, mSong.getUri(), 0, 0);
                mPlayImage.setImageResource(R.drawable.ic_pause_black_48dp);
                return;
            }
            else {
                SpotifyUtils.getPlayer().pause(null);
                mPlayImage.setImageResource(R.drawable.ic_play_arrow_black_48dp);
               /* final int status = (Integer) view.getTag();
                if (status == 1) {
                    SpotifyUtils.getPlayer().pause(null);
                    view.setTag(0);
                } else {
                    SpotifyUtils.getPlayer().resume(null);
                    view.setTag(1);
                }*/
            }
        }
    }

    //inner class adapter to hold crimehold views and create the crimeHolders
    private class SongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap>
    {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
            //Toast.makeText(getApplicationContext(), "Please wait, it may take a few minute...", Toast.LENGTH_SHORT).show();
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}
