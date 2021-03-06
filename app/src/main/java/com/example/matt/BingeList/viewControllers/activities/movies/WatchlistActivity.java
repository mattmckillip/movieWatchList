/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.matt.bingeList.viewControllers.activities.movies;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.matt.bingeList.BuildConfig;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.DrawerHelper;
import com.example.matt.bingeList.uitls.Enums.MovieSort;
import com.example.matt.bingeList.uitls.Enums.ThemeEnum;
import com.example.matt.bingeList.uitls.Enums.ViewType;
import com.example.matt.bingeList.uitls.PreferencesHelper;
import com.example.matt.bingeList.viewControllers.fragments.movies.MovieWatchListFragment;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.Drawer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Provides UI for the main screen.
 */
public class WatchlistActivity extends AppCompatActivity {
    private static final String TAG = WatchlistActivity.class.getSimpleName();
    private Adapter mAdapterViewPager;
    private Drawer mNavigationDrawer;
    private int mViewPagerPosition;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.tabs)
    TabLayout tabs;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Iconics.init(getApplicationContext());
        Iconics.registerFont(new GoogleMaterial());
        if(PreferencesHelper.getTheme(getApplicationContext()) == ThemeEnum.NIGHT_THEME){
            setTheme(R.style.DarkAppTheme_Base);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_activity);

        ButterKnife.bind(this);

        // Adding Toolbar to Main screen
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Movie Watchlist");

        // Setting ViewPager for each Tabs
        setupViewPager(viewPager);

        mViewPagerPosition = 0;

        // Set Tabs inside Toolbar
        tabs.setupWithViewPager(viewPager);
        if( tabs.getTabAt(0) != null && tabs.getTabAt(1) != null) {
            tabs.getTabAt(0).setIcon(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_dvr).sizeDp(24).color(Color.WHITE));
            tabs.getTabAt(1).setIcon(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_playlist_add_check).sizeDp(24).color(Color.WHITE));
        }



        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_menu).sizeDp(16).color(Color.WHITE));
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Create Navigation drawer
        mNavigationDrawer = new DrawerHelper().GetDrawer(this, toolbar, savedInstanceState);

        // Adding Floating Action Button to bottom right of main view
        IconicsDrawable search = new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_add).sizeDp(16).color(Color.WHITE);
        fab.setImageDrawable(search);
    }

    @Override
    protected void onResume() {
        super.onResume();

        MovieWatchListFragment movieWatchListFragment = (MovieWatchListFragment) mAdapterViewPager.getItem(mViewPagerPosition);
        if (movieWatchListFragment != null) {
            movieWatchListFragment.notifyAdapter();
        }
    }

    @OnClick(R.id.fab)
    public void startMovieBrowseActivity(View view) {
        // TODO submit data to server...
        startActivity( new Intent(this, BrowseMoviesActivity.class));
    }

    // Add Fragments to Tabs
    private void setupViewPager(final ViewPager viewPager) {
        mAdapterViewPager = new Adapter(getSupportFragmentManager());

        Bundle watchedMoviesBundle = new Bundle();
        watchedMoviesBundle.putInt("watched", 1);
        MovieWatchListFragment watchedMovies = new MovieWatchListFragment();
        watchedMovies.setArguments(watchedMoviesBundle);

        Bundle watchListMoviesBundle = new Bundle();
        watchListMoviesBundle.putInt("watched", 0);
        MovieWatchListFragment watchListMovies = new MovieWatchListFragment();
        watchListMovies.setArguments(watchListMoviesBundle);

        mAdapterViewPager.addFragment(watchListMovies, "Watchlist");
        mAdapterViewPager.addFragment(watchedMovies, "Watched");
        viewPager.setAdapter(mAdapterViewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mViewPagerPosition = position;
                MovieWatchListFragment movieWatchListFragment = (MovieWatchListFragment) mAdapterViewPager.getItem(position);
                movieWatchListFragment.notifyAdapter();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_movie_sort, menu);

        menu.findItem(R.id.action_sort).setIcon(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_sort).sizeDp(16).color(Color.WHITE));

        int viewMode = PreferencesHelper.getRecyclerviewViewType(getApplicationContext());
        if (viewMode == ViewType.CARD){
            menu.findItem(R.id.card_view).setChecked(true);
        } else if (viewMode == ViewType.COMPACT_CARD){
            menu.findItem(R.id.compact_view).setChecked(true);
        } else if (viewMode == ViewType.LIST){
            menu.findItem(R.id.list_view).setChecked(true);
        }

        int theme = PreferencesHelper.getTheme(getApplicationContext());
        if (theme == ThemeEnum.DAY_THEME){
            menu.findItem(R.id.light_theme).setChecked(true);
        } else if (theme == ThemeEnum.NIGHT_THEME){
            menu.findItem(R.id.dark_theme).setChecked(true);
        }

        int movieSort = PreferencesHelper.getMovieSort(getApplicationContext());
        if (movieSort == MovieSort.DATE_ADDED){
            menu.findItem(R.id.date_added_sort).setChecked(true);
        } else if (movieSort == MovieSort.RUNTIME){
            menu.findItem(R.id.rating_sort).setChecked(true);
        } else if (movieSort == MovieSort.RATING){
            menu.findItem(R.id.rating_sort).setChecked(true);
        }else if (movieSort == MovieSort.ALPHABETICAL){
            menu.findItem(R.id.alphabetical_sort).setChecked(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        ArrayList<MovieWatchListFragment> movieWatchListFragments = new ArrayList<>();
        movieWatchListFragments.add((MovieWatchListFragment) mAdapterViewPager.getItem(0));
        movieWatchListFragments.add((MovieWatchListFragment) mAdapterViewPager.getItem(1));

        switch (item.getItemId()) {

            case R.id.action_settings:
                return true;

            case R.id.card_view:
                PreferencesHelper.setRecyclerviewViewType(ViewType.CARD, getApplicationContext());
                viewPager.setAdapter(mAdapterViewPager);
                viewPager.setCurrentItem(mViewPagerPosition);
                item.setChecked(true);
                tabs.setupWithViewPager(viewPager);
                setTabDrawables();

                return true;

            case R.id.compact_view:
                PreferencesHelper.setRecyclerviewViewType(ViewType.COMPACT_CARD, getApplicationContext());
                viewPager.setAdapter(mAdapterViewPager);
                viewPager.setCurrentItem(mViewPagerPosition);
                item.setChecked(true);
                tabs.setupWithViewPager(viewPager);
                setTabDrawables();

                return true;

            case R.id.list_view:
                PreferencesHelper.setRecyclerviewViewType(ViewType.LIST, getApplicationContext());
                mAdapterViewPager.notifyDataSetChanged();
                viewPager.setAdapter(mAdapterViewPager);
                viewPager.setCurrentItem(mViewPagerPosition);
                item.setChecked(true);
                tabs.setupWithViewPager(viewPager);
                setTabDrawables();

                return true;

            case R.id.action_sort:
                return true;

            case R.id.light_theme:
                PreferencesHelper.setTheme(ThemeEnum.DAY_THEME, getApplicationContext());
                item.setChecked(true);
                finish();
                startActivity(getIntent());;

                return true;

            case R.id.dark_theme:
                PreferencesHelper.setTheme(ThemeEnum.NIGHT_THEME, getApplicationContext());
                item.setChecked(true);
                finish();
                startActivity(getIntent());;

                return true;

            case android.R.id.home:
                mNavigationDrawer.openDrawer();

                return true;

            // Sorting
            case R.id.date_added_sort:
                PreferencesHelper.setMovieSort(MovieSort.DATE_ADDED, getApplicationContext());
                for (MovieWatchListFragment fragment : movieWatchListFragments) {
                    if (fragment != null) {
                        fragment.sort(MovieSort.DATE_ADDED);
                    }
                }
                item.setChecked(true);

                return true;

            case R.id.alphabetical_sort:
                PreferencesHelper.setMovieSort(MovieSort.ALPHABETICAL, getApplicationContext());
                for (MovieWatchListFragment fragment : movieWatchListFragments) {
                    if (fragment != null) {
                        fragment.sort(MovieSort.ALPHABETICAL);
                    }
                }
                item.setChecked(true);

                return true;

            case R.id.rating_sort:
                PreferencesHelper.setMovieSort(MovieSort.RATING, getApplicationContext());

                for (MovieWatchListFragment fragment : movieWatchListFragments) {
                    if (fragment != null) {
                        fragment.sort(MovieSort.RATING);
                    }
                }
                item.setChecked(true);

                return true;

            case R.id.runtime_sort:
                PreferencesHelper.setMovieSort(MovieSort.RUNTIME, getApplicationContext());

                for (MovieWatchListFragment fragment : movieWatchListFragments) {
                    if (fragment != null) {
                        fragment.sort(MovieSort.RUNTIME);
                    }
                }
                item.setChecked(true);

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void setTabDrawables() {
        // Set Tabs inside Toolbar
        if( tabs.getTabAt(0) != null && tabs.getTabAt(1) != null) {
            tabs.getTabAt(0).setIcon(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_dvr).sizeDp(24).color(Color.WHITE));
            tabs.getTabAt(1).setIcon(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_playlist_add_check).sizeDp(24).color(Color.WHITE));
        }
    }

    @Override
    public void onBackPressed() {
        if (this.mNavigationDrawer.isDrawerOpen()) {
            this.mNavigationDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
