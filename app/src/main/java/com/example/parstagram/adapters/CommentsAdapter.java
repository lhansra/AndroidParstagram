package com.example.parstagram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.parstagram.R;
import com.example.parstagram.models.Comment;
import com.example.parstagram.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    Context context;
    List<Comment> comments;
    Post post;

    public CommentsAdapter(Context context, List<Comment> comments, Post post) {
        this.context = context;
        this.comments = comments;
        this.post = post;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        ImageView ivProfile;
        TextView tvUsername;
        TextView tvBody;
        Comment comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvBody = itemView.findViewById(R.id.tvComment);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Comment comment){
            this.comment = comment;
            try {
                tvUsername.setText(comment.getUser().fetchIfNeeded().getUsername());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvBody.setText(comment.getBody());
            ParseFile image = comment.getUser().getParseFile("profile_image");
            if (image!= null) {
                Glide.with(context).load(image.getUrl()).transform(new CircleCrop()).into(ivProfile);
            } else {
                Glide.with(context).load(R.drawable.ic_person_black_24dp).into(ivProfile);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (comment.getUser().getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                comments.remove(comments.get(getAdapterPosition()));
                post.put("comments", comments);
                post.saveInBackground();
                comment.saveInBackground();
                notifyDataSetChanged();
                return true;
            } else {
                Toast.makeText(context,"You cannot delete someone else's comment", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }
}
