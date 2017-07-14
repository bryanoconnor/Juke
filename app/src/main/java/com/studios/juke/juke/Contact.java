package com.studios.juke.juke;


import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Comparator;

public class Contact implements Serializable, Comparable{

    private String mContact;
    private String mNumber;


    public Contact(){

    }


    public Contact(String contact, String number){
        mContact = contact;
        mNumber = number;
    }

    public String getContact(){
        return mContact;
    }

    public String getNumber(){
        return mNumber;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Contact contact = (Contact) o;
        if(contact.getContact().equals(mContact))
            return 0;
        else
            return -1;
    }

    public boolean equals(Object obj){
        Contact contact = (Contact) obj;
        if(contact.getContact().equals(mContact))
            return true;
        else
            return false;
    }
}
