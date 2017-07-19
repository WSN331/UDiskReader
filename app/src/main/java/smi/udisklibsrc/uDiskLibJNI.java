package smi.udisklibsrc;

public class uDiskLibJNI {

    static {
        System.loadLibrary("uDiskLibJNI");
    }

    // Basic API
    public native int OpenDisk();
    public native int CloseDisk();
    public native int GetLibraryVersion(byte[] byVersion);
    public native int GetICVer(int[] pParamObj, int[] nICVer);

    // Tigo API
    public native int GetUSBVIDPID(int[] pParamObj, long[] lVID, long[] lPID);
    public native int LoginDevice(int[] pParamObj, byte[] byPassword);
    public native int LogoutDevice(int[] pParamObj);
    public native int SetPassword(int[] pParamObj, byte[] byOldPassword, byte[] byNewPassword , byte[] byHint);
    public native int GetPasswordHint(int[] pParamObj, byte[] byHint);
    public native int GetUserData(int[] pParamObj, byte[] byReadBuf, int nBufSize);
    public native int SetUserData(int[] pParamObj, byte[] byWriteBuf, int nBufSize);
    public native int EraseAllData(int[] pParamObj);



}

