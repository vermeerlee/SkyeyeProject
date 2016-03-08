package com.xmh.skyeyedemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xmh.skyeyedemo.R;
import com.xmh.skyeyedemo.bean.UserBmobBean;
import com.xmh.skyeyedemo.utils.ContactUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mengh on 2016/2/26 026.
 */
public class EyeListAdapter extends RecyclerView.Adapter<EyeListAdapter.EyeViewHolder> {

    private Context mContext;
    private List<String> mEyeNameList =new ArrayList<>();
    private Map<String,UserBmobBean> mEyeUserMap=new HashMap<>();
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }

    public EyeListAdapter(Context context){
        mContext=context;
    }

    public void setEyeList(List<String> list){
        mEyeNameList.clear();
        if(list!=null&&!list.isEmpty()){
            mEyeNameList.addAll(list);
        }
        notifyDataSetChanged();
        //region 根据用户名获取用户完整信息
        for(final String name: mEyeNameList){
            ContactUtil.pullContactInfoWithUsername(mContext, name, new ContactUtil.OnGetUserInfoListener() {
                @Override
                public void onGetUserInfo(UserBmobBean userBmobBean) {
                    mEyeUserMap.put(name,userBmobBean);
                    notifyDataSetChanged();
                }
            });
        }
        //endregion
    }

    @Override
    public EyeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.layout_eye_item, parent, false);
        return new EyeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EyeViewHolder holder, int position) {
        final String username = mEyeNameList.get(position);
        if(mEyeUserMap.get(username)!=null){
            holder.btnEyeName.setText(mEyeUserMap.get(username).getNickName());
        }
        //点击开启视频请求
        holder.btnEyeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    listener.onClick(username);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEyeNameList.size();
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
