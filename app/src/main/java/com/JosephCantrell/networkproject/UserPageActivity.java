package com.JosephCantrell.networkproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.JosephCantrell.networkproject.Posts.MainDataSet;
import com.JosephCantrell.networkproject.Posts.MainMenuCustomAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class UserPageActivity extends FragmentActivity implements OnMapReadyCallback {

    private int userId;
    private String TAG = "UserPageActivity";
    private JSONArray userPosts;

    private JSONObject userObject;

    private TextView userName;
    private TextView userUserName;
    private TextView userEmail;
    private TextView userPhone;
    private TextView userWebsite;

    private GoogleMap mMap;
    private LatLng LatLngPos;

    private MainMenuCustomAdapter userPostAdapter;
    ArrayList<MainDataSet> userPostsAL = new ArrayList<>();

    private ListView userPostsLV;

    private String username;
    private int globalUserId;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        Bundle extra = getIntent().getExtras();

        userName = findViewById(R.id.TV_User_Page_Name);
        userUserName = findViewById(R.id.TV_User_Page_Username);
        userEmail = findViewById(R.id.TV_User_Page_Email);
        userPhone = findViewById(R.id.TV_User_Page_Phone);
        userWebsite = findViewById(R.id.TV_User_Page_Website);

        userPostsLV = findViewById(R.id.LV_User_Post_List);

        userId = (Integer) extra.get("userId");
        globalUserId = userId;
        Log.e(TAG, "Position: " + userId);

        String userPostsURL = "https://jsonplaceholder.typicode.com/posts?userID=" + userId;
        String userURL = "https://jsonplaceholder.typicode.com/users/" + userId;

        new getUserData().execute(userURL);
        new getUserPosts().execute(userPostsURL);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    private void parseUser(){
        try {

            Double lat;
            Double lng;

            String name = userObject.getString("name");
            username = userObject.getString("username");
            String email = userObject.getString("email");
            String phone = userObject.getString("phone");
            String website = userObject.getString("website");

            JSONObject location = userObject.getJSONObject("address");
            JSONObject geo = location.getJSONObject("geo");

            lat = geo.getDouble("lat");
            lng = geo.getDouble("lng");

            LatLngPos = new LatLng(lat,lng);

            userName.setText(name);
            userUserName.setText(username);
            userEmail.setText(email);
            userPhone.setText(phone);
            userWebsite.setText(website);


            mMap.addMarker(new MarkerOptions().position(LatLngPos).title("User Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLngPos));
            CameraPosition cp = new CameraPosition.Builder().target(LatLngPos).zoom(1).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));




        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseUserPosts(){
        for (int i = 0; i < userPosts.length(); i++) {
            try {

                JSONObject jsonobject = null;
                jsonobject = userPosts.getJSONObject(i);
                int userId = jsonobject.getInt("userId");
                if(userId == globalUserId) {
                    Log.e(TAG, "User ID: " + userId);
                    String postId = jsonobject.getString("id");
                    String title = jsonobject.getString("title");
                    userPostsAL.add(new MainDataSet(postId, username, title));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    class getUserData extends AsyncTask<String, String, JSONObject> {

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

                JSONObject jsonArray = new JSONObject(stringBuffer.toString());
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
        protected void onPostExecute(JSONObject response) {

            userObject = response;
            if (response != null) {
                parseUser();
            }
        }
    }

    class getUserPosts extends AsyncTask<String, String, JSONArray> {

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

            userPosts = response;
            if (response != null) {
                parseUserPosts();
                userPostAdapter = new MainMenuCustomAdapter(getApplicationContext(), userPostsAL);
                userPostsLV.setAdapter(userPostAdapter);
            }
        }
    }
}