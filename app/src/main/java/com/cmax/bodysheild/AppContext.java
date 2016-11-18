/**
 * 
 */
package com.cmax.bodysheild;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;

import com.cmax.bodysheild.enums.AppModel;
import com.cmax.bodysheild.exception.CrashHandler;
import com.github.mikephil.charting.data.LineData;

import org.apache.commons.lang3.StringUtils;

public class AppContext extends Application {

    public static final AppModel appModel = AppModel.Product;

	private static AppContext context;
	private LineData xData;

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
        CrashHandler catchHandler = CrashHandler.getInstance();
        catchHandler.init(getApplicationContext());
    }
	
	public static AppContext getContext() {
		return context;
	}

	public static void setContext(AppContext context) {
		AppContext.context = context;
	}
	
	public String getMetaValue(String metaName) {
        Bundle metaData = null;
        String metaValue = null;
        if (StringUtils.isEmpty(metaName)) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                metaValue = metaData.getString(metaName);
            }
        } catch (NameNotFoundException e) {
        	
        }
        return metaValue;
    }

    /**
     * 预初始化x轴刻度
     */
    private void initLineData(){
        xData = new LineData();
        xData.setValueTextColor(Color.WHITE);
        for(int i = 0; i<86399;i++){
            xData.addXValue(""+i);
        }
        xData.setHighlightEnabled(true);
    }

}
