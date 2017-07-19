package com.hdu.wsn.uDiskReader.usb.jnilib;

/**
 * 操作返回值的意义的封装
 */
public class SmiErrDef {

    // Version V 1.0.0.4

    // Error code define
    public static final int SUCCESS_OK = 0;
    public static final int S_OK = 0;

    public static final int ERR_CREATE_FILE       = -100;
    public static final int ERR_READ_CID          = -101;
    public static final int ERR_WRITE_WPRO        = -102;
    public static final int ERR_READ_WPRO         = -103;
    public static final int ERR_READ_SRF          = -104;
    public static final int ERR_READ_RAM_BUF      = -105;
    public static final int ERR_WRITE_CID         = -106;
    public static final int ERR_INVALID_PASSWD    = -107;
    public static final int ERR_LETTER_NO         = -108;
    public static final int ERR_LENGTH_WRONG      = -109;
    public static final int ERR_PARAM_NULL        = -110;
    public static final int ERR_MEMORY_NOT_ENOUGH = -111;
    public static final int ERR_DISK_NO_FOUND     = -112;
    public static final int ERR_READ_PAR_BUF	    = -113;
    public static final int ERR_LOCK			    = -144;
    public static final int ERR_UNLOCK			    = -145;
    public static final int ERR_WRITE_HIDDEN_SECTOR	= -146;
    public static final int ERR_READ_HIDDEN_SECTOR	= -147;
    public static final int ERR_DATA_CMP		   = -148;
    public static final int ERR_CREATE_THREAD	   = -149;
    public static final int ERR_FORMAT			   = -150;
    public static final int ERR_SWWRITEPROTECT     = -151;
    public static final int ERR_RESVAREA_OVER	   = -152;
    public static final int ERR_MODE_NOT_SUPPORT   = -153;
    public static final int ERR_RESET		       = -154;
    public static final int ERR_PASSWORD_DISABLE   = -155;
    public static final int ERR_PASSWORD_VERIFY	   = -156;
    public static final int ERR_READ_FILE		   = -157;
    public static final int ERR_WRITE_FILE	       = -158;
    public static final int ERR_WRITE_CD		     = -159;
    public static final int ERR_READ_CD			     = -160;
    public static final int ERR_READ_SECTOR		     = -161;
    public static final int ERR_WRITE_SECTOR	     = -162;
    public static final int ERR_GET_LBA_SIZE	     = -163;
    public static final int ERR_SIZE_ERROR           = -164;
    public static final int ERR_PASSWORDTYPE	     = -165;
    public static final int ERR_UPDATE_BOOTBIN		 = -166;
    public static final int ERR_UPDATE_PRETEST8K	 = -167;
    public static final int ERR_ERASE	             = -168;
    public static final int ERR_FLASH_CE		     = -169;
    public static final int ERR_OPEN_SHORT_RW    	 = -170;
    public static final int ERR_OPEN_SHORT_COMPARE   = -171;
    public static final int ERR_ERASE_ISP		     = -172;
    public static final int ERR_PRELOAD_FILE    = -173;
    public static final int ERR_READ_INFO       = -174;
    public static final int ERR_WRITE_INFO      = -175;
    public static final int ERR_NOT_SMI_DEVICE  = -176;
    public static final int ERR_DEVICE_HANG	    = -177;
    public static final int ERR_INQUIRY			  = -178;
    public static final int ERR_FIND_FIRST_FILE	  = -179;
    public static final int ERR_PRELOAD_FAIL	  = -180;
    public static final int ERR_READ_FLASH_PAGE	  = -181;
    public static final int ERR_CHECK_REMAPPING_TABLE = -182;
    public static final int ERR_SCSI_READ		= -183;
    public static final int ERR_SCSI_WRITE		= -184;
    public static final int ERR_NOT_SUPPORT	    = -185;
    public static final int ERR_INTSALL_DRIVER  = -186;
    public static final int ERR_CHECK_DISK_INFO	= -187;
    public static final int ERR_GET_DISK_LABEL	= -188;
    public static final int ERR_FIND_BIT_TABLE	= -189;
    public static final int ERR_WRITE_FLASH	    = -190;
    public static final int ERR_GET_SYSTEM_DISK_FERR_SPACE = -191;
    public static final int ERR_MBR_FORMAT		  = -192;
    public static final int ERR_PBR_FORMAT		  = -193;
    public static final int ERR_UNKNOWN_FORMAT    = -194;
    public static final int ERR_OCCUPIED		  = -195;
    public static final int ERR_EXFAT_FORMAT	  = -196;
    public static final int ERR_MORE_THAN_ONE_CLUSTER = -197;
    public static final int ERR_NOT_EMPTY		   = -198;
    public static final int ERR_DATA_COMPARE	   = -199;
    public static final int ERR_USER_STOP		   = -200;
    public static final int ERR_CLOSE_FILE		   = -201;
    public static final int ERR_READ_I_DATA		   = -202;
    public static final int ERR_NO_MORE_FILES	   = -203;
    public static final int ERR_TOTAL_LUN_SIZE	 = -204;
    public static final int ERR_ICVER			 = -205;
    public static final int ERR_FORAMT_RESERVE	 = -206;
    public static final int ERR_PRELOAD_RESERVE  = -207;
    public static final int ERR_SHELL_EXCUTE	 = -208;
    public static final int ERR_FILE_NOT_FOUND   = -209;
    public static final int ERR_SCSI_CMD_FAIL    = -210;
    public static final int ERR_BUFFER_SIZE      = -211;
    public static final int ERR_DEVICE_REMOVE    = -212;
    public static final int ERR_CONTENT_DATA        = -213;  // SM3269
    public static final int ERR_CHECKSUM            = -214;  // SM3269
    public static final int ERR_INVALID_DATA        = -215;  // SM3269
    public static final int ERR_INVALID_ID          = -216;  // SM3269
    public static final int ERR_FINGER_NOT_EXIST    = -217;  // SM3269
    public static final int ERR_DEVICE_NOT_SUPPORT  = -218;  // SM3269
    public static final int ERR_LUN_NOT_SUPPORT     = -219;  // SM3269
    public static final int ERR_LUN_SIZE_ZERO       = -220;  // SM3269

    public static final int ERR_DEVICE_NO_OPEN      = -501;    // Android AP

    public static String SmiGetErrorMessage(int lErrorCode) {

        String strReturn = " ";
        switch (lErrorCode)
        {
            case ERR_DISK_NO_FOUND:     strReturn = "ERR_DISK_NO_FOUND";   break;
            case ERR_SCSI_READ:         strReturn = "ERR_SCSI_READ";       break;
            case ERR_SCSI_WRITE:        strReturn = "ERR_SCSI_WRITE";      break;
            case ERR_DEVICE_NO_OPEN:    strReturn = "ERR_DEVICE_NO_OPEN";  break;
            case ERR_READ_CID:          strReturn = "ERR_READ_CID";        break;
            case ERR_PASSWORD_VERIFY:   strReturn = "ERR_PASSWORD_VERIFY"; break;
            case ERR_LOCK:              strReturn = "ERR_LOCK";            break;
            case ERR_NOT_SMI_DEVICE:    strReturn = "ERR_NOT_SMI_DEVICE";  break;


            default: strReturn = String.format("%s = %d", "Other error", lErrorCode);
        }

        return strReturn;
    }
}
