package com.cmax.bodysheild.listeners;

import com.cmax.bodysheild.bean.cache.User;

/**
 * Created by Administrator on 2017/1/18 0018.
 */

public interface ProfileDataSuccessListener {
    void success(int profileType, User user);
    void failed();
}
