package com.xmh.skyeyedemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xmh.skyeyedemo.R;
import com.xmh.skyeyedemo.bean.FileBmobBean;
import com.xmh.skyeyedemo.utils.FileUtil;

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
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.bean=mFileList.get(position);
        String str = holder.bean.getVideoFile().getFilename();
        str= FileUtil.parseDateFromFilename(str);
        holder.btnVideo.setText(str);
        holder.btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 播放
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFileList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder{

        public FileBmobBean bean;
        public Button btnVideo;

        public VideoViewHolder(View itemView) {
            super(itemView);
            btnVideo= (Button) itemView.findViewById(R.id.btn_video);
        }
    }
}
