package com.neuqer.n_plus_os.util;

import android.util.Log;

import com.neuqer.n_plus_os.memory.SimulationMemory;
import com.neuqer.n_plus_os.ui.recyclerview.FakeMap;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Handler;

/**
 * Time:2020/12/21 22:04
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public class FileLockUtil {

    private static final String TAG = "FILE_LOCK";
    private HashMap<String, List<FakeMap>> protectedFiles;
    private static volatile FileLockUtil fileLockUtil;
    private HashMap<String, Integer> fileLockMap;
    private FileLockUtil() {
        fileLockMap = new HashMap<>();
        protectedFiles = new HashMap<>();
    }

    public static FileLockUtil getInstance() {
        if (fileLockUtil == null) {
            synchronized (FileLockUtil.class) {
                if (fileLockUtil == null) {
                    fileLockUtil = new FileLockUtil();
                }
            }
        }
        return fileLockUtil;
    }

    public void releaseFileLock(String fileName) {

        Log.d(TAG, "releaseFileLock: 为文件" + fileName + "释放锁");
        fileLockMap.put(fileName, 1);
    }

    public int requestFileOp(String fileName) {
        if (!fileLockMap.containsKey(fileName)) {
            Log.d(TAG, "checkLockState: 文件还未进入锁管理状态");
            throw new IllegalArgumentException("文件还未正式被锁任务管理");
        }
        if (fileLockMap.get(fileName) == 1) {
            fileLockMap.put(fileName, 0);
            return 1;
        }
        Log.d(TAG, "requestFileOp: 文件正在被使用");
        return 0;
    }

    public int checkLockState(String fileName) {
        if (!fileLockMap.containsKey(fileName)) {
            Log.d(TAG, "checkLockState: 文件还未进入锁管理状态");
            throw new IllegalArgumentException("文件还未正式被锁任务管理");
        }
        return fileLockMap.get(fileName);
    }

    public void removeFile(String fileName) {
        if (fileLockMap.containsKey(fileName) && fileLockMap.get(fileName) == 0) {
            Log.d(TAG, "removeFile: 文件正在被占用");
        }

        fileLockMap.remove(fileName);
    }

    public void releaseAllFile() {
        for (String key : fileLockMap.keySet()) {
            fileLockMap.put(key, 1);
        }
    }


    public void saveFiles(List<FakeMap> protectedFile) {
        protectedFiles.put(
                SimulationMemory.getInstance().getCurrentUFD().getUserName(),
                protectedFile
        );
    }

    public List<FakeMap> getProtectedFiles() {

        return protectedFiles.get(SimulationMemory.getInstance().getCurrentUFD().getUserName());
    }
}
