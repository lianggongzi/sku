package com.example.administrator.sku.XiaoWen.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sku.CommonAdapter;
import com.example.administrator.sku.DateUtils;
import com.example.administrator.sku.ExcelUtils;
import com.example.administrator.sku.R;
import com.example.administrator.sku.SPUtils;
import com.example.administrator.sku.ViewHolder;
import com.example.administrator.sku.XiaoWen.ScreenUtils;
import com.example.administrator.sku.XiaoWen.db.LibDao;
import com.example.administrator.sku.XiaoWen.db.TimeDao;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Administrator on 2018\12\5 0005.
 */

public class Scanning_Fragment extends Fragment {
    @BindView(R.id.main_lrv)
    LRecyclerView mainLrv;
    Unbinder unbinder;
    @BindView(R.id.main_btn_chongzhi)
    Button mainBtnChongzhi;
    @BindView(R.id.main_btn)
    Button mainBtn;
    @BindView(R.id.main_lib_tv)
    TextView mainLibTv;
    @BindView(R.id.main_sku_tv)
    TextView mainSkuTv;
    @BindView(R.id.main_lib_iv)
    ImageView mainLibIv;
    @BindView(R.id.main_sku_iv)
    ImageView mainSkuIv;

    private SweetAlertDialog chongfuDialog;
    private SweetAlertDialog sweetAlertDialog;
    private LRecyclerViewAdapter lRecyclerViewAdapter = null;
    private CommonAdapter<String> adapter;
    private List<String> datas = new ArrayList<>(); //PDA机屏幕上的List集合
    boolean isChongfu = false;//没有重复录入
    TimeDao timeDao;
    LibDao libDao;
    private BroadcastReceiver mBrReceiver;

    private File file;
    private ArrayList<ArrayList<String>> recordList;
    private static String[] title = {"区位", "SKU", "扫描时间"};



    public static Scanning_Fragment newInstance() {
        Scanning_Fragment fragment = new Scanning_Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scanning, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        intiAdapter();
        return view;
    }

    private void initView() {
        getsystemscandata();//注册接收数据广播，以TOAST形式显示，退出界面也可以查看数据
        timeDao = new TimeDao(getActivity());
        libDao = new LibDao(getActivity());
        chongfuDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
//        mainSku1Edt.addTextChangedListener(new JumpTextWatcher());
//        mainSku1Edt1.addTextChangedListener(new JumpText1Watcher());
    }

    private void initData(String data) {
        if (mainSkuTv.length() != 0) {
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

                    chongfuDialog.show();
                    isChongfu = true;  //重复了
                    return;
                } else {
                    isChongfu = false;  //没有重复了
                }
            }
            if (isChongfu == false) {
                chongfuDialog.dismiss();
                datas.add(data);
                lRecyclerViewAdapter.notifyDataSetChanged();
//                mainSku1Edt1.setText("");
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void intiAdapter() {
        adapter = new CommonAdapter<String>(getActivity(), R.layout.adapter_scanning, datas) {
            @Override
            public void setData(ViewHolder holder, String s) {
                holder.setText(R.id.adapter_sku, s);
            }
        };
        mainLrv.setLayoutManager(new LinearLayoutManager(getActivity()));
        lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        mainLrv.setAdapter(lRecyclerViewAdapter);
        mainLrv.setLoadMoreEnabled(false);
        mainLrv.setPullRefreshEnabled(false);
        lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
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

    //初始化并弹出对话框方法
    private void showDialog(final String type) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_layout, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        final EditText shoudong = view.findViewById(R.id.dialog_edt);
        TextView cancel = view.findViewById(R.id.cancel_btn);
        TextView sure = view.findViewById(R.id.sure_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //... To-do
                dialog.dismiss();
            }
        });

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //... To-do
                String string = shoudong.getText().toString();
                if (type.equals("lib")) {
                    mainLibTv.setText(string);
                } else {
                    mainSkuTv.setText(string);
                    initData(string);
                }
                dialog.dismiss();
            }
        });

        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4
//        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(getActivity()) / 4 * 3), LinearLayout.LayoutParams.WRAP_CONTENT);
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


    private void initOutExcel(String time, String lib, List list) {
        if (list.size() == 0 || lib.equals("")) {
            Toast.makeText(getActivity(), "请输入资料", Toast.LENGTH_SHORT).show();
        } else {
            boolean isdirectory = false;
            List<String> list1 = list;
            for (int i = 0; i < list1.size(); i++) {
                isdirectory = libDao.insert(time, lib, datas.get(i).toString());//保存所有数据
            }
            if (isdirectory == true) {
                Toast.makeText(getActivity(), "保存并导出成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "保存失败", Toast.LENGTH_SHORT).show();
            }
        }
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
                    if (mainLibTv.getText().equals("")) {
                        mainLibTv.setText(replaceBlank(datat));
                    } else {
                        mainSkuTv.setText(replaceBlank(datat));
                        initData(datat);
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(getstr);
        getActivity().registerReceiver(mBrReceiver, filter);
        Toast.makeText(getActivity(), "注册广播完成，自动接收数据", Toast.LENGTH_LONG).show();
    }

//    private class JumpTextWatcher implements TextWatcher {
//
//        @Override
//        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable editable) {
//            String data = replaceBlank(editable.toString());
//            if (data.equals("")) {
////                editText1.setSelection(editText2.getText().length());//若editText2有内容就将光标移动到文本末尾
//                mainSku1Edt.requestFocus();
//                return;
//            } else {
//                mainSku1Edt1.requestFocus();//让editText2获取焦点
//            }
//        }
//    }


//    private class JumpText1Watcher implements TextWatcher {
//
//        @Override
//        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable editable) {
//            String data = editable.toString();
//            initData(data);
//        }
//    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.main_lib_iv, R.id.main_sku_iv, R.id.main_btn_chongzhi, R.id.main_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_btn_chongzhi:
                mainLibTv.setText("");
                mainSkuTv.setText("");
                datas.clear();
                lRecyclerViewAdapter.notifyDataSetChanged();
//                libDao.delete();
//                timeDao.delete();
                break;
            case R.id.main_btn:
                timeDao.insert(DateUtils.getCurrentTime3());
//                timeDao.insert("2018-12-14");
                String lib = mainLibTv.getText().toString();
                initOutExcel(DateUtils.getCurrentTime3(), lib, datas);
//                initOutExcel("2018-12-14", lib, datas);
                exportExcel(DateUtils.getCurrentTime3());
                mainLibTv.setText("");
                mainSkuTv.setText("");
                datas.clear();
                lRecyclerViewAdapter.notifyDataSetChanged();
                break;
            case R.id.main_lib_iv:
                showDialog("lib");
                break;
            case R.id.main_sku_iv:
                showDialog("sku");
                break;
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
        String fileName = (String) SPUtils.get(getActivity(), "fileName", "");
        if (fileName.equals("")) {
            String excelFile = file.toString() + "/" + excelName + ".xls";
            ExcelUtils.initExcels(getRecordData(), excelFile, title, excelName, getActivity());
//            ExcelUtils.writeObjListToExcels(getRecordData(), fileName, excelName,  getActivity());
        } else {
            ExcelUtils.writeObjListToExcels(getRecordData(), fileName, excelName, getActivity());
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
            beanList.add(mainLibTv.getText().toString());
            beanList.add(datas.get(i));
            beanList.add(DateUtils.getCurrentTime3());
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
