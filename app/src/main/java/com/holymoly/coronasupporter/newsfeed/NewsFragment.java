package com.holymoly.coronasupporter.newsfeed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.fragment.app.Fragment;

import com.holymoly.coronasupporter.R;

public class NewsFragment extends Fragment {
    WebView webView;
    private android.webkit.WebSettings WebSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        webView = view.findViewById(R.id.web1);
        //  webView.setWebViewClient(new WebViewClient()); // 클릭시 새창 안뜨게
        WebSettings = webView.getSettings(); //세부 세팅 등록
        WebSettings.setJavaScriptEnabled(true); // 웹페이지 자바스클비트 허용 여부
        WebSettings.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
        WebSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        WebSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        WebSettings.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부
        WebSettings.setSupportZoom(false); // 화면 줌 허용 여부
        WebSettings.setBuiltInZoomControls(false); // 화면 확대 축소 허용 여부
        WebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부
        WebSettings.setDomStorageEnabled(true);
        webView.loadUrl("http://ncov.mohw.go.kr/tcmBoardList.do?brdId=3&brdGubun=");
        webView.scrollTo(0, 340);
        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        webView=null; // remove webView, prevent chromium to crash
    }
}