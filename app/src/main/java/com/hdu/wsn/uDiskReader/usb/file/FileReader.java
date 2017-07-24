package com.hdu.wsn.uDiskReader.usb.file;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.storage.StorageManager;
import android.view.View;

import com.hdu.wsn.uDiskReader.ui.view.FileAdapter;
import com.hdu.wsn.uDiskReader.ui.view.FileView;
import com.hdu.wsn.uDiskReader.usb.jnilib.UDiskLib;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ASUS on 2017/7/19 0019.
 */

public class FileReader {
    private FileView fileView;
    private Context context;
    private boolean loginFlag = false;    // 登录标记
    private List<File> fileList;
    private List<File> currentFolderList = new ArrayList<>();
    private File currentFolder;  //当前目录

    public FileReader(FileView fileView, Context context) {
        this.fileView = fileView;
        this.context = context;
        fileList = new ArrayList<>();
//        registerReceiver();
    }

//    /**
//     * 读取设备
//     */
//    private void readDeviceList() {
//        String[] result = null;
//        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
//        try {
//            Method method = StorageManager.class.getMethod("getVolumePaths");
//            method.setAccessible(true);
//            try {
//                result = (String[]) method.invoke(storageManager);
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
//            for (int i = 0; i < result.length; i++) {
//                System.out.println("path----> " + result[i] + "\n");
//                if (result[i] != null && result[i].startsWith("/storage") && !result[i].startsWith("/storage/emulated/0")) {
//                    currentFolder = new File(result[i]);
//                    getAllFiles();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 获取文件
//     */
//    private void getAllFiles() {
//        UDiskLib.Init(context);
//        fileList.clear();
//        File files[] = currentFolder.listFiles();
//        if (files != null) {
//            for (File f : files) {
//                System.out.println(f);
//                fileList.add(f);
//            }
//            Collections.sort(fileList, new Comparator<File>() {
//                @Override
//                public int compare(File o1, File o2) {
//                    if (o1.isDirectory()) {
//                        return -1;
//                    } else {
//                        return 1;
//                    }
//                }
//            });
//            changeTitle();
//            FileAdapter adapter = new FileAdapter(fileList);
//            adapter.setSecretHeader(currentFolderList.size() == 0 && !loginFlag);
//            fileView.setAdapter(adapter);
//            initListener(adapter);
//            fileView.setRefreshing(false);
//        }
//    }
//
//    /**
//     * 变化标题
//     */
//    private void changeTitle() {
//        String nowText,preText;
//        nowText = " > " + currentFolder.getName();
//        if (currentFolderList != null && currentFolderList.size() > 0) {
//            if (currentFolderList.size() == 1 && loginFlag) {
//                preText = "> 私密空间";
//            } else {
//                preText = currentFolderList.get(currentFolderList.size() - 1).getName();
//            }
//        } else if (loginFlag) {
//            preText = nowText;
//            nowText = "> 私密空间";
//        } else {
//            preText = "";
//        }
//        fileView.setTitle(preText, nowText);
//    }
//
//    /**
//     * 列表项点击事件
//     * @param adapter 适配器
//     */
//    private void initListener(final FileAdapter adapter) {
//        adapter.setOnRecyclerViewItemClickListener(new FileAdapter.OnRecyclerViewItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                int index = getRealPosition(position);
//                if (index > 0) {
//                    File file = fileList.get(index - 1);
//                    if (file.isDirectory()) {
//                        currentFolderList.add(currentFolder);
//                        currentFolder = file;
//                        getAllFiles();
//                    } else {
//                        FileUtil.openFile(file, context);
//                    }
//                } else {
//                    fileView.showPasswordView();
//                }
//            }
//
//            @Override
//            public void onItemLongClick(View view, int position) {
//                System.out.println("xxxxxxx");
//                int index = getRealPosition(position);
//                if (index > 0) {
//                    File file = fileList.get(index - 1);
////                    final Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
////                    context.startActivity(intent);
//                    boolean result = FileUtil.deleteFile(file);
//                    if (result) {
//                        fileView.getAdapter().removeData(index-1);
//                    }
//                }
//            }
//        });
//
//    }
//
//    /**
//     * 获取文件下标+1 （因此这个值如果是0代表该项不是真实文件，而是私密空间栏）
//     * @param position 列表下标
//     * @return 文件下标+1
//     */
//    private int getRealPosition(int position) {
//        int index = position;
//        if (currentFolderList.size() > 0 || loginFlag) {
//            index ++;
//        }
//        return index;
//    }
//
//    /**
//     * 判断是不是根路径
//     * @return 是否是根路径
//     */
//    public boolean isRootView() {
//        return currentFolderList.size() > 0;
//    }
//
//    /**
//     * 返回上一层目录
//     */
//    public void returnPreFolder() {
//        currentFolder = currentFolderList.get(currentFolderList.size() - 1);
//        currentFolderList.remove(currentFolderList.size() - 1);
//        getAllFiles();
//    }
//
//    /**
//     * 刷新
//     */
//    public void refresh() {
//        if (currentFolder == null) {
//            readDeviceList();
//        } else {
//            getAllFiles();
//        }
//    }
//
//    public void setLoginFlag(boolean loginFlag) {
//        this.loginFlag = loginFlag;
//    }
//
//    public boolean isLogin() {
//        return loginFlag;
//    }
//
//    /**
//     * 注册接收器
//     */
//    private void registerReceiver() {
//        //监听otg插入 拔出
//        IntentFilter usbDeviceStateFilter = new IntentFilter();
//        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
//        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
//        usbDeviceStateFilter.addAction(Intent.ACTION_MEDIA_EJECT);
//        usbDeviceStateFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
//        usbDeviceStateFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
//        usbDeviceStateFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
//        usbDeviceStateFilter.addDataScheme("file");
//
//        context.registerReceiver(usbReceiver, usbDeviceStateFilter);
//
//    }
//
//    /**
//     * 注销广播
//     */
//    public void unRegisterReceive() {
//        if (usbReceiver != null) {
//            context.unregisterReceiver(usbReceiver);
//            usbReceiver = null;
//        }
//    }
//
//    /**
//     * u盘插拔广播接收器
//     */
//    private BroadcastReceiver usbReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            switch (action) {
//                //接收到U盘插入的广播
//                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
//                    fileView.onUDiskInsert(intent);
//                    break;
//                //接收到U盘拔出的广播
//                case UsbManager.ACTION_USB_DEVICE_DETACHED:
//                    fileView.onUDiskRemove(intent);
//                    break;
//                case Intent.ACTION_MEDIA_MOUNTED:
//                    fileView.onUDiskInsert(intent);
//                    break;
//                case Intent.ACTION_MEDIA_REMOVED:
//                    fileView.onUDiskRemove(intent);
//                    break;
//            }
//        }
//    };

}
