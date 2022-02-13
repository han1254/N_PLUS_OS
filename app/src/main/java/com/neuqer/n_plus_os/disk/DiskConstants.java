package com.neuqer.n_plus_os.disk;

/**
 * Time:2020/12/19 23:23
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public class DiskConstants {

    public static final int DISK_BLOCK_NUM = 900;

    public static final int SWAP_BLOCK_NUM = 124;

    public static final int REAL_DISK_SIZE = 900 * 4;//外存真正大小为3600KB

    public static final int DISK_BLOCK_SIZE = 4; //一个内存块大小为4KB

    public static final int DIRECT_ADDRESS_MAX = 40; //混合索引，直接寻址允许文件大小40KB

    public static final int INDIRECT_ADDRESSING_I_MAX = 4 * 1024;//混合索引，一次间址允许文件大小4KB * 1024 = 4MB

    public static final int INDIRECT_ADDRESSING_II_MAX = 4 * 1024 * 1024;//混合索引，二次间址允许文件大小 4KB * 1024 * 1024 = 4GB

    public static final int CREATE_FILE_NO_FREE = -1;
    public static final int CREATE_FILE_SUCCESS = 1;

    public static final int DISK_FREE = 0;

    public static final int DISK_USED = 1;


}
