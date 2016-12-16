package com.cmax.bodysheild.util;

import com.cmax.bodysheild.bean.cache.User;

import java.util.List;

/**
 * Created by Administrator on 2016/12/16 0016.
 */

public class DataUtils {
    public static void addUserToSp(User user){
        List<User> users = SharedPreferencesUtil.getList(Constant.USER_LIST,
                User.class);
        if (!users.contains(user))
        users.add(user);
        SharedPreferencesUtil.setList(Constant.USER_LIST, users);
    }
}
