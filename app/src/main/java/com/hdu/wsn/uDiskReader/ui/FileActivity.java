package com.hdu.wsn.uDiskReader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.storage.StorageManager;
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
import com.hdu.wsn.uDiskReader.usb.file.FileUtil;
import com.hdu.wsn.uDiskReader.usb.jnilib.UDiskConnection;
import com.hdu.wsn.uDiskReader.usb.jnilib.UDiskLib;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, FileView{
    private static String TAG = "MainActivity";

    private Context context;
    private TextView tvDebug, preFolder;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private UDiskLib uDiskLib;
    private boolean alreadyLogin = false;    // SDK操作完后判断是否还处于登录的标记
    private FileReader fileReader;
    private FileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        fileReader = new FileReader(this, context);
        initView();
        registerReceiver();
        onRefresh();
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

    /**
     * 注册接收器
     */
    private void registerReceiver() {
        //监听otg插入 拔出
        IntentFilter usbDeviceStateFilter = new IntentFilter();
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        usbDeviceStateFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        usbDeviceStateFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        usbDeviceStateFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        usbDeviceStateFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        usbDeviceStateFilter.addDataScheme("file");

        registerReceiver(usbReceiver, usbDeviceStateFilter);

    }

    /**
     * u盘插拔广播接收器
     */
    private BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                //接收到U盘插入的广播
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    UsbDevice device_in = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device_in != null) {
                        //进行读写操作
                        Log.e(TAG, "U盘插入");
                        fileReader.setLoginFlag(alreadyLogin);
                        alreadyLogin = false;
                        onRefresh();
                    }
                    break;
                //接收到U盘拔出的广播
                case UsbManager.ACTION_USB_DEVICE_DETACHED:
                    UsbDevice device_out = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device_out != null) {
                        //更新界面
                        Log.e(TAG, "U盘拔出");
                        adapter.notifyDataSetChanged();
                        doLogout();
                    }
                    break;
                case Intent.ACTION_MEDIA_MOUNTED:
                    Log.e(TAG, "U盘插入");
                    fileReader.setLoginFlag(alreadyLogin);
                    alreadyLogin = false;
                    onRefresh();
                    break;
                case Intent.ACTION_MEDIA_REMOVED:
                    device_out = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device_out != null) {
                        //更新界面
                        Log.e(TAG, "U盘拔出");
                        adapter.notifyDataSetChanged();
                        doLogout();
                    }
                    break;
            }
        }
    };

    //刷新界面
    @Override
    public void onRefresh() {
        tvDebug.setText("");
        swipeRefreshLayout.setRefreshing(true);
        fileReader.refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (usbReceiver != null) {
            unregisterReceiver(usbReceiver);
            usbReceiver = null;
        }
        doLogout();
    }

    @Override
    public void onBackPressed() {
        if (fileReader.isRootView()) {
            fileReader.returnPreFolder();
        } else if (fileReader.isLogin()) {
            logout();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public String getNowText() {
        return tvDebug.getText().toString();
    }

    @Override
    public void setTitle(String preText, String nowText) {
        preFolder.setText(preText);
        tvDebug.setText(nowText);
    }

    @Override
    public void setAdapter(FileAdapter adapter) {
        this.adapter = adapter;
        recyclerView.setAdapter(adapter);
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
                fileReader.setLoginFlag(alreadyLogin);
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
        //TODO test
        UDiskConnection.create(uDiskLib, new UDiskConnection.Action() {
            @Override
            public int action(UDiskLib diskLib) {
                return uDiskLib.smiLogoutDevice();
            }
        }).success(new UDiskConnection.CallBack() {
            @Override
            public void call(int result) {
                Toast.makeText(context, "logOut success", Toast.LENGTH_SHORT);
                fileReader.setLoginFlag(false);
                onRefresh();
            }
        }).close().doAction();
    }

}
