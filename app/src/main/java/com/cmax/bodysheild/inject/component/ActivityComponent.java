package com.cmax.bodysheild.inject.component;



import com.cmax.bodysheild.activity.login.LoginActivity2;

import dagger.Component;

/**
 * Created by Administrator on 2016/11/25 0025.
 */
@Component(dependencies = AppComponent.class)
public interface ActivityComponent {
    void inject(LoginActivity2 activity);
}
