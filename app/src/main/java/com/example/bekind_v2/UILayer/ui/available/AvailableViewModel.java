package com.example.bekind_v2.UILayer.ui.available;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AvailableViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AvailableViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is available fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}