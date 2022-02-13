package com.neuqer.n_plus_os.memory.thread_factory.thread;

import android.os.AsyncTask;
import android.util.Log;

import com.neuqer.n_plus_os.memory.MemoryConstants;
import com.neuqer.n_plus_os.memory.SimulationMemory;
import com.neuqer.n_plus_os.ui.IUserFileInitListener;
import com.neuqer.n_plus_os.ui.LoginListener;
import com.neuqer.n_plus_os.ui.recyclerview.FakeMap;
import com.neuqer.n_plus_os.util.FileLockUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Time:2020/12/21 19:32
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public class InitUserFileTask extends AsyncTask<Void, Void, List<FakeMap>> {

    private static final String TAG = "InitUserFile";
    private IUserFileInitListener listener;

    public void setListener(IUserFileInitListener listener) {
        this.listener = listener;
    }

    @Override
    protected List<FakeMap> doInBackground(Void... voids) {
        List<FakeMap> list = new ArrayList<>();
        int result = 0;
        try {
            Thread.sleep(500);
//
//            if (result != MemoryConstants.MEM_GET_UFD_SUCCESS) {
//                Log.d(TAG, "doInBackground: 用户不在MFD中，开始创建新目录");
//                result = SimulationMemory.getInstance().createUfd(strings[0]);//没有该用户的话就创建新的
//            }
//            if (result != MemoryConstants.MEM_CREATE_UFD_SUCCESS) {
//                Log.d(TAG, "doInBackground: 创建用户文件夹UFD失败");
//                throw new IllegalArgumentException("创建失败");
//            }
//            Log.d(TAG, "doInBackground: 创建UFD成功");
//            result = SimulationMemory.getInstance().findUserIsInMFD();//先去MFD读
            if (FileLockUtil.getInstance().getProtectedFiles().size() == 0) {
                HashMap<String, Integer> ufdMap = SimulationMemory.getInstance().getCurrentUFD().getUfdMap();
                for (Map.Entry<String, Integer> entry : ufdMap.entrySet()) {
                    FakeMap obj = new FakeMap(entry.getKey(), entry.getValue());
                    list.add(obj);
                }
            } else {
                return FileLockUtil.getInstance().getProtectedFiles();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<FakeMap> fakeMaps) {
        super.onPostExecute(fakeMaps);
        listener.onFileInit(fakeMaps);
    }
}
