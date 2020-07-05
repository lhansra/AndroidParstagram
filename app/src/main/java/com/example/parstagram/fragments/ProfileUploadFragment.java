package com.example.parstagram.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.parstagram.R;
import com.example.parstagram.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class ProfileUploadFragment extends ComposeFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etDescription = view.findViewById(R.id.etDescription);
        btnTakePicture = view.findViewById(R.id.btnCaptureImage);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        ivPostImage = view.findViewById(R.id.ivPostImage);
        pb = view.findViewById(R.id.pbLoading);

        etDescription.setVisibility(View.GONE);

        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(ProgressBar.VISIBLE);
                ParseUser user = ParseUser.getCurrentUser();
                if (photoFile == null || ivPostImage.getDrawable() == null){
                    Toast.makeText(getContext(), "There is no image", Toast.LENGTH_SHORT).show();
                    return;
                }
                savePost(user, photoFile);
            }
        });
    }

    private void savePost(ParseUser user, File photoFile){
        ParseFile profilePic = new ParseFile(photoFile);
        user.put("profile_image", profilePic);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while saving");
                    Toast.makeText(getContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post save was successful");
                pb.setVisibility(ProgressBar.INVISIBLE);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, new ProfileFragment()).commit();
                ivPostImage.setImageResource(0);
            }
        });
    }
}
