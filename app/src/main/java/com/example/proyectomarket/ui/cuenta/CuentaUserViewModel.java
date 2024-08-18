package com.example.proyectomarket.ui.cuenta;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CuentaUserViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CuentaUserViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Cuenta fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}