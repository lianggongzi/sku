package com.example.administrator.sku;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.jdsjlzx.interfaces.*;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    EditText editText1;
    Button button;
    LRecyclerView lRecyclerView;
    Button chongzhi;
    private SweetAlertDialog chongfuDialog;
    private SweetAlertDialog xingtongDialog;

    private LRecyclerViewAdapter lRecyclerViewAdapter = null;
    private CommonAdapter<String> adapter;
    private List<String> datas = new ArrayList<>(); //PDA机屏幕上的List集合
    boolean isChongfu = false;//没有重复录入
    private File file;
    private String fileName;
    private ArrayList<ArrayList<String>> recordList;
    private static String[] title = {"SKU","数量"};
    private SweetAlertDialog sweetAlertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//禁止输入法


        initView();
        intiAdapter();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {//未获取权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //当用户第一次申请拒绝时，再次申请该权限调用
                Toast.makeText(this, "拨打电话权限", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x01);
        } else {//已经获得了权限
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    exportExcel(DateUtils.getCurrentTime1());
                    datas.clear();
                    lRecyclerViewAdapter.notifyDataSetChanged();
                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0x01) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//授权成功
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        exportExcel(DateUtils.getCurrentTime1());
                    }
                });
            } else {//授权失败
                Toast.makeText(this, "获取权限失败", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void intiAdapter() {
        adapter = new CommonAdapter<String>(this, R.layout.adapter_main, datas) {
            @Override
            public void setData(ViewHolder holder, String s) {
                holder.setText(R.id.adapter_sku, "SKU：" + s);
            }
        };
        lRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        lRecyclerView.setAdapter(lRecyclerViewAdapter);
        lRecyclerView.setLoadMoreEnabled(false);
        lRecyclerView.setPullRefreshEnabled(false);
        lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                sweetAlertDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE);
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
                    }
                });
                sweetAlertDialog.show();
            }
        });
    }

    private void initView() {
        chongfuDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        xingtongDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        editText = findViewById(R.id.main_sku1_edt);
        editText1 = findViewById(R.id.main_sku1_edt1);
        button = findViewById(R.id.main_btn);

        lRecyclerView = findViewById(R.id.main_lrv);
        chongzhi = findViewById(R.id.main_btn_chongzhi);
        chongzhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
                editText1.setText("");
//                datas.clear();
//                lRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
        editText.addTextChangedListener(new JumpTextWatcher());
        editText1.addTextChangedListener(new JumpText1Watcher());

    }


    public static String replaceBlank(String src) {
        String dest = "";
        if (src != null) {
            Pattern pattern = Pattern.compile("\t|\r|\n|\\s*");
            Matcher matcher = pattern.matcher(src);
            dest = matcher.replaceAll("");
        }
        return dest;
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
            String data = replaceBlank(editable.toString());
            Log.d("feng","----------"+data+"=======");
            if (data.equals("")) {
//                editText1.setSelection(editText2.getText().length());//若editText2有内容就将光标移动到文本末尾
                editText.requestFocus();
                return;
            } else {
                editText1.requestFocus();//让editText2获取焦点
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

            if (editText1.length() != 0) {
                if (data.equals(editText.getText().toString())) {
                    Log.d("feng", "相同-------");

                    for (int i = 0; i < datas.size(); i++) {
                        if (data.equals(datas.get(i))) {
                            chongfuDialog
                                    .setTitleText("重复录入...");
                            chongfuDialog.setConfirmText("确定");
                            chongfuDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    chongfuDialog.dismiss();
                                }
                            });
                            xingtongDialog.dismiss();
                            chongfuDialog.show();
                            isChongfu = true;  //重复了
                            return;
                        } else {
                            isChongfu = false;  //没有重复了
                        }
                    }
                    if (isChongfu == false) {
                        xingtongDialog.dismiss();
                        chongfuDialog.dismiss();
                        datas.add(data);
                        lRecyclerViewAdapter.notifyDataSetChanged();
                        editText.setText("");
                        editText1.setText("");

                    }
                } else {
                    Log.d("feng", "不不相同-------");
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
            beanList.add(String.valueOf(i+1));
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
