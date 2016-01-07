package me.stephenbatifol.hackernews;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import me.stephenbatifol.hackernews.adapters.CommentsRecyclerViewAdapter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CommentsFragment extends Fragment {

    private static final String ARG_LIST_PARENTS_COMMENTS = "list-parents-comments";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private static final String TAG = "CommentsFragment";
    ArrayList<Integer> listIdCommentsParents;
    private String mLinkComments;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CommentsFragment() {
    }

    public static CommentsFragment newInstance(ArrayList<String> list) {

        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_LIST_PARENTS_COMMENTS, list);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_LIST_PARENTS_COMMENTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            // TODO: 05/01/16 Change the elements


            Bundle bundle = getArguments();
            //listIdCommentsParents = bundle.getIntegerArrayList("ListComments");
            String jsonString = bundle.getString("ListComments");
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(jsonString);
                Log.d(TAG, "onCreateView: jsonArray  = " + jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*
            ArrayList<JSONArray> listJsonArray = new ArrayList<>();
            for (int i = 0; i < jsonString.size(); i++) {
                try {
                    JSONArray object = new JSONArray(jsonString.get(i));
                    listJsonArray.add(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            */

            //Log.d(TAG, "onCreateView: listJsonArray = " + listJsonArray);
            recyclerView.setAdapter(new CommentsRecyclerViewAdapter(jsonArray, mListener, getActivity()));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onCommentsFragmentClick(int id);
    }
}
