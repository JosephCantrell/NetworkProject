package com.JosephCantrell.networkproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.JosephCantrell.networkproject.Comment.CommentAdapter;
import com.JosephCantrell.networkproject.Comment.CommentDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private String TAG = "CommentActivity";
    private int JsonState = 0;
    private int position;

    ArrayList<CommentDataSet> commentData = new ArrayList<>();

    private JSONArray userArray;
    private JSONArray commentArray;
    private JSONObject postObject;
    ListView commentList;
    private CommentAdapter commentAdapter;
    private LinearLayout theView;

    int globalPostID;

    TextView usernameTV;
    TextView titleTV;
    TextView bodyTV;
    Button makeComment;

    private String commentsURL;
    private int commentID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Bundle extra = getIntent().getExtras();

        commentList = findViewById(R.id.LV_Comment_List);
        theView = findViewById(R.id.userPost);
        usernameTV = this.findViewById(R.id.TV_LV_Post_Username);
        titleTV = this.findViewById(R.id.TV_LV_Post_Title);
        bodyTV = this.findViewById(R.id.TV_LV_Post_Body);
        makeComment = this.findViewById(R.id.Button_Write_Comment);


        position = (Integer) extra.get("position");
        Log.e(TAG, "Position: "+position);

        String postURL = "https://jsonplaceholder.typicode.com/posts/"+position;
        String usersURL = "https://jsonplaceholder.typicode.com/users";
        //String commentsURL = "https://jsonplaceholder.typicode.com/posts/"+position+"/comments";
        commentsURL = "https://jsonplaceholder.typicode.com/comments?postId="+position;


        new getPost().execute(postURL);
        new getUserData().execute(usersURL);
        new getCommentData().execute(commentsURL);



        usernameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent n = new Intent(CommentActivity.this, UserPageActivity.class);
                int userID = 0;
                try{
                    userID = postObject.getInt("userId");
                    Log.e(TAG, "User ID: "+userID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                n.putExtra("userId", userID);
                startActivity(n);
            }
        });

    }

    public void createCommentOnClick(View v){

        Button submitButton;
        final EditText commentEmail;
        final EditText commentTitle;
        final EditText commentBody;
        LayoutInflater layoutInflater = (LayoutInflater) CommentActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View customView = layoutInflater.inflate(R.layout.popup_create_comment, null);
        final PopupWindow commentDisplay;
        commentDisplay = new PopupWindow(customView, 1000 , 2500);
        commentDisplay.setFocusable(true);
        commentDisplay.showAtLocation(commentList, Gravity.CENTER, 0, 0);

        submitButton = customView.findViewById(R.id.Button_Submit_Comment);
        commentEmail = customView.findViewById(R.id.ET_User_Comment_Email);
        commentTitle = customView.findViewById(R.id.ET_User_Comment_Title);
        commentBody = customView.findViewById(R.id.ET_User_Comment_Body);

        //commentBody.setText("Text");

        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String commentEmailContents = commentEmail.getText().toString();
                String commentTitleContents = commentTitle.getText().toString();
                String commentBodyContents = commentBody.getText().toString();
                parseUserComment(commentEmailContents, commentTitleContents, commentBodyContents);
                commentDisplay.dismiss();

            }
        });

    }


    private void parsePost(){
        try {
            int userId = postObject.getInt("userId");
            Log.e(TAG, "User ID: "+userId);
            String username = getUsername(userId);
            String title = postObject.getString("title");
            String body = postObject.getString("body");

            usernameTV.setText(username);
            titleTV.setText(title);
            bodyTV.setText(body);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseUserComment(String email, String title, String body){
        JSONObject postData = new JSONObject();
        try{
            postData.put("postId", globalPostID);
            postData.put("id", commentID);
            postData.put("name", title);
            postData.put("email", email);
            postData.put("body", body);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        new postUserComment().execute(commentsURL, postData.toString());
        int commentID = commentArray.length()+1;
        commentData.add(new CommentDataSet(globalPostID, commentID, title, email, body));
        commentAdapter = new CommentAdapter(getApplicationContext(), commentData);
        commentList.setAdapter(commentAdapter);
    }

    private void parseComments(){
        commentID = 0;
        for (int i = 0; i < commentArray.length(); i++) {
            try {

                JSONObject jsonobject = null;
                jsonobject = commentArray.getJSONObject(i);
                int postID = jsonobject.getInt("postId");
                if(postID == position) {
                    commentID++;
                    globalPostID = postID;
                    Log.e(TAG, "In post PostID");
                    int commentID = jsonobject.getInt("id");
                    String name = jsonobject.getString("name");
                    String email = jsonobject.getString("email");
                    String body = jsonobject.getString("body");
                    commentData.add(new CommentDataSet(postID, commentID, name, email, body));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String getUsername(int id) {
        try {
            id = id-1;
            JSONObject jsonobject = null;
            jsonobject = userArray.getJSONObject(id);
            String username = jsonobject.getString("username");
            return username;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    class getPost extends AsyncTask<String, String, JSONObject> {
        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected JSONObject doInBackground(String... params) {
            String str = params[0];
            URLConnection urlConn = null;
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(str);
                urlConn = url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                StringBuffer stringBuffer = new StringBuffer();
                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    stringBuffer.append(line);
                }

                JSONObject jsonObject= new JSONObject(stringBuffer.toString());
                //JSONObject jsonObject = new JSONObject(stringBuffer.toString());

                Log.i(TAG, "Post Object: " + jsonObject);

                return jsonObject;
            } catch (Exception ex) {
                Log.e("App", "yourDataTask", ex);
                return null;
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        protected void onPostExecute(JSONObject response) {
            postObject = response;
            if (response != null) {
                Log.e(TAG, "Post Object" + postObject);
                JsonState++;
            }
        }
    }

    class getUserData extends AsyncTask<String, String, JSONArray> {

        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected JSONArray doInBackground(String... params) {
            String str = params[0];
            URLConnection urlConn = null;
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(str);
                urlConn = url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                StringBuffer stringBuffer = new StringBuffer();
                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    stringBuffer.append(line);
                }

                JSONArray jsonArray = new JSONArray(stringBuffer.toString());
                //JSONObject jsonObject = new JSONObject(stringBuffer.toString());

                Log.i(TAG, "Json Array: " + jsonArray);

                return jsonArray;
            } catch (Exception ex) {
                Log.e("App", "yourDataTask", ex);
                return null;
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        protected void onPostExecute(JSONArray response) {

            userArray = response;
            if (response != null) {
                JsonState++;
                parsePost();
            }
        }
    }

    class getCommentData extends AsyncTask<String, String, JSONArray> {


        protected void onPreExecute() {
            super.onPreExecute();
            if (JsonState == 0) {
                progressDialog = new ProgressDialog(CommentActivity.this);
                progressDialog.setMessage("Loading Comments, Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        protected JSONArray doInBackground(String... params) {
            String str = params[0];
            URLConnection urlConn = null;
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(str);
                urlConn = url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                StringBuffer stringBuffer = new StringBuffer();
                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    stringBuffer.append(line);
                }

                JSONArray jsonArray = new JSONArray(stringBuffer.toString());
                //JSONObject jsonObject = new JSONObject(stringBuffer.toString());

                Log.i(TAG, "Json Array: " + jsonArray);

                return jsonArray;
            } catch (Exception ex) {
                Log.e("App", "yourDataTask", ex);
                return null;
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        protected void onPostExecute(JSONArray response) {

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            commentArray = response;
            Log.i(TAG, "Comment Array: "+commentArray);

            if (response != null) {
                parseComments();
                commentAdapter = new CommentAdapter(getApplicationContext(), commentData);
                commentList.setAdapter(commentAdapter);
            }
        }
    }

    private class postUserComment extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String data = "";

            HttpURLConnection httpURLConnection = null;
            try {

                httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes("PostData=" + params[1]);
                wr.flush();
                wr.close();

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
            Toast.makeText(CommentActivity.this, "Successful Post", Toast.LENGTH_SHORT).show();
        }
    }


}
