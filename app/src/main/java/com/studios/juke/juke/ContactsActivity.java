package com.studios.juke.juke;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class ContactsActivity extends AppCompatActivity{

    private AutoCompleteTextView lstNames;
    private ListView selectedNames;
    private Button sendButton;
    private HashSet<String> contact_names;

    ArrayAdapter listview_adapter;
    ArrayList<String> listItems;
    ArrayAdapter<String> listview_items_adapter;

    ArrayList<String> numbers_to_send;

    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contact_names = new HashSet<>();
        numbers_to_send = new ArrayList<>();

        lstNames =(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
        final ArrayList<Contact> contacts = initializeContacts();
        listview_adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,contacts);

        listItems =new ArrayList<String>();

        listview_items_adapter =new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);

        selectedNames = (ListView) findViewById(R.id.list_contacts);
        selectedNames.setAdapter(listview_items_adapter);


        lstNames.setAdapter(listview_adapter);
        lstNames.setThreshold(1);
        lstNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact selected = (Contact) parent.getAdapter().getItem(position);

                listItems.add(selected.getContact());
                listview_items_adapter.notifyDataSetChanged();
                lstNames.setText("");
                numbers_to_send.add(selected.getNumber());
            }
        });

        sendButton = (Button)findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode();
            }
        });
    }

    /**
     * Show the contacts in the ListView.
     */
    private ArrayList<Contact> initializeContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            HashSet<Contact> contacts = getContactNames();
            ArrayList<Contact> contacts_list = new ArrayList<Contact>(contacts);
            Collections.sort(contacts_list);
            return contacts_list;
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                initializeContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private HashSet<Contact> getContactNames() {
        HashSet<Contact> contacts = new HashSet<>();
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
                    String clean_number = number.replaceAll("[^0-9.]", "");
                    Contact contact = new Contact(name, clean_number);

                    if(!contact_names.contains(name)){
                        contacts.add(contact);
                        contact_names.add(name);
                    }
                }
            } while (cursor.moveToNext());
        }
        // Close the curosor
        cursor.close();

        return contacts;
    }

    private void sendCode(){
        String separator = ";";
        if(android.os.Build.MANUFACTURER.equalsIgnoreCase("samsung")){
           separator = ",";
        }
        String full_number_list = "";
        for(int i = 0; i < numbers_to_send.size(); ++i){
            full_number_list += numbers_to_send.get(i);
            full_number_list += separator;
        }

        try {
            Uri uri = Uri.parse("smsto:" + full_number_list);
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
            smsIntent.putExtra("sms_body", "this is a test");
            startActivity(smsIntent);

        }catch (Exception e){
            Toast.makeText(getApplicationContext(),
                    "SMS faild, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        finish();
    }
}