package com.hdu.wsn.uDiskReader.ui.presenter;

/**
 * Created by ASUS on 2017/7/24 0024.
 */

public interface FilePresenter {
    /**
     * 判断是不是根路径
     * @return 是否是根路径
     */
    boolean isRootView();

    /**
     * 返回上一层目录
     */
    void returnPreFolder();

    /**
     * 刷新
     */
    void refresh();

    /**
     * 设置登录状态
     * @param loginFlag 登录状态
     */
    void setLoginFlag(boolean loginFlag);

    /**
     * 获取登录状态
     * @return 登录状态
     */
    boolean isLogin();

    /**
     * 注销广播
     */
    void unRegisterReceive();
}
