package com.hdu.wsn.uDiskReader.ui.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.provider.DocumentFile;
import android.view.View;

import com.hdu.wsn.uDiskReader.ui.view.DocumentFileAdapter;
import com.hdu.wsn.uDiskReader.ui.view.FileView;
import com.hdu.wsn.uDiskReader.usb.file.FileUtil;
import com.hdu.wsn.uDiskReader.usb.jnilib.UDiskLib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ASUS on 2017/7/19 0019.
 */

public class DocumentFilePresenter implements FilePresenter{
    private FileView fileView;
    private Context context;
    private boolean loginFlag = false;    // 登录标记
    private List<DocumentFile> fileList;
    private List<DocumentFile> currentFolderList = new ArrayList<>();
    private DocumentFile currentFolder;  //当前目录
    private Uri rootUri;

    public DocumentFilePresenter(FileView fileView, Context context, Uri rootUri) {
        this.fileView = fileView;
        this.context = context;
        this.rootUri = rootUri;
        fileList = new ArrayList<>();
        registerReceiver();
    }

    /**
     * 读取设备
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void readDeviceList() {
        context.getContentResolver().takePersistableUriPermission(rootUri,Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        currentFolder = DocumentFile.fromTreeUri(context, rootUri);
        getAllFiles();
    }

    /**
     * 获取文件
     */
    private void getAllFiles() {
        UDiskLib.Init(context);
        fileList.clear();
        DocumentFile files[] = currentFolder.listFiles();
        if (files != null) {
            for (DocumentFile f : files) {
                System.out.println(f);
                fileList.add(f);
            }
            Collections.sort(fileList, new Comparator<DocumentFile>() {
                @Override
                public int compare(DocumentFile o1, DocumentFile o2) {
                    if (o1.isDirectory()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
            changeTitle();
            DocumentFileAdapter adapter = new DocumentFileAdapter(fileList);
            adapter.setSecretHeader(currentFolderList.size() == 0 && !loginFlag);
            fileView.setAdapter(adapter);
            initListener(adapter);
            fileView.setRefreshing(false);
        }
    }

    /**
     * 变化标题
     */
    private void changeTitle() {
        String nowText,preText;
        nowText = " > " + currentFolder.getName();
        if (currentFolderList != null && currentFolderList.size() > 0) {
            if (currentFolderList.size() == 1 && loginFlag) {
                preText = "> 私密空间";
            } else {
                preText = currentFolderList.get(currentFolderList.size() - 1).getName();
            }
        } else if (loginFlag) {
            preText = nowText;
            nowText = "> 私密空间";
        } else {
            preText = "";
        }
        fileView.setTitle(preText, nowText);
    }

    /**
     * 列表项点击事件
     * @param adapter 适配器
     */
    private void initListener(final DocumentFileAdapter adapter) {
        adapter.setOnRecyclerViewItemClickListener(new DocumentFileAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int index = getRealPosition(position);
                if (index > 0) {
                    DocumentFile file = fileList.get(index - 1);
                    if (file.isDirectory()) {
                        currentFolderList.add(currentFolder);
                        currentFolder = file;
                        getAllFiles();
                    } else {
                        FileUtil.openDocumentFile(file, context);
                    }
                } else {
                    fileView.showPasswordView();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                int index = getRealPosition(position);
                if (index > 0) {
                    DocumentFile file = fileList.get(index - 1);
                    boolean result = FileUtil.deleteFile(file);
                    if (result) {
                        fileView.getAdapter().removeData(index-1);
                    }
                }
            }
        });

    }

    /**
     * 获取文件下标+1 （因此这个值如果是0代表该项不是真实文件，而是私密空间栏）
     * @param position 列表下标
     * @return 文件下标+1
     */
    private int getRealPosition(int position) {
        int index = position;
        if (currentFolderList.size() > 0 || loginFlag) {
            index ++;
        }
        return index;
    }

    @Override
    public boolean isRootView() {
        return currentFolderList.size() > 0;
    }

    @Override
    public void returnPreFolder() {
        currentFolder = currentFolderList.get(currentFolderList.size() - 1);
        currentFolderList.remove(currentFolderList.size() - 1);
        getAllFiles();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void refresh() {
        if (currentFolder == null) {
            readDeviceList();
        } else {
            getAllFiles();
        }
    }

    @Override
    public void setLoginFlag(boolean loginFlag) {
        this.loginFlag = loginFlag;
    }

    @Override
    public boolean isLogin() {
        return loginFlag;
    }

    @Override
    public void unRegisterReceive() {
        if (usbReceiver != null) {
            context.unregisterReceiver(usbReceiver);
            usbReceiver = null;
        }
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

        context.registerReceiver(usbReceiver, usbDeviceStateFilter);

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
                    fileView.onUDiskInsert(intent);
                    break;
                //接收到U盘拔出的广播
                case UsbManager.ACTION_USB_DEVICE_DETACHED:
                    fileView.onUDiskRemove(intent);
                    break;
                case Intent.ACTION_MEDIA_MOUNTED:
                    fileView.onUDiskInsert(intent);
                    break;
                case Intent.ACTION_MEDIA_REMOVED:
                    fileView.onUDiskRemove(intent);
                    break;
            }
        }
    };

}
