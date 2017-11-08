package com.wjika.client.store.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.common.utils.DeviceUtil;
import com.wjika.cardagent.client.R;
import com.wjika.client.store.adapter.LeftDistanceAdapter;
import com.wjika.client.store.adapter.RightAdapter;
import com.wjika.client.db.CityDBManager;
import com.wjika.client.network.entities.CityEntity;
import com.wjika.client.utils.CommonTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacktian on 15/9/6.
 * popwindow
 */
public class CheckDistanceSpinnerView {

	//第一组
	private ListView listView1 = null;
	private ListView listView2 = null;
	private LeftDistanceAdapter leftAdapter = null;
	private RightAdapter rightAdapter = null;
	private int mClickPosition1;
	private int mClickPosition2;
	private OnSpinnerItemClickListener mOnSpinnerItemClickListener;
	private PoupWindowListener mPoupWindowListener;
	private static int screen_w = 0;
	private static int screen_h = 0;

	private List<CityEntity> mCurrentStreetList;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case 20:
					rightAdapter.clear();
					rightAdapter.addDatas(mCurrentStreetList);
					rightAdapter.notifyDataSetChanged();
					rightAdapter.setSelectedPosition(0);
					break;
				default:
					break;
			}
		}
	};

	private PopupWindow mPopupWindow = null;
	private Context mContext;
	private View spinnerGap;

	public CheckDistanceSpinnerView(Activity activity) {
		mContext = activity;
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm); // 获取手机屏幕的大小
		screen_w = dm.widthPixels;
		screen_h = dm.heightPixels - DeviceUtil.dp_to_px(activity, 118);
	}

	public CheckDistanceSpinnerView(Activity activity, OnSpinnerItemClickListener listener) {
		mContext = activity;
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm); // 获取手机屏幕的大小
		screen_w = dm.widthPixels;
		screen_h = dm.heightPixels;
		mOnSpinnerItemClickListener = listener;
	}

	public void addMenuShowListener(PoupWindowListener poupWindowListener) {
		mPoupWindowListener = poupWindowListener;
	}

	/**
	 * 初始化 PopupWindow
	 */
	private void initPopuWindow(View view) {
		/* 第一个参数弹出显示view 后两个是窗口大小 */
		mPopupWindow = new PopupWindow(view, screen_w, screen_h);
		/* 设置背景显示 */
		mPopupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.buy_pop_bg));
//        mPopupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.cell_white_bg));
		/* 设置触摸外面时消失 */
		mPopupWindow.setOutsideTouchable(true);

		mPopupWindow.update();
		mPopupWindow.setTouchable(true);
		/* 设置点击menu以外其他地方以及返回键退出 */
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				if (mPoupWindowListener != null) {
					mPoupWindowListener.poupWindowDismiss(false);
				}
			}
		});
		/**
		 * 1.解决再次点击MENU键无反应问题 2.sub_view是PopupWindow的子View
		 */
		view.setFocusableInTouchMode(true);
	}


	/**
	 * 展示区域选择的对话框
	 */
	public void showSpinnerPop(View view, Animation animation, final List<CityEntity> leftList, int selectedLeftPos, final int selectedRightPos) {
		if (mPoupWindowListener != null) {
			mPoupWindowListener.poupWindowDismiss(true);
		}
		mClickPosition1 = selectedLeftPos;
		mClickPosition2 = selectedRightPos;
		if (leftList != null && selectedLeftPos != 0) {
			mCurrentStreetList = CityDBManager.getCurrentCityStreet(mContext, leftList.get(selectedLeftPos).getId());
		}
		if (mCurrentStreetList == null) {
			mCurrentStreetList = new ArrayList<>();
		}
		if (mCurrentStreetList.size() == 0 ||
				(leftList != null
						&& leftList.size() > selectedLeftPos
						&& !(leftList.get(selectedLeftPos).getId()).equals(mCurrentStreetList.get(0).getId()))) {
			CityEntity defalutCity = new CityEntity();
			defalutCity.setName(leftList.get(selectedLeftPos).getName());
			defalutCity.setId(leftList.get(selectedLeftPos).getId());
			mCurrentStreetList.add(0, defalutCity);
		}

		if (mPopupWindow == null) {
			View view1 = LayoutInflater.from(mContext).inflate(R.layout.buy_distance_spinner_view, null);
			initPopuWindow(view1);
			spinnerGap = view1.findViewById(R.id.spinner_gap);
			listView1 = (ListView) view1.findViewById(R.id.listView1);
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) listView1.getLayoutParams();
			params.height = CommonTools.dp2px(mContext, 400);
			listView1.setLayoutParams(params);
			listView2 = (ListView) view1.findViewById(R.id.listView2);
			leftAdapter = new LeftDistanceAdapter(mContext, leftList);
			listView1.setAdapter(leftAdapter);
			leftAdapter.setSelectedPosition(selectedLeftPos);
			rightAdapter = new RightAdapter(mContext, mCurrentStreetList);
			listView2.setAdapter(rightAdapter);
			if (mClickPosition2 == -1) {

				rightAdapter.setSelectedPosition(0);
			} else {
				rightAdapter.setSelectedPosition(mClickPosition2);
			}
		}
//        view1.setAnimation(animation);
//        view1.startAnimation(animation);

		mPopupWindow.showAsDropDown(view, -4, 0);

		spinnerGap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				close();
			}
		});

		listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
			                        int position, long id) {

				if (mClickPosition1 == position) {
					return;
				}
				mClickPosition1 = position;
				leftAdapter.setSelectedPosition(position);
				leftAdapter.notifyDataSetChanged();
				if (mOnSpinnerItemClickListener != null) {
					mOnSpinnerItemClickListener.onItemClickListener1(parent, view, position, id);
				}
				if (mCurrentStreetList != null) {
					mCurrentStreetList.clear();
				}
				if (position != 0) {
					mCurrentStreetList = CityDBManager.getCurrentCityStreet(mContext, leftList.get(position).getId());
				}
				if (mCurrentStreetList == null) {
					mCurrentStreetList = new ArrayList<>();
				}
				if (mCurrentStreetList.size() == 0 ||
						(leftList != null
								&& leftList.size() > position
								&& !(leftList.get(position).getId()).equals(mCurrentStreetList.get(0).getId()))) {
					CityEntity defalutCity = new CityEntity();
					defalutCity.setName(leftList.get(position).getName());
					defalutCity.setId(leftList.get(position).getId());
					mCurrentStreetList.add(0, defalutCity);
				}

				Message msg = new Message();
				msg.what = 20;
				msg.arg1 = position;
				handler.sendMessageDelayed(msg, 20);
			}
		});

		listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mClickPosition2 = position;
				rightAdapter.setSelectedPosition(position);
				if (mOnSpinnerItemClickListener != null) {
					mOnSpinnerItemClickListener.onItemClickListener2(mCurrentStreetList, parent, view, mClickPosition1, position, id);
				}
				mPopupWindow.dismiss();
			}
		});
	}

	public void close() {
		mPopupWindow.dismiss();
	}

	public interface PoupWindowListener {
		void poupWindowDismiss(boolean isShow);
	}

	public interface OnSpinnerItemClickListener {
		void onItemClickListener1(AdapterView<?> parent, View view, int position, long id);

		void onItemClickListener2(List<CityEntity> currentStreet, AdapterView<?> parent, View view, int position1, int position2, long id);
	}
}
