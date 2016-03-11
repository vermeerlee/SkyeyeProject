package com.xmh.skyeyedemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.easemob.chat.EMChatManager;
import com.xmh.skyeyedemo.R;
import com.xmh.skyeyedemo.activity.VideoListActivity;
import com.xmh.skyeyedemo.activity.CallActivity;
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(final String name: mEyeNameList){
                    ContactUtil.pullContactInfoWithUsername(mContext, name, new ContactUtil.OnGetUserInfoListener() {
                        @Override
                        public void onGetUserInfo(UserBmobBean userBmobBean) {
                            mEyeUserMap.put(name, userBmobBean);
                            notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();
        //endregion
    }

    @Override
    public EyeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.layout_eye_item, parent, false);
        return new EyeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EyeViewHolder holder, int position) {
        final String username = mEyeNameList.get(position);
        //region init layout
        UserBmobBean userBmobBean = mEyeUserMap.get(username);
        if(userBmobBean!=null){
            holder.bean=userBmobBean;
            holder.btnEyeName.setText(userBmobBean.getNickName());
        }
        holder.rlEdit.setVisibility(View.GONE);
        holder.llControl.setVisibility(View.GONE);
        //endregion
        //region init click
        holder.btnEyeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.bean==null){
                    //数据未请求下来，不理会点击事件
                    return;
                }
                if(holder.llControl.getVisibility()==View.GONE) {
                    holder.llControl.setVisibility(View.VISIBLE);
                }else {
                    holder.llControl.setVisibility(View.GONE);
                }
            }
        });
        holder.btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.rlEdit.getVisibility()==View.GONE){
                    holder.etName.setText(holder.bean.getNickName());
                    holder.etName.requestFocus();
                    holder.rlEdit.setVisibility(View.VISIBLE);
                }else {
                    holder.rlEdit.setVisibility(View.GONE);
                }
            }
        });
        holder.btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName=holder.etName.getText().toString().trim();
                if(newName.equals(holder.bean.getNickName())){
                    //未作修改则不作处理
                    holder.rlEdit.setVisibility(View.GONE);
                    holder.llControl.setVisibility(View.GONE);
                    return;
                }
                //保存数据到服务器
                holder.bean.setNickName(newName);
                holder.bean.update(mContext);
                //更新UI
                holder.btnEyeName.setText(newName);
                holder.rlEdit.setVisibility(View.GONE);
                holder.llControl.setVisibility(View.GONE);
            }
        });
        holder.btnRealTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击开启视频请求
                if (!EMChatManager.getInstance().isConnected())
                    Snackbar.make(v, R.string.network_isnot_available, Snackbar.LENGTH_SHORT).show();
                else{
                    mContext.startActivity(new Intent(mContext, CallActivity.class).putExtra(CallActivity.EXTRA_TAG_EYENAME, username));
                }
                holder.rlEdit.setVisibility(View.GONE);
                holder.llControl.setVisibility(View.GONE);
            }
        });
        holder.btnHistoryRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入历史记录界面
                Intent intent = new Intent(mContext, VideoListActivity.class);
                intent.putExtra(VideoListActivity.EXTRA_TAG_EYENAME,username);
                mContext.startActivity(intent);
                holder.rlEdit.setVisibility(View.GONE);
                holder.llControl.setVisibility(View.GONE);
            }
        });
        //endregion
    }

    @Override
    public int getItemCount() {
        return mEyeNameList.size();
    }

    class EyeViewHolder extends RecyclerView.ViewHolder{

        public UserBmobBean bean;

        public Button btnEyeName;

        public Button btnChangeName;
        public Button btnRealTime;
        public Button btnHistoryRecord;
        public LinearLayout llControl;

        public Button btnSure;
        public EditText etName;
        public RelativeLayout rlEdit;

        public EyeViewHolder(View itemView) {
            super(itemView);
            btnEyeName= (Button) itemView.findViewById(R.id.btn_eye_name);

            btnChangeName= (Button) itemView.findViewById(R.id.btn_change_name);
            btnRealTime= (Button) itemView.findViewById(R.id.btn_real_time);
            btnHistoryRecord= (Button) itemView.findViewById(R.id.btn_history);
            llControl = (LinearLayout) itemView.findViewById(R.id.ll_control);

            etName= (EditText) itemView.findViewById(R.id.et_name);
            btnSure= (Button) itemView.findViewById(R.id.btn_sure);
            rlEdit= (RelativeLayout) itemView.findViewById(R.id.rl_edit);
        }
    }

}
