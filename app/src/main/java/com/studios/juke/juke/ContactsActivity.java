package com.studios.juke.juke;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {
    // The ListView
    //private ListView lstNames;
    private RecyclerView lstNames;
    private ContactsAdapter mContactsAdapter;

    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        // Find the list view
        lstNames = (RecyclerView) findViewById(R.id.list);
        lstNames.setLayoutManager(new LinearLayoutManager(this));

        // Read and show the contacts
        showContacts();
    }

    /**
     * Show the contacts in the ListView.
     */
    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            List<Contact> contacts = getContactNames();
            Collections.sort(contacts);
            //mContactsAdapter = new ContactsAdapter<Contact>(this, R.layout.list_item, contacts);
            mContactsAdapter = new ContactsAdapter(contacts);
            lstNames.setAdapter(mContactsAdapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Read the name of all the contacts.
     *
     * @return a list of names.
     */
    private List<Contact> getContactNames() {
        List<Contact> contacts = new ArrayList<>();
        // Get the ContentResolver
        ContentResolver cr = getContentResolver();
        // Get the Cursor of all the contacts
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        // Move the cursor to first. Also check whether the cursor is empty or not.
        if (cursor.moveToFirst()) {
            // Iterate through the cursor
            do {
                // Get the contacts name
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                if(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER)).equals("1")) {
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Contact contact = new Contact(name, number);
                    contacts.add(contact);
                }
            } while (cursor.moveToNext());
        }
        // Close the curosor
        cursor.close();

        return contacts;
    }

    //inner class viewholder that will inflate and OWN your layout
    private class ContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Contact mContact;
        private TextView mContactTextView;
        private TextView mNumberTextView;


        public ContactHolder(LayoutInflater inflater, ViewGroup parent, int resource) {
            super(inflater.inflate(resource, parent, false));
            //set onClickListener for the current object (this)
            itemView.setOnClickListener(this);

            //pull out item text views in this constructor
            mContactTextView = (TextView) itemView.findViewById(R.id.text1);
            mNumberTextView = (TextView) itemView.findViewById(R.id.text2);
        }

        //bind crimes to text views - called each time a new Crime should be displayed (called in CrimeHolder class)
        public void bind(Contact contact) {
            mContact = contact;
            mContactTextView.setText(mContact.getContact());
            mNumberTextView.setText(mContact.getNumber());
        }

        //overide the onclick method THIS IS WHERE A SONG WILL BE PLAYED
        @Override
        public void onClick(View view) {
            Log.e("AAA", "AAdssdgA");
            String phoneNumber = mContact.getNumber();
            String smsBody = "This is an SMS!";

// Add the phone number in the data
            Uri uri = Uri.parse("smsto:" + phoneNumber);
// Create intent with the action and data
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
// smsIntent.setData(uri); // We just set the data in the constructor above
// Set the message
            //smsIntent.putExtra("sms_body", smsBody);
            startActivity(smsIntent);
        }
    }

    //inner class adapter to hold crimehold views and create the crimeHolders
    private class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Contact> mContacts;

        public ContactsAdapter(List<Contact> contacts) {
            mContacts = contacts;
        }

        //onCreateViewHolder must return object 'Crime Holder' because it has to be a HOLDER
        //this is called by the Recycler view when it needs a new ViewHolder to display items with
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //getActivity() returns the activity the fragment is currently associated with
            LayoutInflater layoutInflater = LayoutInflater.from(ContactsActivity.this);

            return new ContactHolder(layoutInflater, parent, R.layout.list_item);
        }

        //position is index position in arrayAdapter basically
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Contact contact = mContacts.get(position);
            //we defined the bind() method in the crimeHolder class
            //it will bind either normal or serious holder
            ((ContactHolder) holder).bind(contact);
        }

        @Override
        public int getItemCount() {
            return mContacts.size();
        }
        /*
        @Override
        public int getItemViewType(int position) {

        }*/
    }
}