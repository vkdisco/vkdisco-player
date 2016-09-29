package io.github.vkdisco.filebrowser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.vkdisco.R;

public class OpenFileActivity extends AppCompatActivity implements ListView.OnItemClickListener{
    public static final String PACKAGE_NAME = "com.alexeychurchill.rangetextanalyzer";
    public static final String EXTRA_FILENAME = PACKAGE_NAME.concat(".EXTRA_FILENAME");
    private static final String ROOT_DIR = "/mnt/";
    private FileBrowserAdapter mAdapter;
    private List<File> mFileList;
    private File mCurrentDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_file);
        mFileList = new ArrayList<>();
        mAdapter = new FileBrowserAdapter(this, mFileList);
        ListView mLVFiles = ((ListView) findViewById(R.id.lvFiles));
        if (mLVFiles != null) {
            mLVFiles.setAdapter(mAdapter);
            mLVFiles.setOnItemClickListener(this);
        }
        initFile();
    }

    private void initFile() {
        File rootFile = new File(ROOT_DIR);
        mCurrentDir = rootFile;
        mFileList.add(null);
        mFileList.addAll(Arrays.asList(rootFile.listFiles()));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File file = mFileList.get(position);
        if (file == null) { //up
            if (mCurrentDir.getPath().contentEquals(ROOT_DIR)) {
                return;
            }
            File parentFile = mCurrentDir.getParentFile();
            if (parentFile != null) {
                mCurrentDir = parentFile;
                File[] files = parentFile.listFiles();
                if (files != null) {
                    mFileList.clear();
                    mFileList.add(null);
                    mFileList.addAll(Arrays.asList(files));
                    mAdapter.notifyDataSetChanged();
                }
            }
            return;
        }
        if (file.isDirectory()) { //dir
            mCurrentDir = file;
            File[] files = file.listFiles();
            if (files != null) {
                mFileList.clear();
                mFileList.add(null);
                mFileList.addAll(Arrays.asList(files));
                mAdapter.notifyDataSetChanged();
            }
        }
        if (file.isFile()) { //File chosen
            String filename = file.getAbsolutePath();
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_FILENAME, filename);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}
