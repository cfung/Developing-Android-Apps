package com.example.cfung.project_1_popular_movie;

import android.graphics.Movie;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyActivity";
    final static String MOVIE_API_URL = "https://api.themoviedb.org/3/movie/popular?api_key=bad34c8d38b0750ab6bef23cb64440ba";
    private GridView gridView = null;
    private CustomAdapter movieAdapter = null;
    ArrayList<MovieModel> AllMovies = null;

    public String makeServiceCall(String reqUrl){
        Log.v(TAG, "starting makeServiceCall..");
        String response = null;
        try{
            Log.v(TAG, "in makeServiceCall - try..");
            URL url = new URL (reqUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            Log.v("resp in makeService: ", in.toString());
            response = convertStreamToString(in);

        } catch (IOException e){
            e.printStackTrace();
        }
        Log.v(TAG, "response in makeSeriviceCall.."+response);
        return response;

    }

    private String convertStreamToString(InputStream is){
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null){
                sb.append(line).append('\n');
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public class MovieQueryTask extends AsyncTask<String, Void, ArrayList<MovieModel>> {

        @Override
        protected ArrayList<MovieModel> doInBackground(String... urls) {
            Log.v(TAG, "starting doInBackground...");
            String response = null;
            ArrayList<MovieModel> resultslist = new ArrayList<MovieModel>();

            try {
                URL url = new URL(urls[0]);
                HttpsURLConnection httpconn = (HttpsURLConnection) url.openConnection();
                httpconn.setRequestMethod("GET");
                InputStream in = new BufferedInputStream((httpconn.getInputStream()));
                response = convertStreamToString(in);
                Log.v(TAG, "json response: "+ response);

                JSONObject results = new JSONObject(response);

                Log.v(TAG, "results response: " + results);

                JSONArray movieResults = results.getJSONArray("results");
                for (int i=0; i<movieResults.length(); i++){
                    //ArrayList<String> movieArray = new ArrayList<String>();
                    JSONObject jsonobject = movieResults.getJSONObject(i);
                    String poster_path = jsonobject.getString("poster_path");
                    Log.v(TAG, "movieResults poster_path...:" + poster_path);
                    String title = jsonobject.getString("original_title");
                    String popularity = jsonobject.getString("popularity");
                    // completed:  add movie to movieArray
                    MovieModel movie = new MovieModel(title, popularity, poster_path);
                    resultslist.add(movie);

                }

            } catch (IOException e){
                e.printStackTrace();
            } catch(JSONException e){
                e.printStackTrace();
            }
            Log.v(TAG, "returning results in doInbackground.."+resultslist);
            return resultslist;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieModel> result){
            super.onPostExecute(result);
            Log.v(TAG, "what is result in onPostExecute.." +result);
            //ArrayList<MovieModel> AllMovies = new ArrayList<MovieModel>();

            if(result != null){

                movieAdapter.clear();
                for (int i=0; i<result.size();i++){
                    Log.v(TAG, "i is..."+i);
                    Log.v(TAG, "what is result in onPostExecute..:"+result.get(i).getMovieName());
                    movieAdapter.add(result.get(i));
                }

            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.v(TAG, "inside onCreate");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null){
            throw new Error("canot find toolbar..");
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        GridView gridView = (GridView) findViewById(R.id.movie_grid);
        AllMovies = new ArrayList<MovieModel>();
        // TODO 2:  What should resources be set to??
        movieAdapter = new CustomAdapter(MainActivity.this, 0, AllMovies);
        gridView.setAdapter(movieAdapter);
        // completed 4:  call asynctask here
        new MovieQueryTask().execute(MOVIE_API_URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.v(TAG,"inside onCreateOptionsMenu..");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_popular:
                return true;

            case R.id.action_toprated:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
