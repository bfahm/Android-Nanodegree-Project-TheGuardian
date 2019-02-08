package com.xencosworks.ianguard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Bola on 2/7/2019.
 */

public class ArticleAdapter extends ArrayAdapter<Article>{

    public ArticleAdapter(Context context, List<Article> articles) {
        super(context, 0, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Article currentArticle = getItem(position);

        TextView titleTv = listItemView.findViewById(R.id.item_title);
        TextView authorTv = listItemView.findViewById(R.id.item_author);
        TextView timeTv = listItemView.findViewById(R.id.item_date);
        TextView sectionTag = listItemView.findViewById(R.id.item_section);

        if(currentArticle!=null){
            titleTv.setText(currentArticle.getWebTitle());
            timeTv.setText(formatDate(currentArticle.getDatePublished())+" "+formatTime(currentArticle.getDatePublished()));
            sectionTag.setText(currentArticle.getSectionName());
            authorTv.setText(currentArticle.getAuthor());
        }
        return listItemView;
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }
}