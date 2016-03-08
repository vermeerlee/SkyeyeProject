package com.xmh.skyeyedemo.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

import com.easemob.chat.EMVideoCallHelper;

/**
 * Created by xmh19 on 2016/2/27 027.
 */
public class CameraHelper implements Camera.PreviewCallback {

    private static final int MESSAGE_RE_RECORD = 10001;
    private static final int RE_RECORD_DELAY = 60 * 1000;

    static final int mwidth = 320;
    static final int mheight = 240;
    private final Context mContext;
    private final EMVideoCallHelper mCallHelper;
    private final SurfaceHolder mSurfaceHolder;

    private boolean startFlag;
    private Camera.CameraInfo cameraInfo;
    private int camera_count;
    private Camera mCamera;
    private Camera.Parameters mParameters;
    private byte[] yuv_frame;
    private byte[] yuv_Rotate90;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;

    private String currentVideoFileName;

    private Handler workHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_RE_RECORD:
                    if (isRecording) {
                        stopVideoRecord();
                        startVideoRecord();
                        //延迟在发送，循环走起
                        workHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                workHandler.sendEmptyMessage(MESSAGE_RE_RECORD);
                            }
                        }, RE_RECORD_DELAY);
                    }
                    break;
            }
        }
    };

    public CameraHelper(Context context, EMVideoCallHelper callHelper, SurfaceHolder holder) {
        this.mContext = context;
        this.mCallHelper = callHelper;
        this.mSurfaceHolder = holder;
    }

    void YUV420spRotate90(byte[] dst, byte[] src, int srcWidth, int srcHeight) {
        int nWidth = 0, nHeight = 0;
        int wh = 0;
        int uvHeight = 0;
        if (srcWidth != nWidth || srcHeight != nHeight) {
            nWidth = srcWidth;
            nHeight = srcHeight;
            wh = srcWidth * srcHeight;
            uvHeight = srcHeight >> 1;//uvHeight = height / 2
        }
        //旋转Y:dst[i*srcHeight+j]=src[j*srcHeight+i]
        int k = 0;
        for (int i = 0; i < srcWidth; i++) {
            int nPos = 0;
            for (int j = 0; j < srcHeight; j++) {
                dst[k] = src[nPos + i];
//                dst[i*srcHeight+j]=src[j*srcWidth+i];
                k++;
                nPos += srcWidth;
            }
        }

        for (int i = 0; i < srcWidth; i += 2) {
            int nPos = wh;
            for (int j = 0; j < uvHeight; j++) {
                dst[k] = src[nPos + i];
                dst[k + 1] = src[nPos + i + 1];
                k += 2;
                nPos += srcWidth;
            }
        }
        return;
    }

    void YUV420spRotate180(byte[] dst, byte[] src, int srcWidth, int srcHeight) {
        int nWidth = 0, nHeight = 0;
        int wh = 0;
        int uvsize = 0;
        int uvHeight = 0;
        if (srcWidth != nWidth || srcHeight != nHeight) {
            nWidth = srcWidth;
            nHeight = srcHeight;
            wh = srcWidth * srcHeight;
            uvHeight = srcHeight >> 1;//uvHeight = height / 2
        }
        uvsize = wh >> 1;
        for (int i = 0; i < wh; i++) {
            dst[wh - 1 - i] = src[i];
        }
        for (int i = 0; i < uvsize; i += 2) {
            dst[wh + uvsize - 2 - i] = src[wh + i];
            dst[wh + uvsize - 1 - i] = src[wh + i + 1];
        }
        return;
    }

    void YUV420spRotate270(byte[] dst, byte[] src, int srcWidth, int srcHeight) {
        int nWidth = 0, nHeight = 0;
        int wh = 0;
        int uvHeight = 0;
        if (srcWidth != nWidth || srcHeight != nHeight) {
            nWidth = srcWidth;
            nHeight = srcHeight;
            wh = srcWidth * srcHeight;
            uvHeight = srcHeight >> 1;//uvHeight = height / 2
        }

        int k = 0;
        for (int i = 0; i < srcWidth; i++) {
            int nPos = srcWidth - 1;
            for (int j = 0; j < srcHeight; j++) {
                dst[k] = src[nPos - i];
                k++;
                nPos += srcWidth;
            }
        }

        for (int i = 0; i < srcWidth; i += 2) {
            int nPos = wh + srcWidth - 1;
            for (int j = 0; j < uvHeight; j++) {
                dst[k] = src[nPos - i - 1];
                dst[k + 1] = src[nPos - i];
                k += 2;
                nPos += srcWidth;
            }
        }
        return;
    }

    void YUV42left2right(byte[] dst, byte[] src, int srcWidth, int srcHeight) {
        // int nWidth = 0, nHeight = 0;
        int wh = 0;
        int uvHeight = 0;
        // if(srcWidth != nWidth || srcHeight != nHeight)
        {
            // nWidth = srcWidth;
            // nHeight = srcHeight;
            wh = srcWidth * srcHeight;
            uvHeight = srcHeight >> 1;// uvHeight = height / 2
        }

        // 转换Y
        int k = 0;
        int nPos = 0;
        for (int i = 0; i < srcHeight; i++) {
            nPos += srcWidth;
            for (int j = 0; j < srcWidth; j++) {
                dst[k] = src[nPos - j - 1];
                k++;
            }

        }
        nPos = wh + srcWidth - 1;
        for (int i = 0; i < uvHeight; i++) {
            for (int j = 0; j < srcWidth; j += 2) {
                dst[k] = src[nPos - j - 1];
                dst[k + 1] = src[nPos - j];
                k += 2;

            }
            nPos += srcWidth;
        }
        return;
    }

    /**
     * 用于后置摄像头的左右翻转
     */
    void YUV42left2rightBack(byte[] dst, byte[] src, int srcWidth, int srcHeight) {
        // int nWidth = 0, nHeight = 0;
        int wh = 0;
        int uvWidth = 0;
        // if(srcWidth != nWidth || srcHeight != nHeight)
        {
            // nWidth = srcWidth;
            // nHeight = srcHeight;
            wh = srcWidth * srcHeight;
            uvWidth = srcWidth >> 1;// uvHeight = height / 2
        }

        // 转换Y
        int k = 0;
        int nPos = 0;
        for (int i = 0; i < srcWidth; i++) {
            nPos += srcHeight;
            for (int j = 0; j < srcHeight; j++) {
                dst[k] = src[nPos - j - 1];
                k++;
            }

        }
        nPos = wh + srcHeight - 1;
        for (int i = 0; i < uvWidth; i++) {
            for (int j = 0; j < srcHeight; j += 2) {
                dst[k] = src[nPos - j - 1];
                dst[k + 1] = src[nPos - j];
                k += 2;

            }
            nPos += srcHeight;
        }
        return;
    }

    public boolean isRecording(){
        return isRecording;
    }

    public String getCurrentVideoFileName(){
        if(isRecording) {
            return currentVideoFileName;
        }
        return null;
    }

    /**
     * 开启相机拍摄
     */
    public void startCapture() {
        try {
            cameraInfo = new Camera.CameraInfo();
            if (mCamera == null) {
                camera_count = Camera.getNumberOfCameras();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    for (int i = 0; i < camera_count; i++) {
                        Camera.CameraInfo info = new Camera.CameraInfo();
                        Camera.getCameraInfo(i, info);
                        // find front camera
                        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                            mCamera = Camera.open(i);
                            Camera.getCameraInfo(i, cameraInfo);
                        }
                    }
                }
                if (mCamera == null) {
                    mCamera = Camera.open();
                    Camera.getCameraInfo(0, cameraInfo);
                }

            }

            mCamera.stopPreview();
            mParameters = mCamera.getParameters();
            //region 调整本地显示方向
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                if (isScreenOriatationPortrait()) {
                    if (cameraInfo.orientation == 270)
                        mCamera.setDisplayOrientation(90);
                    if (cameraInfo.orientation == 90)
                        mCamera.setDisplayOrientation(270);
                } else {
                    if (cameraInfo.orientation == 90)
                        mCamera.setDisplayOrientation(180);
                }
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                if (isScreenOriatationPortrait()) {
                    if (cameraInfo.orientation == 90) {
                        mCamera.setDisplayOrientation(90);
                    }
                } else {

                }
            }
            //endregion

            mParameters.setPreviewSize(mwidth, mheight);
            mParameters.setPreviewFrameRate(15);
            mCamera.setParameters(mParameters);
            int mformat = mParameters.getPreviewFormat();
            int bitsperpixel = ImageFormat.getBitsPerPixel(mformat);
            LogUtil.e("xmh-camera", "pzy bitsperpixel: " + bitsperpixel);
            yuv_frame = new byte[mwidth * mheight * bitsperpixel / 8];
            yuv_Rotate90 = new byte[mwidth * mheight * bitsperpixel / 8];
//            yuv_Rotate90lr = new byte[mwidth * mheight * bitsperpixel / 8];
            mCamera.addCallbackBuffer(yuv_frame);
//             mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.setPreviewCallbackWithBuffer(this);

            EMVideoCallHelper.getInstance().setResolution(mwidth, mheight);

            mCamera.startPreview();
            LogUtil.e("xmh-record","start preview",true);
            //region 开始record
            startVideoRecord();
            isRecording = true;
            workHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    workHandler.sendEmptyMessage(MESSAGE_RE_RECORD);
                }
            }, RE_RECORD_DELAY);//每分钟保存一个文件(延迟1分钟再发，发完就另存为了)
            //endregion
        } catch (Exception e) {
            e.printStackTrace();
            releaseMediaRecorder();
            releaseCamera();
        }
    }

    /**
     * 开始录像
     */
    private void startVideoRecord() {
        try {
            //解锁camera
            mCamera.unlock();
            //初始化MediaRecorder
            mediaRecorder = new MediaRecorder();
            //region 此处顺序不能变
            mediaRecorder.setCamera(mCamera);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            //设置编码格式(API8及以上用第一行，否则用下三行)（视频大小：第一行33MB/min,下三行1.28MB/min——m1_note手机）
//            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
            //region设置录制方向
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                if (isScreenOriatationPortrait()) {
                    if (cameraInfo.orientation == 90) {
                        mediaRecorder.setOrientationHint(90);
                    }
                }
            }
            //endregion
            //设置输出文件
            this.currentVideoFileName=FileUtil.getVideoFileFullName();
            mediaRecorder.setOutputFile(currentVideoFileName);
            //设置预览
            mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
            //endregion
            //准备record
            mediaRecorder.prepare();
            mediaRecorder.start();
            LogUtil.e("xmh-record", "start record",true);
        } catch (Exception e) {
            e.printStackTrace();
            releaseMediaRecorder();
            releaseCamera();
        }
    }

    /**
     * 结束录像
     */
    private void stopVideoRecord() {
        if (mediaRecorder != null) {
            //按顺序处理mediaRecorder
            try {
                mediaRecorder.stop();
            }catch (Exception e){
                e.printStackTrace();
                releaseMediaRecorder();
                releaseCamera();
            }
            mCamera.lock();
            LogUtil.e("xmh-record", "stop record",true);
            //上传文件
            UploadUtil.uploadVideoFile(mContext, currentVideoFileName);
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        if (startFlag == true) {
            //region 根据摄像头及屏幕方向写入及传输数据
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                if (isScreenOriatationPortrait()) {
                    if (cameraInfo.orientation == 90)
                        YUV420spRotate90(yuv_Rotate90, yuv_frame, mwidth, mheight);
                    else if (cameraInfo.orientation == 270)
                        YUV420spRotate270(yuv_Rotate90, yuv_frame, mwidth, mheight);
                    mCallHelper.processPreviewData(mheight, mwidth, yuv_Rotate90);
                } else {
                    if (cameraInfo.orientation == 90) {
                        YUV420spRotate180(yuv_Rotate90, yuv_frame, mwidth, mheight);
                        YUV42left2right(yuv_frame, yuv_Rotate90, mwidth, mheight);
                        mCallHelper.processPreviewData(mheight, mwidth, yuv_frame);
                    } else {
                        YUV42left2right(yuv_Rotate90, yuv_frame, mwidth, mheight);
                        mCallHelper.processPreviewData(mheight, mwidth, yuv_Rotate90);
                    }
                }
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                if (isScreenOriatationPortrait()) {
                    if (cameraInfo.orientation == 90) {
                        YUV420spRotate90(yuv_Rotate90, yuv_frame, mwidth, mheight);
                        YUV42left2rightBack(yuv_frame, yuv_Rotate90, mwidth, mheight);
                        mCallHelper.processPreviewData(mheight, mwidth, yuv_frame);
                    }
                } else {

                }
            }
            //endregion
        }
        camera.addCallbackBuffer(yuv_frame);

    }

    public void setStartFlag(boolean startFlag) {
        this.startFlag = startFlag;
    }

    /**
     * 停止拍摄
     */
    public void stopCapture() {

        isRecording = false;
        stopVideoRecord();
        startFlag = false;
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        LogUtil.e("xmh-record","stop preview",true);
    }

    private boolean isScreenOriatationPortrait() {
        return mContext.getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    private void releaseMediaRecorder() {
        isRecording=false;
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            mCamera.lock();
        }
    }

    private void releaseCamera() {
        isRecording=false;
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}
