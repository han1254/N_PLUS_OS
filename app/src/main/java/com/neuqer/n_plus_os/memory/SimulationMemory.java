package com.neuqer.n_plus_os.memory;

import android.util.Log;

import com.neuqer.n_plus_os.disk.BitMapController;
import com.neuqer.n_plus_os.disk.DataBlock;
import com.neuqer.n_plus_os.disk.DiskBlock;
import com.neuqer.n_plus_os.disk.IndexBlock;
import com.neuqer.n_plus_os.disk.IndexNode;
import com.neuqer.n_plus_os.disk.SimulationDisk;
import com.neuqer.n_plus_os.disk.file_sys.MFD;
import com.neuqer.n_plus_os.disk.file_sys.UFD;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Time:2020/12/20 12:55
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public class SimulationMemory {

    private volatile static SimulationMemory simulationMemory;
    public static final int MEMORY_SIZE = 256;//内存大小为256KB
    public static final int MEMORY_BLOCK_COUNT = 256 / 4;//内存块64个
    public static final int CLEAR_DELETE = 0;
    public static final int CLEAR_LRU = 1;
    private DiskBlock[] memoryList = new DiskBlock[MEMORY_BLOCK_COUNT];
    private MFD mfd;
    private UFD currentUFD = null;
    private SimulationMemory() {
        mfd = SimulationDisk.getInstance().getCurrentMFD();
        memoryList[0] = mfd;
    }

    public static SimulationMemory getInstance() {
        if (simulationMemory == null) {
            synchronized (SimulationMemory.class) {
                if (simulationMemory == null) {
                    simulationMemory = new SimulationMemory();
                }
            }
        }
        return simulationMemory;
    }

    public UFD getCurrentUFD() {
        return currentUFD;
    }

    /**
     * 通过用户名查询用户文件夹是否在MFD内
     * @param userName
     * @return
     * @throws Exception
     */
    public int findUserIsInMFD(String userName) throws Exception {
        int address = mfd.getUfdAddress(userName);
        if (address > 0) {
            currentUFD = SimulationDisk.getInstance().getUfd(address);
            return MemoryConstants.MEM_GET_UFD_SUCCESS;
        }
        return MemoryConstants.MEM_GET_UFD_FAILED;
    }

    public int createUfd(String userName) throws Exception {
        int freeAddress = BitMapController.getInstance().findFreeBlock();
        if (freeAddress == -1) {
            return MemoryConstants.MEM_CREATE_UFD_FAILED;
        }
        int address = mfd.addMfdItem(userName, freeAddress);
        if (address > 0) {
            currentUFD = SimulationDisk.getInstance().getUfd(address);
            return MemoryConstants.MEM_CREATE_UFD_SUCCESS;
        }
        return MemoryConstants.MEM_CREATE_UFD_FAILED;
    }

    public int[] mallocMemoryList(int size, String threadId) {
//        int temp = 0;
//        List<Integer> tempArray = new ArrayList<>();
//        for (int i = 0; i < memoryList.length; i++) {
//            if (memoryList[i] == null) {
//                tempArray.add(i);
//                temp++;
//                if (temp == size)
//                    break;
//            }
//        }
//        int[] res = new int[tempArray.size()];
//        for (int i = 0; i < res.length; i++) {
//            res[i] = tempArray.get(i);
//        }
//        return res;
        return MemoryBitMap.getInstance().mallocFreeMemBlocks(threadId);
    }

    /**
     * 创建文件
     * @param fileName 文件名
     * @param content 文件内容
     * @return 成功则为索引结点地址，失败为-1
     * @throws Exception
     */
    public int createFile(String fileName, String content) throws Exception {
        if (currentUFD == null) {
            throw new Exception("当前UFD为空，一定有问题！");
        }
        return createFile(fileName, content, content.length());
    }

    /**
     * 创建文件（直接使用大小）
     * @param fileName 文件名
     * @param size 文件大小
     * @return 成功为索引结点，失败为-1
     * @throws Exception
     */
    public int createFile(String fileName, int size) throws Exception {
        if (currentUFD == null) {
            throw new Exception("当前UFD为空，一定有问题！");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append("*");
        }
        return createFile(fileName, sb.toString(), size);
    }

    public int createFile(String fileName, String content, int size) throws Exception {
        if (currentUFD == null) {
            throw new Exception("当前UFD为空，一定有问题！");
        }
        return currentUFD.createFile(currentUFD.getUserName(), fileName, content, size);
    }


    private static final String TAG = "getFileAddress";

    /**
     * 通过索引地址，获得文件的所有块按顺序的物理地址
     * 同时读出外存块对应数据，存入内存中的地址
     * @param indexAddress 索引结点
     * @return 该文件的所有块外存地址
     */
    public int[] getFileDataAddresses(int indexAddress, String threadId, int[] memAddresses) throws Exception {

        List<Integer> resList = getFileDataAddresses(indexAddress);

        int[] res = new int[resList.size()];
        for (int i = 0; i < resList.size(); i++) {
            if (i < MemoryConstants.THREAD_DISTRIBUTED_BLOCKS_NUMBER) {
                DataBlock dataBlock = (DataBlock)SimulationDisk.getInstance().findBlock(resList.get(i));
                memoryList[memAddresses[i]] = new MemBlock(
                        dataBlock,
                        memAddresses[i],
                        threadId, true
                );
            }
            res[i] = resList.get(i);
        }
        return res;
    }

    /**
     * 仅获得所有文件外存地址
     * @param indexAddress
     * @return
     */
    public List<Integer> getFileDataAddresses(int indexAddress) {
        IndexNode block = (IndexNode)SimulationDisk.getInstance().findBlock(indexAddress);
        List<Integer> resList = new ArrayList<>();
        int[] tempDirectList = block.getDirectAddress();
        for (int i :
                tempDirectList) {
            if (i > 0) {
                resList.add(i);
            }
        }
        int indexIAddress = block.getIndexIAddress();
        int indexIIAddress = block.getIndexIIAddress();
        if (indexIAddress != -1) {//有二级索引
            IndexBlock indexBlockI = (IndexBlock)SimulationDisk.getInstance().findBlock(indexIAddress);
            int[] temp1 = indexBlockI.getDataPtr();
            for (int i :
                    temp1) {
                if (i > 0) {
                    resList.add(i);
                }
            }
        }
        if (indexIIAddress != -1) {
            throw new IllegalArgumentException("不应该出现三级索引的情况，前面的步骤出现问题！");
        }
        Log.d(TAG, "getFileDataAddresses: 获得地址" + Arrays.toString(resList.toArray()));
        return resList;
    }

    /**
     * 将外存数据读入内存
     * @param diskAddress 文件块外存地址
     * @param memAddress 文件块内存地址
     * @param fileName 文件名
     * @param order 文件内偏移
     */
    public MemBlock setMemBlock(int diskAddress, int memAddress, String fileName, int order, String threadId) throws Exception {
        DataBlock data = (DataBlock)SimulationDisk.getInstance().findBlock(diskAddress);
        if (!data.getFileName().equals(fileName) || data.getBlockOrder() != order) {
            throw new IllegalArgumentException("读出文件与预期不符合！");
        }
        MemBlock block = new MemBlock(
                currentUFD.getUserName(),
                fileName,
                memAddress,
                data.getContent(),
                order,
                threadId, true
        );
        if (memoryList[memAddress] != null) {
            throw new IllegalArgumentException("内存块被非法占用！-->" + memAddress);
        }
        memoryList[memAddress] = block;
        return block;
    }

    public MemBlock getMemBlock(int memAddress) {
        return (MemBlock)memoryList[memAddress];
    }

    public DiskBlock getMemDiskBlock(int address) {
        return memoryList[address];
    }

    /**
     * 12月23 0:34修改，有可能会出错
     * @param memAddress
     * @param clearType
     */
    public void clearMemBlock(int memAddress, int clearType) {
        memoryList[memAddress] = null;
        if (clearType == CLEAR_DELETE) {
            MemoryBitMap.getInstance().releaseMemBlock(memAddress);
        }
    }

    public void setMemBlock(int memAddress, @NonNull MemBlock block) {
        if (memoryList[memAddress] != null) {
            throw new IllegalArgumentException("内存块被非法占用" + memAddress);
        }
        memoryList[memAddress] = block;
    }

    public void clearAllMemory() {
        currentUFD = null;
        for (int i = 0; i < MEMORY_BLOCK_COUNT; i++) {
            memoryList[i] = null;
            MemoryBitMap.getInstance().releaseMemBlock(i);
        }

    }
}
