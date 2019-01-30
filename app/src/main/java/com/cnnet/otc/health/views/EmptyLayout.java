package com.cnnet.otc.health.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.foxchen.ekengmonitor.R;

public class EmptyLayout {
    private Context ctx;
    private ListView listview;
    private ViewGroup parent;
    private final LayoutParams params;
    private LinearLayout ll;
    private View mView ;
    public EmptyLayout(Context ctx,ListView listview){
        this.ctx = ctx;
        this.listview = listview;
        parent = (ViewGroup) listview.getParent();
        params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }
    /**
     *
     * @param btnListener  刷新按键监听 为空时 不显示按键
     */
    public void setEmptyLayout(View.OnClickListener btnListener){

        if(ll != null){
            parent.removeView(ll);
        }

        View	view = LayoutInflater.from(ctx).inflate(R.layout.listview_empty, null);
        ll = (LinearLayout)view.findViewById(R.id.rl);
        Button btn = (Button)view.findViewById(R.id.buttonEmpty);
        btn.setOnClickListener(btnListener);
        if(btnListener == null){
            btn.setVisibility(View.GONE);
        }else{
            btn.setVisibility(View.VISIBLE);
        }
        ll.setLayoutParams(params);
        parent.addView(ll);
        listview.setEmptyView(view);
    }


    /**
     * msg 提示的文字
     * @param btnListener  刷新按键监听 为空时 不显示按键
     */
    public void setEmptyLayout(String msg,OnClickListener btnListener){

        if(ll != null){
            parent.removeView(ll);
        }

        View view = LayoutInflater.from(ctx).inflate(R.layout.listview_empty, null);
        ll = (LinearLayout)view.findViewById(R.id.rl);
        TextView tvMessage = (TextView)view.findViewById(R.id.textViewMessage);
        tvMessage.setText(msg);
        Button btn = (Button)view.findViewById(R.id.buttonEmpty);
        btn.setOnClickListener(btnListener);
        if(btnListener == null){
            btn.setVisibility(View.GONE);
        }else{
            btn.setVisibility(View.VISIBLE);
        }
        ll.setLayoutParams(params);
        parent.addView(ll);
        listview.setEmptyView(view);
    }
    /**
     * msg 提示的文字
     * @param btnListener  刷新按键监听 为空时 不显示按键
     */
    public void setEmptyLayout(int msg_Resouce,OnClickListener btnListener){

        if(ll != null){
            parent.removeView(ll);
        }

        View	view = LayoutInflater.from(ctx).inflate(R.layout.listview_empty, null);
        ll = (LinearLayout)view.findViewById(R.id.rl);
        TextView tvMessage = (TextView)view.findViewById(R.id.textViewMessage);
        tvMessage.setText(msg_Resouce);
        Button btn = (Button)view.findViewById(R.id.buttonEmpty);
        btn.setOnClickListener(btnListener);
        if(btnListener == null){
            btn.setVisibility(View.GONE);
        }else{
            btn.setVisibility(View.VISIBLE);
        }
        ll.setLayoutParams(params);
        parent.addView(ll);
        listview.setEmptyView(view);
    }
    public void setLoadLayout(){
        if(ll != null){
            parent.removeView(ll);
        }
        View	view = LayoutInflater.from(ctx).inflate(R.layout.listview_loading, null);
        ll = (LinearLayout)view.findViewById(R.id.rl);
        ll.setLayoutParams(params);
        parent.addView(ll);
        listview.setEmptyView(view);
    }

    public void setErrorLayout(OnClickListener btnListener){
        if(ll != null){
            parent.removeView(ll);
        }
        View	view = LayoutInflater.from(ctx).inflate(R.layout.listview_error, null);
        ll = (LinearLayout)view.findViewById(R.id.rl);
        Button btn = (Button)view.findViewById(R.id.buttonError);
        btn.setOnClickListener(btnListener);
        ll.setLayoutParams(params);

        parent.addView(ll);
        listview.setEmptyView(view);

    }

    public void setErrorLayout(String text,OnClickListener btnListener){
        if(ll != null){
            parent.removeView(ll);
        }
        View	view = LayoutInflater.from(ctx).inflate(R.layout.listview_error, null);
        ll = (LinearLayout)view.findViewById(R.id.rl);
        TextView tvMessage = (TextView)view.findViewById(R.id.textViewMessage);
        tvMessage.setText(text);
        Button btn = (Button)view.findViewById(R.id.buttonError);
        btn.setOnClickListener(btnListener);
        ll.setLayoutParams(params);
        parent.addView(ll);
        listview.setEmptyView(view);
    }
    public void setErrorLayout(int text_res,OnClickListener btnListener){
        if(ll != null){
            parent.removeView(ll);
        }
        View	view = LayoutInflater.from(ctx).inflate(R.layout.listview_error, null);
        ll = (LinearLayout)view.findViewById(R.id.rl);
        TextView tvMessage = (TextView)view.findViewById(R.id.textViewMessage);
        tvMessage.setText(text_res);
        Button btn = (Button)view.findViewById(R.id.buttonError);
        btn.setOnClickListener(btnListener);
        ll.setLayoutParams(params);
        parent.addView(ll);
        listview.setEmptyView(view);
    }
    /**
     *  自由定义 显示的图标，提示文字，按键名字
     * @param msg 提示文字
     * @param buttonText 按键名字
     * @param ic_res_id 图标  0：不显示图标
     * @param btnListener 按键监听  null不显示 按键
     */
    public void setLayout(String msg,String buttonText,int ic_res_id,OnClickListener btnListener){

        if(ll != null){
            parent.removeView(ll);
        }

        View	view = LayoutInflater.from(ctx).inflate(R.layout.listview_my_layout, null);
        ll = (LinearLayout)view.findViewById(R.id.rl);
        TextView tvMessage = (TextView)view.findViewById(R.id.textViewMessage);
        ImageView ic = (ImageView)view.findViewById(R.id.ic);
        if(ic_res_id == 0){
            ic.setVisibility(view.GONE);
        }else{
            ic.setVisibility(view.VISIBLE);
            ic.setImageResource(ic_res_id);
        }

        tvMessage.setText(msg);
        Button btn = (Button)view.findViewById(R.id.buttonEmpty);
        btn.setText(buttonText);
        btn.setOnClickListener(btnListener);
        if(btnListener == null){
            btn.setVisibility(View.GONE);
        }else{
            btn.setVisibility(View.VISIBLE);
        }
        ll.setLayoutParams(params);
        parent.addView(ll);
        listview.setEmptyView(view);
    }

}
