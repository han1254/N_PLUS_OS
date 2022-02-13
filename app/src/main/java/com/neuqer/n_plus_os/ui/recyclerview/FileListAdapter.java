package com.neuqer.n_plus_os.ui.recyclerview;

import android.content.ClipData;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.neuqer.n_plus_os.R;
import com.neuqer.n_plus_os.databinding.ItemFileBinding;
import com.neuqer.n_plus_os.memory.SimulationMemory;
import com.neuqer.n_plus_os.memory.thread_factory.thread.OpenFileMsgObj;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Time:2020/12/20 20:49
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileItemViewHolder> {

//    private HashMap<String, Integer> mUFDMap;
    private List<FakeMap> fakeMaps = new ArrayList<>();

    private FileOperateListener fileOperateListener;
    private OnGoBtnClickListener onGoBtnClickListener;

    public FileListAdapter(FileOperateListener fileOperateListener) {
//        this.mUFDMap = mUFDMap;
        this.fileOperateListener = fileOperateListener;
//        for (Map.Entry<String, Integer> entry : mUFDMap.entrySet()) {
//            fakeMaps.add(new FakeMap(entry.getKey(), entry.getValue()));
//        }
    }

    public void setOnGoBtnClickListener(OnGoBtnClickListener listener) {
        this.onGoBtnClickListener = listener;
    }

//    public void updateData() {
//        this.mUFDMap = SimulationMemory.getInstance().getCurrentUFD().getUfdMap();
//        fakeMaps.clear();
//        for (Map.Entry<String, Integer> entry : mUFDMap.entrySet()) {
//            fakeMaps.add(new FakeMap(entry.getKey(), entry.getValue()));
//        }
//    }

    public void addList(List<FakeMap> list) {
        fakeMaps.addAll(list);
    }

    public int addItem(FakeMap fakeMap) {
        fakeMaps.add(fakeMap);
        return fakeMaps.size() - 1;
    }

    public void removeItem(int position) {
        fakeMaps.remove(position);
    }

    public List<FakeMap> getFakeMaps() {
        return fakeMaps;
    }

    @NonNull
    @Override
    public FileItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemFileBinding itemFileBinding = ItemFileBinding.inflate(inflater, parent, false);
        FileItemViewHolder fileItemViewHolder = new FileItemViewHolder(itemFileBinding, fileOperateListener, onGoBtnClickListener);
        return fileItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FileItemViewHolder holder, int position) {
        holder.bindItem(fakeMaps.get(position), position, fakeMaps);
    }


    @Override
    public int getItemCount() {
        return fakeMaps.size();
    }


    static class FileItemViewHolder extends RecyclerView.ViewHolder {

        private ItemFileBinding itemDataBinding;
        private String fileName;
        private int indexAddress;
        private int readPage;
        private FileOperate currentState = FileOperate.CLOSE;
        private int position;
        private FileOperateListener fileOperateListener;
        private OnGoBtnClickListener onGoBtnClickListener;
        public FileItemViewHolder(ItemFileBinding itemDataBinding,
                                  FileOperateListener fileOperateListener,
                                  OnGoBtnClickListener onGoBtnClickListener) {
            super(itemDataBinding.getRoot());
            this.itemDataBinding = itemDataBinding;
            this.fileOperateListener = fileOperateListener;
            this.onGoBtnClickListener = onGoBtnClickListener;
            readPage = 0;
        }
        public void bindItem(FakeMap fakeMap, int position, List<FakeMap> fakeMaps) {
            this.fileName = fakeMap.getFileName();
            this.indexAddress = fakeMap.getAddress();
            this.position = position;
            itemDataBinding.fileItemTxtFilename.setText(fileName);
            itemDataBinding.fileItemTxtTotalPage.setText(String.valueOf(fakeMap.getTotalPage()));
            itemDataBinding.fileItemFirstShow.setText(String.valueOf(fakeMap.getCurrentContent()[0]));
            itemDataBinding.fileItemSecondShow.setText(String.valueOf(fakeMap.getCurrentContent()[1]));
            itemDataBinding.fileItemThirdShow.setText(String.valueOf(fakeMap.getCurrentContent()[2]));
            itemDataBinding.fileItemFourthShow.setText(String.valueOf(fakeMap.getCurrentContent()[3]));

            if (fakeMap.isOpen()) {
                itemDataBinding.fileItemFileContent.setVisibility(View.VISIBLE);
            } else {
                itemDataBinding.fileItemFileContent.setVisibility(View.GONE);
            }

            itemDataBinding.fileItemBtnOpen.setOnClickListener(v -> {
                if (itemDataBinding.fileItemFileContent.getVisibility() == View.GONE) {
                    itemDataBinding.fileItemFileContent.setVisibility(View.VISIBLE);
                    fileOperateListener.onOpClick(FileOperate.OPEN, indexAddress, fileName, position, readPage);
                    currentState = FileOperate.OPEN;
                    fakeMaps.get(position).setOpen(true);

                }
            });
            itemDataBinding.fileItemBtnClose.setOnClickListener(v -> {
                if (itemDataBinding.fileItemFileContent.getVisibility() == View.VISIBLE) {
                    itemDataBinding.fileItemFileContent.setVisibility(View.GONE);
                    fileOperateListener.onOpClick(FileOperate.CLOSE, indexAddress, fileName, position, readPage);
                    currentState = FileOperate.CLOSE;
                    fakeMaps.get(position).setOpen(false);
                }
            });
            itemDataBinding.fileItemBtnDelete.setOnClickListener(v -> {
                fileOperateListener.onOpClick(FileOperate.DELETE, indexAddress, fileName, position, readPage);
            });

            itemDataBinding.fileItemBtnGo.setOnClickListener(v -> {
                readPage = Integer.parseInt(itemDataBinding.fileItemEditPage.getText().toString().trim());
                try {
                    fakeMaps.get(position).setOpen(true);
                    onGoBtnClickListener.onGoClick(indexAddress, fileName, position, readPage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

    }

}
