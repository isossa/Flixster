package com.example.flixster.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.flixster.R;
import com.example.flixster.models.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>
{
    Context context;
    List<Movie> movies;

    public MovieAdapter(Context contextIn, List<Movie> moviesIn)
    {
        context = contextIn;
        movies = moviesIn;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Log.d("MovieAdapter", "onCreateViewHolder");
        View movieView = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(movieView);
    }

    // Involves populating data into the item through the view holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Log.d("MovieAdapter", "onBindViewHolder" + position);
        // Get the movie at the  passed in position
        Movie movie = movies.get(position);

        // Bind the movie data int to view holder
        holder.bind(movie);
    }

    // Returns number of items in the list
    @Override
    public int getItemCount()
    {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView title_text_view;
        TextView overview_text_view;
        ImageView poster_image_view;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            title_text_view = itemView.findViewById(R.id.title_text_view);
            overview_text_view = itemView.findViewById(R.id.overview_text_view);
            poster_image_view = itemView.findViewById(R.id.poster_image_view);
        }

        public void bind(Movie movie)
        {
            title_text_view.setText(movie.getTitle());
            overview_text_view.setText(movie.getOverview());
            String imageUrl;
            int width;

            // Phone in landscape mode
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                imageUrl = movie.getBackdropPath();
                width = 342;
            }
            // Phone in portrait mode
            else
            {
                imageUrl = movie.getPosterPath();
                width = 1280;
            }
            Glide.with(context).load(imageUrl).placeholder(R.drawable.loader).override(width, Target.SIZE_ORIGINAL).into(poster_image_view);
        }
    }
}
