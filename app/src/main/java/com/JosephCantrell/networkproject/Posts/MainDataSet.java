package com.JosephCantrell.networkproject.Posts;

import android.util.Log;

public class MainDataSet {
    String postID;
    String username;
    String title;
    String TAG = "MainDataSet";

    public MainDataSet(String ID, String username, String title)
    {
        this.postID = ID;
        Log.i(TAG, "PostId: "+this.postID);
        this.username = username;
        Log.i(TAG, "UserId: "+this.username);
        this.title = title;
        Log.i(TAG, "title: "+this.title);
    }

    public String getPostID() {
        return postID;
    }

    public String getTitle() {
        return title;
    }

    public String getUsername() {
        return username;
    }


}
