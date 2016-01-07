package me.stephenbatifol.hackernews.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Network.VolleySingleton;
import me.stephenbatifol.hackernews.CommentsFragment.OnListFragmentInteractionListener;
import me.stephenbatifol.hackernews.R;

/**
 * {@link RecyclerView.Adapter} that can display an item and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<CommentsRecyclerViewAdapter.ViewHolder> {

    private final JSONArray arrayIdParentsComments;
    private final OnListFragmentInteractionListener mListener;
    private static final String TAG = "CommentsRecyViewAdapter";
    private static final String BASE_LINK = "https://hacker-news.firebaseio.com/v0/item/";
    private static final String END_LINK = ".json?print=pretty";
    private String comment;
    String text = "";
    private Activity mParentActivity;

    public CommentsRecyclerViewAdapter(JSONArray items, OnListFragmentInteractionListener listener, Activity activity) {
        arrayIdParentsComments = items;
        Log.d(TAG, "CommentsRecyclerViewAdapter() called with: " + "items = [" + items + "], listener = [" + listener + "], activity = [" + activity + "]");
        mListener = listener;
        mParentActivity = activity;
        getAllCommentsMaster();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mItem = "This is a test";
        try {
            //holder.mAuthor.setText(arrayIdParentsComments.get(position).toString());

            getAllComments(position, holder);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.mContentView.setText("test2");


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {


                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    //mListener.onCommentsFragmentClick(holder.);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        //return arrayIdParentsComments.size();
        return arrayIdParentsComments.length();
    }


    private ArrayList<String> getAllCommentsMaster() {
        ArrayList<String> listComments = new ArrayList<>();
        /*try {
            for (int i = 0; i < arrayIdParentsComments.length(); i++) {
                getAllComments(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        */

        return listComments;
    }

    private String getAllComments(int position, final ViewHolder holder) throws JSONException {

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET, BASE_LINK + arrayIdParentsComments.get(position) + END_LINK,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, "onResponse() returned: " + response.get("text").toString());
                    text = response.get("text").toString();

                    holder.mAuthor.setText(text);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleySingleton.getInstance(mParentActivity).addToRequestQueue(objectRequest);
        return text;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mAuthor;
        public final TextView mContentView;
        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAuthor = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
