package com.studios.juke.juke;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JoinPartyActivity extends AppCompatActivity {

    @BindView(R.id.join_Button)
    Button mJoinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_party);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.join_Button)
    public void joinParty(View view){

    }
}
