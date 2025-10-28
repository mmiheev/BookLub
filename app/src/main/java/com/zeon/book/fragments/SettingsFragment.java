package com.zeon.book.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zeon.book.R;
import com.zeon.book.activities.LetterLayout;

public class SettingsFragment extends Fragment {
    private static final String APP_URL = "https://play.google.com/store/apps/details?id=com.zeon.book";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        setupClickListeners(view);
        return view;
    }

    private void setupClickListeners(@NonNull View view) {
        view.findViewById(R.id.btnILike).setOnClickListener(v -> openAppInStore());
        view.findViewById(R.id.btnShare).setOnClickListener(v -> shareApp());
        view.findViewById(R.id.btnWriteUs).setOnClickListener(v -> openContactForm());
    }

    private void openAppInStore() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(APP_URL));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            showToast(getString(R.string.no_app_to_open_link));
        }
    }

    private void shareApp() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND)
                    .setType("text/plain")
                    .putExtra(Intent.EXTRA_TEXT, APP_URL);

            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
        } catch (ActivityNotFoundException e) {
            showToast(getString(R.string.no_app_to_share));
        }
    }

    private void openContactForm() {
        try {
            startActivity(new Intent(requireActivity(), LetterLayout.class));
        } catch (ActivityNotFoundException e) {
            showToast(getString(R.string.cant_open_feedback));
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}