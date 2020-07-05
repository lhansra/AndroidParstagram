package com.example.parstagram.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.parstagram.R;
import com.example.parstagram.models.Comment;

import org.parceler.Parcels;

import static android.app.Activity.RESULT_OK;

public class CommentComposeFragment extends DialogFragment implements TextView.OnEditorActionListener {

    public interface CommentComposeListener {
        void onFinishEditDialog(String inputText);
    }

    private EditText mEditText;

    public CommentComposeFragment() { }

    public static CommentComposeFragment newInstance(String title) {
        CommentComposeFragment frag = new CommentComposeFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comment_compose, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mEditText = view.findViewById(R.id.txt_your_name);
        mEditText.setOnEditorActionListener(this);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Add a comment");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text back to activity through the implemented listener
            CommentComposeListener listener = (CommentComposeListener) getActivity();
            listener.onFinishEditDialog(mEditText.getText().toString());
            // Close the dialog and return back to the parent activity
            dismiss();
            return true;
        }
        return false;
    }
    public void onResume() {
        int width = 1000;
        getDialog().getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        // Call super onResume after sizing
        super.onResume();
    }
}
