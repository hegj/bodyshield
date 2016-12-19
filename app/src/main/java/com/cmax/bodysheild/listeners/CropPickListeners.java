package com.cmax.bodysheild.listeners;

import android.content.Intent;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public interface CropPickListeners {
    void cropResult(Intent data);
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    void cropError(Intent data);
}
