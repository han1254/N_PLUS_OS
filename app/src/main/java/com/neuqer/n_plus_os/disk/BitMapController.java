package com.neuqer.n_plus_os.disk;

import com.neuqer.n_plus_os.memory.MemoryConstants;
import com.neuqer.n_plus_os.memory.SimulationMemory;

import java.util.ArrayList;
import java.util.List;

/**
 * Time:2020/12/20 8:37
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public class BitMapController {
    public final int ROW_NUM = 30;
    public final int COL_NUM = 30;
    private int[][] blockMap = new int[ROW_NUM][COL_NUM];
    private volatile static BitMapController bitMapController;
    private BitMapController() {
        blockMap[0][0] = 1;//规定第一块为MFD
    }

    public static BitMapController getInstance() {
        if (bitMapController == null) {
            synchronized (BitMapController.class) {
                if (bitMapController == null) {
                    bitMapController = new BitMapController();
                }
            }
        }
        return bitMapController;
    }

    public int[] findFreeBlocks(int size) {
        int temp = 0;
        List<Integer> resList = new ArrayList<>();
        for (int i = 0; i < ROW_NUM; i++) {
            for (int j = 0; j < COL_NUM; j++) {
                if (blockMap[i][j] == DiskConstants.DISK_FREE) {
                    temp++;
                    resList.add(i * ROW_NUM + j);
                    blockMap[i][j] = DiskConstants.DISK_USED;
                    if (temp == size) {
                        break;
                    }
                }
            }
            if (temp == size)
                break;
        }
        int[] res = new int[resList.size()];
        for (int i = 0; i < resList.size(); i++) {
            res[i] = resList.get(i);
        }
        return res;
    }

    public int findFreeBlock() {
        for (int i = 0; i < ROW_NUM; i++) {
            for (int j = 0; j < COL_NUM; j++) {
                if (blockMap[i][j] == 0) {
                    return i * ROW_NUM + j;
                }
            }
        }
        return -1;
    }

    public void setBlockState(int state, int address) {
        blockMap[address / ROW_NUM][address % ROW_NUM] = state;
    }

    public boolean getState(int address) {
        return (blockMap[address / ROW_NUM][address % ROW_NUM]) == DiskConstants.DISK_USED;
    }
}
