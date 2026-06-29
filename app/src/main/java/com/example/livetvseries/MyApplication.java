package com.example.livetvseries;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDex;

import com.example.util.NetworkUtils;
import com.example.util.StatusBarUtil;
import com.facebook.ads.AudienceNetworkAds;
import com.onesignal.OneSignal;
import com.onesignal.notifications.INotificationClickEvent;
import com.onesignal.notifications.INotificationClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class MyApplication extends Application {
    private static MyApplication mInstance;
    public SharedPreferences preferences;
    public String prefName = "LiveTV";

    public MyApplication() {
        mInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ViewPump.init(ViewPump.builder().addInterceptor(new CalligraphyInterceptor(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/custom.ttf").setFontAttrId(R.attr.fontPath).build())).build());

        OneSignal.initWithContext(this, getString(R.string.onesignal_app_id));
        OneSignal.getNotifications().addClickListener(new ExampleNotificationOpenedHandler());

        mInstance = this;
        AudienceNetworkAds.initialize(this);
        enableEdge();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void saveIsIntroduction(boolean flag) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putBoolean("IsIntroduction", flag);
        editor.apply();
    }

    public boolean getIsIntroduction() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getBoolean("IsIntroduction", false);
    }

    public void saveIsLogin(boolean flag) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putBoolean("IsLoggedIn", flag);
        editor.apply();
    }

    public boolean getIsLogin() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getBoolean("IsLoggedIn", false);
    }

    public void saveIsRemember(boolean flag) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putBoolean("IsLoggedRemember", flag);
        editor.apply();
    }

    public boolean getIsRemember() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getBoolean("IsLoggedRemember", false);
    }


    public void saveRemember(String email, String password) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putString("remember_email", email);
        editor.putString("remember_password", password);
        editor.apply();
    }

    public String getRememberEmail() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("remember_email", "");
    }

    public String getRememberPassword() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("remember_password", "");
    }

    public void saveLogin(String user_id, String user_name, String email) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putString("user_id", user_id);
        editor.putString("user_name", user_name);
        editor.putString("email", email);
        editor.apply();
    }

    public String getUserId() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("user_id", "");
    }

    public String getUserName() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("user_name", "");
    }

    public String getUserEmail() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("email", "");
    }

    public void saveIsNotification(boolean flag) {
        preferences = this.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("IsNotification", flag);
        editor.apply();
    }

    public boolean getNotification() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getBoolean("IsNotification", true);
    }

    public int getScreenWidth() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getInt("screen_width", NetworkUtils.getScreenWidth(MyApplication.this));
    }

    public void setScreenWidth(int screenWidth) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putInt("screen_width", screenWidth);
        editor.apply();
    }

    private class ExampleNotificationOpenedHandler implements INotificationClickListener {
        @Override
        public void onClick(@NonNull INotificationClickEvent result) {
            JSONObject data = result.getNotification().getAdditionalData();
            String isExternalLink, postId, postType;
            if (data != null) {
                try {
                    isExternalLink = data.getString("external_link");
                    postId = data.getString("post_id");
                    postType = data.getString("type");
                    if (!postId.equals("0")) {
                        Class<?> aClass;
                        switch (postType) {
                            case "movie":
                                aClass = MovieDetailsActivity.class;
                                break;
                            case "series":
                                aClass = SeriesDetailsActivity.class;
                                break;
                            default:
                                aClass = TVDetailsActivity.class;
                                break;
                        }
                        Intent intent = new Intent(MyApplication.this, aClass);
                        intent.putExtra("Id", postId);
                        intent.putExtra("isNotification", true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Intent intent;
                        if (!isExternalLink.equals("false")) {
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(isExternalLink));
                        } else {
                            intent = new Intent(MyApplication.this, SplashActivity.class);
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Intent intent = new Intent(MyApplication.this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    public void enableEdge() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                StatusBarUtil.setStatusBar(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
    }
}
