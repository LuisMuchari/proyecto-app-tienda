package com.example.proyectomarket.ui.map;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.proyectomarket.R;
import com.example.proyectomarket.databinding.FragmentMapBinding;


public class MapFragment extends Fragment implements View.OnClickListener{

    private WebView webView;

    private FragmentMapBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //MapViewModel slideshowViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        webView = root.findViewById(R.id.w_web);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://www.google.com/maps/place/altoke+MARKET/@-12.0563626,-77.0626032,20.5z/data=!4m6!3m5!1s0x9105c9242ca4fd3b:0xd0bce682ff60ebc0!8m2!3d-12.056282!4d-77.0625989!16s%2Fg%2F11b6wd7s30?entry=ttu");
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {

    }

}