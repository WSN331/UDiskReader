package com.hdu.wsn.uDiskReader.usb.jnilib;

/**
 * Created by ASUS on 2017/7/12 0012.
 */

/**
 * 用于U盘连接的回调
 */
public class UDiskConnection {
    private UDiskConnection.CallBack success;
    private UDiskConnection.CallBack error;
    private UDiskConnection.Action action;
    private UDiskLib diskLib;
    private boolean isClose = false;

    /**
     * 创建连接
     * @param diskLib u盘连接库对象
     * @param action 操作
     * @return 连接
     */
    public static UDiskConnection create(UDiskLib diskLib, UDiskConnection.Action action) {
        UDiskConnection connection = new UDiskConnection();
        return connection.action(diskLib, action);
    }

    /**
     * 指定需要执行的操作
     * @param diskLib u盘连接库对象
     * @param action 操作
     * @return 连接
     */
    public UDiskConnection action(UDiskLib diskLib, UDiskConnection.Action action) {
        this.diskLib = diskLib;
        this.action = action;
        return this;
    }

    /**
     * 指定成功的回调
     * @param success 回调对象
     * @return 连接本身
     */
    public UDiskConnection success(UDiskConnection.CallBack success) {
        this.success = success;
        return this;
    }

    /**
     * 指定失败的回调
     * @param error 回调
     * @return 连接本身
     */
    public UDiskConnection error(UDiskConnection.CallBack error) {
        this.error = error;
        return this;
    }

    /**
     * 指定连接是否在执行后关闭
     * @return 连接本身
     */
    public UDiskConnection close() {
        this.isClose = true;
        return this;
    }

    /**
     * 执行操作
     */
    public void doAction() {
        int result = action.action(diskLib);
        if (result == SmiErrDef.S_OK) {
            if (success != null) {
                success.call(result);
            }
        } else {
            if (error != null) {
                error.call(result);
            }
        }
        if (diskLib !=null && isClose) {
            diskLib.smiCloseDisk();
        }
    }

    /**
     * 操作结果的回调接口
     */
    public interface CallBack {
        /**
         * 操作回调
         * @param result 操作的返回值
         */
        void call(int result);
    }

    /**
     * 操作执行体的回调接口
     */
    public interface Action {
        /**
         * 需要执行的操作
         * @param diskLib 连接对象
         * @return 操作返回值
         */
        int action(UDiskLib diskLib);
    }

}
