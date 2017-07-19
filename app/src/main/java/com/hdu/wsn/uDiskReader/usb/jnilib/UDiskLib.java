package com.hdu.wsn.uDiskReader.usb.jnilib;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 连接执行体类，调用Native方法去执行U盘SDK的操作
 */
public class UDiskLib {
    private static UDiskLib instance;

    /**
     * 单例模式创建连接执行体
     * @param context APP环境上下文
     * @return 连接执行体
     */
    public static UDiskLib create(Context context) {
        if (instance == null || !(instance instanceof UDiskLib)) {
            synchronized (UDiskLib.class) {
                instance = new UDiskLib(context);
            }
        }
        if(!instance.isInit()) {
            synchronized (UDiskLib.class) {
                instance.smiOpenDisk();
            }
        }
        return instance;
    }

    /**
     * 初始化连接
     * @param context 连接上下文
     */
    public static void Init (Context context) {
        if (instance == null || !(instance instanceof UDiskLib)) {
            synchronized (UDiskLib.class) {
                instance = new UDiskLib(context);
            }
        }
        instance.smiUSBInit();
    }

    /**
     * 关闭连接
     * @return 关闭结果
     */
    public static int close() {
        if (instance == null) {
            return SmiErrDef.ERR_DEVICE_NO_OPEN;
        }
        return instance.smiCloseDisk();
    }

    // ---------- Define ----------
    public static final int NO_DEF_IC = 0xffff;
    public static final int PARAM_CNT = 5;

    // ---------- Define Error String ----------
    public static final String ACTION_USB_PERMISSION = "smi.udisklibdemo.USB_PERMISSION";
    public static final String TAG = "UDiskLib";

    private PendingIntent mPermissionIntent;
    private UsbManager mUsbManager;
    private UsbDevice mUsbDevice;
    private UsbEndpoint mEndpointIn;
    private UsbEndpoint mEndpointOut;
    private UsbDeviceConnection mUsbConnection;
    private UsbInterface mUsbInterface;

    private int nResult = SmiErrDef.S_OK;
    private Context mContext;
    private boolean mIsDeviceOpen = false;

    // Native interface usage
    private smi.udisklibsrc.uDiskLibJNI myNativeIF;
    private int[] mParamObj = new int[PARAM_CNT];

    /**
     * 保护化的构造函数，用于单例模式实现
     * @param mContextData 环境上下文
     */
    protected UDiskLib(Context mContextData) {
        mContext = mContextData;
        myNativeIF = new smi.udisklibsrc.uDiskLibJNI();
    }

    /**
     * 判断是否初始化过了
     * @return 判断是否初始化了
     */
    private boolean isInit() {
        return mIsDeviceOpen;
    }

    /**
     * 初始化
     * @return 初始化成功与否
     */
    private int smiUSBInit() {

        mUsbManager = (UsbManager)mContext.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        if (deviceList.size() == 0) {
            return SmiErrDef.ERR_DISK_NO_FOUND;
        }
        // Enumerating devices
        while (deviceIterator.hasNext()) {
            mUsbDevice = deviceIterator.next();
            mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);

            mUsbManager.requestPermission(mUsbDevice, mPermissionIntent);
        }

        return SmiErrDef.S_OK;
    }

    /**
     * 打开U盘
     * @return 结果
     */
    public int smiOpenDisk() {

        Log.d(TAG, "SmiOpenDisk ...");

        // Step1: Init USB manager
        nResult = smiUSBInit();
        if (nResult != SmiErrDef.S_OK) {
            return nResult;
        }

        try {
            mUsbConnection = mUsbManager.openDevice(mUsbDevice);
            mUsbInterface = mUsbDevice.getInterface(0);
            mUsbConnection.claimInterface(mUsbInterface, true);

            mUsbInterface = mUsbDevice.getInterface(0);
            for (int i = 0; i < mUsbInterface.getEndpointCount(); i++) {
                if (mUsbInterface.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                    if (mUsbInterface.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN)
                        mEndpointIn = mUsbInterface.getEndpoint(i);
                    else
                        mEndpointOut = mUsbInterface.getEndpoint(i);
                } else {
                    Log.d(TAG, "Not Bulk");
                }
            }

            // Step2: Assign parameters
            mParamObj[0] = PARAM_CNT;
            mParamObj[1] = mUsbConnection.getFileDescriptor();
            mParamObj[2] = mEndpointIn.getAddress();
            mParamObj[3] = mEndpointOut.getAddress();
            mParamObj[4] = NO_DEF_IC; // IC Version

            // Step3: Call Init function
            myNativeIF.OpenDisk();

            // Step4: Get IC version number & check it
            int[] nICLocalVer = new int[1];
            myNativeIF.GetICVer(mParamObj, nICLocalVer);
            mParamObj[4] = nICLocalVer[0]; // Assign IC version nubmer
            if (nICLocalVer[0] == NO_DEF_IC) {
                return SmiErrDef.ERR_NOT_SMI_DEVICE;
            }

            // Step5: Open disk was done.
            mIsDeviceOpen = true;

            // Log related information
            Log.d(TAG, "SMI Native Interface Parameter: 0x" +
                    Integer.toHexString(mParamObj[1]) + " 0x"  // mFD
                  + Integer.toHexString(mParamObj[2]) + " 0x"  // mEPIn
                  + Integer.toHexString(mParamObj[3]) + " 0x"  // mEPOut
                  + Integer.toHexString(mParamObj[4]));        // mICVer

            return SmiErrDef.S_OK;

        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
            mUsbConnection = null;
        }
        return SmiErrDef.ERR_DISK_NO_FOUND;
    }

    /**
     * 关闭U盘
     * @return 结果
     */
    public int smiCloseDisk() {

        try {
            mUsbConnection.releaseInterface(mUsbInterface);
            mUsbConnection.close();
            mUsbConnection = null;
            mEndpointOut = null;
            mEndpointIn = null;

            // Call uninit function
            myNativeIF.CloseDisk();
            mIsDeviceOpen = false;

            Log.d(TAG, "SmiCloseDisk ...");
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return SmiErrDef.S_OK;
    }

    /**
     *
     * @param byVersion
     * @return
     */
    public int smiGetLibraryVersion(byte[] byVersion)
    {
        return myNativeIF.GetLibraryVersion(byVersion);
    }

    /**
     * 登录
     * @param byPassword u盘密码
     * @return 结果
     */
    public int smiLoginDeviceByBytes(byte[] byPassword)
    {
        if (!mIsDeviceOpen) {
            return SmiErrDef.ERR_DEVICE_NO_OPEN;
        }

        return myNativeIF.LoginDevice(mParamObj, byPassword);
    }

    /**
     * 用字符串执行登录
     * @param sPassword 密码
     * @return
     */
    public int smiLoginDeviceByStr(String sPassword) {
        int nIdx;
        byte[] byPassword = new byte[sPassword.length() + 1];
        for (nIdx = 0; nIdx < sPassword.length(); nIdx++) {
            byPassword[nIdx] = (byte) sPassword.charAt(nIdx);
        }
        byPassword[nIdx] = 0;
        return smiLoginDeviceByBytes(byPassword);
    }

    /**
     * 登出
     * @return 结果
     */
    public int smiLogoutDevice()
    {
        if (!mIsDeviceOpen) {
            return SmiErrDef.ERR_DEVICE_NO_OPEN;
        }

        return myNativeIF.LogoutDevice(mParamObj);
    }

    /**
     *
     * @return
     */
    public int smiEraseAllData()
    {
        if (!mIsDeviceOpen) {
            return SmiErrDef.ERR_DEVICE_NO_OPEN;
        }

        return myNativeIF.EraseAllData(mParamObj);
    }

    /**
     *
     * @param lVID
     * @param lPID
     * @return
     */
    public int smiGetUSBVIDPID(long[] lVID, long[] lPID)
    {
        if (!mIsDeviceOpen) {
            return SmiErrDef.ERR_DEVICE_NO_OPEN;
        }

        return myNativeIF.GetUSBVIDPID(mParamObj, lVID, lPID);
    }

    /**
     * 获取用户信息
     * @param byReadBuf 读入的字节
     * @param nBufSize 字节长度
     * @return 结果
     */
    public int smiGetUserData(byte[] byReadBuf, int nBufSize)
    {
        if (!mIsDeviceOpen) {
            return SmiErrDef.ERR_DEVICE_NO_OPEN;
        }

        return myNativeIF.GetUserData(mParamObj, byReadBuf, nBufSize);
    }

    /**
     * 重置用户信息
     * @param byWriteBuf 写入的字节数组
     * @param nBufSize 数组长度
     * @return 结果
     */
    public int smiSetUserData(byte[] byWriteBuf, int nBufSize)
    {
        if (!mIsDeviceOpen) {
            return SmiErrDef.ERR_DEVICE_NO_OPEN;
        }

        return myNativeIF.SetUserData(mParamObj, byWriteBuf, nBufSize);
    }

    /**
     * 重置u盘密码
     * @param byOldPassword 旧密码
     * @param byNewPassword 新密码
     * @param byHint
     * @return 成功与否
     */
    public int smiSetPassword(byte[] byOldPassword, byte[] byNewPassword , byte[] byHint)
    {
        if (!mIsDeviceOpen) {
            return SmiErrDef.ERR_DEVICE_NO_OPEN;
        }

        return myNativeIF.SetPassword(mParamObj, byOldPassword, byNewPassword, byHint);
    }

    // --------------------------------------------------------------

    /**
     *
     * @param byHint
     * @return
     */
    public int smiGetPasswordHint(byte[] byHint)
    {
        if (!mIsDeviceOpen) {
            return SmiErrDef.ERR_DEVICE_NO_OPEN;
        }

        return myNativeIF.GetPasswordHint(mParamObj, byHint);
    }

}
