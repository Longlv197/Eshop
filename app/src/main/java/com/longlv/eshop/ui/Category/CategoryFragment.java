package com.longlv.eshop.ui.Category;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.longlv.eshop.R;
import com.longlv.eshop.ui.Setting.SettingViewModel;

public class CategoryFragment extends Fragment {

    private CategoryViewModel mCategoryViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mCategoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        View root = inflater.inflate(R.layout.category_fragment, container, false);
        final TextView textView = root.findViewById(R.id.text_Category);
        mCategoryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}