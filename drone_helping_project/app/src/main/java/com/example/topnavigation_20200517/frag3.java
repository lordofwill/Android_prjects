package com.example.topnavigation_20200517;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class frag3 extends Fragment  {
    private FragmentActivity mContext;
    private WebView SeungHyeonWebview;
    private WebSettings SeungHeyounSeeting;
    private View view;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (FragmentActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragwww,container,false);
        SeungHyeonWebview = (WebView)view.findViewById(R.id.webSeungHyeon);
        SeungHyeonWebview.setWebViewClient(new WebViewClient());
        SeungHeyounSeeting = SeungHyeonWebview.getSettings();
        SeungHeyounSeeting.setJavaScriptEnabled(true);
        SeungHyeonWebview.loadUrl("https://www.enjoydrone.com/");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }
}

