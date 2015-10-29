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
	//��ָ���һ���ʱ����С�ٶ�  
    private static final int XSPEED_MIN = 150;  
    //��ָ���һ���ʱ����С����  
    private static final int XDISTANCE_MIN = 300;  
    //��¼��ָ����ʱ�ĺ����ꡣ  
    private float xDown;  
    //��¼��ָ�ƶ�ʱ�ĺ����ꡣ  
    private float xMove;  
    //���ڼ�����ָ�������ٶȡ�  
    private VelocityTracker mVelocityTracker;  
	Db db = new Db(MyApplication.getContext());

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// �����ޱ���
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
			Toast.makeText(RecordActivity.this, "ɾ����һ���ɾ�", Toast.LENGTH_SHORT).show();
			return true;
		}
	};

	private OnItemClickListener OnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Toast.makeText(RecordActivity.this, "����ɾ��", Toast.LENGTH_SHORT).show();
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
				//��ľ���
				int distanceX = (int) (xMove - xDown);
				//��ȡ˳ʱ�ٶ�
				int xSpeed = getScrollVelocity();
				//�������ľ�����������趨����С�����һ�����˲���ٶȴ��������趨���ٶ�ʱ�����ص���һ��activity
				if(distanceX > XDISTANCE_MIN && xSpeed > XSPEED_MIN) {
					finish();
					//�����л����������ұ߽��룬����˳�
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
	 * ����VelocityTracker���󣬲�������content����Ļ����¼����뵽VelocityTracker���С�
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
	 * ����VelocityTracker����
	 */
	private void recycleVelocityTracker() {
		mVelocityTracker.recycle();
		mVelocityTracker = null;
	}
	
	/**
	 * ��ȡ��ָ��content���滬�����ٶȡ�
	 * 
	 * @return �����ٶȣ���ÿ�����ƶ��˶�������ֵΪ��λ��
	 */
	private int getScrollVelocity() {
		mVelocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) mVelocityTracker.getXVelocity();
		return Math.abs(velocity);
	}
}