package com.neuqer.n_plus_os.ui.recyclerview;

/**
 * Time:2020/12/21 20:46
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public interface OnGoBtnClickListener {
    void onGoClick(int fileIndexAddress,
                   String fileName,
                   int itemPosition,
                   int readPage) throws Exception;
}
