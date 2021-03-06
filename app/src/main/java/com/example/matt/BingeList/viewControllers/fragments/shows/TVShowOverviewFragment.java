package com.example.matt.bingeList.viewControllers.fragments.shows;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.matt.bingeList.BuildConfig;
import com.example.matt.bingeList.models.Cast;
import com.example.matt.bingeList.models.Credits;
import com.example.matt.bingeList.models.Crew;
import com.example.matt.bingeList.models.NetflixRouletteResponse;
import com.example.matt.bingeList.models.shows.TVShow;
import com.example.matt.bingeList.MyApplication;
import com.example.matt.bingeList.R;
import com.example.matt.bingeList.models.shows.TVShowQueryReturn;
import com.example.matt.bingeList.models.shows.TVShowResult;
import com.example.matt.bingeList.uitls.API.NetflixAPI;
import com.example.matt.bingeList.uitls.API.TVShowAPI;
import com.example.matt.bingeList.uitls.Enums.NetflixStreaming;
import com.example.matt.bingeList.viewControllers.activities.CastActivity;
import com.example.matt.bingeList.viewControllers.activities.shows.SimilarShowsActivity;
import com.example.matt.bingeList.viewControllers.adapters.shows.BrowseTVShowsAdapter;
import com.example.matt.bingeList.viewControllers.adapters.CastAdapter;
import com.example.matt.bingeList.viewControllers.adapters.CrewAdapter;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Matt on 6/14/2016.
 */
public class TVShowOverviewFragment extends Fragment {
    private static final String TAG = TVShowOverviewFragment.class.getSimpleName();
    private static final int NUMBER_OF_CREW_TO_DISPLAY = 5;
    private static final int NUMBER_OF_SIMILAR_SHOWS_TO_DISPLAY = 5;
    private FragmentActivity listener;
    private int mShowId;
    private int vibrantColor;
    private int mutedColor;
    private Realm mUiRealm;
    private TVShow mShow;
    private Context mContext;
    private Credits mCredits;
    private RealmList<Cast> mCast = new RealmList<>();
    private CastAdapter castAdapter;
    private RealmList<TVShow> mSimilarShowList = new RealmList<>();
    private BrowseTVShowsAdapter similarMovieAdapter;
    private int mNetflixId;

    @BindView(R.id.scroll_view)
    NestedScrollView scroll_view;

    @BindView(R.id.rating)
    RatingBar stars;

    @BindView(R.id.plot_title)
    TextView plotTitle;

    @BindView(R.id.cast_title)
    TextView castTitle;

    @BindView(R.id.similar_shows_title)
    TextView similarShowsTitle;

    @BindView(R.id.overview_title)
    TextView overviewTitle;

    @BindView(R.id.runtime)
    TextView runtime;

    @BindView(R.id.user_rating)
    TextView userRating;

    @BindView(R.id.streaming_header)
    TextView mStreamingHeader;

    @BindView(R.id.netflix_image)
    ImageView mNetflixImage;

    @BindView(R.id.more_info)
    LinearLayout layout;

    @BindView(R.id.expand_text_view)
    ExpandableTextView plot;

    @BindView(R.id.cast_recycler_view)
    RecyclerView castRecyclerView;

    @BindView(R.id.similar_shows_recycler_view)
    RecyclerView similarShowsRecyclerView;

    @BindView(R.id.see_more_cast)
    Button seeMoreCastButton;

    @BindView(R.id.see_more_shows)
    Button seeMoreShowsButton;

    @OnClick(R.id.see_more_cast)
    public void seeMoreCast(View view) {
        Intent intent = new Intent(mContext, CastActivity.class);
        intent.putExtra("id", mShowId);
        intent.putExtra("title", mShow.getName());
        intent.putExtra("isMovie", false);
        startActivity(intent);
    }


    @OnClick(R.id.see_more_shows)
    public void setSeeMoreShowsButton(View view) {
        Intent intent = new Intent(mContext, SimilarShowsActivity.class);
        intent.putExtra(mContext.getString(R.string.showId), mShowId);
        intent.putExtra("vibrantColor", vibrantColor);

        startActivity(intent);
    }

    @OnClick(R.id.netflix_image)
    public void openNetflix(View view) {
        Uri uri = Uri.parse(mContext.getString(R.string.netflix_base_url) + mNetflixId);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            this.listener = (FragmentActivity) context;
        }
    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShowId = getArguments().getInt(getContext().getString(R.string.showId), 0);
        Log.d(getContext().getString(R.string.showId), Integer.toString(mShowId));
        vibrantColor = getArguments().getInt("vibrantColor", 0);
        mutedColor = getArguments().getInt("mutedColor", 0);
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.tvshow_overview, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
    }

    // This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mUiRealm = ((MyApplication) getActivity().getApplication()).getUiRealm();
        mShow = mUiRealm.where(TVShow.class).equalTo("id", mShowId).findFirst();
        mContext = getContext();

        setAdapters();

        if (mShow == null) {


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(mContext.getString(R.string.tv_show_base_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            TVShowAPI service = retrofit.create(TVShowAPI.class);

            Call<TVShow> call = service.getTVShow(Integer.toString(mShowId));

            call.enqueue(new Callback<TVShow>() {
                @Override
                public void onResponse(Call<TVShow> call, Response<TVShow> response) {
                    mShow = response.body();
                    //mShow.setBackdropPath("https://image.tmdb.org/t/p/w500//" + mShow.getBackdropPath());
                    if (mShow != null) {
                        updateUI();
                    } else {
                        Snackbar.make(castRecyclerView, "Error loading data", Snackbar.LENGTH_SHORT);
                    }
                }

                @Override
                public void onFailure(Call<TVShow> call, Throwable t) {
                    Log.d("getMovie()", "Callback Failure");
                }
            });
        } else {
            updateUI();
        }
    }

    private void updateUI() {
        plot.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {

            }
        });

        castRecyclerView.setFocusable(false);
        similarShowsRecyclerView.setFocusable(false);

        setData();
        setColors();
        loadCredits();
        loadSimilarShows();
    }

    //HELPER METHODS
    private void setColors() {
        //Color titles
        overviewTitle.setTextColor(vibrantColor);
        plotTitle.setTextColor(vibrantColor);
        castTitle.setTextColor(vibrantColor);
        similarShowsTitle.setTextColor(vibrantColor);
        LayerDrawable starProgressDrawable = (LayerDrawable) stars.getProgressDrawable();
        starProgressDrawable.getDrawable(2).setColorFilter(mutedColor, PorterDuff.Mode.SRC_ATOP);
        starProgressDrawable.getDrawable(1).setColorFilter(mutedColor, PorterDuff.Mode.SRC_ATOP);
        seeMoreCastButton.setTextColor(mutedColor);
        seeMoreShowsButton.setTextColor(mutedColor);
    }

    private void setData() {
        mNetflixImage.setVisibility(View.GONE);
        mStreamingHeader.setVisibility(View.GONE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://netflixroulette.net/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NetflixAPI service = retrofit.create(NetflixAPI.class);

        Call<NetflixRouletteResponse> call = service.checkNetflix(mShow.getExternalIds().getImdbId());

        call.enqueue(new Callback<NetflixRouletteResponse>() {
            @Override
            public void onResponse(Call<NetflixRouletteResponse> call, Response<NetflixRouletteResponse> response) {
                Log.d(TAG, response.raw().toString());
                if (response.isSuccessful()) {
                    if (response.body().getNetflixId() != null && !response.body().getNetflixId().equals("null")) {
                        mNetflixId = response.body().getNetflixId();
                        mNetflixImage.setVisibility(View.VISIBLE);
                        mStreamingHeader.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<NetflixRouletteResponse> call, Throwable t) {}
        });

        // Add data
        plot.setText(mShow.getOverview());
        stars.setRating(mShow.getVoteAverage().floatValue());
        runtime.setText(Integer.toString(mShow.getNumberOfSeasons()) + " seasons");
        userRating.setText(Double.toString(mShow.getVoteAverage()) + "/10");
    }

    private void loadSimilarShows() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mContext.getString(R.string.tv_show_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TVShowAPI service = retrofit.create(TVShowAPI.class);

        final Call<TVShowQueryReturn> similarMoviesCall = service.getSimilarShows(Integer.toString(mShowId));

        similarMoviesCall.enqueue(new Callback<TVShowQueryReturn>() {
            @Override
            public void onResponse(Call<TVShowQueryReturn> call, Response<TVShowQueryReturn> response) {
                if (response.isSuccessful()) {
                    List<TVShowResult> tempSimilarShows = response.body().getResults();

                    mSimilarShowList = new RealmList<>();
                    for (int i = 0; i < tempSimilarShows.size() || i < NUMBER_OF_SIMILAR_SHOWS_TO_DISPLAY; i++) {
                        TVShow tvShow = new TVShow();
                        tvShow.setName(tempSimilarShows.get(i).getName());
                        tvShow.setId(tempSimilarShows.get(i).getId());
                        tvShow.setOverview(tempSimilarShows.get(i).getOverview());
                        tvShow.setBackdropPath(mContext.getString(R.string.image_base_url) + mContext.getString(R.string.image_size_w500) + tempSimilarShows.get(i).getBackdropPath());
                        mSimilarShowList.add(tvShow);
                    }
                    similarShowsRecyclerView.setAdapter(new BrowseTVShowsAdapter(mSimilarShowList, mContext, mUiRealm));
                    similarShowsRecyclerView.setFocusable(false);
                }
            }

            @Override
            public void onFailure(Call<TVShowQueryReturn> call, Throwable t) {
                if (BuildConfig.DEBUG) {
                    Log.d("getSimilarMovies()", "No Response");
                }
            }
        });
    }

    private void loadCredits() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "loadCredits()");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mContext.getString(R.string.tv_show_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TVShowAPI service = retrofit.create(TVShowAPI.class);

        Call<Credits> call = service.getCredits(Integer.toString(mShowId));
        call.enqueue(new Callback<Credits>() {
            @Override
            public void onResponse(Call<Credits> call, Response<Credits> response) {
                mCredits = response.body();
                mCast = mCredits.getCast();

                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Credits - " + mCredits.toString());
                    Log.d(TAG, "PersonCast - " + mCast.toString());
                }

                Integer castSize = Math.min(NUMBER_OF_CREW_TO_DISPLAY, mCast.size());

                // Populate cast and crew recycler views
                castRecyclerView.setAdapter(new CastAdapter(mCast, mContext, castSize));

                castRecyclerView.setFocusable(false);
            }

            @Override
            public void onFailure(Call<Credits> call, Throwable t) {
                if (BuildConfig.DEBUG) {
                    Log.d("GetCredits()", "Callback Failure");
                }
            }
        });
    }

    private void setAdapters() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "setAdapters()");
        }

        // PersonCast recycler view
        castAdapter = new CastAdapter(mCast, mContext, NUMBER_OF_CREW_TO_DISPLAY);
        RecyclerView.LayoutManager castLayoutManager = new LinearLayoutManager(mContext);
        castRecyclerView.setLayoutManager(castLayoutManager);
        castRecyclerView.setItemAnimator(new DefaultItemAnimator());
        castRecyclerView.setAdapter(castAdapter);

        // Similar Moves recycler view
        similarMovieAdapter = new BrowseTVShowsAdapter(mSimilarShowList, mContext, mUiRealm);
        RecyclerView.LayoutManager similaryMovieLayoutManager = new LinearLayoutManager(mContext);
        similarShowsRecyclerView.setLayoutManager(similaryMovieLayoutManager);
        similarShowsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        similarShowsRecyclerView.setAdapter(similarMovieAdapter);
    }

}
