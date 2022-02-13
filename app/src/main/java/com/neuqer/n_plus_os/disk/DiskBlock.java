package com.neuqer.n_plus_os.disk;

/**
 * Time:2020/12/19 18:42
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public abstract class DiskBlock {

    public final int mBlockSize = 4;//一个磁盘块大小为4KB
    public final int USED = 1;
    public final int FREE = 0;
    protected int mDiskAddress;
    private int mIsUsed = 0;
    private boolean isMemBlock = false;


    public DiskBlock(int mDiskAddress) {
        this.mDiskAddress = mDiskAddress;

        BitMapController.getInstance().setBlockState(USED, mDiskAddress);

    }

    public DiskBlock(int mDiskAddress, boolean isMemBlock) {
        this.mDiskAddress = mDiskAddress;
        this.isMemBlock = isMemBlock;
    }

    public int getDiskAddress() {
        return mDiskAddress;
    }

    abstract public DiskType getDiskType();

    public enum DiskType {
        DATA,//数据
        INDEX_NODE,//索引结点
        INDEX_BLOCK,//索引块
        MFD,
        UFD,
        MEMORY
    }
}
