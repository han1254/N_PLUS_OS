package com.neuqer.n_plus_os.memory.thread_factory;

import android.os.Handler;
import android.util.Log;

import com.neuqer.n_plus_os.memory.MemoryConstants;
import com.neuqer.n_plus_os.memory.SimulationMemory;
import com.neuqer.n_plus_os.memory.thread_factory.thread.CreateFileRunnable;
import com.neuqer.n_plus_os.memory.thread_factory.thread.OpenFileRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Time:2020/12/20 15:23
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public class ThreadManager {

    private volatile static ThreadManager threadManager;
    public static final int WHAT_CREATE_FILE = 0;
    public static final int WHAT_READ_FILE = 1;
    public static final int WHAT_CLOSE_FILE = 2;
    public static final int WHAT_DELETE_FILE = 3;
    public static final int WHAT_REPLACE_BLOCK = 4;
    private int runningThreadCount = 0;
    private static final String TAG = "ThreadManager";
    private HashMap<String, RunnableInfo> openThreadMap;//为每一个文件保存一个线程
    private int blockThreadCount = 0;

    private List<WorkRunnable> blockedThreads = new ArrayList<>();

    private ThreadManager(){
        openThreadMap = new HashMap<>();
    }
    public static ThreadManager getInstance() {
        if (threadManager == null) {
            synchronized (ThreadManager.class) {
                if (threadManager == null) {
                    threadManager = new ThreadManager();
                }
            }
        }
        return threadManager;
    }

    /**
     * 创建文件线程
     * @param handler
     * @return
     * @throws Exception
     */
    public CreateFileRunnable createThreadAndMallocMemory(Handler handler) throws Exception {
        CreateFileRunnable thread = new CreateFileRunnable(handler);
        int[] memAddressesList = SimulationMemory.getInstance()
                .mallocMemoryList(MemoryConstants.THREAD_DISTRIBUTED_BLOCKS_NUMBER, thread.threadId);
        if (memAddressesList.length < MemoryConstants.THREAD_DISTRIBUTED_BLOCKS_NUMBER) {
            blockedThreads.add(thread);//分配内存不够或者没有剩余内存，则挂起
            return null;
        } else {
            thread.setDistributedMemBlocksAd(memAddressesList);
        }
        return thread;
    }

    /**
     * 创建打开文件线程
     * @param handler
     * @param fileIndexAddress
     * @param fileName
     * @param itemPosition
     * @return
     * @throws Exception
     */
    public OpenFileRunnable createThreadAndMallocMemory(Handler handler,
                                                        int fileIndexAddress,
                                                        String fileName,
                                                        int itemPosition) throws Exception {
        OpenFileRunnable thread = new OpenFileRunnable(handler, fileIndexAddress, fileName, itemPosition);
        int[] memAddressesList = SimulationMemory.getInstance()
                .mallocMemoryList(MemoryConstants.THREAD_DISTRIBUTED_BLOCKS_NUMBER, thread.threadId);
        if (memAddressesList.length < MemoryConstants.THREAD_DISTRIBUTED_BLOCKS_NUMBER) {
            blockedThreads.add(thread);//分配内存不够或者没有剩余内存，则挂起
            return null;
        } else {
            thread.setDistributedMemBlocksAd(memAddressesList);
            openThreadMap.put(fileName,
                    new RunnableInfo(thread.getDistributedMemBlocksAd(),
                            thread.getFileDataAddresses(),
                            thread.getThreadId()));
        }
        return thread;
    }

    public void removeRunnable(String fileName) {
        openThreadMap.remove(fileName);
    }

    public OpenFileRunnable getRunnable(Handler handler,
                                        int fileIndexAddress,
                                        String fileName,
                                        int itemPosition) {
        if ((!openThreadMap.containsKey(fileName)) || openThreadMap.get(fileName) == null) {
            Log.d(TAG, "getRunnable: 没有对应的RunnableInfo");
            return null;
        }
        OpenFileRunnable openFileRunnable = new OpenFileRunnable(handler, fileIndexAddress, fileName, itemPosition);
        try {
            openFileRunnable.setDistributedMemBlocksAd(openThreadMap.get(fileName).getMemAddress());
            openFileRunnable.setThreadId(openThreadMap.get(fileName).getThreadId());
            openFileRunnable.setFileDataAddresses(openThreadMap.get(fileName).getFileDiskAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return openFileRunnable;

    }

    public void setRunningThreadCount(int runningThreadCount) {
        this.runningThreadCount = runningThreadCount;
    }

    public void decreaseRunningThreadCount() {
        runningThreadCount = runningThreadCount - 1;
    }

    public void increaseRunningThreadCount() {
        runningThreadCount = runningThreadCount + 1;
    }

    public int getRunningThreadCount() {
        return runningThreadCount;
    }

    public HashMap<String, RunnableInfo> getOpenThreadMap() {
        return openThreadMap;
    }

    public class RunnableInfo {
        private int[] memAddress;
        private int[] fileDiskAddress;
        private String threadId;

        public RunnableInfo(int[] memAddress, int[] fileDiskAddress, String threadId) {
            this.memAddress = memAddress;
            this.threadId = threadId;
            this.fileDiskAddress = fileDiskAddress;
        }

        public int[] getMemAddress() {
            return memAddress;
        }

        public String getThreadId() {
            return threadId;
        }

        public int[] getFileDiskAddress() {
            return fileDiskAddress;
        }
    }
}


