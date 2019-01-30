package com.goldtel.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.goldtel.R;
import com.goldtel.dialog.GoldtelAlertDialog;
import com.goldtel.dialog.GoldtelProgressDialog;

public class MainActivity extends Activity {
	private Context context = MainActivity.this;
	private AlertDialog ad;
	private android.content.DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					System.out.println("POSUTUVE");
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					System.out.println("NEGATIVE");
					break;
				case DialogInterface.BUTTON_NEUTRAL:
					System.out.println("NEUTRAL");
					break;
				default:
					break;
			}
		}
	};
	private Handler handler = new Handler() {
		public void handleMessage(final Message msg) {
			switch (msg.what) {
				case 1001:
					new Thread() {
						public void run() {
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							ad.dismiss();
							startActivity(new Intent(context, SeActivity.class));
						};
					}.start();
					break;
				default:
					break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initControls();
	}

	public Builder createAlertDlgBuilder() {
		RadioButton rdb = (RadioButton) this.findViewById(R.id.radio0);
		if (rdb.isChecked())
			return new AlertDialog.Builder(this);
		else {
			rdb = (RadioButton) this.findViewById(R.id.radio1);
			if (rdb.isChecked())
				return new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyTheme));
			else
				return new GoldtelAlertDialog.Builder(context);
		}
	}
	
	public AlertDialog createProgressDialog() {
		RadioButton rdb = (RadioButton) this.findViewById(R.id.radio0);
		if (rdb.isChecked())
			return new ProgressDialog(this);
		else {
			rdb = (RadioButton) this.findViewById(R.id.radio1);
			if (rdb.isChecked())
				return new ProgressDialog(this);
			else
				return new GoldtelProgressDialog(this);
		}
	}

	OnClickListener ls = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Builder bd = createAlertDlgBuilder();
			bd.setCancelable(true);
			String s = "abc\n12345\n中文ABC\nabc123\n中文中文跑马灯拉克发跑马灯拉克发跑马灯拉克发跑马灯拉克发跑马灯拉克发跑马灯拉克发跑马灯拉克发跑马灯拉克发跑马灯拉克发离开abc123\nabc\n12345\n中文ABC\nabc123\n中文abc123";
			final String svs[] = s.split("\n");
			switch (v.getId()) {
				case R.id.button11:
					AlertDialog pd = createProgressDialog();
//					pd.setTitle("进度对话框");
//					pd.setButton("是", new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//						}
//					});
//					pd.setProgressStyle(GoldtelProgressDialog.STYLE_HORIZONTAL);
//					pd.setProgressStyle(GoldtelProgressDialog.STYLE_SPINNER);
//					pd.setMessage("请等待请等待...");
					pd.setMessage(null);
					pd.show();
					pd.setOnCancelListener(new OnCancelListener(){
						@Override
						public void onCancel(DialogInterface dialog) {
						}
					});
//					GoldtelProgressDialog.show(context, null, "正在-请稍等...", false, true);
					break;
				case R.id.button10:
					View customFrame = LayoutInflater.from(context).inflate(R.layout.goldtel_alertex_dialog_custom_frame_layout, null);
					((AnimationDrawable)customFrame.findViewById(R.id.customFrameLoadImg).getBackground()).start();
					((TextView)customFrame.findViewById(R.id.customFrameMsg)).setText("正在Xxxxxx，请稍等...");
					bd.setView(customFrame);
					// bd.setCancelable(false);
					// bd.setTitle("提示");
					// bd.setPositiveButton("确认", null);
					ad = bd.create();
//					ad.setCanceledOnTouchOutside(false);
					ad.show();
					handler.obtainMessage(1001, ad).sendToTarget();
					break;
				case R.id.button1:
					bd.setTitle("标题消息自定义对话框自定义标题消息自定义对话框自定义标题消息自定义对话框自定义标题消息自定义对话框自定义");
					bd.setMessage("Huz alert");
					bd.setIcon(R.drawable.goldtel_alertex_dlg_title_icon_def);
					bd.setPositiveButton("是", listener);
					bd.setNegativeButton("否", listener);
					bd.setNeutralButton("取消取消取消取消UUUUUU我", listener);
					// TextView textTitle = new TextView(context);
					// textTitle.setText("自定义Title");
					// textTitle.setTextSize(22);
					// bd.setCustomTitle(textTitle);
					AlertDialog adl2 = bd.show();
//					adl2.setCanceledOnTouchOutside(true);
					break;
				case R.id.button2:
					bd.setTitle("自定义对话框标题输入绿卡时间的flak螺丝钉解放拉贾老师看见flak的减肥啦");
					final EditText edittext_Msg = new EditText(MainActivity.this);
					edittext_Msg.setGravity(Gravity.TOP);
					edittext_Msg.setLines(8);
					edittext_Msg.setTextColor(getResources().getColor(R.color.alertex_dlg_edit_text_color));
					edittext_Msg.setBackgroundDrawable(getResources().getDrawable(R.drawable.goldtel_alertex_dlg_textinput_drawable));
					bd.setView(edittext_Msg);
					bd.setPositiveButton("确定确定确定确定", null);
					bd.setNeutralButton("否否否否否否", null);
					bd.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							edittext_Msg.setFocusable(false);
						}
					});
					AlertDialog gad = bd.show();
					break;
				case R.id.button3:
					bd.setTitle("自定义对话框");
					bd.setIcon(R.drawable.ic_dialog_info);
					bd.setItems(svs, null);
					bd.setPositiveButton("是", null);
					bd.setNeutralButton("否", null);
					bd.setNegativeButton("取消", null);
					bd.show();
					break;
				case R.id.button4:
					bd.setTitle("自定义对话框");
					bd.setSingleChoiceItems(svs, 2, null);
					bd.setPositiveButton("是", null);
					bd.setNegativeButton("取消", null);
					bd.show();
					break;
				case R.id.button5:
					bd.setTitle("只有标题_自定义对话框");
					bd.setNegativeButton("取消", null);
					bd.show();
					break;
				case R.id.button6:
					bd.setMessage("只有消息只有消息只有消息只有消息只有消息只有消息只有消息");
					// bd.setPositiveButton("是", null);
					// bd.setNeutralButton("否", null);
					// bd.setNegativeButton("取消", null);
					bd.show();
					break;
				case R.id.button7:
					bd.setIcon(R.drawable.ic_dialog_alert);
					bd.setTitle("自定义对话框");
					boolean[] cks = new boolean[svs.length];
					cks[0] = true;
					bd.setMultiChoiceItems(svs, cks, new OnMultiChoiceClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which, boolean isChecked) {
							if (isChecked) {
								Toast.makeText(context, "" + svs[which] + "被选中", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(context, "" + svs[which] + "被取消", Toast.LENGTH_SHORT).show();
							}
						}
					});
					bd.setPositiveButton("是", null);
					bd.setNeutralButton("取消", null);
					bd.show();
					break;
				case R.id.button8:
					bd.setTitle("自定纯消息框");
					bd.setIcon(R.drawable.ic_dialog_alert);
					bd.setMessage("自定");
					bd.show();
					break;
				case R.id.button9:
					bd.setTitle("纯列表框对话框");
					bd.setItems(svs, null);
					bd.setIcon(R.drawable.ic_dialog_alert);
					bd.create().show();
					break;
				default:
					break;
			}
		}
	};

	private void initControls() {
		Button btn;
		btn = (Button) this.findViewById(R.id.button1);
		btn.setOnClickListener(ls);
		btn = (Button) this.findViewById(R.id.button2);
		btn.setOnClickListener(ls);
		btn = (Button) this.findViewById(R.id.button3);
		btn.setOnClickListener(ls);
		btn = (Button) this.findViewById(R.id.button4);
		btn.setOnClickListener(ls);
		btn = (Button) this.findViewById(R.id.button5);
		btn.setOnClickListener(ls);
		btn = (Button) this.findViewById(R.id.button6);
		btn.setOnClickListener(ls);
		btn = (Button) this.findViewById(R.id.button7);
		btn.setOnClickListener(ls);
		btn = (Button) this.findViewById(R.id.button8);
		btn.setOnClickListener(ls);
		btn = (Button) this.findViewById(R.id.button9);
		btn.setOnClickListener(ls);
		btn = (Button) this.findViewById(R.id.button10);
		btn.setOnClickListener(ls);
		btn = (Button) this.findViewById(R.id.button11);
		btn.setOnClickListener(ls);
	}
}
