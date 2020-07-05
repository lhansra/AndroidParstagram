package com.example.parstagram.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.parstagram.EndlessRecyclerViewScrollListener;
import com.example.parstagram.adapters.PostsAdapter;
import com.example.parstagram.R;
import com.example.parstagram.models.Post;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends PostsFragment {
    private ParseUser user = ParseUser.getCurrentUser();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = bundle.getParcelable("user");
        }

        appBarLayout=view.findViewById(R.id.my_appbar_container);
        fab=view.findViewById(R.id.fab);
        ImageView ivProfile = view.findViewById(R.id.imageView_avatar);
        CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.collapsing_container);
        //collapsingToolbarLayout.setTitleEnabled(false);
        collapsingToolbarLayout.setTitle(user.getUsername());
        appBarLayout.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
        ParseFile profileImage = user.getParseFile("profile_image");
        if (profileImage !=null) {
            Glide.with(this).load(profileImage.getUrl()).transform(new CircleCrop()).into(ivProfile);
        } else {
            Glide.with(this).load(R.drawable.ic_person_white_24dp).transform(new CircleCrop()).into(ivProfile);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment frag = new ProfileUploadFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, frag).commit();
            }
        });

        rvPosts = view.findViewById(R.id.rvPosts);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        posts=new ArrayList<>();

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        adapter=new PostsAdapter(getContext(),posts, layoutManager);
        rvPosts.setLayoutManager(layoutManager);

//        final int spacing = 0;
//        rvPosts.setPadding(spacing, spacing, spacing, spacing);
//        rvPosts.setClipToPadding(false);
//        rvPosts.setClipChildren(false);
//        rvPosts.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//                outRect.set(spacing, spacing, spacing, spacing);
//            }
//        });

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

    @Override
    protected void queryPosts() {
        //super.queryPosts();
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, user);
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

    @Override
    protected void loadNextData(int page) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
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
