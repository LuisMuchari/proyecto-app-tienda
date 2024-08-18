package com.example.proyectomarket.ui.detallepedido;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DetalleViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DetalleViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is detalle fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}