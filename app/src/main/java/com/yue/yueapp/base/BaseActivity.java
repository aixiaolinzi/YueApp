package com.yue.yueapp.base;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.afollestad.materialdialogs.color.CircleView;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yue.yueapp.Constant;
import com.yue.yueapp.R;
import com.yue.yueapp.utils.SettingUtil;

/**
 * 描述:
 * Created by yzz on 2017/12/12.
 */

public abstract class BaseActivity extends RxAppCompatActivity {

    private static final String TAG = "BaseActivity";
    protected SlidrInterface slidrInterface;
    private int iconType = -1;

    /**
     * 初始化 Toolbar
     */
    protected void initToolBar(Toolbar toolbar, boolean homeAsUpEnabled, String title) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(homeAsUpEnabled);
    }

    /**
     * 初始化滑动返回
     */
    protected void initSlidable() {
        int isSlidable = SettingUtil.getInstance().getSlidable();
        if (isSlidable != Constant.SLIDABLE_DISABLE) {
            SlidrConfig config = new SlidrConfig.Builder()
                    .edge(isSlidable == Constant.SLIDABLE_EDGE)
                    .build();
            slidrInterface = Slidr.attach(this, config);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.iconType = SettingUtil.getInstance().getCustomIconValue();
        initSlidable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int color = SettingUtil.getInstance().getColor();
        int drawable = Constant.ICONS_DRAWABLES[SettingUtil.getInstance().getCustomIconValue()];
        if (getSupportActionBar() != null)
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(CircleView.shiftColorDown(color));
            // 最近任务栏上色
            ActivityManager.TaskDescription tDesc = new ActivityManager.TaskDescription(
                    getString(R.string.app_name),
                    BitmapFactory.decodeResource(getResources(), drawable),
                    color);
            setTaskDescription(tDesc);
            if (SettingUtil.getInstance().getNavBar()) {
                getWindow().setNavigationBarColor(CircleView.shiftColorDown(color));
            } else {
                getWindow().setNavigationBarColor(Color.BLACK);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onDestroy() {
//        for (Call call : InitApp.getOkHttpClient().dispatcher().queuedCalls()) {
//            call.cancel();
//        }
//        for (Call call : InitApp.getOkHttpClient().dispatcher().runningCalls()) {
//            call.cancel();
//        }
//        super.onDestroy();
//    }

    @Override
    public void onBackPressed() {
        // Fragment 逐个出栈
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    protected void onStop() {

        if (iconType != SettingUtil.getInstance().getCustomIconValue()) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    String act = ".SplashActivity_";

                    for (String s : Constant.ICONS_TYPE) {
                        getPackageManager().setComponentEnabledSetting(new ComponentName(BaseActivity.this, getPackageName() + act + s),
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                PackageManager.DONT_KILL_APP);
                    }

                    act += Constant.ICONS_TYPE[SettingUtil.getInstance().getCustomIconValue()];

                    getPackageManager().setComponentEnabledSetting(new ComponentName(BaseActivity.this, getPackageName() + act),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP);
                }
            }).start();
        }

        super.onStop();
    }

}
