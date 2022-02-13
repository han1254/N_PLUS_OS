package com.neuqer.n_plus_os.memory.thread_factory;

import android.os.Handler;

import com.neuqer.n_plus_os.memory.MemoryConstants;

import java.util.Arrays;
import java.util.UUID;


/**
 * Time:2020/12/20 19:07
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public class WorkRunnable implements Runnable {

    protected Handler handler;
    protected String threadId = UUID.randomUUID().toString();
    protected int[] distributedMemBlocksAd = new int[MemoryConstants.THREAD_DISTRIBUTED_BLOCKS_NUMBER];//存储分配的内存块的地址

    public WorkRunnable(Handler handler) {
        super();
        this.handler = handler;
        Arrays.fill(distributedMemBlocksAd, -1);
    }


    public String getThreadId() {
        return threadId;
    }

    public void setDistributedMemBlocksAd(int[] memBlocks) throws Exception {
        if (memBlocks == null || memBlocks.length < MemoryConstants.THREAD_DISTRIBUTED_BLOCKS_NUMBER) {
            throw new Exception("内存分配出现问题！");
        }
        distributedMemBlocksAd = memBlocks;
    }

    @Override
    public void run() {
        ThreadManager.getInstance().setRunningThreadCount(ThreadManager.getInstance().getRunningThreadCount() + 1);
    }

    public int[] getDistributedMemBlocksAd() {
        return distributedMemBlocksAd;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }
}
