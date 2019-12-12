package com.JosephCantrell.networkproject.Comment;

public class CommentDataSet {
    int postID;
    int commentNumber;
    String title;
    String email;
    String body;

    public CommentDataSet(int postID, int commentNumber, String title, String email, String body){
        this.postID = postID;
        this.commentNumber = commentNumber;
        this.title = title;
        this.email = email;
        this.body = body;
    }

    public int getPostID() {
        return postID;
    }

    public int getCommentNumber() {
        return commentNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getEmail() {
        return email;
    }

    public String getBody() {
        return body;
    }

}
