package com.example.parstagram;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.parstagram.adapters.CommentsAdapter;
import com.example.parstagram.fragments.CommentComposeFragment;
import com.example.parstagram.fragments.PostsFragment;
import com.example.parstagram.fragments.ProfileFragment;
import com.example.parstagram.models.Comment;
import com.example.parstagram.models.Likes;
import com.example.parstagram.models.Post;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class PostDetailsActivity extends AppCompatActivity implements CommentComposeFragment.CommentComposeListener {

    public static final String TAG = "PostDetailsActivity";

    TextView tvUsername;
    TextView tvUsername2;
    TextView tvDescription;
    TextView tvTimeStamp;
    ImageView profilePic;
    ImageView ivPost;
    ImageView ivLike;
    ImageView ivComment;
    RecyclerView rvComments;
    CommentsAdapter commentsAdapter;
    List<Comment> comments;
    Post post;

    ParseUser user = ParseUser.getCurrentUser();
    ArrayList<Post> arrayLikes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        Intent intent = getIntent();
        post = (Post)intent.getSerializableExtra("post");

        comments = new ArrayList<>();
        arrayLikes = new ArrayList<>();
        arrayLikes = (ArrayList<Post>) user.get("posts_liked");
        Log.i(TAG, arrayLikes.toString());

        tvUsername = findViewById(R.id.tvUsername);
        tvUsername2 = findViewById(R.id.tvUsername2);
        tvDescription = findViewById(R.id.tvDescription);
        tvTimeStamp = findViewById(R.id.tvTimeStamp);
        profilePic = findViewById(R.id.ivProfile);
        ivPost = findViewById(R.id.ivPost);
        ivLike = findViewById(R.id.ivLike);
        ivComment = findViewById(R.id.ivComment);
        rvComments = findViewById(R.id.rvComments);

        Log.i(TAG, arrayLikes.toString());

        for (int i = 0; i < arrayLikes.size();i++){
            if (arrayLikes.get(i).getObjectId().equals(post.getObjectId())){
                Glide.with(PostDetailsActivity.this).load(R.drawable.ic_favorite_black_24dp).override(60, 60).into(ivLike);
            } else {
                Glide.with(PostDetailsActivity.this).load(R.drawable.ic_favorite_border_black_24dp).override(60, 60).into(ivLike);
            }
        }

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser poster = post.getUser();
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", poster);
                ProfileFragment fragInfo = new ProfileFragment();
                fragInfo.setArguments(bundle);
                FrameLayout frameLayout = findViewById(R.id.flContainer);
                RelativeLayout layout1 = findViewById(R.id.relLayout);
                RelativeLayout layout2 = findViewById(R.id.layout2);
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.GONE);
                ivPost.setVisibility(View.GONE);
                rvComments.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);

                getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragInfo).commit();
            }
        });

        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, arrayLikes.toString());
                Log.i(TAG, post.getObjectId());
                for (int i = 0; i < arrayLikes.size();i++) {
                    if (arrayLikes.get(i).getObjectId().equals(post.getObjectId())) {
                        arrayLikes.remove(i);
                        user.put("posts_liked", arrayLikes);
                        user.saveInBackground();
                        post.put("numLikes", post.getInt("numLikes") - 1);
                        post.saveInBackground();
                        Glide.with(PostDetailsActivity.this).load(R.drawable.ic_favorite_border_black_24dp).into(ivLike);
                        return;
                    }
                }
                arrayLikes.add(post);
                user.add("posts_liked", post);
                post.put("numLikes", post.getInt("numLikes") + 1);
                Glide.with(PostDetailsActivity.this).load(R.drawable.ic_favorite_black_24dp).into(ivLike);
                user.saveInBackground();
                post.saveInBackground();
            }
        });

        ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentDialog();
                post.saveInBackground();
            }
        });

        tvUsername2.setText(post.getUser().getUsername());
        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());
        tvTimeStamp.setText(Post.getRelativeTimeAgo(post.getCreatedAt().toString()));
        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(PostDetailsActivity.this).load(image.getUrl()).transform(new CenterCrop(), new RoundedCornersTransformation(40, 0)).into(ivPost);
        }
        ParseFile file = post.getUser().getParseFile("profile_image");
        if (file != null){
            Glide.with(this).load(file.getUrl()).transform(new CircleCrop()).into(profilePic);
        } else {
            Glide.with(this).load(R.drawable.ic_person_black_24dp).into(profilePic);
        }

        commentsAdapter = new CommentsAdapter(this, comments, post);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(commentsAdapter);
        queryComments();

    }

    protected void queryComments() {
        ArrayList<Comment> array = (ArrayList<Comment>) post.get("comments");
        comments.clear();
        comments.addAll(array);
        Log.i(TAG, comments.toString());
        commentsAdapter.notifyDataSetChanged();
    }

    public void showCommentDialog() {
        FragmentManager fm = getSupportFragmentManager();
        CommentComposeFragment editNameDialogFragment = CommentComposeFragment.newInstance("Add a comment");
        editNameDialogFragment.show(fm, "fragment_comment_compose");
    }

    @Override
    public void onFinishEditDialog(String inputText) {
        String body = inputText;
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setBody(body);
        Log.i(TAG, comment.getBody());
        comment.saveInBackground();
        comments.add(comment);
        post.put("comments", comments);
        post.saveInBackground();
        Log.i(TAG, comments.toString());
        commentsAdapter.notifyItemInserted(comments.size()-1);
        rvComments.smoothScrollToPosition(comments.size()-1);
    }
}
