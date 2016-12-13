package io.github.vkdisco.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.vkdisco.R;

/**
 * Files dialog
 */

public class FileDialog extends DialogFragment implements
        FileDialogAdapter.OnFileItemChosenListener {
    private SelectMode mSelectMode = SelectMode.SINGLE_FILE;
    private Set<String> mExtensions = new HashSet<>();
    private List<File> mFiles = new ArrayList<>();
    private FileDialogAdapter mAdapter;
    private File mCurrentFile;
    private OnFileSelectedListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        mAdapter = new FileDialogAdapter(mFiles);
        mAdapter.setListener(this);
        RecyclerView rvFiles = new RecyclerView(getContext());
        rvFiles.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFiles.setAdapter(mAdapter);
        if (mCurrentFile != null) {
            getSubFilesAndDirs(mCurrentFile);
        }
        return builder.setTitle(R.string.text_label_choose_file)
                .setView(rvFiles)
                .create();
    }

    public void setStartFile(File startFile) {
        mCurrentFile = startFile;
    }

    public void setSelectMode(SelectMode selectMode) {
        this.mSelectMode = selectMode;
    }

    public void setListener(OnFileSelectedListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onRootChosen() {
        if (mCurrentFile == null) {
            return;
        }
        File parentFile = mCurrentFile.getParentFile();
        if (parentFile == null) {
            return;
        }
        mCurrentFile = parentFile;
        getSubFilesAndDirs(parentFile);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFileChosen(File file) {
        if (file == null) {
            return;
        }
        if (file.isDirectory()) { // Directory
            mCurrentFile = file;
            getSubFilesAndDirs(file);
        }
        if (file.isFile()) { // File
            if (mSelectMode == SelectMode.SINGLE_FILE) {
                if (mListener != null) {
                    mListener.onFileSelected(file);
                }
                dismiss();
            }
            if (mSelectMode == SelectMode.MULTIPLE_FILE) {
                if (mListener != null) {
                    mListener.onFileSelected(file);
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void getSubFilesAndDirs(File file) {
        if (file == null) {
            return;
        }
        mFiles.clear();
        Collections.addAll(mFiles, file.listFiles());
    }

    public enum SelectMode {
        SINGLE_FILE,
        MULTIPLE_FILE
    }

    public interface OnFileSelectedListener {
        void onFileSelected(File file);
    }
}
