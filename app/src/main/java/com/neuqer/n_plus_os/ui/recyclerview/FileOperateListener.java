package com.neuqer.n_plus_os.ui.recyclerview;

/**
 * Time:2020/12/20 21:53
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public interface FileOperateListener {
    void onOpClick(FileOperate opType,
                   int fileIndexAddress,
                   String fileName,
                   int itemPosition,
                   int readPage);
}
