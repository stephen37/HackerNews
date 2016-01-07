package Network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by stephen on 27/12/15.
 */
public class VolleySingleton {

    //Top stories in JSON: https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty
    private static final String TAG = "VolleySingleton";

    private RequestQueue mRequestQueue;
    private static Context mContext;
    private static VolleySingleton mInstance;


    public VolleySingleton(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public void addToRequestQueue(Request req) {
        getRequestQueue().add(req);
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

}
