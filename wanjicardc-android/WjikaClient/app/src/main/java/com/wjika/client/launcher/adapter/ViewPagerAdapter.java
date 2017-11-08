package com.wjika.client.launcher.adapter;


import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wjika.cardagent.client.R;
import com.wjika.client.main.controller.MainActivity;
import com.wjika.client.utils.ConfigUtils;

import java.util.List;

/**
 * Created by jacktian on 15/8/27.
 * 引导页adapter
 */
public class ViewPagerAdapter extends PagerAdapter {

	// 界面列表
	private List<View> views;
	private Activity activity;

	public ViewPagerAdapter(List<View> views, Activity activity) {
		this.views = views;
		this.activity = activity;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	// 获得当前界面数
	@Override
	public int getCount() {
		if (views != null) {
			return views.size();
		}
		return 0;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = views.get(position);
		container.addView(view);

		TextView btnStart = (TextView) view.findViewById(R.id.guide_btn_start);
		if (position == views.size() - 1) {
			btnStart.setVisibility(View.VISIBLE);
			btnStart.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 设置已经引导
					setGuided();
					goHome();
				}
			});
		} else {
			btnStart.setVisibility(View.GONE);
		}
		return view;
	}

	private void goHome() {
		activity.startActivity(new Intent(activity, MainActivity.class));
	}

	/**
	 * method desc：设置已经引导过了，下次启动不用再次引导
	 */
	private void setGuided() {
		ConfigUtils.setShowGuide(activity, false);
	}

	// 判断是否由对象生成界面
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}
}
