package com.example.a220523.ui.tag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.a220523.databinding.FragmentTagBinding;
import com.example.a220523.ui.tag.TagViewModel;

public class TagFragment extends Fragment {

    private TagViewModel tagViewModel;
    private FragmentTagBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        tagViewModel =
                new ViewModelProvider(this).get(TagViewModel.class);

        binding = FragmentTagBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textTag;
        tagViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
