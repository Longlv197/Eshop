package com.longlv.eshop.ui.Carts;

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
import com.longlv.eshop.ui.Category.CategoryViewModel;

public class CartsFragment extends Fragment {

    private CartsViewModel mCartsViewModel;

    public static CartsFragment newInstance() {
        return new CartsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mCartsViewModel = ViewModelProviders.of(this).get(CartsViewModel.class);
        View root = inflater.inflate(R.layout.carts_fragment, container, false);
        final TextView textView = root.findViewById(R.id.text_Carts);
        mCartsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}