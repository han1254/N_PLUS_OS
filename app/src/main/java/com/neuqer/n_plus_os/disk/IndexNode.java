package com.neuqer.n_plus_os.disk;

import java.util.Arrays;

/**
 * Time:2020/12/19 23:36
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function: 内存块——索引结点
 */
public class IndexNode extends DiskBlock {
    private String fileName = "";
    private int fileLength = 0;//以KB为单位
    private int[] mDirectAddress = new int[10];
    private int mIndexIAddress = -1;
    private int mIndexIIAddress = -1;

    public IndexNode(int address, String fileName, int size) {
        super(address);
        Arrays.fill(mDirectAddress, -1);
        this.fileName = fileName;
        this.fileLength = size;
    }

    public void setFileLength(int length) {
        this.fileLength = length;
    }

    public int getFileLength() {
        return fileLength;
    }

    public void setDirectAddress(int index, int address) {
        mDirectAddress[index] = address;
    }

    public void setIndexIAddress(int mIndexIAddress) {
        this.mIndexIAddress = mIndexIAddress;
    }

    public void setIndexIIAddress(int mIndexIIAddress) {
        this.mIndexIIAddress = mIndexIIAddress;
    }

    public int[] getDirectAddress() {
        return mDirectAddress;
    }

    public int getIndexIAddress() {
        return mIndexIAddress;
    }

    public int getIndexIIAddress() {
        return mIndexIIAddress;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public DiskType getDiskType() {
        return DiskType.INDEX_NODE;
    }
}
