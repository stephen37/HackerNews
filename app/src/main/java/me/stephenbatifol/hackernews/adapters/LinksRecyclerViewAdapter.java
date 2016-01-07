package me.stephenbatifol.hackernews.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.stephenbatifol.hackernews.CommentsFragment;
import me.stephenbatifol.hackernews.R;

/**
 * Created by stephen on 27/12/15.
 */
public class LinksRecyclerViewAdapter extends RecyclerView.Adapter<LinksRecyclerViewAdapter.ViewHolder> {

    private JSONArray jsonArray;
    private static final String TAG = "LinksRecyViewAdapter";
    private List<JSONObject> listJsonObjects;
    private Context context;
    private WebView webViewBrowser;
    private ViewHolder viewHolder;
    private ArrayList<JSONArray> listIdParentsComments;
    private CommentsFragment commentsFragment;
    private ArrayList<String> listIdParentsString;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public RelativeLayout relativeLayout;
        public ImageButton mUpvoteImageButton;
        public TextView mLinkTextView;
        public TextView mTitleTextView;
        public TextView mNumberComments;
        public TextView mAuthor;
        public TextView mScoreTextView;
        public ListenerLinkTextView mListenerTextView;

        public ViewHolder(RelativeLayout v, TextView score, ImageButton imageButton, TextView title, TextView link, TextView nbComments, TextView authorAndTime,
                          ListenerLinkTextView listener) {
            super(v);
            relativeLayout = v;
            mScoreTextView = score;
            mUpvoteImageButton = imageButton;
            mTitleTextView = title;
            mLinkTextView = link;
            mNumberComments = nbComments;
            mAuthor = authorAndTime;
            mListenerTextView = listener;
            mLinkTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v instanceof TextView) {
                mListenerTextView.onClickListener((TextView) v);
            }
        }
    }

    public interface ListenerLinkTextView {
        void onClickListener(TextView textView);
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public LinksRecyclerViewAdapter(JSONArray jsonArray, Context context, List<JSONObject> listObjects, ArrayList<JSONArray> listIdParentsComments) {
        this.jsonArray = jsonArray;
        this.context = context;
        this.listJsonObjects = listObjects;
        this.listIdParentsComments = listIdParentsComments;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public LinksRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        // create a new view
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card, parent, false);


        //Initialize the elements of the view
        TextView scoreTextView = (TextView) v.findViewById(R.id.score_text_view);
        TextView titleTextView = (TextView) v.findViewById(R.id.title_text_view);
        TextView linkTextView = (TextView) v.findViewById(R.id.link_text_view);
        TextView commentsTextView = (TextView) v.findViewById(R.id.number_comments_text_view);
        TextView authorTextView = (TextView) v.findViewById(R.id.time_and_author_text_view);
        ImageButton imageButton = (ImageButton) v.findViewById(R.id.image_button_upvote);


        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v, scoreTextView, imageButton, titleTextView, linkTextView, commentsTextView, authorTextView,
                new ListenerLinkTextView() {
                    //Implement the link Listener on every card.
                    @Override
                    public void onClickListener(TextView textView) {
                        Uri uri = Uri.parse(textView.getText().toString());
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                        textView.getContext().startActivity(browserIntent);
                    }
                });

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        try {
            int score = Integer.parseInt(listJsonObjects.get(position).getString("score"));
            if (score == 0 || score == 1)
                holder.mScoreTextView.setText(listJsonObjects.get(position).getString("score") + "\npoint");
            else
                holder.mScoreTextView.setText(listJsonObjects.get(position).getString("score") + "\npoints");

            holder.mTitleTextView.setText(listJsonObjects.get(position).getString("title"));
            holder.mLinkTextView.setText(listJsonObjects.get(position).getString("url"));
            int nbComments = Integer.parseInt(listJsonObjects.get(position).getString("descendants"));
            if (nbComments == 0 || nbComments == 1)
                holder.mNumberComments.setText(listJsonObjects.get(position).getString("descendants") + " comment");
            else
                holder.mNumberComments.setText(listJsonObjects.get(position).getString("descendants") + " comments");
            holder.mAuthor.setText("By " + listJsonObjects.get(position).getString("by"));

            //Convert the Unix time given by the API in readable time.
            long unixTime = Long.parseLong(listJsonObjects.get(position).getString("time"));

            Date test = new java.util.Date(System.currentTimeMillis() - unixTime);
            //Log.d(TAG, "onBindViewHolder: " + test);

            holder.mUpvoteImageButton.setBackgroundResource(R.mipmap.ic_thumb_up_black_48dp);


            holder.mNumberComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: click sur les comments");

                    commentsFragment = new CommentsFragment();

                    Bundle bundle = new Bundle();

                    listIdParentsString = new ArrayList<>();

                    for (int i = 0; i < listIdParentsComments.size(); i++) {
                        listIdParentsString.add(listIdParentsComments.get(i).toString());
                    }
                    //Log.d(TAG, "onClick: list of ith element : " + listIdParentsString.get(position) + " position = " + position);
                    bundle.putString("ListComments", listIdParentsString.get(position));
                    FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
                    commentsFragment.setArguments(bundle);


                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.drawer_layout, commentsFragment).commit();

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listJsonObjects.size();
    }
}
