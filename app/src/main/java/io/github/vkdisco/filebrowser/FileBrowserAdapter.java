package io.github.vkdisco.filebrowser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.File;
import java.util.List;

import io.github.vkdisco.R;

public class FileBrowserAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<File> mFileList;

    public FileBrowserAdapter(Context context, List<File> mFileList) {
        this.mFileList = mFileList;
        this.mInflater = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
    }

    @Override
    public int getCount() {
        return mFileList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.list_file, parent, false);
            holder = new ViewHolder();
            holder.ivFileIcon = ((ImageView) view.findViewById(R.id.ivFileIcon));
            holder.tvFileName = ((TextView) view.findViewById(R.id.tvFileName));
            view.setTag(holder);
        } else {
            holder = ((ViewHolder) view.getTag());
        }
        File file = mFileList.get(position);
        if (file != null) {
            holder.tvFileName.setText(file.getName());
            if (file.isFile()) {
                holder.ivFileIcon.setImageResource(R.drawable.ic_file);
            }
            if (file.isDirectory()) {
                holder.ivFileIcon.setImageResource(R.drawable.ic_folder);
            }
        } else {
            holder.tvFileName.setText("..");
            holder.ivFileIcon.setImageResource(R.drawable.ic_up);
        }
        return view;
    }

    private static class ViewHolder {
        ImageView ivFileIcon;
        TextView tvFileName;
    }
}
