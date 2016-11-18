package com.cmax.bodysheild.activity;

import android.os.Bundle;

import com.cmax.bodysheild.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedbackActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.backBtn)
    void back(){
        finish();
    }
}
