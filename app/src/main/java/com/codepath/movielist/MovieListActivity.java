package com.codepath.movielist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.codepath.movielist.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class MovieListActivity extends AppCompatActivity {

    //CONSTANTS
    //base url of API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    //parameter name
    public final static String API__KEY_PARAM = "api_key";
    //tag for logging from this activity, MovieListActivity
    public final static String TAG = "MovieListActivity";

    //instance fields
    AsyncHttpClient client;
    //base URL for loading imgs
    String imageBaseUrl;
    //poster size to use when fetching imgs
    String posterSize;
    //list of currently playing movies
    ArrayList<Movie> movies;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        movies = new ArrayList<>();
        client = new AsyncHttpClient();
        getConfiguration();
        getNowPlaying();
    }

    private void getNowPlaying() {
        //create the url
        String url = API_BASE_URL + "/movie/now_playing";
        //set up request parameters
        RequestParams params = new RequestParams();
        params.put(API__KEY_PARAM, getString(R.string.api_key)); //this is API key: always necessary!!!
        //execute a GET request that expects a response from JSON object
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject images = response.getJSONObject("images");
                    //get image base url
                    imageBaseUrl = images.getString("secure_base_url");
                    //get poster size
                    JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");
                    //use the option at index 3 or w342 as fallback
                    posterSize = posterSizeOptions.optString(3, "w342");
                    Log.i(TAG, String.format("Loaded config w imageBaseUrl %s and posterSize %s", imageBaseUrl, posterSize));

                } catch (JSONException e) {
                    logError("Failed parsing config", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get configuration", throwable, true);
            }
        });
    }

    //get the config from API
    private void getConfiguration() {
        //create the url
        String url = API_BASE_URL + "/configuration";
        //set up request parameters
        RequestParams params = new RequestParams();
        params.put(API__KEY_PARAM, getString(R.string.api_key)); //this is API key: always necessary!!!
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
