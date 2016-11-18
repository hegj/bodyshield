package com.cmax.bodysheild.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.bean.VersionMsg;
import com.cmax.bodysheild.util.JsonUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends Activity {

    private static final String TAG = AboutUsActivity.class.getSimpleName();

    @Bind(R.id.versionName)
    TextView versionName;
    @Bind(R.id.checkResult)
    TextView checkResult;

    private String currentVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        try {
            currentVersion = getVersionName();
            if(StringUtils.isBlank(currentVersion)){
                currentVersion = "1.0";
            }
        } catch (Exception e) {
            currentVersion = "1.0";
            e.printStackTrace();
        }
        versionName.setText(currentVersion);
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    Toast.makeText(AboutUsActivity.this, R.string.checkResult1, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    checkResult.setText(R.string.checkResult2);
                    checkResult.setLinkTextColor(Color.WHITE);
                    checkResult.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                    checkResult.setMovementMethod(LinkMovementMethod.getInstance());
                    break;
                case 3:
                    Toast.makeText(AboutUsActivity.this, R.string.checkResult3, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }

    };

    @OnClick(R.id.backBtn)
    void back(){
        finish();
    }

    @OnClick(R.id.checkUpdateBtn)
    void checkUpdate(){
//        handler.post(myRunnable);
        Thread t = new Thread(myRunnable);
        t.start();
    }

    private void sendMsg(){

    }


    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            String path = "https://www.clientinterface.net/json.php";
            // 新建HttpPost对象
            HttpPost httpPost = new HttpPost(path);
//            // Post参数
//            List<NameValuePair> params = new ArrayList<NameValuePair>();
//            params.add(new BasicNameValuePair("i_user_id", "301"));
//            params.add(new BasicNameValuePair("s_api_key", "GPPmPbqbWHKt36FwcvfGsM60vyXk9dNPAsmkg0fT"));
//            params.add(new BasicNameValuePair("i_zone_id", "301"));
//            params.add(new BasicNameValuePair("s_class", "c_app"));
//            params.add(new BasicNameValuePair("s_method", "f_get_current_version"));
//            params.add(new BasicNameValuePair("i_app_id", "301"));

            try{
                // 设置字符集
//                HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                // 先封装一个 JSON 对象
                JSONObject args = new JSONObject();
                JSONObject param = new JSONObject();
                param.put("i_user_id", 301);
                param.put("s_api_key", "GPPmPbqbWHKt36FwcvfGsM60vyXk9dNPAsmkg0fT");
                param.put("i_zone_id", 301);
                param.put("s_class", "c_app");
                param.put("s_method", "f_get_current_version");
                param.put("i_app_id", 301);
                args.put("a_args",param);
                // 绑定到请求 Entry
                StringEntity entity = new StringEntity(args.toString());
                // 设置参数实体
                httpPost.setEntity(entity);
                httpPost.addHeader("Content-Type", "application/json");
                httpPost.addHeader("User-Agent", "imgfornote");
                // 获取HttpClient对象
                HttpClient httpClient = new DefaultHttpClient();
                // 获取HttpResponse实例
                HttpResponse httpResp = httpClient.execute(httpPost);
                // 判断是够请求成功
                if (200 == httpResp.getStatusLine().getStatusCode()) {
                    // 获取返回的数据
                    String result = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
                    VersionMsg versionMsg = JsonUtil.toBeanFromJson(VersionMsg.class,result);
                    if(versionMsg != null && "00000".equals(versionMsg.getS_code())){
//                        if(currentVersion.equals(versionMsg.getA_result().getS_current_version())){
//                            handler.sendEmptyMessage(1);
//                        }else {
//
//                        }
                        float cv = 0f;
                        float sv = 0f;
                        cv = Float.valueOf(currentVersion);
                        sv = Float.valueOf(versionMsg.getA_result().getS_current_version());
                        if(cv < sv){
                            handler.sendEmptyMessage(2);
                        }else {
                            handler.sendEmptyMessage(1);
                        }
                        Log.i(TAG,"当前版本："+cv +" 服务器版本："+sv);
                    }
                    Log.i(TAG, "HttpPost方式请求成功，返回数据如下：");
                    Log.i(TAG, result);
                } else {
                    Log.i(TAG, "HttpPost方式请求失败");
                }
            }catch (Exception e){
                handler.sendEmptyMessage(3);
                Log.i(TAG, "HttpPost方式请求出错");
                e.printStackTrace();
            }
        }
    };

    private String getVersionName() throws Exception
    {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
        String version = packInfo.versionName;
        return version;
    }
}
