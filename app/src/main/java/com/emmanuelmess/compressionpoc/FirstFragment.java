package com.emmanuelmess.compressionpoc;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    private Compressor.CompressedBitmap compressedBitmap;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

        final List<Drawable> imageList = ApplicationsOnDevice.getAllAppImages(requireContext());

        for (final Drawable drawable: imageList) {
            compressedBitmap = Compressor.compress(drawable);
            final Drawable decompressedDrawable = Compressor.decompress(getResources(), compressedBitmap);

            view.<ImageView>findViewById(R.id.imageView).setImageDrawable(decompressedDrawable);
        }
    }
}