package io.github.vkdisco.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import io.github.vkdisco.R;

/**
 * File dialog adapter
 */

public class FileDialogAdapter extends RecyclerView.Adapter<FileDialogAdapter.FileViewHolder> {
    private List<File> mFiles;
    private OnFileItemChosenListener mListener;

    public FileDialogAdapter(List<File> files) {
        this.mFiles = files;
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.file_list_item, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FileViewHolder holder, int position) {
        if (position == 0) {
            holder.bindRoot(mListener);
            return;
        }
        holder.bind(mFiles.get(position - 1), mListener);
    }

    @Override
    public int getItemCount() {
        return mFiles.size() + 1;
    }

    public void setListener(OnFileItemChosenListener listener) {
        this.mListener = listener;
    }

    public interface OnFileItemChosenListener {
        void onRootChosen();
        void onFileChosen(File file);
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIVType;
        private TextView mTVName;
        private boolean mToRoot = false;
        private File mFile;
        private OnFileItemChosenListener mListener;

        public FileViewHolder(View itemView) {
            super(itemView);
            mIVType = ((ImageView) itemView.findViewById(R.id.ivType));
            mTVName = ((TextView) itemView.findViewById(R.id.tvName));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        if (mToRoot) {
                            mListener.onRootChosen();
                        } else {
                            mListener.onFileChosen(mFile);
                        }
                    }
                }
            });
        }

        public void bind(File file, OnFileItemChosenListener listener) {
            if (file.isFile()) {
                mIVType.setImageResource(R.drawable.ic_insert_drive_file_black);
            }
            if (file.isDirectory()) {
                mIVType.setImageResource(R.drawable.ic_folder_black);
            }
            mTVName.setText(file.getName());
            mToRoot = false;
            mFile = file;
            mListener = listener;
        }

        public void bindRoot(OnFileItemChosenListener listener) {
            mIVType.setImageResource(R.drawable.ic_arrow_upward_black);
            mTVName.setText("...");
            mToRoot = true;
            mFile = null;
            mListener = listener;
        }
    }
}
