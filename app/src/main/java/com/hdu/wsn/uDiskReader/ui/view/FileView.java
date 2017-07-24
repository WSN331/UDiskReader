package com.hdu.wsn.uDiskReader.ui.view;

import android.content.Intent;

/**
 * Created by ASUS on 2017/7/19 0019.
 */

public interface FileView {

    /**
     * 设置标题
     * @param preText 前标题
     * @param nowText 现标题
     */
    void setTitle(String preText, String nowText);

    /**
     * 给列表设置适配器
     * @param adapter 适配器
     */
    void setAdapter(DocumentFileAdapter adapter);

    /**
     * 获取适配器
     * @return 适配器
     */
    DocumentFileAdapter getAdapter();

    /**
     * 设置刷新条
     * @param b 设置控制
     */
    void setRefreshing(boolean b);

    /**
     * 显示密码框
     */
    void showPasswordView();

    /**
     * u盘插入的操作
     * @param intent
     */
    void onUDiskInsert(Intent intent);

    /**
     * u盘移除的操作
     * @param intent
     */
    void onUDiskRemove(Intent intent);
}
