package com.jingye.coffeemac.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jingye.coffeemac.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by Hades on 2016/10/31.
 */

public class DialogTitleProgress extends BaseDialog implements View.OnClickListener {

    private static final String TITLE = "title";
    private static final String CANCELSTR = "cancelstr";
    private static final String URL = "url";
    private static final String FILEPATH = "filepath";
    private static final String TAG = DialogTitleProgress.class.getSimpleName();
    private Button btnCancel;
    private Button btnOk;
    private ProgressBar pbProgress;
    private DownLoadTask downLoadTask;
    private IDownloadDialogListener listener;

    public static DialogTitleProgress newInstance(String title, String cancelStr,String url,String filePath) {
        DialogTitleProgress dialogTitle = new DialogTitleProgress();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(CANCELSTR, cancelStr);
        args.putString(URL, url);
        args.putString(FILEPATH,filePath);
        dialogTitle.setArguments(args);
        return dialogTitle;
    }

    public void setListener(IDownloadDialogListener listener){
        this.listener=listener;
    }

    public void setProgress(int progress) {
        if (pbProgress != null) {
            if (progress > 100) {
                pbProgress.setProgress(100);
            } else {
                pbProgress.setProgress(progress);
            }
        }
    }

    @Override
    protected View onBaseCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_title_progress, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText(getArguments().getString(TITLE, "假装有信息提示"));

        pbProgress = (ProgressBar) view.findViewById(R.id.pbProgress);


        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCancel.setText(getArguments().getString(CANCELSTR, "取消"));



        downLoadTask=new DownLoadTask();
        downLoadTask.execute(getArguments().getString(URL),getArguments().getString(FILEPATH));
//        downLoadTask.cancel()

        btnCancel.setOnClickListener(this);
    }

    @Override
    protected int setDialogWidth() {
        return 600;
    }

    @Override
    public void onClick(View view) {
        if (isVisible()) {
            if(downLoadTask!=null){
                downLoadTask.cancel(true);
            }
            dismiss();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(downLoadTask!=null){
            downLoadTask.cancel(true);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //设置为不可取消
        dialog.setCancelable(false);
        //设置为dialog外点击不可取消
        dialog.setCanceledOnTouchOutside(false);
        //设置点击返回键或搜索键不可取消
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                } else {
                    return false; //默认返回 false
                }
            }
        });
        return dialog;
    }

    public interface IDownloadDialogListener{
        void onDownloadSuccess();
    }

    class DownLoadTask extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... params) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(params[1]);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(isVisible()){
                dismiss();
            }

            if(listener!=null){
                listener.onDownloadSuccess();
            }

        }
    }
}
