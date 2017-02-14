package com.cmax.bodysheild.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.base.BaseActivity;
import com.cmax.bodysheild.base.bean.BaseRequestData;
import com.cmax.bodysheild.base.view.IStateView;
import com.cmax.bodysheild.bean.UserProfileInfo;
import com.cmax.bodysheild.http.HttpMethods;
import com.cmax.bodysheild.http.RxJavaHttpHelper;
import com.cmax.bodysheild.http.rxschedulers.RxSchedulersHelper;
import com.cmax.bodysheild.http.rxsubscriber.ProgressSubscriber;
import com.cmax.bodysheild.util.DialogUtils;
import com.cmax.bodysheild.util.IntentUtils;
import com.cmax.bodysheild.util.KeyBoardUtils;
import com.cmax.bodysheild.util.ToastUtils;
import com.cmax.bodysheild.util.UIUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedbackActivity extends BaseActivity implements IStateView {
    @Bind(R.id.backBtn)
    ImageView backBtn;
    @Bind(R.id.et_message)
    TextInputEditText etMessage;
    @Bind(R.id.et_contact)
    TextInputEditText etContact;
    private ProgressDialog progressDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feedback;
    }

    @OnClick(R.id.backBtn)
    void back() {
        finish();
    }

    @Override
    protected void initEvent(Bundle savedInstanceState) {
        super.initEvent(savedInstanceState);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @OnClick(R.id.saveTextBtn)
    void clickSaveTextBtn(TextView saveTextBtn) {
        KeyBoardUtils.closeKeybord(etMessage);
        String message = etMessage.getText().toString();
        if (TextUtils.isEmpty(message)){
            ToastUtils.showFailToast(UIUtils.getString(R.string.input_feedback_message));
            return;
        }
        String contact = etContact.getText().toString().trim();
        if (TextUtils.isEmpty(contact)){
            ToastUtils.showFailToast(UIUtils.getString(R.string.input_user_contact));
            return;
        }
        Map<String,String> map = new HashMap<String, String>() ;
        map.put("uid",UIUtils.getUserId()+"");
        map.put("content",message);
        map.put("contact",contact);
        HttpMethods.getInstance().apiService.feedBack(map) .compose(RxJavaHttpHelper.<Object>handleResult())  .compose( RxSchedulersHelper.<Object>applyIoTransformer())
        .subscribe(new ProgressSubscriber<Object>(this) {
            @Override
            public void _onError(String message) {
                ToastUtils.showFailToast(message);
            }

            @Override
            public void _onNext(Object o) {
                onCompleted();
            }

            @Override
            public void _onCompleted() {
                finish();
                ToastUtils.showSuccessToast(UIUtils.getString(R.string.feedback_success_message));
            }
        });

    }

    @Override
    public void showProgressDialog() {
        if (progressDialog==null)
        progressDialog = DialogUtils.showProgressDialog(this,UIUtils.getString(R.string.feedback_loading_message));
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog!=null &&progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}
