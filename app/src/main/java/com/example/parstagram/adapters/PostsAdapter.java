package com.example.parstagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.parstagram.PostDetailsActivity;
import com.example.parstagram.R;
import com.example.parstagram.fragments.ProfileFragment;
import com.example.parstagram.models.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONException;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public static final String TAG = "PostsAdapter";

    Context context;
    private List<Post> posts;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Post> likes;
    ParseUser user = ParseUser.getCurrentUser();
    List<Post> arrayLikes = (ArrayList<Post>) user.get("posts_liked");

    public PostsAdapter(Context context, List<Post> posts, RecyclerView.LayoutManager layoutManager) {
        this.context = context;
        this.posts = posts;
        this.layoutManager = layoutManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View postView = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvUsername;
        private TextView tvDescription;
        private ImageView ivPost;
        private TextView tvTimeStamp;
        private ImageView ivProfile;
        private TextView tvUsername2;
        private RelativeLayout relativeLayout;
        private RelativeLayout relativeLayout2;
        private ImageView ivLike;
        private FrameLayout frameLayout;
        private RelativeLayout layout1;
        private RelativeLayout layout2;
        Post post;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvUsername=itemView.findViewById(R.id.tvUsername);
            this.tvDescription=itemView.findViewById(R.id.tvDescription);
            this.ivPost=itemView.findViewById(R.id.ivPost);
            this.tvTimeStamp=itemView.findViewById(R.id.tvTimeStamp);
            this.ivProfile=itemView.findViewById(R.id.ivProfile);
            this.tvUsername2=itemView.findViewById(R.id.tvUsername2);
            this.relativeLayout=itemView.findViewById(R.id.relLayout);
            this.relativeLayout2=itemView.findViewById(R.id.layout2);
            this.ivLike=itemView.findViewById(R.id.ivLike);
            itemView.setOnClickListener(this);
        }

        public void bind(final Post post){
            this.post = post;
            tvUsername.setText(post.getUser().getUsername());
            tvUsername2.setText(post.getUser().getUsername());
            tvDescription.setText(post.getDescription());
            tvTimeStamp.setText(Post.getRelativeTimeAgo(post.getCreatedAt().toString()));
            if (layoutManager instanceof GridLayoutManager){
                relativeLayout.setVisibility(View.GONE);
                relativeLayout2.setVisibility(View.GONE);
                ivPost.getLayoutParams().height = 350;
                ivPost.getLayoutParams().width = 350;
            } else {
                ParseFile file = post.getUser().getParseFile("profile_image");
                if (file!=null) {
                    Glide.with(context).load(file.getUrl()).transform(new CircleCrop()).into(ivProfile);
                } else {
                    Glide.with(context).load(R.drawable.ic_person_black_24dp).transform(new CircleCrop()).into(ivProfile);
                }
                relativeLayout.setVisibility(View.VISIBLE);
                relativeLayout2.setVisibility(View.VISIBLE);
            }
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context)
                        .load(image.getUrl())
                        .transform(new CenterCrop(), new RoundedCornersTransformation(40,0))
                        .into(ivPost);
            } else {
                Glide.with(context)
                        .load(R.drawable.ic_block_black_24dp)
                        .transform(new RoundedCornersTransformation(40,0))
                        .into(ivPost);
            }
            ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseUser poster = post.getUser();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("user", poster);
                    ProfileFragment fragInfo = new ProfileFragment();
                    fragInfo.setArguments(bundle);
                    ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragInfo).commit();
                }
            });

            for (int i = 0; i < arrayLikes.size();i++) {
                if (arrayLikes.get(i).getObjectId().equals(post.getObjectId())) {
                    Glide.with(context).load(R.drawable.ic_favorite_black_24dp).override(60, 60).into(ivLike);
                } else {
                    Glide.with(context).load(R.drawable.ic_favorite_border_black_24dp).override(60, 60).into(ivLike);
                }
            }
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
                            Glide.with(context).load(R.drawable.ic_favorite_border_black_24dp).into(ivLike);
                            return;
                        }
                    }
                    arrayLikes.add(post);
                    user.add("posts_liked", post);
                    post.put("numLikes", post.getInt("numLikes") + 1);
                    Glide.with(context).load(R.drawable.ic_favorite_black_24dp).into(ivLike);
                    user.saveInBackground();
                    post.saveInBackground();
                }
            });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position!= RecyclerView.NO_POSITION) {
                Log.i(TAG, "Viewholder tapped at position " + position + posts.get(position).getDescription());
                Intent intent = new Intent(context, PostDetailsActivity.class);
                Post post = posts.get(position);
                intent.putExtra("post", (Serializable) post);
                ParseFile image = post.getImage();
                if (image != null) {
                    intent.putExtra("imageUrl", image.getUrl());
                } else {
                    intent.putExtra("imageUrl", "");
                }
                context.startActivity(intent);
            }
        }
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }
}
