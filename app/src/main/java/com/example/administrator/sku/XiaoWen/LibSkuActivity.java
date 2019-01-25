package com.example.administrator.sku.XiaoWen;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.sku.CommonAdapter;
import com.example.administrator.sku.DateUtils;
import com.example.administrator.sku.ExcelUtils;
import com.example.administrator.sku.R;
import com.example.administrator.sku.SPUtils;
import com.example.administrator.sku.ViewHolder;
import com.example.administrator.sku.XiaoWen.db.LibDao;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018\12\10 0010.
 */

public class LibSkuActivity extends AppCompatActivity {
    @BindView(R.id.libsku_lib_tv)
    TextView libskuLibTv;
    @BindView(R.id.libsku_lrv)
    LRecyclerView libskuLrv;
    @BindView(R.id.libsku_time_tv)
    TextView libskuTimeTv;
    @BindView(R.id.save_btn)
    Button saveBtn;
    private LRecyclerViewAdapter lRecyclerViewAdapter = null;
    private CommonAdapter<String> adapter;
    private List<String> datas = new ArrayList<>(); //PDA机屏幕上的List集合
    LibDao libDao;

    private File file;
    private ArrayList<ArrayList<String>> recordList;
    private static String[] title = {"区位", "SKU", "扫描时间"};
    String time;
    String lib;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_libsku);
        ButterKnife.bind(this);
        initView();
        intiAdapter();
    }

    private void initView() {
        libDao = new LibDao(this);
        time = getIntent().getStringExtra("time");
        lib = getIntent().getStringExtra("lib");
        libskuLibTv.setText(lib);
        libskuTimeTv.setText(time);
        datas.addAll(libDao.selectzong(time, lib));
    }


    private void intiAdapter() {
        adapter = new CommonAdapter<String>(this, R.layout.adapter_scanning, datas) {
            @Override
            public void setData(ViewHolder holder, String s) {
                holder.setText(R.id.adapter_sku, s);
            }
        };
        libskuLrv.setLayoutManager(new LinearLayoutManager(this));
        lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        libskuLrv.setAdapter(lRecyclerViewAdapter);
        libskuLrv.setLoadMoreEnabled(false);
        libskuLrv.setPullRefreshEnabled(false);
        lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {

            }
        });
    }

    @OnClick(R.id.save_btn)
    public void onViewClicked() {
        String time = DateUtils.getCurrentTime3();
//        String time = "2018-12-11";
        if (time.equals(SPUtils.get(this, "time", ""))) {
            exportExcel(time);
        } else {
            SPUtils.remove(this, "fileName");
            exportExcel(time);
        }
    }

    /**
     * 导出excel
     *
     * @param
     */
    public void exportExcel(String excelName) {
        file = new File(getSDPath() + "/Record");
        makeDir(file);
        String fileName = (String) SPUtils.get(this, "fileName", "");
        if (fileName.equals("")) {
            String excelFile = file.toString() + "/" + excelName + ".xls";
            ExcelUtils.initExcels(getRecordData(), excelFile, title, excelName, this);
//            ExcelUtils.writeObjListToExcels(getRecordData(), fileName, excelName,  getActivity());
        } else {
            ExcelUtils.writeObjListToExcels(getRecordData(), fileName, excelName, this);
        }
    }

    /**
     * 将数据集合 转化成ArrayList<ArrayList<String>>
     *
     * @return
     */
    private ArrayList<ArrayList<String>> getRecordData() {
        recordList = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            ArrayList<String> beanList = new ArrayList<String>();
//            beanList.add("1");
            beanList.add(lib);
            beanList.add(datas.get(i));
            beanList.add(time);
            recordList.add(beanList);

        }
        return recordList;
    }

    private String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        String dir = sdDir.toString();
        return dir;
    }

    public void makeDir(File dir) {
        if (!dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        dir.mkdir();
    }
}
