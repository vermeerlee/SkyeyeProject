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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            start_record();
            Log.d("desk", "enable");
            Toast.makeText(this, "enable", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean stopService(Intent name) {
        try {
            stop_record();
            Toast.makeText(this, "disable", Toast.LENGTH_SHORT).show();
            Log.d("desk", "disable");
        } catch (Exception e) {
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return super.stopService(name);
    }

    // 开始录音
    public void start_record() throws Exception {
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
            sendSoundFile = new File(Environment.getExternalStorageDirectory().getCanonicalFile() + "/xmh/_rec/" + fileName + ".3gpp");
            recorder = new MediaRecorder();
            // 设置录音的声音来源(必须在输出格式之前)
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置录制的声音的输出格式（必须在设置声音编码格式之前设置）
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            // 设置声音编码的格式
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(sendSoundFile.getAbsolutePath());
            recorder.prepare();
            // 开始录音
            recorder.start();
        }
    }

    // 停止录音
    public void stop_record() {
        // 停止录音
        recorder.stop();
        // 释放资源
        recorder.release();
        recorder = null;
    }
}
