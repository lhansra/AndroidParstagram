package com.example.parstagram.models;

import android.text.format.DateUtils;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ParseClassName("Post")
public class Post extends ParseObject implements Serializable {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_NUM_LIKES = "numLikes";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_COMMENTS = "comments";


    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }
    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }
    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }
    public void setImage(ParseFile image){
        put(KEY_IMAGE, image);
    }
    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}