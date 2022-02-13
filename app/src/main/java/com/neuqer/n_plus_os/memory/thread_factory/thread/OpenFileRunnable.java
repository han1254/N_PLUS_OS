package com.neuqer.n_plus_os.memory.thread_factory.thread;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.neuqer.n_plus_os.disk.DataBlock;
import com.neuqer.n_plus_os.disk.DiskBlock;
import com.neuqer.n_plus_os.disk.SimulationDisk;
import com.neuqer.n_plus_os.memory.MemBlock;
import com.neuqer.n_plus_os.memory.MemoryConstants;
import com.neuqer.n_plus_os.memory.SimulationMemory;
import com.neuqer.n_plus_os.memory.thread_factory.ThreadManager;
import com.neuqer.n_plus_os.memory.thread_factory.WorkRunnable;
import com.neuqer.n_plus_os.util.FileLockUtil;

/**
 * Time:2020/12/21 9:39
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public class OpenFileRunnable extends WorkRunnable {

    private static final String TAG = "OPEN_FILE";
    private final int SWAP_AREA = 0;
    private final int DISK_AREA = 1;
    private int indexAddress;
    private String fileName;
    private int totalPage;
    private int readPage;
    private int itemPosition;
    private int[] fileDataAddresses;

    public OpenFileRunnable(Handler handler, int indexAddress, String fileName, int itemPosition) {
        super(handler);
        this.indexAddress = indexAddress;
        this.fileName = fileName;
        this.itemPosition = itemPosition;
    }

    public void setIndexAddress(int indexAddress) {
        this.indexAddress = indexAddress;
    }

    public void setReadPage(int readPage) {
        this.readPage = readPage;
    }

    public void setItemPosition(int itemPosition) {
        this.itemPosition = itemPosition;
    }

    @Override
    public void run() {
        super.run();
        if ((fileDataAddresses == null)
                || fileDataAddresses.length == 0
                || isMemoryEmpty()) {
            try {//如果进入这种情况，说明当前还未将数据读入内存
                fileDataAddresses = SimulationMemory.getInstance().
                        getFileDataAddresses(indexAddress, threadId, distributedMemBlocksAd);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (fileDataAddresses.length > distributedMemBlocksAd.length) {
                Log.d(TAG, "start: 文件大于8个内存块，实际需要：" + fileDataAddresses.length);
            }
            totalPage = fileDataAddresses.length;
        }

        if (readPage + 1 > totalPage) {
            throw new IllegalArgumentException("访问页数不能大于总页数！");
        }

        // 先在8块内存中查找
        for (int i = 0; i < MemoryConstants.THREAD_DISTRIBUTED_BLOCKS_NUMBER; i++) {
            MemBlock memBlock = SimulationMemory.getInstance().getMemBlock(distributedMemBlocksAd[i]);
            if (memBlock != null && memBlock.getBlockOrder() == readPage) {//命中
                memBlock.visited();//设置访问
                increaseNotVisited(i);//增加未访问次数，命中的除外
                String content = memBlock.getContent();
                sendMessage(content);
                ThreadManager.getInstance().decreaseRunningThreadCount();
                return;
            }
        }

        //内存未命中，先去兑换区
        for (int i = 0; i < SimulationDisk.getInstance().getSwapBlocks().length; i++) {
            DiskBlock diskBlock = SimulationDisk.getInstance().getSwapBlocks()[i];
            if (diskBlock instanceof MemBlock) {
                if (((MemBlock) diskBlock).getFileName().equals(fileName)
                        && ((MemBlock) diskBlock).getBlockOrder() == readPage) {//命中

                    int inputAddress = myLRU(SWAP_AREA, i);//执行LRU算法
                    SimulationMemory.getInstance().setMemBlock(inputAddress, (MemBlock) diskBlock);//写入内存
                    sendMessage(((MemBlock) diskBlock).getContent());
                    ThreadManager.getInstance().decreaseRunningThreadCount();
                    return;
                }
            }
        }

        //兑换区未命中，到磁盘
        int diskAddress = fileDataAddresses[readPage];
        int inputAddress = myLRU(DISK_AREA, diskAddress);
        DataBlock block = (DataBlock)SimulationDisk.getInstance().findBlock(diskAddress);
        try {
            SimulationMemory.getInstance().setMemBlock(inputAddress,
                    new MemBlock(block, inputAddress, threadId, true));//写入内存
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendMessage(block.getContent());
        ThreadManager.getInstance().decreaseRunningThreadCount();
    }

    private void sendReplaceBlockAd(int inputAddress) {
        Message obtain = Message.obtain();
        obtain.what = ThreadManager.WHAT_REPLACE_BLOCK;
        obtain.obj = inputAddress;
        handler.sendMessage(obtain);
    }

    /**
     * LRU算法，获得可以写入的内存块地址
     * @return 换出的内存块地址
     */
    private int myLRU(int area, int address) {
        Log.d(TAG, "myLRU: 执行LRU算法");
        int max = Integer.MIN_VALUE;
        int page = -1;
        int res = -1;
        for (int i = 0; i < MemoryConstants.THREAD_DISTRIBUTED_BLOCKS_NUMBER; i++) {
            MemBlock block = SimulationMemory.getInstance()
                    .getMemBlock(distributedMemBlocksAd[i]);
            if (block.getNotVisitedTime() > max && !block.isBeingVisited()) {
                max = block.getNotVisitedTime();
                res = distributedMemBlocksAd[i];
                page = block.getBlockOrder();
                sendReplaceBlockAd(page);
            }
        }
        Log.d(TAG, "myLRU: 替换的页"+(page + 1));
        if (area == SWAP_AREA) {
            SimulationDisk.getInstance().clearSwap(address);//将换入内存的块从兑换去清空
        }
        //如果是与磁盘进行交换，则不用清空磁盘对应位置

        SimulationDisk.getInstance()
                .transToSwap(SimulationMemory.getInstance().getMemBlock(res));//将被换出的块送入兑换区
        SimulationMemory.getInstance().clearMemBlock(res, SimulationMemory.CLEAR_LRU);//清空这个地址的内存
        return res;
    }

    private void increaseNotVisited(int visitedPosition) {
        // 块数可能不足8块
        for (int i = 0; i < Math.min(MemoryConstants.THREAD_DISTRIBUTED_BLOCKS_NUMBER, fileDataAddresses.length); i++) {
            MemBlock block = SimulationMemory.getInstance()
                    .getMemBlock(distributedMemBlocksAd[i]);
            if (i == visitedPosition) {
                continue;
            }
            block.increaseNotVisitedTime();
        }
    }

    private void sendMessage(String content) {
        char[] contentSplit = new char[MemoryConstants.MEM_PAGE_SIZE];
        for (int j = 0; j < Math.min(MemoryConstants.MEM_PAGE_SIZE, content.length()); j++) {
            contentSplit[j] = content.charAt(j);
        }

        OpenFileMsgObj obj = new OpenFileMsgObj(contentSplit, totalPage, readPage, itemPosition);
        Message obtain = Message.obtain();
        obtain.what = ThreadManager.WHAT_READ_FILE;
        obtain.obj = obj;
        handler.sendMessage(obtain);
    }

    private boolean isMemoryEmpty() {
        for (int address :
                distributedMemBlocksAd) {
            if (SimulationMemory.getInstance()
                    .getMemBlock(address) != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 假定线程中保存着他的数据的所有地址
     * @return
     */
    public int[] getFileDataAddresses() {
        return fileDataAddresses;
    }

    public void setFileDataAddresses(int[] fileDataAddresses) {
        this.fileDataAddresses = fileDataAddresses;
    }
}
