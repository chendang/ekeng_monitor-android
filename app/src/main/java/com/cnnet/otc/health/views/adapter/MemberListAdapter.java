package com.cnnet.otc.health.views.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.bean.Member;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.util.StringUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SZ512 on 2016/1/11.
 */
public class MemberListAdapter extends BaseAdapter {

    private List<Member> lists;
    private Context ctx;
    private LayoutInflater inflater = null;
    private OnClickCheckBoxListener checkBoxListener;

    /****************加载图片对象**************/
    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public MemberListAdapter(Context ctx, List<Member> lists, OnClickCheckBoxListener checkBoxListener) {
        this.ctx = ctx;
        this.lists = lists;
        if(this.lists == null) {
            this.lists = new ArrayList<>();
        }
        this.checkBoxListener = checkBoxListener;
        inflater = LayoutInflater.from(ctx);

        options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.mipmap.img_default_head)
                .showImageForEmptyUri(R.mipmap.img_default_head)
                .showImageOnFail(R.mipmap.img_default_head)

                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(false)
                        //.displayer(new RoundedBitmapDisplayer(20))
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .build();
    }

    @Override
    public int getCount() {
        if(lists != null) {
            return lists.size();
        }
        return 0;
    }

    @Override
    public Member getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.lv_member_list_item, null);
            viewHolder.ivHead = (ImageView) view.findViewById(R.id.iv_head);
            viewHolder.tvName = (TextView) view.findViewById(R.id.iv_name);
            viewHolder.tvGender = (TextView) view.findViewById(R.id.tv_gender);
            viewHolder.tvPhone =(TextView)view.findViewById(R.id.tv_phone);
            viewHolder.tvBrithday = (TextView)view.findViewById(R.id.tv_brithday);
            viewHolder.cbChecked = (CheckBox) view.findViewById(R.id.checkBox);
            viewHolder.cbChecked.setOnClickListener(checkBoxClick);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Member item = lists.get(position);
        viewHolder.ivHead.setImageResource(R.mipmap.img_default_head);
        if(StringUtil.isNotEmpty(item.getmHeadPath())) {
            imageLoader.displayImage(StringUtil.getMemberPictureUrl(item.getmHeadPath()), viewHolder.ivHead, options);
        }
        viewHolder.cbChecked.setTag(position);
        viewHolder.cbChecked.setChecked(item.isSelect());
        viewHolder.tvName.setText(item.getName());
        viewHolder.tvGender.setText(StringUtil.getGenderStr(item.getSex(), ctx));
        viewHolder.tvPhone.setText(item.getmPhone());
        viewHolder.tvBrithday.setText(item.getmBrithday());
        return view;
    }

    /**
     * 刷新adapter的数据
     * @param members
     */
    public void setData(List<Member> members){
        if(members!=null) {
            this.lists = members;

        }
        else
        {
            this.lists = new ArrayList<Member>();

        }
    }

    public void setNewData(List<Member> new_members) {
        if(new_members.size()>0)
        {
            for(Member member:this.lists)
            {
                if(member.getUniqueKey().equals(new_members.get(0).getUniqueKey()))
                {
                    return;
                }
            }
            this.lists.addAll(new_members);
        }
    }

    /*public void refreshData(List<Member> members) {
        setNewData(members);
        this.notifyDataSetChanged();
    }*/

    public List<Member> getData(){
        return this.lists;
    }

    public void selectItem(int position){
        Member m = lists.get(position);
        m.setIsSelect(!m.isSelect());
        notifyDataSetChanged();
        if(checkBoxListener != null )this.checkBoxListener.onClickItemCheckBox(position,false);
    }

    public List<Member> getSelectAllMembers(){//获取所有被选中的文件
        List<Member> selectFiles = new ArrayList<Member>();
        for(Member m:lists){
            if(m.isSelect()){
                selectFiles.add(m);
            }
        }
        return selectFiles;
    }

    /**
     * 全选/取消全选
     * @param b
     */
    public void setSelectAll(boolean b){
        if(b){
            for(Member m : lists){
                m.setIsSelect(true);
            }
        }else{
            for(Member m : lists){
                m.setIsSelect(false);
            }
        }
        this.notifyDataSetChanged();
        if(checkBoxListener != null ) this.checkBoxListener.onClickItemCheckBox(-1, b);
    }

    private View.OnClickListener checkBoxClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            selectItem(position);
        }
    };

    public interface OnClickCheckBoxListener{
        /**
         *
         * @param position position >0 点击 该position位置的checkbox,position =-1 表示选择或取消全部item
         * @param isAll  true 选择全部
         */
        public  void onClickItemCheckBox(int position ,boolean isAll);
    }

    class ViewHolder {
        ImageView ivHead;
        TextView tvName;
        TextView tvGender;
        TextView tvPhone;
        TextView tvBrithday;
        CheckBox cbChecked;
        ImageView delete;
    }
}
