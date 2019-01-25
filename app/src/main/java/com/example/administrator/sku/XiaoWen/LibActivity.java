package com.example.administrator.sku.XiaoWen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.example.administrator.sku.CommonAdapter;
import com.example.administrator.sku.R;
import com.example.administrator.sku.ViewHolder;
import com.example.administrator.sku.XiaoWen.db.LibDao;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Administrator on 2018\12\10 0010.
 */

public class LibActivity extends AppCompatActivity {
    @BindView(R.id.lib_lrv)
    LRecyclerView libLrv;

    private LRecyclerViewAdapter lRecyclerViewAdapter = null;
    private CommonAdapter<String> adapter;
    private List<String> datas = new ArrayList<>(); //PDA机屏幕上的List集合
    LibDao libDao;
    String time;
    boolean isChongfu = false;//没有重复录入

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib);
        ButterKnife.bind(this);
        intiAdapter();
        initView();
    }

    private void initView() {
        libDao = new LibDao(this);
        time = getIntent().getStringExtra("time");
        HashSet<String> hs = new HashSet<String>(libDao.select(time));
        Iterator<String> iterator=hs.iterator();
        while(iterator.hasNext()){
            datas.add(iterator.next());
            lRecyclerViewAdapter.notifyDataSetChanged();
        }
    }



    private void intiAdapter() {
        adapter = new CommonAdapter<String>(this, R.layout.adapter_time, datas) {
            @Override
            public void setData(ViewHolder holder, String s) {
                holder.setText(R.id.time_tv, s);
            }
        };
        libLrv.setLayoutManager(new LinearLayoutManager(this));
        lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        libLrv.setAdapter(lRecyclerViewAdapter);
        libLrv.setLoadMoreEnabled(false);
        libLrv.setPullRefreshEnabled(false);
        lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                Intent intent = new Intent(LibActivity.this, LibSkuActivity.class);
                intent.putExtra("lib", datas.get(position));
                intent.putExtra("time", time);
                startActivity(intent);
            }
        });
    }
}
