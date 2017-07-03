package com.studios.juke.juke;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.studios.juke.juke.UserAuth.AuthUiActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PartyActivity extends MenuBarOptions {

    private static final String LOG_TAG = "PartyActivity";
    public static final String ANONYMOUS = "anonymous";
    private static final String RETURNED_SONG_KEY = "returnedSong";
    private static final String EXTRA_SEARCH_KEYWORD = "keyword";
    public static final int DEFAULT_SEARCH_LENGTH_LIMIT = 100;
    public static final int RC_SONG_PICKER = 2;
    private SongAdapter mSongAdapter;
    private Toolbar mToolbar;
    private List<Song> songList;


    // Firebase instance variables
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mPartyDatabaseReference;
    private DatabaseReference mMembersDatabaseReference;
    private DatabaseReference mSongsDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private String mPartyID;
    private String mUsername;

    @BindView(R.id.partyListView)
    ListView mSongListView;

    @BindView(R.id.partySearchButton)
    Button mSearchButton;

    @BindView(R.id.partyEditText)
    EditText mSongEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);
        ButterKnife.bind(this);

        // Initialize Toolbar
        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mPartyDatabaseReference = mFirebaseDatabase.getReference().child("parties");
        createParty();
        mUsername = ANONYMOUS;
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        // Initialize song ListView and its adapter
        songList = new ArrayList<>();
        mSongAdapter = new SongAdapter(this, R.layout.list_item_song, songList);
        mSongListView.setAdapter(mSongAdapter);

        // Enable Search button when there's text to search
        mSongEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSearchButton.setEnabled(true);
                } else {
                    mSearchButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSongEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_SEARCH_LENGTH_LIMIT)});


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    onSignedInInitialized(user.getDisplayName());
                } else {
                    onSignedOutCleanup();
                    startActivity(AuthUiActivity.createIntent(PartyActivity.this));
                    finish();
                    return;
                }
            }
        };

    }

    @OnClick(R.id.partySearchButton)
    public void search(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(EXTRA_SEARCH_KEYWORD, mSongEditText.getText().toString());
        startActivityForResult(intent, RC_SONG_PICKER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SONG_PICKER && resultCode == RESULT_OK) {
            final Song songPicked = (Song) data.getExtras().getSerializable(RETURNED_SONG_KEY);
            mSongsDatabaseReference.push().setValue(songPicked);
        }
    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        mSongAdapter.clear();
        detachDatabaseReadListener();
    }

    private void onSignedInInitialized(String displayName) {
        mUsername = displayName;
        attachDatabaseReadListener();
    }

    private void detachDatabaseReadListener(){
        if(mChildEventListener != null){
            mSongsDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    private void attachDatabaseReadListener(){
        if(mChildEventListener == null)
        {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Song song = dataSnapshot.getValue(Song.class);
                    mSongAdapter.add(song);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Song song = dataSnapshot.getValue(Song.class);
                    //songList.remove(song);
                    mSongAdapter.remove(song);
                    mSongAdapter.notifyDataSetChanged();
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mSongsDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAuthStateListener != null)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        detachDatabaseReadListener();
        mSongAdapter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void createParty(){
        mPartyID = mPartyDatabaseReference.push().getKey();
        mSongsDatabaseReference = mPartyDatabaseReference.child(mPartyID).child("songs");
        mMembersDatabaseReference = mPartyDatabaseReference.child(mPartyID).child("members");
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mMembersDatabaseReference.child(mFirebaseUser.getUid().toString()).setValue("owner");
    }
}
