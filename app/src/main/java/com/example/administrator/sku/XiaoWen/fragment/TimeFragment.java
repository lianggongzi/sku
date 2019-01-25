package com.example.administrator.sku.XiaoWen.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.sku.CommonAdapter;
import com.example.administrator.sku.R;
import com.example.administrator.sku.ViewHolder;
import com.example.administrator.sku.XiaoWen.LibActivity;
import com.example.administrator.sku.XiaoWen.db.TimeDao;
import com.github.jdsjlzx.interfaces.ILoadMoreFooter;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.github.jdsjlzx.view.LoadingFooter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Administrator on 2018\12\5 0005.
 */

public class TimeFragment extends Fragment {

    @BindView(R.id.time_lrv)
    LRecyclerView timeLrv;
    Unbinder unbinder;

    public static TimeFragment newInstance() {
        TimeFragment fragment = new TimeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private LRecyclerViewAdapter lRecyclerViewAdapter = null;
    private CommonAdapter<String> adapter;
    private List<String> datas = new ArrayList<>(); //PDA机屏幕上的List集合
    TimeDao timeDao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        intiAdapter();
        initData();
        return view;
    }

    //通过add hide show 方式来切换Fragment（Fragment1不走任何生命周期,但会调onHiddenChanged方法）
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        initData();
    }

    private void initData() {
        datas.clear();
        List<String> list = timeDao.select();
        datas.addAll(list);
    }

    private void initView() {
        timeDao = new TimeDao(getActivity());
    }

    private void intiAdapter() {
        adapter = new CommonAdapter<String>(getActivity(), R.layout.adapter_time, datas) {
            @Override
            public void setData(ViewHolder holder, String s) {
                holder.setText(R.id.time_tv, s);
            }
        };
        timeLrv.setLayoutManager(new LinearLayoutManager(getActivity()));
        lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        timeLrv.setAdapter(lRecyclerViewAdapter);
//        timeLrv.setLoadMoreEnabled(false);
//        timeLrv.setPullRefreshEnabled(false);
        timeLrv.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader); //设置下拉刷新 Progress 的样式
//        timeSearchLrv.setArrowImageView(R.drawable.iconfont_downgrey);  //设置下拉刷新箭头
        timeLrv.setOnRefreshListener(new OnRefreshListener() {  //下拉刷新
            @Override
            public void onRefresh() {
                initData();
                timeLrv.refreshComplete(1);
                lRecyclerViewAdapter.notifyDataSetChanged();
            }
        });


        lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                Intent intent = new Intent(getActivity(), LibActivity.class);
                intent.putExtra("time", datas.get(position));
                startActivity(intent);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
