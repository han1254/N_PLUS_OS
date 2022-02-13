package com.neuqer.n_plus_os.disk;

/**
 * Time:2020/12/20 11:57
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public class DataBlock extends DiskBlock {
    private String userName;
    private String fileName;
    private String content = "";
    private int realSize;
    int blockOrder;//文件块在原文件中的顺序

    public DataBlock(String userName, String fileName, int mDiskAddress, String content, int blockOrder) throws Exception {
        super(mDiskAddress);
        this.userName = userName;
        this.fileName = fileName;
        if (content.length() > 4) {
            throw new Exception("文件内容过大，无法放入一个内存块！");
        }
        this.content = content;
        realSize = content.length();
        this.blockOrder = blockOrder;
    }

    public DataBlock(String userName,
                     String fileName,
                     int mDiskAddress,
                     String content,
                     int blockOrder,
                     boolean isMemBlock) throws Exception {
        super(mDiskAddress, isMemBlock);
        this.userName = userName;
        this.fileName = fileName;
        if (content.length() > 4) {
            throw new Exception("文件内容过大，无法放入一个内存块！");
        }
        this.content = content;
        realSize = content.length();
        this.blockOrder = blockOrder;
    }

    public String getContent() {
        return content;
    }

    public String getUserName() {
        return userName;
    }

    public String getFileName() {
        return fileName;
    }

    public int getRealSize() {
        return realSize;
    }

    public int getBlockOrder() {
        return blockOrder;
    }

    @Override
    public DiskType getDiskType() {
        return DiskType.DATA;
    }
}
