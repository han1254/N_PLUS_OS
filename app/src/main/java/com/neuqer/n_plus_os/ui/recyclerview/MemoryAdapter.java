package com.neuqer.n_plus_os.ui.recyclerview;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.neuqer.n_plus_os.R;
import com.neuqer.n_plus_os.disk.DataBlock;
import com.neuqer.n_plus_os.disk.DiskBlock;
import com.neuqer.n_plus_os.disk.IndexBlock;
import com.neuqer.n_plus_os.disk.IndexNode;
import com.neuqer.n_plus_os.disk.SimulationDisk;
import com.neuqer.n_plus_os.memory.MemBlock;
import com.neuqer.n_plus_os.memory.MemoryBitMap;
import com.neuqer.n_plus_os.memory.SimulationMemory;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Time:2020/12/22 12:30
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder> {



    @NonNull
    @Override
    public MemoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_disk_block, parent, false);
        return new MemoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoryViewHolder holder, int position) {
        holder.bindItem(position);
    }

    @Override
    public int getItemCount() {
        return 64;
    }

    static class MemoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView1;
        private TextView txtBlockNumber;
        private TextView textView;
        public MemoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView1 = itemView.findViewById(R.id.item_disk_img1);
            textView = itemView.findViewById(R.id.item_disk_txt);
            txtBlockNumber = itemView.findViewById(R.id.txt_disk_block_number);
        }

        public void bindItem(int position) {
            DiskBlock block = SimulationMemory.getInstance().getMemDiskBlock(position);
            if (!MemoryBitMap.getInstance().getState(position)) {
                setItem("#50C1E9", "0", position);
                return;
            }

            if (block == null) {
                setItem("#d9d9f3", MemoryBitMap.getInstance().getThreadId(position), position);
                return;
            }

            switch (block.getDiskType()) {
                case MEMORY:
                    setItem("#d9d9f3", ((MemBlock)block).getFileName(), position);
                    break;
                case MFD:
                    setItem("#f6003c", "MFD", position);
                    break;
                default:
                    setItem("#50C1E9", "0", position);
                    break;
            }

        }

        private void setItem(String color, String content, int position) {
            imageView1.setColorFilter(Color.parseColor(color));
            textView.setText(content);
            txtBlockNumber.setText(String.valueOf(position));
        }
    }

}
