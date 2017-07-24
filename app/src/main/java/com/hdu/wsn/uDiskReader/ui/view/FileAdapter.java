package com.hdu.wsn.uDiskReader.ui.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdu.wsn.uDiskReader.R;
import com.hdu.wsn.uDiskReader.usb.file.FileUtil;

import java.io.File;
import java.util.List;

/**
 * Created by 杨健 on 2017/5/10.
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> implements View.OnLongClickListener, View.OnClickListener {
    // 文件列表
    private List<File> data;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener = null;
    // 是否以私密空间item打头
    private boolean secretHeader;

    public void setSecretHeader(boolean secretHeader) {
        this.secretHeader = secretHeader;
        if (secretHeader) {
            data.add(null);
        }
    }

    public void removeData(int position) {
        data.remove(position);
        notifyDataSetChanged();
    }

    public FileAdapter(List<File> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item_layout, parent, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int index = position;
        if (!secretHeader) {
            index++;
        }
        if (index > 0) {
            File file = data.get(index - 1);
            String name = file.getName();
            String end = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();
            if (file.isDirectory()) {
                holder.iv_file.setImageResource(R.drawable.folder);
            } else {
                holder.iv_file.setImageResource(FileUtil.getFileImage(end));
            }
            holder.tv_file.setText(name);
            holder.itemView.setTag(position);
        } else {
            holder.iv_file.setImageResource(R.drawable.secret_space);
            holder.tv_file.setText("私密空间");
            holder.itemView.setTag(0);
        }

    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public boolean onLongClick(View v) {
        if (onRecyclerViewItemClickListener != null) {
            onRecyclerViewItemClickListener.onItemLongClick(v, (int) v.getTag());
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (onRecyclerViewItemClickListener != null) {
            onRecyclerViewItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_file;
        private TextView tv_file;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_file = (ImageView) itemView.findViewById(R.id.file_icon);
            tv_file = (TextView) itemView.findViewById(R.id.file_name);
        }

    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

}
