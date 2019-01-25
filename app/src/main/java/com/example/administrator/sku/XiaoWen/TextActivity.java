package com.example.administrator.sku.XiaoWen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sku.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018\12\7 0007.
 */

public class TextActivity extends AppCompatActivity {

    @BindView(R.id.btn1)
    Button btn1;
    @BindView(R.id.btn2)
    Button btn2;
    @BindView(R.id.btn3)
    Button btn3;
    @BindView(R.id.btn4)
    Button btn4;
    @BindView(R.id.btn5)
    Button btn5;
    private SoundPool soundPool;
    List<Integer> soundIdList = new ArrayList<>();

    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.text1)
    TextView text1;

    private BroadcastReceiver mBrReceiver;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layou_text);
        ButterKnife.bind(this);
        getsystemscandata();
        getYin();

    }

    /**
     * 获取接受到的扫描数据,注册广播
     */
    public void getsystemscandata() {

        final String getstr = "com.android.receive_scan_action";
        mBrReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(getstr)) {
                    String datat = intent.getStringExtra("data");
                    if (text.getText().toString().equals("")) {
                        text.setText(datat);
                    } else {
                        text1.setText(datat);
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(getstr);
        registerReceiver(mBrReceiver, filter);
        Toast.makeText(this, "注册广播完成，自动接收数据", Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void getYin() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                // 设置场景
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                // 设置类型
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        soundPool = new SoundPool.Builder()
                // 设置上面的属性
                .setAudioAttributes(audioAttributes)
                // 设置最多10个音频流文件
                .setMaxStreams(10).build();

        // 加载音频流到soundPool中去，并且用List存储起来
        soundIdList.add(soundPool.load(this, R.raw.a1, 1));
        soundIdList.add(soundPool.load(this , R.raw.a2 , 1));
        soundIdList.add(soundPool.load(this , R.raw.a3 , 1));
        soundIdList.add(soundPool.load(this , R.raw.a4 , 1));
        soundIdList.add(soundPool.load(this , R.raw.a5 , 1));

    }


    @OnClick({R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                soundPool.play( soundIdList.get(0) , 1 ,1 , 0 , 0 , 1);
                break;
            case R.id.btn2:
                soundPool.play( soundIdList.get(1) , 1 ,1 , 0 , 0 , 1);
                break;
            case R.id.btn3:
                soundPool.play( soundIdList.get(2) , 1 ,1 , 0 , 0 , 1);
                break;
            case R.id.btn4:
                soundPool.play( soundIdList.get(3) , 1 ,1 , 0 , 0 , 1);
                break;
            case R.id.btn5:
                soundPool.play( soundIdList.get(4) , 1 ,1 , 0 , 0 , 1);
                break;
        }
    }
}
