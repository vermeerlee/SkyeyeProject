package com.xmh.skyeyedemo.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;

import com.easemob.chat.EMVideoCallHelper;

/**
 * Created by xmh19 on 2016/2/27 027.
 */
public class CameraHelper {

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

    public CameraHelper(Context context,EMVideoCallHelper callHelper, SurfaceHolder holder){
        this.mContext=context;
        this.mCallHelper=callHelper;
        this.mSurfaceHolder=holder;

    }

    public static void YUV420spRotate90(byte[]  dst, byte[] src, int srcWidth, int srcHeight) {
        int nWidth = 0, nHeight = 0;
        int wh = 0;
        int uvHeight = 0;
        if(srcWidth != nWidth || srcHeight != nHeight) {
            nWidth = srcWidth;
            nHeight = srcHeight;
            wh = srcWidth * srcHeight;
            uvHeight = srcHeight >> 1;//uvHeight = height / 2
        }
        //旋转Y
        int k = 0;
        for(int i = 0; i < srcWidth; i++) {
            int nPos = 0;
            for(int j = 0; j < srcHeight; j++) {
                dst[k] = src[nPos + i];
                k++;
                nPos += srcWidth;
            }
        }

        for(int i = 0; i < srcWidth; i+=2){
            int nPos = wh;
            for(int j = 0; j < uvHeight; j++) {
                dst[k] = src[nPos + i];
                dst[k + 1] = src[nPos + i + 1];
                k += 2;
                nPos += srcWidth;
            }
        }
        return;
    }

    public static void YUV420spRotate180(byte[]  dst, byte[] src, int srcWidth, int srcHeight) {
        int nWidth = 0, nHeight = 0;
        int wh = 0;
        int uvsize = 0;
        int uvHeight = 0;
        if(srcWidth != nWidth || srcHeight != nHeight)  {
            nWidth = srcWidth;
            nHeight = srcHeight;
            wh = srcWidth * srcHeight;
            uvHeight = srcHeight >> 1;//uvHeight = height / 2
        }
        uvsize = wh>>1;
        for(int i = 0;i<wh;i++){
            dst[wh-1-i]=src[i];
        }
        for(int i = 0;i<uvsize;i+=2){
            dst[wh+uvsize-2-i]= src[wh+i];
            dst[wh+uvsize-1-i]= src[wh+i+1];
        }
        return;
    }

    public static void YUV420spRotate270(byte[]  dst, byte[] src, int srcWidth, int srcHeight) {
        int nWidth = 0, nHeight = 0;
        int wh = 0;
        int uvHeight = 0;
        if(srcWidth != nWidth || srcHeight != nHeight){
            nWidth = srcWidth;
            nHeight = srcHeight;
            wh = srcWidth * srcHeight;
            uvHeight = srcHeight >> 1;//uvHeight = height / 2
        }

        int k = 0;
        for(int i = 0; i < srcWidth; i++){
            int nPos = srcWidth - 1;
            for(int j = 0; j < srcHeight; j++)
            {
                dst[k] = src[nPos - i];
                k++;
                nPos += srcWidth;
            }
        }

        for(int i = 0; i < srcWidth; i+=2){
            int nPos = wh + srcWidth - 1;
            for(int j = 0; j < uvHeight; j++) {
                dst[k] = src[nPos - i - 1];
                dst[k + 1] = src[nPos - i];
                k += 2;
                nPos += srcWidth;
            }
        }
        return;
    }

    public static void YUV42left2right(byte[] dst, byte[] src, int srcWidth, int srcHeight) {
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
     * 开启相机拍摄
     */
    public void startCapture(){
        try {
            cameraInfo = new Camera.CameraInfo();
            if (mCamera == null) {
                // mCamera = Camera.open();
                camera_count = Camera.getNumberOfCameras();
                Log.e("xmh-camera", "camera count:" + camera_count);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    for (int i = 0; i < camera_count; i++) {
                        Camera.CameraInfo info = new Camera.CameraInfo();
                        Camera.getCameraInfo(i, info);
                        // find front camera
                        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                            Log.e("xmh-camera", "to open back camera");
                            mCamera = Camera.open(i);
                            Camera.getCameraInfo(i, cameraInfo);
                        }
                    }
                }
                if (mCamera == null) {
                    Log.e("xmh-camera", "AAAAA OPEN camera");
                    mCamera = Camera.open();
                    Camera.getCameraInfo(0, cameraInfo);
                }

            }

            mCamera.stopPreview();
            mParameters = mCamera.getParameters();
            if (isScreenOriatationPortrait()) {
                if(cameraInfo.orientation == 270 || cameraInfo.orientation == 0)
                    mCamera.setDisplayOrientation(90);
                if(cameraInfo.orientation == 90)
                    mCamera.setDisplayOrientation(270);
            }else{
                if(cameraInfo.orientation == 90)
                    mCamera.setDisplayOrientation(180);
            }

            mParameters.setPreviewSize(mwidth, mheight);
            mParameters.setPreviewFrameRate(15);
            mCamera.setParameters(mParameters);
            int mformat = mParameters.getPreviewFormat();
            int bitsperpixel = ImageFormat.getBitsPerPixel(mformat);
            Log.e("xmh-camera", "pzy bitsperpixel: " + bitsperpixel);
            yuv_frame = new byte[mwidth * mheight * bitsperpixel / 8];
            yuv_Rotate90 = new byte[mwidth * mheight * bitsperpixel / 8];
//            yuv_Rotate90lr = new byte[mwidth * mheight * bitsperpixel / 8];
            mCamera.addCallbackBuffer(yuv_frame);
//             mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    if (startFlag == true) {
                        // 根据屏幕方向写入及传输数据
                        if (isScreenOriatationPortrait()) {
                            if(cameraInfo.orientation == 90 || cameraInfo.orientation == 0)
                                CameraHelper.YUV420spRotate90(yuv_Rotate90, yuv_frame, mwidth, mheight);
                            else if(cameraInfo.orientation == 270)
                                CameraHelper.YUV420spRotate270(yuv_Rotate90, yuv_frame, mwidth, mheight);
                            mCallHelper.processPreviewData(mheight, mwidth, yuv_Rotate90);
                        } else {
                            if(cameraInfo.orientation == 90 || cameraInfo.orientation == 0)
                            {
                                CameraHelper.YUV420spRotate180(yuv_Rotate90, yuv_frame, mwidth, mheight);
                                CameraHelper.YUV42left2right(yuv_frame, yuv_Rotate90, mwidth, mheight);
                                mCallHelper.processPreviewData(mheight, mwidth, yuv_frame);
                            }
                            else
                            {
                                CameraHelper.YUV42left2right(yuv_Rotate90, yuv_frame, mwidth, mheight);
                                mCallHelper.processPreviewData(mheight, mwidth, yuv_Rotate90);
                            }

                        }
                    }
                    camera.addCallbackBuffer(yuv_frame);
                }
            });

            EMVideoCallHelper.getInstance().setResolution(mwidth, mheight);

            mCamera.startPreview();
            Log.d("xmh-camera", "camera start preview");
        } catch (Exception e) {
            e.printStackTrace();
            if(mCamera != null)
                mCamera.release();
        }
    }

    public void setStartFlag(boolean startFlag) {
        this.startFlag = startFlag;
    }

    /**
     * 停止拍摄
     */
    public void stopCapture() {

        startFlag = false;
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private boolean isScreenOriatationPortrait() {
        return mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

}
