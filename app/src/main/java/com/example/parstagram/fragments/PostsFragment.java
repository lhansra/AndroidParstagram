package com.example.parstagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.parstagram.EndlessRecyclerViewScrollListener;
import com.example.parstagram.adapters.PostsAdapter;
import com.example.parstagram.R;
import com.example.parstagram.models.Post;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostsFragment extends Fragment {

    public static final String TAG = "PostsFragment";

    protected RecyclerView rvPosts;
    protected PostsAdapter adapter;
    protected List<Post> posts;
    protected SwipeRefreshLayout swipeContainer;
    protected EndlessRecyclerViewScrollListener scrollListener;
    protected AppBarLayout appBarLayout;
    protected FloatingActionButton fab;

    public PostsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appBarLayout=view.findViewById(R.id.my_appbar_container);
        fab=view.findViewById(R.id.fab);
        appBarLayout.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);

        rvPosts = view.findViewById(R.id.rvPosts);
        swipeContainer = view.findViewById(R.id.swipeContainer);


        posts=new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter=new PostsAdapter(getContext(),posts, layoutManager);
        rvPosts.setLayoutManager(layoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextData(page);
            }
        };
        rvPosts.addOnScrollListener(scrollListener);
        rvPosts.setAdapter(adapter);
        queryPosts();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                queryPosts();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Could not retrieve posts", e);
                    return;
                }
                for (Post post : objects){
                    Log.i(TAG, "Post: " + post.getDescription() + ", Username: " + post.getUser().getUsername());
                    posts.add(post);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    protected void loadNextData(int page) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.setSkip(20 * page);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Could not retrieve posts", e);
                    return;
                }
                for (Post post : objects){
                    Log.i(TAG, "Post: " + post.getDescription() + ", Username: " + post.getUser().getUsername());
                    posts.add(post);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
