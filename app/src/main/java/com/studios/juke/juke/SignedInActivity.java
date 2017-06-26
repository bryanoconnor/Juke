package com.studios.juke.juke;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.auth.IdpResponse;

public class SignedInActivity extends AppCompatActivity {
    private static final String EXTRA_SIGNED_IN_CONFIG = "extra_signed_in_config";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in);
    }

    public static Intent createIntent(
            Context context,
            IdpResponse idpResponse) {
        Intent startIntent = idpResponse == null ? new Intent() : idpResponse.toIntent();

        return startIntent.setClass(context, SignedInActivity.class);
    }
}
