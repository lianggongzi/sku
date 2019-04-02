package com.example.administrator.sku;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sku.XiaoWen.BaseActivity;
import com.github.jdsjlzx.interfaces.*;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class SKU_N5Activity extends BaseActivity {

    @BindView(R.id.sku_yuan)
    EditText skuYuan;
    @BindView(R.id.sku_1)
    EditText sku1;
    @BindView(R.id.main_btn_chongzhi)
    Button mainBtnChongzhi;
    @BindView(R.id.main_btn)
    Button mainBtn;
    @BindView(R.id.sku_lry)
    LRecyclerView skuLry;
    @BindView(R.id.sku_zong)
    TextView skuZong;
    private LRecyclerViewAdapter lRecyclerViewAdapter = null;
    private CommonAdapter<String> adapter;
    private List<String> datas = new ArrayList<>(); //PDA机屏幕上的List集合
    private SweetAlertDialog sweetAlertDialog;
    private SweetAlertDialog xingtongDialog;
    private File file;
    private ArrayList<ArrayList<String>> recordList;
    private static String[] title = {"SKU", "数量"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sku__n5);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//禁止输入法
        xingtongDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        intiAdapter();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {//未获取权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //当用户第一次申请拒绝时，再次申请该权限调用
                Toast.makeText(this, "拨打电话权限", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x01);
        } else {//已经获得了权限
            mainBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initReset();
                }
            });
        }
        skuYuan.addTextChangedListener(new JumpTextWatcher());
        sku1.addTextChangedListener(new JumpText1Watcher());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0x01) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//授权成功
                mainBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initReset();
                    }
                });
            } else {//授权失败
                Toast.makeText(this, "获取权限失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void updateCount() {

    }

    @Override
    public void updateList(String data) {
        if (!data.equals("Decode is interruptted or timeout ...")){
            String skuyuan = skuYuan.getText().toString();
            if (skuyuan.isEmpty()) {
                skuYuan.setText(data);
            } else {
                sku1.setText(data);
                if (data.equals(skuyuan)) {
                    datas.add(data);
                    lRecyclerViewAdapter.notifyDataSetChanged();
                    xingtongDialog.dismiss();
                } else {
                    xingtongDialog
                            .setTitleText("SKU不匹配...");
                    xingtongDialog.setConfirmText("确定");
                    xingtongDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            xingtongDialog.dismiss();
                        }
                    });
                    xingtongDialog.show();
                }
            }
            skuZong.setText("总数："+datas.size());
        }
    }

    private class JumpTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String data = editable.toString();

            if (data.equals("")) {
//                editText1.setSelection(editText2.getText().length());//若editText2有内容就将光标移动到文本末尾
                skuYuan.requestFocus();
                return;
            } else {
                sku1.requestFocus();
            }
        }
    }

    private class JumpText1Watcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String data = editable.toString();
          String  skuyuan= skuYuan.getText().toString();
//            sku1.setText(data);
            if (data.equals(skuyuan)) {
                datas.add(data);
                lRecyclerViewAdapter.notifyDataSetChanged();
                xingtongDialog.dismiss();
            } else {
                xingtongDialog
                        .setTitleText("SKU不匹配...");
                xingtongDialog.setConfirmText("确定");
                xingtongDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        xingtongDialog.dismiss();
                    }
                });
                xingtongDialog.show();
            }
            skuZong.setText("总数："+datas.size());
        }
    }

    private void intiAdapter() {
        adapter = new CommonAdapter<String>(this, R.layout.adapter_main, datas) {
            @Override
            public void setData(ViewHolder holder, String s) {
                holder.setText(R.id.adapter_sku, "SKU：" + s);

            }
        };
        skuLry.setLayoutManager(new LinearLayoutManager(this));
        lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        skuLry.setAdapter(lRecyclerViewAdapter);
        skuLry.setLoadMoreEnabled(false);
        skuLry.setPullRefreshEnabled(false);
        skuLry.refreshComplete(20);
        lRecyclerViewAdapter.setOnItemClickListener(new com.github.jdsjlzx.interfaces.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                sweetAlertDialog = new SweetAlertDialog(SKU_N5Activity.this, SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialog.showCancelButton(true);
                sweetAlertDialog.setCancelText("取消");
                sweetAlertDialog.setTitleText("确定删除此条信息?");
                sweetAlertDialog.setConfirmText("确定");
                sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                });
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        datas.remove(position);
                        lRecyclerViewAdapter.notifyDataSetChanged();
                        sweetAlertDialog.dismiss();
                        skuZong.setText("总数："+datas.size());
                    }
                });
                sweetAlertDialog.show();
            }
        });
    }

    @OnClick({R.id.main_btn_chongzhi, R.id.main_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_btn_chongzhi:
                skuYuan.setText("");
                sku1.setText("");
                datas.clear();
                lRecyclerViewAdapter.notifyDataSetChanged();
                skuZong.setText("总数："+datas.size());
                break;
            case R.id.main_btn:
                initReset();
                break;
        }
    }

    public void initReset() {
        exportExcel(DateUtils.getCurrentTime1());
        skuYuan.setText("");
        sku1.setText("");
        datas.clear();
        lRecyclerViewAdapter.notifyDataSetChanged();
        skuZong.setText("总数："+datas.size());
    }

    /**
     * 导出excel
     *
     * @param
     */
    public void exportExcel(String excelName) {
        file = new File(getSDPath() + "/Record");
        makeDir(file);
//        String fileName = (String) SPUtils.get(getActivity(), "fileName", "");
//        if (fileName.equals("")) {
//            String excelFile = file.toString() + "/" + excelName + ".xls";
//            ExcelUtils.initExcels(getRecordData(), excelFile, title, excelName, getActivity());
////            ExcelUtils.writeObjListToExcels(getRecordData(), fileName, excelName,  getActivity());
//        } else {
//            ExcelUtils.writeObjListToExcels(getRecordData(), fileName, excelName, getActivity());
//        }
        ExcelUtils.initExcel(file.toString() + "/" + excelName + ".xls", title);
        ExcelUtils.writeObjListToExcel(getRecordData(), file.toString() + "/" + excelName + ".xls", this);
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
            beanList.add(datas.get(i));
            beanList.add(String.valueOf(i + 1));
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
