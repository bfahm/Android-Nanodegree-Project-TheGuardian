package com.xencosworks.ianguard;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.ArrayList;

/**
 * Created by Bola on 2/8/2019.
 */

public class ArticleLoader extends AsyncTaskLoader<ArrayList<Article>> {
    private String incomingUrl;

    public ArticleLoader(Context context, String url) {
        super(context);
        incomingUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Article> loadInBackground() {
        // Equivalent to doInBackground of the simple AsyncTask class

        // Make sure that there are urls, and the first (that will be used) url is not null
        // if that, return early.
        if(incomingUrl==null){
            return null;
        }

        return QueryUtils.extractArticles(incomingUrl);
    }
}
