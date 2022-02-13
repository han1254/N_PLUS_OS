package com.neuqer.n_plus_os.memory;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Time:2020/12/22 8:07
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public class MemoryBitMap {

    private boolean[] memoryListFlags = new boolean[SimulationMemory.MEMORY_BLOCK_COUNT];
    private String[] memoryUsedThreadIds = new String[SimulationMemory.MEMORY_BLOCK_COUNT];
    private static final String TAG = "MEMORY_BITMAP";

    private static volatile MemoryBitMap memoryBitMap;
    private MemoryBitMap() {

    }
    public static MemoryBitMap getInstance() {
        if (memoryBitMap == null) {
            synchronized (MemoryBitMap.class) {
                if (memoryBitMap == null) {
                    memoryBitMap = new MemoryBitMap();
                }
            }
        }
        return memoryBitMap;
    }

    /**
     * 设置内存块占用
     * @param memAddress 内存块地址
     */
    public void setUse(int memAddress) {
        if (memoryListFlags[memAddress]) {
            Log.d(TAG, "setUse: 内存位置被非法占用！");
            throw new IllegalArgumentException("内存位置被非法占用");
        }
        memoryListFlags[memAddress] = true;
    }

    public void releaseMemBlock(int memAddress) {
        memoryListFlags[memAddress] = false;
        memoryUsedThreadIds[memAddress] = "";
    }

    public int[] mallocFreeMemBlocks(String threadId) {
        int temp = 0;
        List<Integer> resList = new ArrayList<>();
        for (int i = 0; i < memoryListFlags.length; i++) {
            if (!memoryListFlags[i]) {
                temp++;
                resList.add(i);
            }
            if (temp == MemoryConstants.THREAD_DISTRIBUTED_BLOCKS_NUMBER)
                break;
        }
        Log.d(TAG, "mallocFreeMemBlocks: 找到"+temp+"块空闲内存");
        if (temp < MemoryConstants.THREAD_DISTRIBUTED_BLOCKS_NUMBER)
            return null;
        int[] res = new int[MemoryConstants.THREAD_DISTRIBUTED_BLOCKS_NUMBER];
        for (int i = 0; i < MemoryConstants.THREAD_DISTRIBUTED_BLOCKS_NUMBER; i++) {
            res[i] = resList.get(i);
            memoryListFlags[res[i]] = true;//该内存被占用
            memoryUsedThreadIds[res[i]] = threadId;//设置内存占用线程ID
        }
        return res;
    }

    public boolean getState(int memAddress) {
        return memoryListFlags[memAddress];
    }

    public String getThreadId(int memAddress) {
        return memoryUsedThreadIds[memAddress];
    }

}
