package com.neuqer.n_plus_os.disk.file_sys;



import com.neuqer.n_plus_os.disk.DiskBlock;
import com.neuqer.n_plus_os.disk.SimulationDisk;

import java.util.HashMap;

/**
 * Time:2020/12/19 18:35
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function: Main File Directory
 */
public class MFD extends DiskBlock {

    private HashMap<String, Integer> mMfdMap = new HashMap<>();//<username, address>

    public MFD(int mDiskAddress) {
        super(mDiskAddress);
    }

    public int getUfdAddress(String userName) {
        if (!mMfdMap.containsKey(userName)) {
            return -1;
        }
        if (mMfdMap.get(userName) == null) {
            throw new IllegalArgumentException("无UFD与"+userName+"对应");
        }
        return mMfdMap.get(userName);
    }

    public int addMfdItem(String userName, int address) {
        if (mMfdMap.size() >= DirectoryConstants.MAX_COUNT) {
            System.out.println("MFD已满，无法增加用户");
            return DirectoryConstants.MFD_NO_FREE;
        }
        mMfdMap.put(userName, address);
        SimulationDisk.getInstance().addMFDItem(userName, address);//写入外存
        return address;
    }

    @Override
    public DiskBlock.DiskType getDiskType() {
        return DiskType.MFD;
    }
}
