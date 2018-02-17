package com.manav.edfora;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    Myadapter myadapter;
    ArrayList<Model> model=new ArrayList<Model>();
    ProgressBar progressBar;
    private static final String URL_TO_READ="http://starlord.hackerearth.com/studio";

    //pagination
    private boolean loading = true;
    TextView history,playlist;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        history=(TextView)findViewById(R.id.history);
        playlist=(TextView)findViewById(R.id.playlist);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        progressBar=(ProgressBar)findViewById(R.id.progress);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        if(!isNetworkAvailable())
        {
            Toast.makeText(this,"network not available Kindly connect to network",Toast.LENGTH_LONG).show();
        }
        else {
            getData();
        }

        //pagination
      /*  recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;

                            getData();
                        }
                    }
                }
            }
        });*/


        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,YourPlaylist.class);
                startActivity(intent);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,Histroy.class);
                startActivity(intent);
            }
        });
    }


    //creating jsonarray request using volley

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        JsonArrayRequest Request = new JsonArrayRequest(URL_TO_READ, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        //Dismissing progress dialog
                        progressBar.setVisibility(View.GONE);

                        //calling method to parse json array
                       parse(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       // VolleyLog.d(TAG, "Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });

        //adding request to request queue
      AppController.getInstance(this).addtoRequestQueue(Request);
    }


   // parsing the response

    public void parse(JSONArray response) {

        for (int i = 0; i < response.length(); i++) {

            //creating data for adapter
            Model add=new Model();
            JSONObject json=null;
            try {
                json=response.getJSONObject(i);
                add.setSong(json.getString("song"));
                add.setUrl(json.getString("url"));
                add.setArtists(json.getString("artists"));
                add.setCover_image(json.getString("cover_image"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            model.add(add);
        }
        myadapter=new Myadapter(model,this,false);
        recyclerView.setAdapter(myadapter);
    }

 //option menu for search bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_item,menu);
        MenuItem menuItem =menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);


        return true;
    }


    //search functionality for custom recycler view
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText=newText.toLowerCase();
        ArrayList<Model> al=new ArrayList<Model>();
        for(Model Data: model)
        {
            String name=Data.getSong().toLowerCase();
            if(name.contains(newText))
            {
                al.add(Data);
            }
        }
        myadapter.setFilter(al);
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
