package com.cmax.bodysheild.activity;

import android.os.Bundle;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedbackActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_feedback;
    }

    @OnClick(R.id.backBtn)
    void back(){
        finish();
    }
}
