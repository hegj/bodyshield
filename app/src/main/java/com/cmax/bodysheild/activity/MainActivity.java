package com.cmax.bodysheild.activity;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.activity.fragment.HomeFragment;
import com.cmax.bodysheild.activity.fragment.MeFragment;
import com.cmax.bodysheild.base.BaseActivity;
import com.cmax.bodysheild.util.Constant;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    private long exitTime = 0;

    private static FragmentManager fMgr;

    public HomeFragment homeFragment;

    public MeFragment meFragment;
    /**
     * 当前类容
     */
    private Fragment content;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        fMgr = getFragmentManager();
        homeItemClick();

    }

    @OnClick(R.id.homeItem)
    public void homeItemClick(){

        if (homeFragment == null){
            homeFragment = new HomeFragment();
        }

        switchContent(homeFragment);
    }

    @OnClick(R.id.meItem)
    public void meItemClick(){

        if (meFragment == null){
            meFragment = new MeFragment();
        }
        switchContent(meFragment);
    }

    @OnClick(R.id.tab2)
    public void tab2ItemClick(){
        //弹出Toast提示
        Toast.makeText(MainActivity.this,"你点击了tab2！！",Toast.LENGTH_SHORT).show();
    }

    private void switchContent(Fragment fragment) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (null == content) {
            transaction.add(R.id.content, fragment).commit();
            content = fragment;
        } else {
            if (content != fragment) {
                if (!fragment.isAdded()) {
                    // 隐藏当前的fragment，add下一个到Activity中
                    transaction.hide(content).add(R.id.content, fragment).commit();
                } else {
                    // 隐藏当前的fragment，显示下一个
                    transaction.hide(content).show(fragment).commit();
                }
                content = fragment;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            // 判断是否在两秒之内连续点击返回键，是则退出，否则不退出
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                // 将系统当前的时间赋值给exitTime
                exitTime = System.currentTimeMillis();
            } else {
                exitApp();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出应用程序的方法，发送退出程序的广播
     */
    private void exitApp() {
        Intent intent = new Intent();
        intent.setAction(Constant.EXIT);
        this.sendBroadcast(intent);
    }
}
