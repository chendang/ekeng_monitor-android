package com.kingnewblesdk;

/**
 * Created by hdr on 15/12/21.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foxchen.ekengmonitor.R;
import com.kitnew.ble.QNData;
import com.kitnew.ble.QNItemData;
import com.kitnew.ble.utils.QNLog;

public class IndicatorAdapter extends RecyclerView.Adapter<IndicatorAdapter.IndicatorViewHolder> {

    QNData qnData;
    String  sex;
    String  height;
    LayoutInflater inflater;

    public IndicatorAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setQnData(QNData qnData) {
        this.qnData = qnData;
        notifyDataSetChanged();
    }
    public void setHeight(String height) {
        this.height= height;
    }
    public void setSex(String sex) {
        this.sex= sex;
    }
    @Override
    public IndicatorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.indicator_list_item, parent, false);
        return new IndicatorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(IndicatorViewHolder holder, int position) {
        holder.init(qnData.getAll().get(position));
    }

    @Override
    public int getItemCount() {
        return qnData == null ? 0 : qnData.size();
    }

    class IndicatorViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv;
        TextView valueTv;
        TextView remarkTv;
        public IndicatorViewHolder(View itemView) {
            super(itemView);
            nameTv = (TextView) itemView.findViewById(R.id.nameTv);
            valueTv = (TextView) itemView.findViewById(R.id.valueTv);
            remarkTv = (TextView) itemView.findViewById(R.id.remarkTv);
        }

        void init(QNItemData itemData) {
            nameTv.setText(itemData.name);
            QNLog.log("he", itemData.name + "   " +itemData.type+"   " +itemData.valueStr+"   "+ itemData.value);
            if (itemData.type == QNData.TYPE_BODY_SHAPE) {
                valueTv.setText(itemData.valueStr);
            } else {
                switch (itemData.type) {
                    case 2:
                        valueTv.setText(itemData.value + "Kg");
                        remarkTv.setText("" + "");
                        break;
                    case 3:
                        valueTv.setText(itemData.value + "");
                        if(itemData.value<18.5){
                            remarkTv.setText("偏低" + "");
                        }else if(itemData.value>25){
                            remarkTv.setText("肥胖" + "");
                        }else{
                            remarkTv.setText("正常" + "");
                        }
                        break;
                    case 9:
                        valueTv.setText(itemData.value + "");
                        if("1".equals(sex)){
                            if(itemData.value<49){
                                remarkTv.setText("偏低" + "");
                            }else if(itemData.value>=59){
                                remarkTv.setText("偏高" + "");
                            }else{
                                remarkTv.setText("正常" + "");
                            }
                        }else {
                            if(itemData.value<40){
                                remarkTv.setText("偏低" + "");
                            }else if(itemData.value>=50){
                                remarkTv.setText("偏高" + "");
                            }else{
                                remarkTv.setText("正常" + "");
                            }
                        }
                        break;
                    case 10:
                        valueTv.setText(itemData.value + "Kg");
                        remarkTv.setText("" + "");
                        break;
                    case 4:
                        if("1".equals(sex))
                             valueTv.setText(itemData.value+11 + "%");
                        else
                            valueTv.setText(itemData.value + "%");
                        break;
                    case 7:
                        valueTv.setText(itemData.value + "%");
                        remarkTv.setText("" + "");
                        break;
                    case 5:
                        valueTv.setText(itemData.value + "%");
                        if("1".equals(sex)){
                            if(itemData.value<8.6){
                                remarkTv.setText("偏低" + "");
                            }else if(itemData.value<=20.7 && itemData.value>=16.7){
                                remarkTv.setText("偏高" + "");
                            }else if(itemData.value>20.7 ){
                                remarkTv.setText("严重偏高" + "");
                            } else{
                                remarkTv.setText("正常" + "");
                            }
                        }else {
                            if(itemData.value<18.5){
                                remarkTv.setText("偏低" + "");
                            }else if(itemData.value<=30.8 && itemData.value>=26.7){
                                remarkTv.setText("偏高" + "");
                            }else if(itemData.value>30.8 ){
                                remarkTv.setText("严重偏高" + "");
                            } else{
                                remarkTv.setText("正常" + "");
                            }
                        }
                        break;
                    case 6:
                        valueTv.setText(itemData.value + "");
                        if(itemData.value<=14 && itemData.value>9){
                            remarkTv.setText("偏高" + "");
                        }else if(itemData.value>14 ){
                            remarkTv.setText("严重超标" + "");
                        } else{
                            remarkTv.setText("正常" + "");
                        }
                        break;
                    case 11:
                        valueTv.setText(itemData.value + "%");
                        if("1".equals(sex)){
                            if(itemData.value<16){
                                remarkTv.setText("偏低" + "");
                            }else if(itemData.value>18){
                                remarkTv.setText("偏高" + "");
                            }else{
                                remarkTv.setText("正常" + "");
                            }
                        }else {
                            if(itemData.value<14){
                                remarkTv.setText("偏低" + "");
                            }else if(itemData.value>16){
                                remarkTv.setText("偏高" + "");
                            }else{
                                remarkTv.setText("正常" + "");
                            }
                        }

                        break;
                    case 12:
                        valueTv.setText(itemData.value + "kcal");
                        remarkTv.setText("" + "");
                        break;
                    default:
                        valueTv.setText(itemData.value + "");
                        remarkTv.setText("" + "");
                        break;
                }
            }

        }
    }

}


