package com.codepath.movielist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class MovieListActivity extends AppCompatActivity {

    //CONSTANTS
    //base url of API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    //parameter name
    public final static String API__KEY_PARAM = "api_key";
    //the API key -- TODO make sure to move the key to a more secure location later
    public final static String API_KEY = "add20abf45fb1ede810d71e7b4c6b8cc";
    //tag for logging from this activity, MovieListActivity
    public final static String TAG = "MovieListActivity";

    //instance fields
    AsyncHttpClient client;
    //base URL for loading imgs
    String imageBaseUrl;
    //poster size to use when fetching imgs
    String posterSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        client = new AsyncHttpClient();
        getConfiguration();
    }

    //get the config from API
    private void getConfiguration() {
        //create the url
        String url = API_BASE_URL + "/configuration";
        //set up request parameters
        RequestParams params = new RequestParams();
        params.put(API__KEY_PARAM, API_KEY); //this is API key: always necessary!!!
        //execute a GET request that expects a response from JSON object
        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    JSONObject images = response.getJSONObject("images");
                    //get image base url
                    imageBaseUrl = images.getString("secure_base_url");
                    //get poster size
                    JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");
                    posterSize = posterSizeOptions.optString(3, "w342");
                } catch(JSONException e) {
                    logError("Failed parsing configuration", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get configuration", throwable, true);
            }
        });
    }

    private void logError (String message, Throwable error, boolean alertUser){
        //always logs the error
        Log.e(TAG, message, error);
        //alert the user so as to avoid silent errors
        if (alertUser){
            //show long toast with error msg
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
