package com.JosephCantrell.networkproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.JosephCantrell.networkproject.Posts.MainDataSet;
import com.JosephCantrell.networkproject.Posts.MainMenuCustomAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    String TAG = "MAIN";
    int INTERNET_PERMISSION_CODE = 100;
    ListView mainMenuList;
    ArrayList<MainDataSet> mainPosts = new ArrayList<>();

    int JsonState = 0;

    JSONArray PostData;
    JSONArray CommentData;


    private MainMenuCustomAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();


        mainMenuList = findViewById(R.id.mainMenuListView);

        String mainURL = "https://jsonplaceholder.typicode.com/posts";
        String UserURL = "https://jsonplaceholder.typicode.com/users";
        JsonState = 0;
        new retreiveData().execute(mainURL) ;
        new retrieveUserData().execute(UserURL);

        mainMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {

                Intent n = new Intent(getApplicationContext(), CommentActivity.class);
                int newposition = position+1;
                n.putExtra("position", newposition);
                Log.e(TAG, "Position: " +newposition);
                startActivity(n);
            }
        });
    }

    public void parseMainData(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonobject = null;
                jsonobject = jsonArray.getJSONObject(i);
                int userId = jsonobject.getInt("userId");
                Log.e(TAG, "User ID: "+userId);
                String username = getUsername(userId);
                String postId = jsonobject.getString("id");
                String title = jsonobject.getString("title");
                mainPosts.add(new MainDataSet(postId, username, title));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String getUsername(int id) {
        try {
            id = id -1;
            JSONObject jsonobject = null;
            jsonobject = CommentData.getJSONObject(id);
            String username = jsonobject.getString("username");
            return username;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, INTERNET_PERMISSION_CODE);
        } else {
            //Toast.makeText(view, "Permission already grated", Toast.LENGTH_SHORT).show();
            Log.i(TAG, Manifest.permission.INTERNET + " already granted");
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == INTERNET_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,
                        "Camera Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(this,
                        "Camera Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


    class retreiveData extends AsyncTask<String, String, JSONArray> {


        protected void onPreExecute() {
            super.onPreExecute();
            if (JsonState == 0) {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Please wait");
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


            if (JsonState == 0) {
                Log.i(TAG, "Post Data");
                PostData = response;
            }
            if (JsonState == 1) {
                Log.i(TAG, "Comment Data");
                CommentData = response;
            }
            //Log.i(TAG, jsonObjectGetString(jsonObject,"userId"));


            if (response != null) {
                Log.e(TAG, "JsonState "+JsonState);
                JsonState++;

            }
        }
    }

    class retrieveUserData extends AsyncTask<String, String, JSONArray> {

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


            CommentData = response;

            //Log.i(TAG, jsonObjectGetString(jsonObject,"userId"));


            if (response != null) {
                Log.e(TAG, "JsonState" + JsonState);
                JsonState++;

                if (JsonState == 2) {
                    Log.i(TAG, "Parsing");
                    parseMainData(PostData);
                    mainAdapter = new MainMenuCustomAdapter(getApplicationContext(), mainPosts);
                    mainMenuList.setAdapter(mainAdapter);
                }

            }
        }
    }
}

