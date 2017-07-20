package com.studios.juke.juke;


import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Comparator;

import static android.R.transition.move;

public class Contact implements Serializable, Comparable<Contact>{

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
    public int compareTo(Contact othercontact) {
        //compare name
        int nameDiff = mContact.compareToIgnoreCase(othercontact.getContact());
        if(nameDiff != 0){
            return nameDiff;
        }
        //names are equals compare age
        return -1;
    }

    @Override
    public String toString() {
        String result = mContact + '\n' + mNumber;
        return result;
    }
}
