package com.xencosworks.ianguard;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Article>>{

    private static final String TAG = "MainActivity";
    private int pageNumber = 1;
    private String REQUEST_URL_NEW = "https://content.guardianapis.com/search?";
    private ArticleAdapter adapter;
    private static final int LOADER_ID = 1;
    private LoaderManager loaderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            ListView articleListView = findViewById(R.id.list_view);

            adapter = new ArticleAdapter(this, new ArrayList<Article>());

            articleListView.setAdapter(adapter);

            View emptyView = findViewById(R.id.empty_view);
            emptyView.setVisibility(View.GONE);

            articleListView.setEmptyView(emptyView);

            articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Article currentArticle = adapter.getItem(position);

                    Uri articleUri = Uri.parse(currentArticle.getWebUrl());

                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);

                    startActivity(websiteIntent);
                }
            });

            // Those lines are needed to start the Loader effect..
            loaderManager = getLoaderManager();
            loaderManager.initLoader(LOADER_ID, null, this);

            handleNavigationClicks();
        }else{
            handleEmptyViewAndLoading(1);
        }
    }

    @Override
    public Loader<ArrayList<Article>> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // retrieve the number that was stored by the preferences
        String artNum = sharedPreferences.getString(
                getString(R.string.settings_article_number_key),
                getString(R.string.settings_article_number_default));

        // retrieve the order setting that was stored by the preferences
        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        // handle cases where user enter a value not accepted by the server.
        if(Integer.parseInt(artNum)>200){
            artNum="200";
        }

        // start building the required url
        Uri baseUri = Uri.parse(REQUEST_URL_NEW);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("page-size", artNum);
        uriBuilder.appendQueryParameter("page", String.valueOf(pageNumber));
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("api-key", "test");

        String resultedUrl = uriBuilder.toString();

        Log.v(TAG, "current url"+ resultedUrl);
        return new ArticleLoader(this, resultedUrl);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Article>> loader, ArrayList<Article> articles) {
        handleEmptyViewAndLoading(0);
        // Clear previous data and re-add new data
        adapter.clear();
        if (articles != null && !articles.isEmpty()) {
            adapter.addAll(articles);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Article>> loader) {
        adapter.clear();
    }

    private void handleEmptyViewAndLoading(int state){
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        ImageView imageView = findViewById(R.id.empty_image);
        TextView textViewTitle = findViewById(R.id.empty_title);
        TextView textViewSub = findViewById(R.id.empty_sub);

        switch (state){
            case 0:
                // NOTES: setting resource programmatically insures the blinking effect (of waiting data to
                // load) won't happen, rather than just setting visibility (which doesn't work)
                imageView.setImageResource(R.drawable.ic_warning_black_24dp);
                textViewTitle.setText(R.string.empty_title);
                textViewSub.setText(R.string.empty_subtitle);
                break;
            case 1:
                imageView.setImageResource(R.drawable.ic_signal_cellular_connected_no_internet_2_bar_black_24dp);
                textViewTitle.setText(R.string.conn_title);
                textViewSub.setText(R.string.conn_sub);
                break;
        }
    }

    private void handleNavigationClicks(){
        ImageButton next = findViewById(R.id.btn_nav_next);
        final ImageButton prev = findViewById(R.id.btn_nav_prev);
        FloatingActionButton fab = findViewById(R.id.btn_nav_website);
        final TextView chipPageNumber = findViewById(R.id.chip_page_number);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageNumber+=1;
                chipPageNumber.setText(Integer.toString(pageNumber));
                loaderManager.restartLoader(LOADER_ID, null, MainActivity.this);
                Log.v(TAG, "current Page should be:" + pageNumber);

                // handle color of button programmatically
                PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.colorPrimary),
                        PorterDuff.Mode.SRC_ATOP);

                prev.setColorFilter(porterDuffColorFilter);
                prev.setClickable(true);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pageNumber!=1) {
                    // to ensure not going below zero
                    pageNumber -= 1;
                    chipPageNumber.setText(Integer.toString(pageNumber));
                    loaderManager.restartLoader(LOADER_ID, null, MainActivity.this);
                    Log.v(TAG, "current Page should be:" + pageNumber);

                    if (pageNumber == 1) {
                        // handle color of button programmatically
                        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.colorDivider),
                                PorterDuff.Mode.SRC_ATOP);

                        prev.setColorFilter(porterDuffColorFilter);
                        prev.setClickable(false);
                    }else {
                        // handle color of button programmatically
                        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.colorPrimary),
                                PorterDuff.Mode.SRC_ATOP);

                        prev.setColorFilter(porterDuffColorFilter);
                        prev.setClickable(true);
                    }
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri theGuardianHomePage = Uri.parse("https://www.theguardian.com/international");
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, theGuardianHomePage);
                startActivity(websiteIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
