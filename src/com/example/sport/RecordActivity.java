package com.example.sport;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RecordActivity extends ListActivity {
	private TextView tv;
	private SQLiteDatabase dbWriter, dbReader;
	private SimpleCursorAdapter scAdapter;
	//手指向右滑动时的最小速度  
    private static final int XSPEED_MIN = 150;  
    //手指向右滑动时的最小距离  
    private static final int XDISTANCE_MIN = 300;  
    //记录手指按下时的横坐标。  
    private float xDown;  
    //记录手指移动时的横坐标。  
    private float xMove;  
    //用于计算手指滑动的速度。  
    private VelocityTracker mVelocityTracker;  
	Db db = new Db(MyApplication.getContext());

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.record);
		initView();
//		ListView listView = new ListView(RecordActivity.this);
		dbReader = db.getReadableDatabase();
		dbWriter = db.getWritableDatabase();
		Cursor c = dbReader.query("record", null, null, null, null, null, null);
		if (c.getCount() == 0) {
			tv.setVisibility(View.VISIBLE);
		} else {
			tv.setVisibility(View.GONE);
		}
		scAdapter = new SimpleCursorAdapter(RecordActivity.this, R.layout.list_record, c,
				new String[] { "date", "count", "time" }, new int[] { R.id.tv1, R.id.tv2, R.id.tv3 });
		scAdapter.changeCursor(c);
		setListAdapter(scAdapter);
		getListView().setOnTouchListener(listViewOnTouchListener);
		getListView().setOnItemClickListener(OnItemClickListener);
		getListView().setOnItemLongClickListener(OnItemLongClickListener);
	}
	
	private void initView() {
		tv = (TextView) findViewById(R.id.tv_record);
	}

	private OnItemLongClickListener OnItemLongClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			Cursor c = scAdapter.getCursor();
			c.moveToPosition(position);
			int itemId = c.getInt(c.getColumnIndex("_id"));
			dbWriter.delete("record", "_id=?", new String[] { itemId + "" });
			refreshListView();
			Toast.makeText(RecordActivity.this, "删除了一条成就", Toast.LENGTH_SHORT).show();
			return true;
		}
	};

	private OnItemClickListener OnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Toast.makeText(RecordActivity.this, "长按删除", Toast.LENGTH_SHORT).show();
		}
	};

	private void refreshListView() {
		Cursor c = dbReader.query("record", null, null, null, null, null, null);
		scAdapter.changeCursor(c);
	}
	
	private OnTouchListener listViewOnTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			createVelocityTracker(event);
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				xDown = event.getRawX();
				break;
			case MotionEvent.ACTION_MOVE:
				xMove = event.getRawX();
				//活动的距离
				int distanceX = (int) (xMove - xDown);
				//获取顺时速度
				int xSpeed = getScrollVelocity();
				//当滑动的距离大于我们设定的最小距离且滑动的瞬间速度大于我们设定的速度时，返回到上一个activity
				if(distanceX > XDISTANCE_MIN && xSpeed > XSPEED_MIN) {
					finish();
					//设置切换动画，从右边进入，左边退出
					overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
				}
				break;
			case MotionEvent.ACTION_UP:
				recycleVelocityTracker();
				break;
			}
			return false;
		}
	};
	
	/**
	 * 创建VelocityTracker对象，并将触摸content界面的滑动事件加入到VelocityTracker当中。
	 * 
	 * @param event
	 *        
	 */
	private void createVelocityTracker(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
	}
	
	/**
	 * 回收VelocityTracker对象。
	 */
	private void recycleVelocityTracker() {
		mVelocityTracker.recycle();
		mVelocityTracker = null;
	}
	
	/**
	 * 获取手指在content界面滑动的速度。
	 * 
	 * @return 滑动速度，以每秒钟移动了多少像素值为单位。
	 */
	private int getScrollVelocity() {
		mVelocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) mVelocityTracker.getXVelocity();
		return Math.abs(velocity);
	}
}