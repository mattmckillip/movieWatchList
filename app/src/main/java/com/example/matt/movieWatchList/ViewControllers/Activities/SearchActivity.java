package com.example.matt.movieWatchList.viewControllers.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.TextView;

import com.example.matt.movieWatchList.Models.POJO.Movie;
import com.example.matt.movieWatchList.Models.POJO.QueryReturn;
import com.example.matt.movieWatchList.Models.POJO.Result;
import com.example.matt.movieWatchList.Models.Realm.JSONMovie;
import com.example.matt.movieWatchList.R;
import com.example.matt.movieWatchList.uitls.MovieAPI;
import com.example.matt.movieWatchList.uitls.SearchMoviesAPI;
import com.example.matt.movieWatchList.viewControllers.adapters.CastAdapter;
import com.example.matt.movieWatchList.viewControllers.adapters.SearchAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Matt on 6/7/2016.
 */
public class SearchActivity extends AppCompatActivity {
    Adapter adapterViewPager;
    private DrawerLayout mDrawerLayout;
    private SearchAdapter searchAdapter;
    private List<Result> searchResults;

    @BindView(R.id.search_recycler_view)
    RecyclerView searchRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);

        searchResults = new ArrayList<>();
        searchAdapter = new SearchAdapter(searchResults, getApplicationContext());
        RecyclerView.LayoutManager castLayoutManager = new LinearLayoutManager(getApplicationContext());
        searchRecyclerView.setLayoutManager(castLayoutManager);
        searchRecyclerView.setItemAnimator(new DefaultItemAnimator());
        searchRecyclerView.setAdapter(searchAdapter);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolber);
        setSupportActionBar(toolbar);

        for(int i = 0; i < toolbar.getChildCount(); i++){
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView){
                TextView tv = (TextView) view;
                Typeface titleFont = Typeface.
                        createFromAsset(this.getAssets(), "fonts/Lobster-Regular.ttf");
                if(tv.getText().equals(this.getTitle())){
                    tv.setTypeface(titleFont);
                    break;
                }
            }
        }
        // Create Navigation drawer and inlfate layout
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Set behavior of Navigation drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Set item in checked state
                        menuItem.setChecked(true);
                        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);

                        //Check to see which item was being clicked and perform appropriate action
                        switch (menuItem.getItemId()) {

                            //Replacing the main content with ContentFragment
                            case R.id.watch_list_menu_item:
                                Intent watchListIntent = new Intent(SearchActivity.this, MainActivity.class);
                                startActivity(watchListIntent);
                                return true;

                            case R.id.browse_menu_item:
                                Intent browseIntent = new Intent(SearchActivity.this, BrowseActivity.class);
                                startActivity(browseIntent);
                                return true;

                            case R.id.search_menu_item:
                                mDrawerLayout.closeDrawers();
                                return true;

                            case R.id.settings_menu_item:
                                Intent settingsIntent = new Intent(SearchActivity.this, SettingsActivity.class);
                                startActivity(settingsIntent);
                                return true;
                        }

                        // Closing drawer on item click
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });


        TextView navHeaderText = (TextView) findViewById(R.id.nav_header_text);
        Typeface font = Typeface.
                createFromAsset(this.getAssets(), "fonts/Lobster-Regular.ttf");
        //navHeaderText.setTypeface(font);
    }

    /*
     * Drawer Functions
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("SearchView", "onQueryTextSubmit");

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://api.themoviedb.org/3/search/movie/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                SearchMoviesAPI service = retrofit.create(SearchMoviesAPI.class);

                Call<QueryReturn> call = service.searchKeywords(query.replaceAll(" ", "+"));

                call.enqueue(new Callback<QueryReturn>() {
                    @Override
                    public void onResponse(retrofit.Response<QueryReturn> response, Retrofit retrofit) {
                        Log.d("getMovie()", "Callback Success");
                        List<Result> results = response.body().getResults();

                        searchRecyclerView.setAdapter(new SearchAdapter(results, getApplicationContext()));
                        searchRecyclerView.setFocusable(false);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d("getMovie()", "Callback Failure");
                    }
                });

                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });
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
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}