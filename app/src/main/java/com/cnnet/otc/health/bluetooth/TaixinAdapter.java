package com.cnnet.otc.health.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.bean.FetalHeart;

public class TaixinAdapter extends ArrayAdapter<TaixinTextandImage> {

	 public HashMap<Integer, Boolean> isSelected=new HashMap<Integer, Boolean>();;
	private ListView listView;
	private List<TaixinTextandImage> ImageAndTexts;
	private List<TaixinTextandImage> DeletcImageAndTexts = new ArrayList<TaixinTextandImage>();
	public List<String> Massagetests = new ArrayList<String>();
	public String name =null;
	private List<FetalHeart> fetalHeartsTextList = new ArrayList<FetalHeart>();

	public TaixinAdapter(Activity activity, List<TaixinTextandImage> imageAndTexts,
			ListView listView,String name) {
		super(activity, 0, imageAndTexts);
		this.listView = listView;
		this.name = name;
		this.ImageAndTexts = imageAndTexts;
		init();                                                                                                                                                                                   
		System.out.println("�鿴Adapter��Username���" + name);
	}

	
	
	
	public HashMap<Integer, Boolean> getIsSelected()
	 {
	  return isSelected;
	 }
	 //��ʼ����������checkbox��Ϊδѡ��״̬
	 private void init()
	 {
//	  isSelected = new HashMap<Integer, Boolean>();
	  for (int i = 0; i < ImageAndTexts.size(); i++)
	  {
	   isSelected.put(i, false);
	  }
	 }
	
	
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public TaixinTextandImage getItem(int position) {
		return ImageAndTexts.get(position);
	}

	@Override
	public int getCount() {
		return ImageAndTexts.size();
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
			LayoutInflater inflater = activity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.taixin_list, null);
			TextView xuhao = (TextView) convertView.findViewById(R.id.listxuhao);
			TextView FHRDate = (TextView) convertView.findViewById(R.id.listspo2);
			TextView time = (TextView) convertView.findViewById(R.id.time);
			CheckBox checkbox = (CheckBox) convertView
					.findViewById(R.id.checkbox);

			TaixinTextandImage imageAndText = getItem(position);

		
	checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						String Massagetest ;
						TaixinTextandImage DelectimageAndText = null;
						FetalHeart  fetalHeart =null;
						Massagetest = "\n\n"+name+" 在   " + getItem(position).getTime().substring(0, 19)+" 的胎心测试结果为:"
								+ "  FHR:" + getItem(position).getTaixin()+" bpm "+ " \n ";

						fetalHeart = new FetalHeart(getItem(position).getTime().substring(0, 19),(double)getItem(position).getTaixin());
						
						DelectimageAndText = getItem(position);
						if (isChecked) {
							  	isSelected.put(position, true);	
								DeletcImageAndTexts.add(DelectimageAndText);
								fetalHeartsTextList.add(fetalHeart);
								Massagetests.add(Massagetest);
						} else {
							isSelected.put(position, false);
							fetalHeartsTextList.remove(fetalHeart);
							DeletcImageAndTexts.remove(DelectimageAndText);
							Massagetests.remove(Massagetest);
						}				
					}

				});
		checkbox.setChecked(isSelected.get(position));  
		System.out.println("�鿴Adapter��List_Text���" + imageAndText.toString());
		time.setText(imageAndText.getTime().substring(0, 19));
		FHRDate.setText(imageAndText.getTaixin() + "bpm");
		xuhao.setText(position + 1 + "");
		return convertView;
	}

	public void addNewsItem(List<TaixinTextandImage> dataArray) {
		for (TaixinTextandImage _item : dataArray) {

			ImageAndTexts.add(0,_item);
			init();  
		}
	}

	public List<String> getMessage() {

		return Massagetests;
	}

	public List<TaixinTextandImage> getImageText() {
		System.out.println("adapterɾ������" + DeletcImageAndTexts.size());
		return DeletcImageAndTexts;
	}
	
	public List<FetalHeart> gettaixinTextandImages(){
		return fetalHeartsTextList;
		
	}
	

	static class ViewHolder {
		TextView xuhao, spo2, pr, pi, time;
		CheckBox checkbox;
	}


}
