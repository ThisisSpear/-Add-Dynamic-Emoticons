package com.example.geonchang.myemoticontestapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.geonchang.myemoticontestapp.databinding.FragmentEmoticonDownloadBinding;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmoticonDownloadFragment extends Fragment {

    private MainFragment parentFrag;
    private FragmentEmoticonDownloadBinding binding;
    private File path;
    private File outputFile;
    private ProgressDialog progressBar;
    private String url;

    private File outputFileArr[]; //파일명까지 포함한 경로

    public EmoticonDownloadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        progressBar = new ProgressDialog(getContext());
        progressBar.setMessage("다운로드중");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setIndeterminate(true);
        progressBar.setCancelable(false);
        path = MainFragment.path;
        parentFrag = ((MainFragment) EmoticonDownloadFragment.this.getParentFragment());
        url = "http://mirpsck2.cafe24.com/emoticon/emoticon";
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_emoticon_download, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        if (parentFrag.emoticonList.size() == 0) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.view_emoticon_empty, binding.layout, false);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            v.setLayoutParams(lp);
            binding.layout.addView(v);
        }
        for (int i = 0; i < parentFrag.emoticonList.size(); i++) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.view_emoticon_list, binding.layout, false);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            v.setLayoutParams(lp);

            ImageView imageView = v.findViewById(R.id.emoticon_list_image);
            TextView textView = v.findViewById(R.id.emoticon_list_text);
            Button button = v.findViewById(R.id.emoticon_list_btn);

            outputFile = new File(path, "emoticon/emoticon" + i + "/"); //파일명까지 포함함 경로의 File 객체 생성

            button.setText(outputFile.exists() ? "다운로드 완료" : "다운로드");
            v.setBackgroundColor(outputFile.exists() ? ContextCompat.getColor(getContext(), R.color.emoticon_on) : ContextCompat.getColor(getContext(), R.color.emoticon_off));

            textView.setText(parentFrag.emoticonList.get(i).name);

            Glide.with(getContext()).load(url + i + "/icon.png").into(imageView);

            binding.layout.addView(v);

            final int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String fileURL = url + finalI + "/";
                    outputFile = new File(path, "emoticon/emoticon" + finalI + "/"); // 파일명까지 포함함 경로의 File 객체 생성

                    if (!outputFile.exists()) { // 이모티콘 파일 경로가 없는 경우

                        final DownloadFilesTask downloadTask = new DownloadFilesTask(getContext());
                        String arr[] = {fileURL, outputFile + "", String.valueOf(finalI)};
                        downloadTask.execute(arr);
                        progressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                downloadTask.cancel(true);
                            }
                        });
                    } else {
                        Log.e("MainActivity2", "이모티콘 파일 있음");
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("이모티콘 다운로드");
                        builder.setMessage("이미 이모티콘이 존재합니다. 다시 다운로드 받을까요?");
                        builder.setNegativeButton("아니오",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                        builder.setPositiveButton("예",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        outputFile.delete(); //파일 삭제
                                        final DownloadFilesTask downloadTask = new DownloadFilesTask(getContext());
                                        String arr[] = {fileURL, outputFile + "", String.valueOf(finalI)};
                                        downloadTask.execute(arr);
                                        progressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                downloadTask.cancel(true);
                                            }
                                        });
                                    }
                                });
                        builder.show();
                    }
                }
            });
        }
    }

    private class DownloadFilesTask extends AsyncTask<String, String, Integer> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadFilesTask(Context context) {
            this.context = context;
        }

        //파일 다운로드를 시작하기 전에 프로그레스바를 화면에 보여줍니다.
        @Override
        protected void onPreExecute() { //2
            super.onPreExecute();

            //사용자가 다운로드 중 파워 버튼을 누르더라도 CPU가 잠들지 않도록 해서
            //다시 파워버튼 누르면 그동안 다운로드가 진행되고 있게 됩니다.
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();

            progressBar.show();
        }

        //파일 다운로드를 진행합니다.
        @Override
        protected Integer doInBackground(String... string_url) { //3

            int emoticonCount = parentFrag.emoticonList.get(Integer.parseInt(string_url[2])).count;
            outputFileArr = new File[emoticonCount * 2 + 1];
            int count;
            InputStream input = null;
            OutputStream output = null;
            URLConnection connection;
            String[] arr = {"gif", "png"};
            int cnt = 0;
            boolean isEvenOdd = true;

            try {
                for (String extension : arr) {

                    for (int i = 0; i < emoticonCount; i++) {


                        if (isEvenOdd) {
                            cnt++;
                            float per = ((float) cnt / emoticonCount) * 100;
                            String str = "이모티콘 다운로드 중 " + cnt + "개 / " + emoticonCount + "개 (" + (int) per + "%)";
                            publishProgress("" + ((cnt * 100) / emoticonCount), str);
                            isEvenOdd = !isEvenOdd;
                        } else {
                            isEvenOdd = !isEvenOdd;
                        }

                        URL url = new URL(string_url[0] + extension + "/emoticon_" + i + "." + extension);
                        connection = url.openConnection();
                        connection.connect();

                        //파일 크기를 가져옴
                        //URL 주소로부터 파일다운로드하기 위한 input stream
                        input = new BufferedInputStream(url.openStream(), 8192);

                        File tempFile = new File(string_url[1], extension);
                        if (!tempFile.exists()) {
                            // 디렉토리가 존재하지 않으면 디렉토리 생성
                            tempFile.mkdirs();
                        }
                        Log.e("MainActivity2", "tempFile : " + tempFile);
                        //outputFile = new File(path, "emoticon/Alight1.gif"); //파일명까지 포함함 경로의 File 객체 생성
                        if (extension.equals("gif")) {
                            outputFileArr[i] = new File(string_url[1], extension + "/emoticon_" + i + "." + extension);
                            // SD카드에 저장하기 위한 Output stream
                            output = new FileOutputStream(outputFileArr[i]);
                        } else {
                            outputFileArr[i + emoticonCount] = new File(string_url[1], extension + "/emoticon_" + i + "." + extension);
                            // SD카드에 저장하기 위한 Output stream
                            output = new FileOutputStream(outputFileArr[i + emoticonCount]);
                        }

                        byte data[] = new byte[1024];
                        while ((count = input.read(data)) != -1) {
                            //파일에 데이터를 기록합니다.
                            output.write(data, 0, count);
                        }
                        // Flush output
                        output.flush();

                        // Close streams
                        output.close();
                        input.close();
                    }
                }

                URL url = new URL(string_url[0] + "/icon.png");
                connection = url.openConnection();
                connection.connect();
                input = new BufferedInputStream(url.openStream(), 8192);

                outputFileArr[emoticonCount * 2] = new File(string_url[1], "icon.png");
                Log.e("MainActivity2", "outputFileArr[" + (emoticonCount * 2) + "] : " + outputFileArr[emoticonCount * 2]);
                // SD카드에 저장하기 위한 Output stream
                output = new FileOutputStream(outputFileArr[emoticonCount * 2]);

                byte data[] = new byte[1024];
                while ((count = input.read(data)) != -1) {

                    //파일에 데이터를 기록합니다.
                    output.write(data, 0, count);
                }
                // Flush output
                output.flush();

                // Close streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                    Log.e("Error: ", ignored.getMessage());
                }
                mWakeLock.release();
            }
            return 1;
        }

        //다운로드 중 프로그레스바 업데이트
        @Override
        protected void onProgressUpdate(String... progress) { //4
            super.onProgressUpdate(progress);

            // if we get here, length is known, now set indeterminate to false
            progressBar.setIndeterminate(false);
            progressBar.setMax(100);
            progressBar.setProgress(Integer.parseInt(progress[0]));
            progressBar.setMessage(progress[1]);
        }

        //파일 다운로드 완료 후
        @Override
        protected void onPostExecute(Integer size) { //5
            super.onPostExecute(size);
            progressBar.dismiss();

            if (size > 0) {
                for (int i = 0; i < outputFileArr.length; i++) {
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(Uri.fromFile(outputFileArr[i]));
                    getContext().sendBroadcast(mediaScanIntent);
                }
                parentFrag.emoticonInit(parentFrag.view);
            } else
                Toast.makeText(getContext(), "다운로드 에러", Toast.LENGTH_LONG).show();
        }
    }
}
