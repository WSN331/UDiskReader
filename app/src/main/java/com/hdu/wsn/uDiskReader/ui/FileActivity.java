package com.hdu.wsn.uDiskReader.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ethanco.lib.PasswordDialog;
import com.ethanco.lib.abs.OnPositiveButtonListener;
import com.hdu.wsn.uDiskReader.R;
import com.hdu.wsn.uDiskReader.ui.presenter.DocumentFilePresenter;
import com.hdu.wsn.uDiskReader.ui.presenter.FilePresenter;
import com.hdu.wsn.uDiskReader.ui.view.DocumentFileAdapter;
import com.hdu.wsn.uDiskReader.ui.view.FileView;
import com.hdu.wsn.uDiskReader.ui.view.MyItemDecoration;
import com.hdu.wsn.uDiskReader.usb.jnilib.UDiskConnection;
import com.hdu.wsn.uDiskReader.usb.jnilib.UDiskLib;

public class FileActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, FileView {
    private static String TAG = "MainActivity";

    private Context context;
    private TextView tvDebug, preFolder;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private UDiskLib uDiskLib;
    private boolean alreadyLogin = false;    // SDK操作完后判断是否还处于登录的标记
    private FilePresenter filePresenter;
    private DocumentFileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        initView();
        initPermission();
    }

    /**
     * 初始化权限
     */
    private void initPermission() {
        Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);//ACTION_OPEN_DOCUMENT
        startActivityForResult(intent, 42);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == 42 && resultCode == Activity.RESULT_OK) {
            Uri rootUri;
            if (resultData != null) {
                rootUri = resultData.getData();
                filePresenter = new DocumentFilePresenter(this, context, rootUri);
                onRefresh();
            }
        }
    }

    /**
     * 初始化界面
     */
    private void initView() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        preFolder = (TextView) findViewById(R.id.pre_folder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyItemDecoration(this, MyItemDecoration.VERTICAL_LIST));
        swipeRefreshLayout.setColorSchemeColors(Color.YELLOW, Color.BLUE, Color.RED, Color.GREEN);
        swipeRefreshLayout.setOnRefreshListener(this);
        tvDebug = (TextView) findViewById(R.id.tv_debug);
        initClick();
    }

    /**
     * 初始化点击事件
     */
    private void initClick() {
        preFolder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (preFolder.getText() != null && !preFolder.getText().equals("")) {
                    onBackPressed();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        tvDebug.setText("");
        swipeRefreshLayout.setRefreshing(true);
        if (filePresenter == null) {
            initPermission();
        } else {
            filePresenter.refresh();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        filePresenter.unRegisterReceive();
        doLogout();
    }

    @Override
    public void onBackPressed() {
        if (filePresenter.isRootView()) {
            filePresenter.returnPreFolder();
        } else if (filePresenter.isLogin()) {
            logout();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void setTitle(String preText, String nowText) {
        preFolder.setText(preText);
        tvDebug.setText(nowText);
    }

    @Override
    public void setAdapter(DocumentFileAdapter adapter) {
        this.adapter = adapter;
        recyclerView.setAdapter(adapter);
    }

    @Override
    public DocumentFileAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void setRefreshing(boolean b) {
        swipeRefreshLayout.setRefreshing(b);
    }

    @Override
    public void showPasswordView() {
        PasswordDialog.Builder builder = new PasswordDialog.Builder(FileActivity.this)
                .setTitle(R.string.please_input_password)  //Dialog标题
                .setBoxCount(4) //设置密码位数
                .setBorderNotFocusedColor(R.color.colorSecondaryText) //边框颜色
                .setDotNotFocusedColor(R.color.colorSecondaryText)  //密码圆点颜色
                .setPositiveListener(new OnPositiveButtonListener() {
                    @Override //确定
                    public void onPositiveClick(DialogInterface dialog, int which, String text) {
                        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                        doLogin(text);
                    }
                })
                .setNegativeListener(new DialogInterface.OnClickListener() {
                    @Override //取消
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "取消", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.create().show();
    }

    @Override
    public void onUDiskInsert(Intent intent) {
        //进行读写操作
        Log.e(TAG, "U盘插入");
        filePresenter.setLoginFlag(alreadyLogin);
        alreadyLogin = false;
        initPermission();
    }

    @Override
    public void onUDiskRemove(Intent intent) {
        UsbDevice device_out = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (device_out != null) {
            //更新界面
            Log.e(TAG, "U盘拔出");
            adapter.notifyDataSetChanged();
            doLogout();
        }
    }

    /**
     * 执行登录
     *
     * @param text 密码
     */
    private void doLogin(final String text) {
        uDiskLib = UDiskLib.create(context);
        UDiskConnection.create(uDiskLib, new UDiskConnection.Action() {
            @Override
            public int action(UDiskLib diskLib) {
                return uDiskLib.smiLoginDeviceByStr(text);
            }
        }).success(new UDiskConnection.CallBack() {
            @Override
            public void call(int result) {
                Toast.makeText(context, "login success", Toast.LENGTH_SHORT);
                alreadyLogin = true;
                filePresenter.setLoginFlag(alreadyLogin);
                onRefresh();
            }
        }).close().doAction();

    }

    /**
     * 退出登录的弹框
     */
    private void logout() {
        new AlertDialog.Builder(FileActivity.this)
                .setTitle("退出登录")
                .setMessage("确定退出登录吗？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doLogout();
                    }
                })
                .setNegativeButton("否", null)
                .show();
    }

    /**
     * 执行登出
     */
    private void doLogout() {
        uDiskLib = UDiskLib.create(context);
        UDiskConnection.create(uDiskLib, new UDiskConnection.Action() {
            @Override
            public int action(UDiskLib diskLib) {
                return uDiskLib.smiLogoutDevice();
            }
        }).success(new UDiskConnection.CallBack() {
            @Override
            public void call(int result) {
                Toast.makeText(context, "logOut success", Toast.LENGTH_SHORT);
                filePresenter.setLoginFlag(false);
                onRefresh();
            }
        }).close().doAction();
    }

}
