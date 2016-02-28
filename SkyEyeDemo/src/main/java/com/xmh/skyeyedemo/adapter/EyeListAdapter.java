package com.xmh.skyeyedemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xmh.skyeyedemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mengh on 2016/2/26 026.
 */
public class EyeListAdapter extends RecyclerView.Adapter<EyeListAdapter.EyeViewHolder> {

    private Context mContext;
    private List<String> mEyeList=new ArrayList<>();
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }

    public EyeListAdapter(Context context){
        mContext=context;
    }

    public void setEyeList(List<String> list){
        mEyeList.clear();
        if(list!=null&&!list.isEmpty()){
            mEyeList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public EyeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.layout_eye_item, parent, false);
        return new EyeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EyeViewHolder holder, final int position) {
        holder.btnEyeName.setText(mEyeList.get(position));
        //TODO 添加点击事件，点击时开启视频请求
        holder.btnEyeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    listener.onClick(mEyeList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEyeList.size();
    }

    class EyeViewHolder extends RecyclerView.ViewHolder{

        public Button btnEyeName;

        public EyeViewHolder(View itemView) {
            super(itemView);
            btnEyeName= (Button) itemView.findViewById(R.id.btn_eye_name);
        }
    }

    public interface OnItemClickListener{
        public abstract void onClick(String username);
    }
}
