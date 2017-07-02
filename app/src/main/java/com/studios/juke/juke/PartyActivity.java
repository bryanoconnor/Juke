package com.studios.juke.juke;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.studios.juke.juke.UserAuth.AuthUiActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PartyActivity extends AppCompatActivity {

    private static final String LOG_TAG = "PartyActivity";

    public static final String ANONYMOUS = "anonymous";
    private static final String RETURNED_SONG_KEY = "returnedSong";
    private static final String EXTRA_SEARCH_KEYWORD = "keyword";
    public static final int DEFAULT_SEARCH_LENGTH_LIMIT = 100;
    public static final int RC_SONG_PICKER = 2;
    public static final int RC_SIGN_IN = 1;
    private String mUsername;
    private SongAdapter mSongAdapter;


    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mPartyDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mSongPhotosStorageReference;

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


        // Initialize Firebase components
        mUsername = ANONYMOUS;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mPartyDatabaseReference = mFirebaseDatabase.getReference().child("songs");
        mSongPhotosStorageReference = mFirebaseStorage.getReference().child("song_photos");

        // Initialize song ListView and its adapter
        List<Song> songList = new ArrayList<>();
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
        Log.e("AAA", Integer.toString(requestCode));
        if(requestCode == RC_SONG_PICKER && resultCode == RESULT_OK) {
            final Song songPicked = (Song) data.getExtras().getSerializable(RETURNED_SONG_KEY);
            Log.e("AAA", Integer.toString(requestCode));
            mPartyDatabaseReference.push().setValue(songPicked);
            /*Uri selectedImageUri = Uri.parse(songPicked.getImageUrl());
            StorageReference photoRef = mSongPhotosStorageReference.child(selectedImageUri.getLastPathSegment());
            photoRef.putFile(selectedImageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.e("AAA", "SSSS");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Song addedSong = new Song(songPicked.getSongName(), songPicked.getArtist(), songPicked.getUri(), downloadUrl.toString());
                    mPartyDatabaseReference.push().setValue(addedSong);
                }
            });*/
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
            mPartyDatabaseReference.removeEventListener(mChildEventListener);
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

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mPartyDatabaseReference.addChildEventListener(mChildEventListener);
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
}
