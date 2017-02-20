package com.cmax.bodysheild.util;

import android.text.TextUtils;

/**
 * Created by Administrator on 2017/2/20 0020.
 */
public class HtmlUtils {
    public static String getHtmlString(String onlyBodyHtml) {
        String content="";
        if (!TextUtils.isEmpty(onlyBodyHtml)) {

            content = "<html> \n" +
                    "<head> \n" +
                    "<style type=\"text/css\"> \n" +
                    "body {text-align:justify; font-size: 16px; line-height: 150%}\n" +
                    "</style> \n" +
                    "</head> \n" +
                    "<body>" + onlyBodyHtml + "</body> \n</html>";

        }
        return content;
    }
}
