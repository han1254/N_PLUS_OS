package com.neuqer.n_plus_os.disk.file_sys;

/**
 * Time:2020/12/19 22:16
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public class DirectoryConstants {
    public static final int DIRECTORY_NODE_SIZE = 16;//UNIX系统中，一个目录项为16B，
    public static final int MAX_COUNT = 256;//一块内存为4KB，能够记录2^8即256个目录项

    public static final int FILE_ALREADY_EXIST = 0;
    public static final int NO_FREE_DISK_BLOCK = 1;
    public static final int UFD_ITEM_INSERT_SUCCESS = 2;

    public static final int MFD_NO_FREE = -1;
    public static final int MFD_INSERT_SUCCESS = 1;
    public static final int MFD_USER_NOT_EXIST = 2;
}
