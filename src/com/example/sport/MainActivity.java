package com.example.sport;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button btn_count, btn_Time, btn_reset;
	private TextView tv, tv_count, tv_history;
	private int i = 0, count = 0;
	private String time;
	private MyCount mc;
	private NumberPicker picker;
	private long m;
	private Animation anim_btn_downIn, anim_btn_downOut, anim_btn_upIn, anim_btn_upOut;
	private SQLiteDatabase dbWriter;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				btn_count.setText(count + "个");
				tv_count.setText(count + "个");
			}
			if (msg.what == 2) {
				btn_count.setText("0个");
			}
		};
	};
	Db db = new Db(MainActivity.this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initView();
		btn_Time.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				picker = new NumberPicker(MainActivity.this);
				picker.setMaxValue(60);
				picker.setMinValue(0);
				picker.setOnValueChangedListener(onSecondChangedListener);
				AlertDialog mAlertDialog = new AlertDialog.Builder(MainActivity.this).setView(picker)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						anim_btn_upOut = AnimationUtils.loadAnimation(MainActivity.this,

						R.anim.btn_up_out);
						btn_Time.startAnimation(anim_btn_upOut);
						btn_Time.setVisibility(8);
					}
				}).create();
				mAlertDialog.show();
				i = 1;
				Message msg = handler.obtainMessage();
				count = 0;
				msg.what = 2;
				handler.sendMessage(msg);
			}
		});
		tv_history.setClickable(true);
		tv_history.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, RecordActivity.class);
				startActivity(intent);
			}
		});
		btn_count.setOnClickListener(new ButtonClickListner());
		btn_reset.setOnClickListener(new ResetClickListener());
	}

	private void initView() {
		tv = (TextView) findViewById(R.id.tv);
		btn_count = (Button) findViewById(R.id.btn_count);
		btn_Time = (Button) findViewById(R.id.btn_Time);
		btn_reset = (Button) findViewById(R.id.btn_reset);
		tv_count = (TextView) findViewById(R.id.tv_count);
		tv_history = (TextView) findViewById(R.id.tv_history);

		btn_reset.setVisibility(8);
		tv_count.setVisibility(8);
	}

	private NumberPicker.OnValueChangeListener onSecondChangedListener = new OnValueChangeListener() {

		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			tv.setText("时间：" + String.valueOf(newVal) + "秒");
			m = (long) newVal;
			mc = new MyCount(m * 1000, 1000);
			time = m + "";
		}
	};

	class ButtonClickListner implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			Message msg = handler.obtainMessage();
			if (v.getId() == R.id.btn_count) {
				if (i == 0) {
					count++;
					msg.what = 1;
					handler.sendMessage(msg);
				} else {
					count++;
					msg.what = 1;
					handler.sendMessage(msg);
					if (i == 1) {
						mc.start();
						i = 2;
					}
					if (i == 3) {
						mc.cancel();
					}
				}
			}
		}
	}

	class ResetClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			Message msg = handler.obtainMessage();
			if (v.getId() == R.id.btn_reset) {
				count = 0;
				msg.what = 2;
				handler.sendMessage(msg);
				i = 3;
			}
			btn_count.setVisibility(0);
			btn_reset.setVisibility(8);
			tv_count.setVisibility(8);
			anim_btn_downOut = AnimationUtils.loadAnimation(MainActivity.this, R.anim.btn_out);
			btn_reset.startAnimation(anim_btn_downOut);
			anim_btn_upIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.btn_up_in);
			btn_Time.startAnimation(anim_btn_upIn);
			btn_Time.setVisibility(0);
		}
	}

	class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			tv.setText("时间：" + millisUntilFinished / 1000 + "秒");
		}

		@Override
		public void onFinish() {
			tv.setText("结束");
			btn_count.setVisibility(8);
			btn_reset.setVisibility(0);
			tv_count.setVisibility(0);
			anim_btn_downIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.btn_in);
			btn_reset.startAnimation(anim_btn_downIn);

			ContentValues cv = new ContentValues();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
			String date = simpleDateFormat.format(new Date());
			cv.put("date", date + "");
			cv.put("time", time + "秒内");
			cv.put("count", count + "个");
			dbWriter = db.getWritableDatabase();
			dbWriter.insert("record", null, cv);
			// System.out.println(String.format("date=%s,time=%s,count=%s",
			// date, time, count));
			Toast.makeText(MainActivity.this, "记下了你的成就", Toast.LENGTH_SHORT).show();
		}
	}
}