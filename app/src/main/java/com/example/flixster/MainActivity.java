package com.example.flixster;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.EndlessRecyclerViewScrollListener;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity
{
    private static String API_URL;
    private static final String TAG;
    private List<Movie> movies;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int nextPage = 1; // Default page to request during first API call

    static {
        API_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
        TAG = "MainActivity";
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movies = new ArrayList<>();

        RecyclerView movieRecyclerView = findViewById(R.id.movies_recycler_view);

        // Create an adapter
        final MovieAdapter movieAdapter = new MovieAdapter(this, movies);

        final AsyncHttpClient client = new AsyncHttpClient();

        loadNextDataFromApi(client, movieAdapter, nextPage++);

        // Set the adapter on the recycler view
        movieRecyclerView.setAdapter(movieAdapter);

        // Set a layout manager in the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        movieRecyclerView.setLayoutManager(linearLayoutManager);

        // Adds the scroll listener to RecyclerView
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager)
        {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view)
            {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(client, movieAdapter, nextPage++);
            }
        };
        movieRecyclerView.addOnScrollListener(scrollListener);
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(AsyncHttpClient client, final MovieAdapter movieAdapter, int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        final String URL = String.format("%s&page=%s", API_URL, offset);

        client.get(URL, new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json)
            {
                Log.d(TAG, "onSuccess" + " " + URL);

                JSONObject jsonObject = json.jsonObject;
                try
                {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results " + results);
                    movies.addAll(Movie.fromJsonArray(results));
                    movieAdapter.notifyDataSetChanged();

                    // Display time after API call
                    TextView last_updated_text_view = findViewById(R.id.last_updated_text_view);
                    String lastUpdated = new SimpleDateFormat("h:mm a", Locale.US).format(new Date());
                    last_updated_text_view.setText("Last Updated: " + lastUpdated);
                    Log.d("MainActivity", "" + lastUpdated);

                    Log.i(TAG, "Movies size " + movies.size());

                }
                catch (JSONException e)
                {
                    Log.d(TAG, "JSON Exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable)
            {
                Log.d(TAG, "onFailure" + " " + response);
            }
        });
    }
}