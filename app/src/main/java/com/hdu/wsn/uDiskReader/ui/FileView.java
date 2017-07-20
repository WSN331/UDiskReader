package com.hdu.wsn.uDiskReader.ui;

/**
 * Created by ASUS on 2017/7/19 0019.
 */

public interface FileView {

    /**
     *  获取现在的标题
     * @return 现在标题
     */
    String getNowText();

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
    void setAdapter(FileAdapter adapter);

    /**
     * 设置刷新条
     * @param b 设置控制
     */
    void setRefreshing(boolean b);

    /**
     * 显示密码框
     */
    void showPasswordView();
}
