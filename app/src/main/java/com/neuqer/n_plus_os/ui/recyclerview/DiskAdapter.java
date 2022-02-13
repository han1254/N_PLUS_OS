package com.neuqer.n_plus_os.ui.recyclerview;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.neuqer.n_plus_os.R;
import com.neuqer.n_plus_os.disk.BitMapController;
import com.neuqer.n_plus_os.disk.DataBlock;
import com.neuqer.n_plus_os.disk.DiskBlock;
import com.neuqer.n_plus_os.disk.IndexBlock;
import com.neuqer.n_plus_os.disk.IndexNode;
import com.neuqer.n_plus_os.disk.SimulationDisk;

import java.text.BreakIterator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Time:2020/12/22 9:29
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public class DiskAdapter extends RecyclerView.Adapter<DiskAdapter.DiskViewHolder> {

    @NonNull
    @Override
    public DiskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_disk_block, parent, false);
        return new DiskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiskViewHolder holder, int position) {
        holder.bindItem(position);
    }

    @Override
    public int getItemCount() {
        return 900;
    }

    static class DiskViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView1;
        private TextView txtBlockNumber;
        private TextView textView;

        public DiskViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView1 = itemView.findViewById(R.id.item_disk_img1);
            textView = itemView.findViewById(R.id.item_disk_txt);
            txtBlockNumber = itemView.findViewById(R.id.txt_disk_block_number);
        }

        public void bindItem(int position) {
            DiskBlock block = SimulationDisk.getInstance().findBlock(position);
            if (block == null) {
                setItem("#50C1E9", "0", position);
                return;
            }


            switch (block.getDiskType()) {
                case INDEX_NODE:
                    setItem("#d9d9f3", ((IndexNode)block).getFileName(), position);
                    break;
                case INDEX_BLOCK:
                    String t = "一级结点";
                    if (((IndexBlock)block).getType() == IndexBlock.IndexType.INDEX_TYPE_II)
                        t = "二级结点";
                    setItem("#9dd3a8", t, position);
                    break;
                case MFD:
                    setItem("#f6003c", "MFD", position);
                    break;
                case UFD:
                    setItem("#7A57D1", "UFD", position);
                    break;
                case DATA:
                    DataBlock block1 = (DataBlock) block;
                    setItem("#FFE869", block1.getFileName() + ":" + block1.getContent(), position);
                    break;
                default:
                    setItem("#50C1E9", "0", position);
            }

        }

        private void setItem(String color, String content, int position) {
            imageView1.setColorFilter(Color.parseColor(color));
            textView.setText(content);
            txtBlockNumber.setText(String.valueOf(position));
        }
    }
}
