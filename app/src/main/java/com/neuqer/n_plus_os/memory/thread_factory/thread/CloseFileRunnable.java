package com.neuqer.n_plus_os.memory.thread_factory.thread;

import android.os.Handler;
import android.os.Message;

import com.neuqer.n_plus_os.memory.SimulationMemory;
import com.neuqer.n_plus_os.memory.thread_factory.ThreadManager;
import com.neuqer.n_plus_os.memory.thread_factory.WorkRunnable;

/**
 * Time:2020/12/21 21:52
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function: 解除文件内存被线程的占用
 */
public class CloseFileRunnable implements Runnable {

    private String fileName;
    private int[] fileAddressInMem;
    private Handler handler;
    public CloseFileRunnable(Handler handler, String fileName, int[] fileAddressInMem) {
        this.handler = handler;
        this.fileName = fileName;
        this.fileAddressInMem = fileAddressInMem;
    }

    @Override
    public void run() {
        ThreadManager.getInstance().increaseRunningThreadCount();
        for (int address :
                fileAddressInMem) {
            SimulationMemory.getInstance().clearMemBlock(address, SimulationMemory.CLEAR_DELETE);
        }
        ThreadManager.getInstance().decreaseRunningThreadCount();
        ThreadManager.getInstance().removeRunnable(fileName);
        Message obtain = Message.obtain();
        obtain.what = ThreadManager.WHAT_CLOSE_FILE;
        obtain.obj = fileName;
        handler.sendMessage(obtain);
    }


}
