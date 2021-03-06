package com.neuqer.n_plus_os.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.neuqer.n_plus_os.R;
import com.neuqer.n_plus_os.memory.thread_factory.thread.CloseFileRunnable;
import com.neuqer.n_plus_os.memory.thread_factory.thread.CreateFileRunnable;
import com.neuqer.n_plus_os.memory.thread_factory.IOperateComplete;
import com.neuqer.n_plus_os.memory.thread_factory.ThreadManager;
import com.neuqer.n_plus_os.memory.thread_factory.thread.DeleteFileRunnable;
import com.neuqer.n_plus_os.memory.thread_factory.thread.InitUserFileTask;
import com.neuqer.n_plus_os.memory.thread_factory.thread.OpenFileMsgObj;
import com.neuqer.n_plus_os.memory.thread_factory.thread.OpenFileRunnable;
import com.neuqer.n_plus_os.ui.dialog.MyDialog1;
import com.neuqer.n_plus_os.ui.recyclerview.FakeMap;
import com.neuqer.n_plus_os.ui.recyclerview.FileListAdapter;
import com.neuqer.n_plus_os.ui.recyclerview.OnGoBtnClickListener;
import com.neuqer.n_plus_os.util.FileLockUtil;
import com.neuqer.n_plus_os.util.NowString;

import java.util.HashMap;
import java.util.List;
import java.util.logging.FileHandler;

public class FileManagementActivity extends AppCompatActivity implements IOperateComplete, IUserFileInitListener {

    private ImageView mAddBtn;
    private RecyclerView mRecyclerView;
    private MyDialog1 dialog1;
    private TextView txtLogWindow;
    private FileListAdapter adapter;
    private String info = "";

    private InitUserFileTask task;
    private FileManageHandler handler;
    private static final String TAG = "FragmentManagement";



    @SuppressLint("HandlerLeak")
    class FileManageHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ThreadManager.WHAT_CREATE_FILE:
//                    adapter.updateData();
//                    adapter.notifyDataSetChanged();
//                    Toast.makeText(FileManagementActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                    int position = adapter.addItem((FakeMap)msg.obj);
                    updateLog("??????????????????");
                    updateLog("???????????????"+ ThreadManager.getInstance().getRunningThreadCount());
                    adapter.notifyItemChanged(position);
                    break;
                case ThreadManager.WHAT_READ_FILE:

                    OpenFileMsgObj obj = (OpenFileMsgObj) msg.obj;
                    (adapter.getFakeMaps().get(obj.getItemPosition())).setCurrentContent(obj.getContent());
                    (adapter.getFakeMaps().get(obj.getItemPosition())).setCurrentPage(obj.getPage());
                    (adapter.getFakeMaps().get(obj.getItemPosition())).setTotalPage(obj.getMaxPage());
                    adapter.notifyDataSetChanged();
                    StringBuilder res = new StringBuilder();
                    for (int i = 0; i < 4; i++) {
                        res.append(obj.getContent()[i]);
                    }
                    updateLog("????????????:" + res.toString());
                    break;
                case ThreadManager.WHAT_CLOSE_FILE:
                    String fileName = (String)msg.obj;
                    updateLog("??????" + fileName + "????????? ????????????");
                    updateLog("???????????????"+ ThreadManager.getInstance().getRunningThreadCount());
                    updateLog("???????????????");
                    FileLockUtil.getInstance().releaseFileLock(fileName);
                    break;
                case ThreadManager.WHAT_DELETE_FILE:
                    updateLog("??????????????????");
                    int itemPosition = (int)msg.obj;
                    adapter.removeItem(itemPosition);
                    adapter.notifyDataSetChanged();
                    break;
                case ThreadManager.WHAT_REPLACE_BLOCK:
                    int pos = (int) msg.obj;
//                     updateLog("??????LRU???????????????:" + (pos + 1));
                     break;
                default:
                    break;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_management);
        initView();
        initRecyclerView();
        initDialog();

        task = new InitUserFileTask();
        task.setListener(this);
        task.execute();
        updateLog("???????????????????????????");

        handler = new FileManageHandler();

        mAddBtn.setOnClickListener(v -> {
            dialog1.show();
        });

    }

    private void initDialog() {
        dialog1 = new MyDialog1(this);
        dialog1.setOnCenterItemClickListener(new MyDialog1.OnCenterItemClickListener() {
            @Override
            public void onConfirmClick(String title, String content) throws Exception {
                // TODO: 2020/12/20 ????????????
                dialog1.dismiss();
                CreateFileRunnable runnable = ThreadManager.getInstance().createThreadAndMallocMemory(new FileManageHandler());
                if (runnable != null) {
                    updateLog("?????????????????????????????????????????????");
                    updateLog("????????????????????????" + (ThreadManager.getInstance().getRunningThreadCount() + 1));
                    txtLogWindow.setText(info);
                    runnable.setFileName(title);
                    runnable.setContent(content);
                    new Thread(runnable).start();
                } else {
                    updateLog("???????????????????????????");
                }

            }

            @Override
            public void onCancelClick() {
                dialog1.dismiss();
            }
        });
    }

    private void initRecyclerView() {
        adapter = new FileListAdapter((opType, fileIndexAddress, fileName, position, readPage) -> {
            // TODO: 2020/12/21 ?????????????????????????????????????????????
            switch (opType) {
                case OPEN:
                    Log.d("operate_file", "onCreate: ????????????");

                    try {
                        openFile(fileIndexAddress, fileName, position, readPage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case CLOSE:
                    Log.d("operate_file", "onCreate: ????????????");
                    closeFile(fileIndexAddress, fileName, position, readPage);
                    break;
                case DELETE:
                    Log.d("operate_file", "onCreate: ????????????");
                    myDeleteFile(fileIndexAddress, fileName, position, readPage);
                    break;
                default:
                    Log.d("operate_file", "onCreate: ????????????");
                    break;
            }

        });
        adapter.setOnGoBtnClickListener((fileIndexAddress, fileName, itemPosition, readPage) -> {
            FakeMap temp = adapter.getFakeMaps().get(itemPosition);
            if (readPage > temp.getTotalPage()) {
                Toast.makeText(this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
                return;
            }
//            OpenFileRunnable thread;
//            OpenFileRunnable runnable = ThreadManager.getInstance().getRunnable(fileName);
//            if (runnable != null)
//                thread = runnable;
//            else {
//                thread = ThreadManager.getInstance()
//                        .createThreadAndMallocMemory(handler, fileIndexAddress, fileName, itemPosition);
//            }
            OpenFileRunnable thread = getOpenFileRunnable(fileIndexAddress, fileName, itemPosition, readPage);
            if (thread == null) {
                Log.d(TAG, "initRecyclerView: Go???????????????thread??????");
            } else {
                thread.setIndexAddress(fileIndexAddress);
                thread.setItemPosition(itemPosition);
                thread.setReadPage(readPage - 1);
                new Thread(thread).start();
            }

        });
        mRecyclerView.setAdapter(adapter);
    }

    private void initView() {
        mAddBtn = (ImageView) findViewById(R.id.file_manage_btn_add);
        mRecyclerView = (RecyclerView) findViewById(R.id.file_manage_recyclerview);
        txtLogWindow = findViewById(R.id.txt_log_window);
        txtLogWindow.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    /**
     * ????????????
     * @param fileIndexAddress
     * @param fileName
     * @param itemPosition
     * @param readPage
     */
    private void myDeleteFile(int fileIndexAddress, String fileName, int itemPosition, int readPage) {
        updateLog("??????????????????????????????");
        if (FileLockUtil.getInstance().requestFileOp(fileName) == 0) {
            Toast.makeText(this, "???????????????", Toast.LENGTH_SHORT).show();
        } else {
            updateLog("??????????????????????????????");
            new Thread(new DeleteFileRunnable(fileIndexAddress, handler, itemPosition, fileName)).start();
        }
    }

    /**
     * ????????????
     * @param fileIndexAddress
     * @param fileName
     * @param itemPosition
     * @param readPage
     */
    private void closeFile(int fileIndexAddress, String fileName, int itemPosition,  int readPage) {

        new Thread(new CloseFileRunnable(handler,
                fileName, ThreadManager
                .getInstance()
                .getOpenThreadMap()
                .get(fileName).getMemAddress())).start();
    }

    /**
     * ????????????
     * @param fileIndexAddress
     * @param fileName
     * @param itemPosition
     * @param readPage
     * @throws Exception
     */
    private void openFile(int fileIndexAddress, String fileName, int itemPosition, int readPage) throws Exception {
        Log.d(TAG, "openFile: ???????????????");
        updateLog("???????????????");
        if (FileLockUtil.getInstance().requestFileOp(fileName) == 0) {
            Toast.makeText(this, "???????????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }

       OpenFileRunnable thread = getOpenFileRunnable(fileIndexAddress, fileName, itemPosition, readPage);
        if (thread == null) {
            updateLog("?????????????????????????????????");
        } else {
            thread.setIndexAddress(fileIndexAddress);
            thread.setItemPosition(itemPosition);
            thread.setReadPage(readPage);
            updateLog("??????????????????..");

            new Thread(thread).start();
        }
    }

    private OpenFileRunnable getOpenFileRunnable(int fileIndexAddress, String fileName, int itemPosition, int readPage) {
        OpenFileRunnable thread = null;
        if (ThreadManager.getInstance().getOpenThreadMap().containsKey(fileName)) {
            thread = ThreadManager.getInstance().getRunnable(handler, fileIndexAddress, fileName, itemPosition);
        } else {
            try {
                updateLog("??????????????????...");
                updateLog("???????????????"+ (ThreadManager.getInstance().getRunningThreadCount() + 1));
                thread = ThreadManager.getInstance().createThreadAndMallocMemory(handler, fileIndexAddress, fileName, itemPosition);
                updateLog("??????????????????");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return thread;
    }

    private void updateLog(String str) {
        info += NowString.getCurrentTime() + ":" + str + "\n";
        txtLogWindow.setText(info);
    }

    @Override
    public void createFileComplete(int res) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void deleteFileComplete(int res) {

    }

    @Override
    public void onFileInit(List<FakeMap> list) {
        adapter.addList(list);
        adapter.notifyDataSetChanged();
        updateLog("???????????????");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // FileLockUtil.getInstance().releaseAllFile();
        FileLockUtil.getInstance().saveFiles(adapter.getFakeMaps());
    }
}