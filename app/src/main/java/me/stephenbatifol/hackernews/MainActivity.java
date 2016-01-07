package me.stephenbatifol.hackernews;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Network.VolleySingleton;
import me.stephenbatifol.hackernews.adapters.LinksRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CommentsFragment.OnListFragmentInteractionListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final String TAG = "MainActivity";
    public final static String BASIC_URL_ITEM = "https://hacker-news.firebaseio.com/v0/item/";
    public final static String TOP_STORIES_URL = "https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty";
    private List<JSONObject> listJsonObjects = new ArrayList<>();
    private ArrayList<JSONArray> listJsonParentsComments = new ArrayList<>();

    private JSONArray arrayResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_main);

        //Improve the performance of the recycler view
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        //Item url : https://hacker-news.firebaseio.com/v0/item/10801368.json?print=pretty

        RequestQueue requestQueue = VolleySingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        JsonArrayRequest arrayRequest = new JsonArrayRequest(
                Request.Method.GET, TOP_STORIES_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray idResponse) {
                arrayResponse = idResponse;

                mAdapter = new LinksRecyclerViewAdapter(arrayResponse, MainActivity.this, listJsonObjects, listJsonParentsComments);
                //Specify the adapter of the RecyclerView
                mRecyclerView.setAdapter(mAdapter);
                if (arrayResponse != null) {
                    try {
                        for (int i = 0; i < 20; i++) {
                            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, BASIC_URL_ITEM + idResponse.getString(i) + ".json?print=pretty", null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            //Log.d(TAG, "onResponse: " + response.toString());
                                            listJsonObjects.add(response);
                                            try {
                                                JSONArray kidsArray = new JSONArray(response.get("kids").toString());
                                                if (kidsArray.length() != 0) {

                                                    listJsonParentsComments.add(kidsArray);
                                                    mAdapter.notifyDataSetChanged();
                                                }
                                                Log.d(TAG, "onResponse: Kids in JSon" + kidsArray);
                                            } catch (JSONException e) {
                                                Log.e(TAG, "onResponse: kidsArray is empty");
                                            }
                                            mAdapter.notifyDataSetChanged();

                                        }

                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(TAG, "onErrorResponse: " + error);
                                }
                            });
                            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(objectRequest);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    Log.d(TAG, "onCreate: ArrayRequest is null");
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(arrayRequest);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCommentsFragmentClick(int id) {
        Log.d(TAG, "onCommentsFragmentClick: click");

    }
}
