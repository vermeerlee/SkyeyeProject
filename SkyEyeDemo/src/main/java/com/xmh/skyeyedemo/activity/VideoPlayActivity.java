/*
 * Copyright (C) 2013 yixia.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xmh.skyeyedemo.activity;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.DownloadListener;
import com.xmh.skyeyedemo.R;
import com.xmh.skyeyedemo.base.BaseActivity;
import com.xmh.skyeyedemo.bean.FileBmobBean;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VideoPlayActivity extends BaseActivity {

    public static final String EXTRA_TAG_VIDEO_URL = "VideoUrl";

    @Bind(R.id.v_background)View v;
    @Bind(R.id.surface_view)
    VideoView mVideoView;
    @Bind(R.id.cl_snackbar)CoordinatorLayout snackbarContainer;

    private FileBmobBean fileBmobBean;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_video_play);
        ButterKnife.bind(this);
        Vitamio.isInitialized(this);


        intData();
        requestData();
    }

    private void intData() {
        fileBmobBean = (FileBmobBean) getIntent().getSerializableExtra(EXTRA_TAG_VIDEO_URL);
    }

    private void requestData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        //下载文件
        BmobProFile.getInstance(this).download(fileBmobBean.getFilenameForDownload(), new DownloadListener() {
            @Override
            public void onSuccess(final String path) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        playfunction(path);
                    }
                });
            }

            @Override
            public void onProgress(String path, final int percent) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.setProgress(percent);
                        progressDialog.setMessage(VideoPlayActivity.this.getResources().getString(R.string.decoding));
                    }
                });
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        //弹出提示“点击重试”，snackbar滑动消失
                        Snackbar.make(snackbarContainer,R.string.load_fail,Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestData();
                            }
                        });
                    }
                });
            }
        });
    }

    void playfunction(String path) {
        mVideoView.setVideoPath(path);
        MediaController mediaController = new MediaController(this);
        mVideoView.setMediaController(mediaController);
        mVideoView.requestFocus();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setPlaybackSpeed(1.0f);
            }
        });
    }

    @OnClick(R.id.v_background)
    void onClick(){
        mVideoView.onKeyDown(-1,null);
    }
}
