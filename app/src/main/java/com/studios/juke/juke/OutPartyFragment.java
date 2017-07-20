package com.studios.juke.juke;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Intent;

/**
 * A simple {@link Fragment} subclass.
 */
public class OutPartyFragment extends Fragment {

    private Button mCreateButton;
    private Button mJoinButton;

    public OutPartyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_out_party, container, false);



        mCreateButton = (Button) rootView.findViewById(R.id.create_party);
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //first log into spotify
                Intent intent2 = new Intent(getActivity(), SpotifyLoginActivity.class);
                startActivity(intent2);

                //then send code to contacts
                Intent intent = new Intent(getActivity(), ContactsActivity.class);
                startActivity(intent);
            }
        });

        mJoinButton = (Button) rootView.findViewById(R.id.join_party);

        mJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), JoinPartyActivity.class);
                startActivity(intent);
            }
        });


        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
