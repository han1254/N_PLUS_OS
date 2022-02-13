package com.neuqer.n_plus_os.memory.thread_factory.thread;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.neuqer.n_plus_os.memory.MemoryBitMap;
import com.neuqer.n_plus_os.memory.SimulationMemory;
import com.neuqer.n_plus_os.memory.thread_factory.ThreadManager;
import com.neuqer.n_plus_os.memory.thread_factory.WorkRunnable;
import com.neuqer.n_plus_os.ui.recyclerview.FakeMap;
import com.neuqer.n_plus_os.util.FileLockUtil;


/**
 * Time:2020/12/20 23:01
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public class CreateFileRunnable extends WorkRunnable {

    private static final String TAG = "CreateFileThread";
    private String fileName;
    private String content;
    public CreateFileRunnable(Handler handler) {
        super(handler);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void run() {
        super.run();
        int res = -1;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            res = SimulationMemory.getInstance().createFile(fileName, content);
            Log.d(TAG, "run: 创建文件，文件地址为" + res);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (res == -1) {
            Log.d(TAG, "run: 创建文件失败");
        }

        Log.d(TAG, "run: 文件进入锁管理");
        FileLockUtil.getInstance().releaseFileLock(fileName);

        FakeMap map = new FakeMap(fileName, res);
        Message obtain = Message.obtain();
        obtain.what = ThreadManager.WHAT_CREATE_FILE;
        obtain.obj = map;
        for (int value : distributedMemBlocksAd) {
            SimulationMemory.getInstance().clearMemBlock(value, SimulationMemory.CLEAR_DELETE);//将申请的内存清空
        }
        ThreadManager.getInstance().decreaseRunningThreadCount();
        handler.sendMessage(obtain);
    }


}
