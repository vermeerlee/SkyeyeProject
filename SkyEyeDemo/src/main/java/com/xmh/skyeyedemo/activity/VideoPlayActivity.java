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


import android.os.Bundle;
import android.widget.Toast;

import com.xmh.skyeyedemo.R;
import com.xmh.skyeyedemo.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VideoPlayActivity extends BaseActivity {

	public static final String EXTRA_TAG_VIDEO_URL="VideoUrl";

	@Bind(R.id.surface_view)VideoView mVideoView;

	private String path;
	private String fileUrl;


	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_video_play);
		ButterKnife.bind(this);
		Vitamio.isInitialized(this);

		initView();
		initData();

		playfunction();

	}
	private void initView() {
	}

	private void initData() {
		fileUrl = getIntent().getStringExtra(EXTRA_TAG_VIDEO_URL);
		//TODO 下载文件
	}




	void playfunction(){
		mVideoView = (VideoView) findViewById(R.id.surface_view);

//		path="http://file.bmob.cn/M03/D8/CC/oYYBAFbg2_SAOOInAAtpxT9bS00651.mp4";
		path="http://dlqncdn.miaopai.com/stream/MVaux41A4lkuWloBbGUGaQ__.mp4";
      if (path == "") {
			// Tell the user to provide a media file URL/path.
			Toast.makeText(VideoPlayActivity.this, "Please edit VideoViewDemo Activity, and set path" + " variable to your media file URL/path", Toast.LENGTH_LONG).show();
			return;
		} else {
			/*
			 * Alternatively,for streaming media you can use
			 * mVideoView.setVideoURI(Uri.parse(URLstring));
			 */
			mVideoView.setVideoPath(path);
//		  mVideoView.setVideoURI(Uri.parse(path));
		  MediaController mediaController = new MediaController(this);
		  mVideoView.setMediaController(mediaController);
//		  mediaController.show();
//		  mediaController.hide();
			mVideoView.requestFocus();

			mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mediaPlayer) {
					// optional need Vitamio 4.0
					mediaPlayer.setPlaybackSpeed(1.0f);
				}
			});
		}
	}
}
