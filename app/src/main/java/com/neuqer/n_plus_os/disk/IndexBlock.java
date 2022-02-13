package com.neuqer.n_plus_os.disk;

import java.util.Arrays;

/**
 * Time:2020/12/19 23:35
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:内存块——索引块
 */
public class IndexBlock extends DiskBlock {

    private int[] mDataPtr = new int[1024];
    private IndexType mType;

    public IndexBlock(IndexType type, int address) {
        super(address);
        Arrays.fill(mDataPtr, -1);
        mType = type;
    }

    public void setPtr(int index, int address) {
        if (index >= 1024) {
            throw new IllegalArgumentException("索引只能设置1024个块地址！");
        } else {
            mDataPtr[index] = address;
        }
    }

    public int[] getDataPtr() {
        return mDataPtr;
    }

    public IndexType getType() {
        return mType;
    }

    @Override
    public DiskType getDiskType() {
        return DiskType.INDEX_BLOCK;
    }

    public enum IndexType {
        INDEX_TYPE_I,//一级索引

        INDEX_TYPE_II//二级索引
    }
}
