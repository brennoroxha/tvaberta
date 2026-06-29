package com.example.util;


import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

import com.example.livetvseries.R;


public class StatusBarUtil {
    public static void setStatusBar(Activity activity) {
        Window window = activity.getWindow();
        Drawable background = ContextCompat.getDrawable(activity, R.drawable.status_bar);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setBackgroundDrawable(background);
        EdgeUtils.enable(activity);
    }
}
