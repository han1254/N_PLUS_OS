package com.neuqer.n_plus_os.memory;


import com.neuqer.n_plus_os.disk.DataBlock;
import com.neuqer.n_plus_os.disk.DiskBlock;

/**
 * Time:2020/12/20 14:37
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public class MemBlock extends DataBlock {

    private String threadId;

    private int notVisitedTime = 0;
    private boolean isBeingVisited = false;

    public MemBlock(String userName,
                    String fileName,
                    int mAddress,
                    String content,
                    int blockOrder,
                    String threadId,
                    boolean isMemBlock) throws Exception {
        super(userName, fileName, mAddress, content, blockOrder, isMemBlock);
        this.threadId = threadId;
    }

    public MemBlock(DataBlock dataBlock, int memAddress, String threadId, boolean isMemBlock) throws Exception {
        super(
                dataBlock.getUserName(),
                dataBlock.getFileName(),
                memAddress,
                dataBlock.getContent(),
                dataBlock.getBlockOrder(),
                isMemBlock
        );
        this.threadId = threadId;
    }



    public void increaseNotVisitedTime() {
        isBeingVisited = false;
        notVisitedTime = notVisitedTime + 1;
    }

    public void visited() {
        notVisitedTime = 0;
        isBeingVisited = true;
    }

    public int getNotVisitedTime() {
        return notVisitedTime;
    }

    public boolean isBeingVisited() {
        return isBeingVisited;
    }


    public void clearFlag() {
        isBeingVisited = false;
        notVisitedTime = 0;
    }

    public String getThreadId() {
        return threadId;
    }

    @Override
    public DiskType getDiskType() {
        return DiskType.MEMORY;
    }
}
