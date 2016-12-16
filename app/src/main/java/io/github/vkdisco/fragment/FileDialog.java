package io.github.vkdisco.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private EditText mFileName;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        mAdapter = new FileDialogAdapter(mFiles);
        mAdapter.setListener(this);

        LinearLayout llBase = new LinearLayout(getContext());
        llBase.setOrientation(LinearLayout.VERTICAL);

        RecyclerView rvFiles = new RecyclerView(getContext());
        rvFiles.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFiles.setAdapter(mAdapter);

        mFileName = new EditText(getContext());

        if (mSelectMode == SelectMode.SAVE_FILE) {
            TextView tvFileNameLabel = new TextView(getContext());
            tvFileNameLabel.setText(R.string.text_label_filename);
            llBase.addView(tvFileNameLabel);
            llBase.addView(mFileName);
        }

        llBase.addView(rvFiles);

        if (mCurrentFile != null) {
            getSubFilesAndDirs(mCurrentFile);
        }

        builder.setTitle(R.string.text_label_choose_file)
                .setView(llBase);

        if (mSelectMode == SelectMode.SAVE_FILE) {
            builder.setPositiveButton(R.string.text_button_dialog_save, null);
        }

        final AlertDialog fileDialog = builder.create();

        if (mSelectMode == SelectMode.SAVE_FILE) {
            fileDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button button = fileDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mFileName.getText().toString().isEmpty()) {
                                Toast.makeText(getContext(), R.string.text_filename_empty, Toast.LENGTH_SHORT)
                                        .show();
                                return;
                            }
                            if (mListener != null) {
                                mListener.onFileSelected(
                                        new File(mCurrentFile, mFileName.getText().toString())
                                );
                            }
                            fileDialog.dismiss();
                        }
                    });
                }
            });
        }

        return fileDialog;
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
        if (mSelectMode != SelectMode.SAVE_FILE) {
            Collections.addAll(mFiles, file.listFiles());
        } else {
            for (File listedFile : file.listFiles()) {
                if (listedFile.isDirectory()) {
                    mFiles.add(listedFile);
                }
            }
        }
    }

    public enum SelectMode {
        SINGLE_FILE,
        MULTIPLE_FILE,
        SAVE_FILE
    }

    public interface OnFileSelectedListener {
        void onFileSelected(File file);
    }
}
