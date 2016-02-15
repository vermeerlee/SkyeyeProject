package com.xmh.deskcontrol;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Date;

/**
 * Created by xmh19 on 2016/2/5 005.
 */
public class MyService extends Service {

    /**
     * 当前日期
     */
    static Date currentDate;
    /**
     * 文件名
     */
    static String fileName;
    /**
     * 文件
     */
    static File sendSoundFile;
    /**
     * 录音器
     */
    static MediaRecorder recorder;
    /**是否继续线程*/
    static boolean isGoon=false;
    /**计时线程*/
    static TimeThread timeThreadhread;

    @Override
    public void onCreate() {
        super.onCreate();
        timeThreadhread =new TimeThread();
        isGoon=true;
        Log.e("xmh","create service");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("xmh","start service");
        try {
            start_record();
            timeThreadhread.start();
            Log.d("desk", "enable");
            Toast.makeText(this, "enable", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e("xmh","destory service");
        try {
            stop_record();
            isGoon=false;
            Toast.makeText(this, "disable", Toast.LENGTH_SHORT).show();
            Log.d("desk", "disable");
        } catch (Exception e) {
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        super.onDestroy();
    }

    // 开始录音
    public void start_record() throws Exception {
        Log.e("xmh","start re");
        if (!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return;
            //show_status("SD卡不存在,请插入SD卡!");
        } else {
            // 获取当前时间
            currentDate = new Date(System.currentTimeMillis());
            fileName = DateFormat.format("yyyyMMddHHmmss", currentDate).toString();
            // 创建保存录音的音频文件
            sendSoundFile = new File(Environment.getExternalStorageDirectory().getCanonicalFile() + "/xmh/_rec");
            // 如果目录不存在
            if (!sendSoundFile.exists()) {
                sendSoundFile.mkdirs();
            }
            sendSoundFile = new File(Environment.getExternalStorageDirectory().getCanonicalFile() + "/xmh/_rec/" + fileName + ".3gp");
            recorder = new MediaRecorder();
            // 设置录音的声音来源(必须在输出格式之前)
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置录制的声音的输出格式（必须在设置声音编码格式之前设置）
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(sendSoundFile.getAbsolutePath());
            // 设置声音编码的格式
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.prepare();
            // 开始录音
            recorder.start();
        }
    }

    // 停止录音
    public void stop_record() {
        Log.e("xmh","stop re");
        // 停止录音
        recorder.stop();
        // 释放资源
        recorder.release();
        recorder = null;
    }

    /**计时线程*/
    class TimeThread extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                while (isGoon) {
                    Thread.sleep(10 * 60 * 1000);
                    stop_record();
                    start_record();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
