package com.longlv.eshop.ui.Logout;

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

public class LogoutFragment extends Fragment {

    private LogoutViewModel mLogoutViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mLogoutViewModel = ViewModelProviders.of(this).get(LogoutViewModel.class);
        View root = inflater.inflate(R.layout.logout_fragment, container, false);
        final TextView textView = root.findViewById(R.id.text_Logout);
        mLogoutViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}