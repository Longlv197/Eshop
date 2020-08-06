package com.longlv.eshop.ui.Carts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CartsViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<String> mText;

    public CartsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is carts fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}