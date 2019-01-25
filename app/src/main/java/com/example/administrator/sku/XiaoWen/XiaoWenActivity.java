package com.example.administrator.sku.XiaoWen;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.administrator.sku.R;
import com.example.administrator.sku.SPUtils;
import com.example.administrator.sku.XiaoWen.bean.MessageEvent;
import com.example.administrator.sku.XiaoWen.fragment.Scanning_Fragment;
import com.example.administrator.sku.XiaoWen.fragment.TimeFragment;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018\12\5 0005.
 */

public class XiaoWenActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {
    @BindView(R.id.fragment_demo_ll)
    LinearLayout fragmentDemoLl;
    @BindView(R.id.bottom_navigation_bar)
    BottomNavigationBar bottomNavigationBar;
    Scanning_Fragment scanningFragment;
    TimeFragment directoryFragment;

    private Fragment mContent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiaowen);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.mipmap.saoma, "扫码").setActiveColor("#3F51B5"))
                .addItem(new BottomNavigationItem(R.mipmap.saoma, "目录").setActiveColor("#3F51B5"))
                .setFirstSelectedPosition(0)
                .initialise();
        bottomNavigationBar.setTabSelectedListener(this);
//        setDefaultFragment();

        setDefaultFragment();
    }

    /**
     * 设置默认的fragment，即//第一次加载界面;
     */
    private void setDefaultFragment() {
        scanningFragment = Scanning_Fragment.newInstance();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_demo_ll, scanningFragment).commit();
        mContent = scanningFragment;
    }

    /**
     * 修改显示的内容 不会重新加载 *
     */
    public void switchContent(Fragment to) {
        FragmentManager fragmentManager = getFragmentManager();
        if (mContent != to) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (!to.isAdded()) { // 先判断是否被add过
                transaction.hide(mContent).add(R.id.fragment_demo_ll, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(mContent).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
            mContent = to;
        }
    }
    @Override
    public void onTabSelected(int position) {
        FragmentManager fm = this.getFragmentManager();
        //开启事务
        FragmentTransaction transaction = fm.beginTransaction();
        switch (position) {
            case 0:
                if (scanningFragment == null) {
                    scanningFragment = Scanning_Fragment.newInstance();
                }
//                showCustomizeDialog(srttingFragmenr, (Integer) SPUtils.get(this, "position", 1));
                //将当前的事务添加到了回退栈
//                transaction.addToBackStack(null);
//                transaction.add(R.id.fragment_demo_ll, fragment1);
                switchContent(scanningFragment);
                break;
            case 1:
                if (directoryFragment == null) {
                    directoryFragment = TimeFragment.newInstance();
                }
//                transaction.replace(R.id.fragment_demo_ll, directoryFragment);
                switchContent(directoryFragment);
                break;

            default:
                break;
        }
        // 事务提交
        transaction.commit();
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        /**
         * 退出系统
         */
        new AlertDialog.Builder(this) // 设置对话框标题内容
                .setTitle("是否要关闭系统？") // 设置按钮及按钮按下事件
                .setNeutralButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                        SPUtils.clear(XiaoWenActivity.this);
                    }
                }).setNegativeButton("取消", null).show();

    }
}
