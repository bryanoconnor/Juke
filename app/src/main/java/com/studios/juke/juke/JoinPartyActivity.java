package com.studios.juke.juke;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JoinPartyActivity extends AppCompatActivity {

    private static final String RETURNED_PARTY_KEY = "returnedParty";
    private static String mPartyID;
    private static Party mParty;

    @BindView(R.id.join_Button)
    Button mJoinButton;

    @BindView(R.id.join_EditText)
    EditText mJoinEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_party);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.join_Button)
    public void joinParty(View view){
        mPartyID = mJoinEditText.getText().toString();
        mParty = new Party(mPartyID);
        Intent returnIntent = new Intent();
        returnIntent.putExtra(RETURNED_PARTY_KEY,mParty);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
