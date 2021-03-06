package com.example.matt.bingeList.uitls;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.matt.bingeList.R;
import com.example.matt.bingeList.uitls.Enums.ThemeEnum;
import com.example.matt.bingeList.viewControllers.activities.SearchActivity;
import com.example.matt.bingeList.viewControllers.activities.SettingsActivity;
import com.example.matt.bingeList.viewControllers.activities.StatisticsActivity;
import com.example.matt.bingeList.viewControllers.activities.movies.BrowseMoviesActivity;
import com.example.matt.bingeList.viewControllers.activities.movies.WatchlistActivity;
import com.example.matt.bingeList.viewControllers.activities.shows.TVShowBrowseActivity;
import com.example.matt.bingeList.viewControllers.activities.shows.YourShowsActivity;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

/**
 * Created by Matt on 6/15/2016.
 */
public class DrawerHelper {
    //save our header or result
    private Drawer result = null;

    public Drawer GetDrawer(final Activity currentActivity, Toolbar toolbar, Bundle savedInstanceState) {
        int backgroundColor = Color.WHITE;
        int primaryTextColor = Color.DKGRAY;
        int secondaryTextColor = Color.GRAY;
        int iconColor = Color.GRAY;

        if(PreferencesHelper.getTheme(currentActivity.getApplicationContext()) == ThemeEnum.NIGHT_THEME){
            backgroundColor = Color.DKGRAY;
            primaryTextColor = Color.LTGRAY;
            secondaryTextColor = Color.GRAY;
            iconColor = Color.GRAY;
        }

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(currentActivity)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withHeader(R.layout.nav_header)
                .addDrawerItems(
                        new ExpandableDrawerItem().withName(R.string.drawer_item_show_header).withIcon(MaterialDesignIconic.Icon.gmi_tv_alt_play).withIdentifier(1).withSelectable(false).withTextColor(primaryTextColor).withIconColor(iconColor).withSubItems(
                                new SecondaryDrawerItem().withName(R.string.drawer_item_show_watchlist).withLevel(2).withIcon(CommunityMaterial.Icon.cmd_television_guide).withIdentifier(1000).withTextColor(secondaryTextColor).withIconColor(iconColor),
                                new SecondaryDrawerItem().withName(R.string.drawer_item_show_browse).withLevel(2).withIcon(GoogleMaterial.Icon.gmd_whatshot).withIdentifier(1001).withTextColor(secondaryTextColor).withIconColor(iconColor)
                        ),
                        new ExpandableDrawerItem().withName(R.string.drawer_item_movie_header).withIcon(CommunityMaterial.Icon.cmd_movie).withIdentifier(2).withSelectable(false).withTextColor(primaryTextColor).withIconColor(iconColor).withSubItems(
                                new SecondaryDrawerItem().withName(R.string.drawer_item_movie_watchlist).withLevel(2).withIcon(MaterialDesignIconic.Icon.gmi_tv_list).withIdentifier(2000).withTextColor(secondaryTextColor).withIconColor(iconColor),
                                new SecondaryDrawerItem().withName(R.string.drawer_item_movie_browse).withLevel(2).withIcon(GoogleMaterial.Icon.gmd_whatshot).withIdentifier(2001).withTextColor(secondaryTextColor).withIconColor(iconColor)
                        ),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_stats).withIcon(GoogleMaterial.Icon.gmd_insert_chart).withTextColor(primaryTextColor).withIdentifier(3001).withIconColor(iconColor),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_search).withIcon(GoogleMaterial.Icon.gmd_search).withTextColor(primaryTextColor).withIdentifier(3000).withIconColor(iconColor),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_about).withIcon(GoogleMaterial.Icon.gmd_info).withIdentifier(4000).withSelectable(false).withTextColor(secondaryTextColor).withIconColor(iconColor),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(4001).withTextColor(secondaryTextColor).withIconColor(iconColor)
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //check if the drawerItem is set.
                        //there are different reasons for the drawerItem to be null
                        //--> click on the header
                        //--> click on the footer
                        //those items don't contain a drawerItem

                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 1000) {
                                intent = new Intent(currentActivity, YourShowsActivity.class);
                            } else if (drawerItem.getIdentifier() == 1001) {
                                intent = new Intent(currentActivity, TVShowBrowseActivity.class);
                            } else if (drawerItem.getIdentifier() == 2000) {
                                intent = new Intent(currentActivity, WatchlistActivity.class);
                            } else if (drawerItem.getIdentifier() == 2001) {
                                intent = new Intent(currentActivity, BrowseMoviesActivity.class);
                            } else if (drawerItem.getIdentifier() == 3000) {
                                intent = new Intent(currentActivity, SearchActivity.class);
                            } else if (drawerItem.getIdentifier() == 3001) {
                                intent = new Intent(currentActivity, StatisticsActivity.class);
                            } else if (drawerItem.getIdentifier() == 4000) {
                                intent = null;
                                //intent = new Intent(currentActivity, ExpandableExampleActivity.class);
                            } else if (drawerItem.getIdentifier() == 4001) {
                                intent = new Intent(currentActivity, SettingsActivity.class);
                            }
                            if (intent != null) {
                                currentActivity.startActivity(intent);
                                //WatchlistActivity.this.startActivity(intent);
                            }
                        }

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .withSliderBackgroundColor(backgroundColor)
                .build();

        result.getAdapter().expand(1);
        result.getAdapter().expand(4);

        if (currentActivity.getClass().equals(YourShowsActivity.class)) {
            result.setSelection(1000, false);
        } else if (currentActivity.getClass().equals(TVShowBrowseActivity.class)) {
            result.setSelection(1001, false);
        } else if (currentActivity.getClass().equals(WatchlistActivity.class)) {
            result.setSelection(2000, false);
        } else if (currentActivity.getClass().equals(BrowseMoviesActivity.class)) {
            result.setSelection(2001, false);
        } else if (currentActivity.getClass().equals(SearchActivity.class)) {
            result.setSelection(3000, false);
        } else if (currentActivity.getClass().equals(StatisticsActivity.class)) {
            result.setSelection(3001, false);
        } else if (currentActivity.getClass().equals(SettingsActivity.class)) {
            result.setSelection(4001, false);
        }

        return result;
    }

}
