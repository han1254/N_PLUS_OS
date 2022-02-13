package com.neuqer.n_plus_os.memory.thread_factory.thread;

import android.os.Handler;
import android.os.Message;

import com.neuqer.n_plus_os.disk.IndexNode;
import com.neuqer.n_plus_os.disk.SimulationDisk;
import com.neuqer.n_plus_os.memory.MemoryConstants;
import com.neuqer.n_plus_os.memory.SimulationMemory;
import com.neuqer.n_plus_os.memory.thread_factory.ThreadManager;
import com.neuqer.n_plus_os.util.FileLockUtil;

import java.util.List;


/**
 * Time:2020/12/21 23:00
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public class DeleteFileRunnable implements Runnable {

    private int fileIndexAddress;
    private Handler handler;
    private int itemPosition;
    private String fileName;

    public DeleteFileRunnable(int fileIndexAddress, Handler handler, int itemPosition, String fileName) {
        this.fileIndexAddress = fileIndexAddress;
        this.handler = handler;
        this.itemPosition = itemPosition;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        ThreadManager.getInstance().increaseRunningThreadCount();
        List<Integer> fileDataAddresses = SimulationMemory.getInstance().getFileDataAddresses(fileIndexAddress);

        for (int address :
                fileDataAddresses) {
            SimulationDisk.getInstance().deleteDiskBlock(address);
        }//删除外存数据块
        IndexNode block = (IndexNode) SimulationDisk.getInstance().findBlock(fileIndexAddress);
        int indexIAddress = block.getIndexIAddress();
        int indexIIAddress = block.getIndexIIAddress();
        if (indexIAddress != -1) {
            SimulationDisk.getInstance().deleteDiskBlock(indexIAddress);
        }
        if (indexIIAddress != -1) {
            SimulationDisk.getInstance().deleteDiskBlock(indexIIAddress);
        }
        SimulationDisk.getInstance().deleteDiskBlock(fileIndexAddress);//删除索引结点
        SimulationMemory.getInstance().getCurrentUFD().getUfdMap().remove(fileName);//删除UFD对应
        ThreadManager.getInstance().decreaseRunningThreadCount();
        FileLockUtil.getInstance().removeFile(fileName);
        Message obtain = Message.obtain();
        obtain.what = ThreadManager.WHAT_DELETE_FILE;
        obtain.obj = itemPosition;
        handler.sendMessage(obtain);
    }
}
