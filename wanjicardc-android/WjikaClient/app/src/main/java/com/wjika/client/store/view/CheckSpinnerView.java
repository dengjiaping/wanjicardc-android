package com.wjika.client.store.view;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.common.utils.DeviceUtil;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.OptionEntity;
import com.wjika.client.store.adapter.LeftAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacktian on 15/9/6.
 * popwindow
 */
public class CheckSpinnerView {

	//第一组
	private ListView listView1 = null;
	private LeftAdapter leftAdapter = null;
	private OnSpinnerItemClickListener mOnSpinnerItemClickListener;
	private PoupWindowListener mPoupWindowListener;
	private static int screen_w = 0;
	private static int screen_h = 0;

	private PopupWindow mPopupWindow = null;
	private Context mContext;
	private View spinnerGap;

	public void addPoupWindowListener(PoupWindowListener poupWindowListener) {
		mPoupWindowListener = poupWindowListener;
	}

	public CheckSpinnerView(Activity activity) {
		mContext = activity;
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm); // 获取手机屏幕的大小
		screen_w = dm.widthPixels;
		screen_h = dm.heightPixels - DeviceUtil.dp_to_px(activity, 120);
	}

	public CheckSpinnerView(Activity activity, OnSpinnerItemClickListener listener) {
		mContext = activity;
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm); // 获取手机屏幕的大小
		screen_w = dm.widthPixels;
		screen_h = dm.heightPixels;
		mOnSpinnerItemClickListener = listener;
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
	public void showSpinnerPop(View view, Animation animation, final List<? extends OptionEntity> leftList, int selectedLeftPos) {
		if (mPoupWindowListener != null) {
			mPoupWindowListener.poupWindowDismiss(true);
		}
		List<OptionEntity> leftOPtionsList = new ArrayList<>();
		for (OptionEntity entity : leftList) {
			leftOPtionsList.add(entity);
		}
		if (mPopupWindow == null) {
			View view1 = LayoutInflater.from(mContext).inflate(R.layout.buy_distance_spinner_view, null);
			initPopuWindow(view1);

			spinnerGap = view1.findViewById(R.id.spinner_gap);
			listView1 = (ListView) view1.findViewById(R.id.listView1);
			ListView listView2 = (ListView) view1.findViewById(R.id.listView2);
			listView2.setVisibility(View.GONE);

			leftAdapter = new LeftAdapter(mContext, leftOPtionsList);
			setListViewHeightBasedOnChildren(listView1);
			listView1.setAdapter(leftAdapter);
			listView1.setDividerHeight(0);
			leftAdapter.setSelectedPosition(selectedLeftPos);

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
				leftAdapter.setSelectedPosition(position);
				if (mOnSpinnerItemClickListener != null) {
					mOnSpinnerItemClickListener.onItemClickListener1(parent, view, position, id);
				}
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
	}

	private void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter adapter = listView1.getAdapter();
		if (adapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < adapter.getCount(); i++) {
			View listItem = adapter.getView(i, null, listView1);
			listItem.measure(0, 0);
			listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
		listView.setLayoutParams(params);
	}
}
