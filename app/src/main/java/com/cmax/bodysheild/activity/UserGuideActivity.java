package com.cmax.bodysheild.activity;

import android.os.Bundle;
import android.webkit.WebView;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.base.BaseActivity;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserGuideActivity extends BaseActivity {

    @Bind(R.id.webView)
    WebView webView;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_guide;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        loadUserGuide();
    }

    @OnClick(R.id.backBtn)
    void back(){
        finish();
    }

    private void loadUserGuide(){
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            webView.loadUrl("file:///android_asset/userGuide.html");
        else
            webView.loadUrl("file:///android_asset/userGuide_EN.html");
    }
}
