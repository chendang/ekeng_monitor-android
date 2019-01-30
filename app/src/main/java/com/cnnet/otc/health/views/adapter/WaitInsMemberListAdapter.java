package com.cnnet.otc.health.views.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.bean.Member;
import com.cnnet.otc.health.util.StringUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

/**
 * Created by SZ512 on 2016/1/11.
 */
public class WaitInsMemberListAdapter extends BaseAdapter {

    private List<Member> lists;
    private Context ctx;
    private LayoutInflater inflater = null;
    private int checkedPosition = -1;

    /****************加载图片对象**************/
    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public WaitInsMemberListAdapter(Context ctx) {
        this.ctx = ctx;
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
    public Object getItem(int position) {
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
            view = inflater.inflate(R.layout.lv_member_wait_inspector_item, null);
            viewHolder.ivHead = (ImageView) view.findViewById(R.id.iv_head);
            viewHolder.tvName = (TextView) view.findViewById(R.id.iv_name);
            viewHolder.tvGender = (TextView) view.findViewById(R.id.tv_gender);
            viewHolder.tvPhone =(TextView)view.findViewById(R.id.tv_phone);
            viewHolder.tvBrithday = (TextView)view.findViewById(R.id.tv_brithday);
            viewHolder.items = (LinearLayout) view.findViewById(R.id.visi_layout);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Member item = lists.get(position);
        viewHolder.ivHead.setImageResource(R.mipmap.img_default_head);
        if(StringUtil.isNotEmpty(item.getmHeadPath())) {
            imageLoader.displayImage(StringUtil.getMemberPictureUrl(item.getmHeadPath()), viewHolder.ivHead, options);
        }
        viewHolder.tvName.setText(item.getName());
        viewHolder.tvGender.setText(StringUtil.getGenderStr(item.getSex(), ctx));
        viewHolder.tvPhone.setText(item.getmPhone());
        viewHolder.tvBrithday.setText(item.getmBrithday());
        if(checkedPosition == position) {
            viewHolder.items.setBackgroundColor(ctx.getResources().getColor(R.color.listview_item_down_color));
        } else {
            viewHolder.items.setBackgroundResource(R.drawable.xml_listview_item_bg);
        }
        return view;
    }

    class ViewHolder {
        LinearLayout items;
        ImageView ivHead;
        TextView tvName;
        TextView tvGender;
        TextView tvPhone;
        TextView tvBrithday;
    }

    public void setData(List<Member> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }

    public void setCheckPosition(int position) {
        checkedPosition = position;
        notifyDataSetChanged();
    }

    public void remove(int position) {
        if(lists != null && lists.size() > position) {
            lists.remove(position);
            notifyDataSetChanged();
        }
    }
}
