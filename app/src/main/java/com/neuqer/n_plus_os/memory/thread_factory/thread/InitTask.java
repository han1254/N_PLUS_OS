package com.neuqer.n_plus_os.memory.thread_factory.thread;

import android.os.AsyncTask;

import com.neuqer.n_plus_os.memory.MemoryConstants;
import com.neuqer.n_plus_os.memory.SimulationMemory;
import com.neuqer.n_plus_os.ui.LoginListener;


/**
 * Time:2020/12/20 15:55
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public class InitTask extends AsyncTask<String, Void, Integer> {

    private LoginListener listener;

    public InitTask(LoginListener listener) {
        this.listener = listener;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        int result = 0;
        try {
            Thread.sleep(500);
            result = SimulationMemory.getInstance().findUserIsInMFD(strings[0]);//先去MFD读
            if (result == MemoryConstants.MEM_GET_UFD_SUCCESS) {
                return result;
            }
            result = SimulationMemory.getInstance().createUfd(strings[0]);//没有该用户的话就创建新的
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        listener.onOperateComplete(integer);
    }
}
