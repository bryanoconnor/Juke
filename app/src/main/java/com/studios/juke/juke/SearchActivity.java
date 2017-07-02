package com.studios.juke.juke;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends MenuBarOptions implements LoaderCallbacks<List<Song>> {

    private EditText mEditSong;
    private Toolbar mToolbar;

    private static final String EXTRA_SEARCH_KEYWORD = "keyword";
    private static final String RETURNED_SONG_KEY = "returnedSong";
    public static final String LOG_TAG = "TEST_LOGS";
    private static final int SONG_LOADER_ID = 1;
    public static boolean isLoaded;
    private static boolean runOnce = true;

    private String mSearchedSong;
    private ArrayList<Song> mSongs = new ArrayList<>();
    private RecyclerView mSongRecyclerView;
    private SongAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);

        mSongRecyclerView = (RecyclerView) findViewById(R.id.song_recycler_view);
        mSongRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        mSearchedSong = intent.getStringExtra(EXTRA_SEARCH_KEYWORD);
        //get requested song then start the loader
        createLoader();

        updateUI();
    }

    private void createLoader(){
        isLoaded = true;

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            // Get instance of LoaderManager and start the loader
            Log.i(LOG_TAG, "initLoader() Called");
            if (runOnce) {
                getLoaderManager().initLoader(SONG_LOADER_ID, null, this);
                runOnce = false;
            } else {
                getLoaderManager().restartLoader(SONG_LOADER_ID, null, this);
            }

        } else {
            //progressBar.setVisibility(View.GONE);
            //emptyView.setText("No Internet Connection");
        }
    }

    private void updateUI() {
        mAdapter = new SongAdapter(mSongs);
        mSongRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public Loader<List<Song>> onCreateLoader(int i, Bundle bundle) {

        String url = "https://api.spotify.com/v1/search?q=" + mSearchedSong + "&type=track";
        return new SpotifySearchLoader(this, url, SpotifyUtils.getAccessToken() );
    }

    @Override
    public void onLoadFinished(Loader<List<Song>> loader, List<Song> songs) {
        mSongs.clear();

        if (songs != null && !songs.isEmpty() && isLoaded) {
            //progressBar.setVisibility(View.GONE);
            isLoaded = false;
            mSongs.addAll(songs);
            updateUI();
        } else {
            //progressBar.setVisibility(View.GONE);
            //Toast.makeText(SpotifySearchActivity.this, "Error Populating List", Toast.LENGTH_LONG).show();
            Log.i(LOG_TAG, "Error populating list");
        }


    }

    @Override
    public void onLoaderReset(Loader<List<Song>> loader) {
        // Clear the adapter of previous song data
        mSongs.clear();
        Log.i("SpotifySearchActivity", "onLoaderReset() Called");
    }






    //inner class viewholder that will inflate and OWN your layout
    private class SongHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Song mSong;
        private TextView mTitleTextView;
        private TextView mArtistTextView;
        private ImageView mPicture;
        //private ImageView mPlayImage;


        public SongHolder(LayoutInflater inflater, ViewGroup parent, int resource) {
            super(inflater.inflate(resource, parent, false));
            //set onClickListener for the current object (this)
            itemView.setOnClickListener(this);

            //pull out item text views in this constructor
            mTitleTextView = (TextView) itemView.findViewById(R.id.song_title);
            mArtistTextView = (TextView) itemView.findViewById(R.id.song_artist);
            mPicture = (ImageView) itemView.findViewById(R.id.song_picture);
            //mPlayImage = (ImageView) itemView.findViewById(R.id.play_button);
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
            Intent returnIntent = new Intent();
            returnIntent.putExtra(RETURNED_SONG_KEY,mSong);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
            //set tag for play/pause functionality
            //mPlayButton.setTag(1);
          /*  if(SpotifyUtils.getPlayer().getPlaybackState().isPlaying == false) {
                SpotifyUtils.getPlayer().playUri(null, mSong.getUri(), 0, 0);
                mPlayImage.setImageResource(R.drawable.ic_pause_black_48dp);
                return;
            }*/
           /* else {
                SpotifyUtils.getPlayer().pause(null);
                mPlayImage.setImageResource(R.drawable.ic_play_arrow_black_48dp);*/
               /* final int status = (Integer) view.getTag();
                if (status == 1) {
                    SpotifyUtils.getPlayer().pause(null);
                    view.setTag(0);
                } else {
                    SpotifyUtils.getPlayer().resume(null);
                    view.setTag(1);
                }*/
           // }
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
            LayoutInflater layoutInflater = LayoutInflater.from(SearchActivity.this);

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