package com.xmh.skyeyedemo.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.DownloadListener;
import com.xmh.skyeyedemo.R;
import com.xmh.skyeyedemo.activity.VideoPlayActivity;
import com.xmh.skyeyedemo.bean.FileBmobBean;
import com.xmh.skyeyedemo.utils.FileUtil;
import com.xmh.skyeyedemo.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mengh on 2016/3/9 009.
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder>{

    private Context mContext;
    private List<FileBmobBean> mFileList=new ArrayList<>();

    public VideoListAdapter(Context context){
        mContext=context;
    }

    public void setVideoList(List<FileBmobBean> list){
        mFileList.clear();
        if(list!=null&&!list.isEmpty()){
            mFileList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_video_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideoViewHolder holder, int position) {
        holder.bean=mFileList.get(position);
        String str = holder.bean.getVideoFile().getFilename();
        str= FileUtil.parseDateFromFilename(str);
        holder.btnVideo.setText(str);
        holder.llControl.setVisibility(View.GONE);
        //region set listener
        holder.btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.llControl.getVisibility()==View.VISIBLE){
                    holder.llControl.setVisibility(View.GONE);
                }else {
                    holder.llControl.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放
                Intent intent = new Intent(mContext, VideoPlayActivity.class);
                intent.putExtra(VideoPlayActivity.EXTRA_TAG_VIDEO_URL,holder.bean);
                mContext.startActivity(intent);
                holder.llControl.setVisibility(View.GONE);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobProFile.getInstance(mContext).deleteFile(holder.bean.getFilenameForDownload(), null);
                mFileList.remove(holder.bean);
                notifyDataSetChanged();
            }
        });
        holder.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(mContext);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage(mContext.getString(R.string.downloading));
                progressDialog.show();
                BmobProFile.getInstance(mContext).download(holder.bean.getFilenameForDownload(), new DownloadListener() {
                    @Override
                    public void onSuccess(final String path) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final String dstPath=FileUtil.getDownloadPath()+holder.bean.getVideoFile().getFilename();
                                FileUtil.copyFile(path,dstPath);
                                holder.btnDownload.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        File file = new File(dstPath);
                                        String folder = file.getParent();
                                        LogUtil.e("xmh-download", folder);
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_VIEW);
                                        intent.setDataAndType(Uri.fromFile(new File(folder)), "*/*");
                                        mContext.startActivity(intent);
                                    }
                                });
                            }
                        }).start();

                    }

                    @Override
                    public void onProgress(String path, int persent) {
                        progressDialog.setProgress(persent);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        progressDialog.dismiss();
                    }
                });
            }
        });
        //endregion
    }

    @Override
    public int getItemCount() {
        return mFileList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder{

        public FileBmobBean bean;

        public Button btnVideo;

        public LinearLayout llControl;
        public Button btnPlay;
        public Button btnDownload;
        public Button btnDelete;

        public VideoViewHolder(View itemView) {
            super(itemView);
            btnVideo= (Button) itemView.findViewById(R.id.btn_video);

            llControl= (LinearLayout) itemView.findViewById(R.id.ll_control);
            btnPlay= (Button) itemView.findViewById(R.id.btn_play);
            btnDownload= (Button) itemView.findViewById(R.id.btn_download);
            btnDelete= (Button) itemView.findViewById(R.id.btn_delete);
        }
    }
}
