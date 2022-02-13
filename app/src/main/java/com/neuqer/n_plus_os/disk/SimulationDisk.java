package com.neuqer.n_plus_os.disk;

import android.util.Log;


import com.neuqer.n_plus_os.disk.file_sys.MFD;
import com.neuqer.n_plus_os.disk.file_sys.UFD;

import static android.content.ContentValues.TAG;

/**
 * Time:2020/12/19 18:44
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public class SimulationDisk {

    public static final int DISK_SIZE = 4 * 1024;//外存大小为4KB * 1024 = 4M
    private DiskBlock[] diskBlocks = new DiskBlock[DiskConstants.DISK_BLOCK_NUM];
    private DiskBlock[] swapBlocks = new DiskBlock[DiskConstants.SWAP_BLOCK_NUM];
    private MFD currentMFD = null;
    private volatile static SimulationDisk simulationDisk;
    private SimulationDisk() {
        currentMFD = new MFD(0);
        diskBlocks[0] = currentMFD;
    }

    public static SimulationDisk getInstance() {
        if (simulationDisk == null) {
            synchronized (SimulationDisk.class) {
                if (simulationDisk == null) {
                    simulationDisk = new SimulationDisk();
                }
            }
        }
        return simulationDisk;
    }

    public MFD getCurrentMFD() {
        return currentMFD;
    }

    public UFD getUfd(int address) throws Exception {
        DiskBlock temp = diskBlocks[address];
        if (!(temp instanceof UFD)) {
            throw new Exception("文件存储错误，地址无法找到UFD");
        }
        return (UFD)temp;
    }

    /**
     * 在磁盘中存入一个文件
     * @param userName 用户名
     * @param fileName 文件名
     * @param fileContent 文件内容（一个字符代表1KB）
     * @param size 文件大小（以KB为单位）
     * @return 返回创建结果：失败为-1，成功为索引结点地址
     * @throws Exception 创建过程中出现异常
     */
    public int createFile(String userName, String fileName, String fileContent, int size) throws Exception {

        IndexBlock indexBlockI = null;
        IndexBlock indexBlockII = null;


        if (fileContent.length() != size) {
            throw new Exception("文件实际大小与标明大小不一致！");
        }

        int ufdStartAddress = currentMFD.getUfdAddress(userName);

        if (ufdStartAddress == -1) {
            throw new Exception("无"+userName+"的用户目录！");
        }
        if (ufdStartAddress == 0) {
            throw new Exception(userName + "的用户目录不应该出现在0位置处");
        }

        if (ufdStartAddress > DiskConstants.DISK_BLOCK_NUM - 1) {
            throw new Exception("位置超过了外存大小！");
        }

        int mallocBlockCount = (fileContent.length() / DiskConstants.DISK_BLOCK_SIZE) + ((fileContent.length() % 4) > 0 ? 1 : 0);
        int[] freeAddresses = BitMapController.getInstance().findFreeBlocks(mallocBlockCount);//为文件申请外存
        if (freeAddresses.length < mallocBlockCount) {
            return DiskConstants.CREATE_FILE_NO_FREE;
        }

        final int indexNodeAddress = BitMapController.getInstance().findFreeBlock();//为索引结点申请一个内存
        if (indexNodeAddress == -1) {
            return DiskConstants.CREATE_FILE_NO_FREE;
        }
        IndexNode indexNode = new IndexNode(indexNodeAddress, fileName, size);

        String[] splitBlockContents = new String[mallocBlockCount];

        for (int i = 0; i < mallocBlockCount; i++) {
            if (i == mallocBlockCount - 1) {
                splitBlockContents[i] = fileContent.substring(i * 4);
            } else {
                splitBlockContents[i] = fileContent.substring(i * 4, (i + 1) * 4);
            }
        }
        for (int i = 0; i < mallocBlockCount; i++) {
            int address = freeAddresses[i];
            diskBlocks[address] = new DataBlock(userName, fileName, address, splitBlockContents[i], i);
            if (i < DiskConstants.DIRECT_ADDRESS_MAX / DiskConstants.DISK_BLOCK_SIZE) {
                indexNode.setDirectAddress(i, address);
            } else if (i < (DiskConstants.REAL_DISK_SIZE) / DiskConstants.DISK_BLOCK_SIZE) {
                if (indexBlockI == null) {
                    int indexBlockIAddress = BitMapController.getInstance().findFreeBlock();
                    indexNode.setIndexIAddress(indexBlockIAddress);
                    indexBlockI = new IndexBlock(IndexBlock.IndexType.INDEX_TYPE_I, indexBlockIAddress);
                    diskBlocks[indexBlockIAddress] = indexBlockI;//将二级索引添加到外存中
                }
                indexBlockI.setPtr(i, address);
            } else {
                throw new IllegalArgumentException("老哥，不至于吧，不会真的有人创建那么大的文件吧？");
            }
        }
        diskBlocks[indexNodeAddress] = indexNode;
        return indexNodeAddress;
    }

    public void addMFDItem(String userName, int address) {
        UFD res = new UFD(address, userName);
        diskBlocks[address] = res;
    }

    public void addUFDItem(String fileName, int ufdAddress, int indexAddress, int size) {
//        IndexNode res = new IndexNode(address, fileName, size);
//        diskBlocks[address] = res;
        ((UFD)diskBlocks[ufdAddress]).getUfdMap().put(fileName, indexAddress);
    }

    public DiskBlock findBlock(int address) {
        if (diskBlocks[address] == null) {
            Log.d(TAG, "findBlock: 访问获得的块未被占用，也许哪里出错了");
        }
        return diskBlocks[address];
    }

    public void transToSwap(DataBlock dataBlock) {
        for (int i = 0; i < swapBlocks.length; i++) {
            if (swapBlocks[i] == null) {
                swapBlocks[i] = dataBlock;
                break;
            }
        }
    }

    public void clearSwap(int address) {
        swapBlocks[address] = null;
    }

    public DiskBlock[] getSwapBlocks() {
        return swapBlocks;
    }

    public void deleteDiskBlock(int address) {
        BitMapController.getInstance().setBlockState(DiskConstants.DISK_FREE, address);
        diskBlocks[address] = null;
    }
}
