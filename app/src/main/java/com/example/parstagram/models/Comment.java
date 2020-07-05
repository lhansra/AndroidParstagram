package com.example.parstagram.models;


import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_POST = "post";
    public static final String KEY_BODY = "body";

    public String getBody(){
        return getString(KEY_BODY);
    }
    public void setBody(String body){
        put(KEY_BODY, body);
    }
    public ParseObject getPost(){
        return getParseObject(KEY_POST);
    }
    public void setPost(Post post){
        put(KEY_POST, post);
    }
    public ParseUser getUser(){
        try {
            return fetchIfNeeded().getParseUser(KEY_USER);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

}
