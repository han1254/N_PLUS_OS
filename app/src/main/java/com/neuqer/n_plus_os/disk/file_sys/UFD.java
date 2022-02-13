package com.neuqer.n_plus_os.disk.file_sys;



import com.neuqer.n_plus_os.disk.BitMapController;
import com.neuqer.n_plus_os.disk.DiskBlock;
import com.neuqer.n_plus_os.disk.SimulationDisk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Time:2020/12/19 18:35
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function: User File Directory
 */
public class UFD extends DiskBlock {

    private HashMap<String, Integer> mUfdMap = new HashMap<>();
    private UFD mNextUfd = null;
    private int mNextUfdAddress = -1;
    private String userName;

    public UFD(int mDiskAddress, String userName) {
        super(mDiskAddress);
        this.userName = userName;
    }

    public int createFile(String userName, String fileName, String fileContent, int size) throws Exception {
        int res = SimulationDisk.getInstance().createFile(userName, fileName, fileContent, size);
        if (res > 0) {
            addUfdItem(fileName, res, size);
        }
        return res;
    }

    public int addUfdItem(String fileName, int indexAddress, int size) {
        if (mUfdMap.size() >= DirectoryConstants.MAX_COUNT) {
//            if (mNextUfd == null) {
//                // TODO: 2020/12/20 创建新的UFD，并赋值给mNextUfd
//                int freeAddress = BitMapController.getInstance().findFreeBlock();
//                if (freeAddress == -1) {
//                    return DirectoryConstants.NO_FREE_DISK_BLOCK;
//                }
//                SimulationDisk.getInstance().addMFDItem(userName, freeAddress);
//                mNextUfd = (UFD) SimulationDisk.getInstance().findBlock(freeAddress);
//                if (mNextUfd == null) {
//                    throw new NullPointerException("UFD创建失败！");
//                }
//            }
//            return mNextUfd.addUfdItem(fileName, address, size);
            throw new ArrayIndexOutOfBoundsException("文件数量过多！");
        } else if (mUfdMap.containsKey(fileName)) {
            System.out.println(fileName + "已经存在，请更换文件名！");
            return DirectoryConstants.FILE_ALREADY_EXIST;
        } else {
            mUfdMap.put(fileName, indexAddress);
            SimulationDisk.getInstance().addUFDItem(fileName, mDiskAddress, indexAddress, size);
            return DirectoryConstants.UFD_ITEM_INSERT_SUCCESS;
        }
    }

    public String getUserName() {
        return userName;
    }


    public HashMap<String, Integer> getUfdMap() {
        return mUfdMap;
    }

    public int getFileIndex(String fileName) {
        return mUfdMap.get(fileName);
    }

    @Override
    public DiskType getDiskType() {
        return DiskType.UFD;
    }
}
