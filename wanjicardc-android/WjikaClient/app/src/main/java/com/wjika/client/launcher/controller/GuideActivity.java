package com.wjika.client.launcher.controller;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.BaseActivity;
import com.wjika.client.launcher.adapter.ViewPagerAdapter;
import com.wjika.client.network.entities.GuideEntity;
import com.wjika.client.utils.ExitManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacktian on 15/8/26.
 * 引导页
 */
public class GuideActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

	private List<GuideEntity> guideEntities;
	// 底部小点图片
	private ImageView[] dots;
	// 记录当前选中位置
	private int currentIndex;
	private LinearLayout dotContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide_act);
		ExitManager.instance.remove(this);//nohistory属性的activity不在ExitManager里记录
		guideEntities = new ArrayList<>();
		guideEntities.add(new GuideEntity(R.drawable.launcher_img_guide_1, "国家新规", "网络支付 实名管理迎新规"));
		guideEntities.add(new GuideEntity(R.drawable.launcher_img_guide_2, "实名认证", "账户实名 买卡用卡更安全"));
		guideEntities.add(new GuideEntity(R.drawable.launcher_img_guide_3, "保障安全", "平安银行 为资金安全护航"));
		guideEntities.add(new GuideEntity(R.drawable.launcher_img_guide_4, "包子商城", "充值包子当钱花，最优惠"));
		guideEntities.add(new GuideEntity(R.drawable.launcher_img_guide_5, "全新改版", "内容更丰富 活动更精彩"));

		// 初始化页面
		initViews();
		// 初始化底部小点
		initDots();
	}

	private void initViews() {
		LayoutInflater inflater = LayoutInflater.from(this);
		List<View> views = new ArrayList<>();
		// 初始化引导图片列表
		for (int i = 0; guideEntities != null && i < guideEntities.size(); i++) {
			View view = inflater.inflate(R.layout.guide_viewpager_item, null);
			ImageView guideImg = (ImageView) view.findViewById(R.id.guide_img);
			TextView guideTitle = (TextView) view.findViewById(R.id.guide_title);
			TextView guideDesc = (TextView) view.findViewById(R.id.guide_desc);
			guideImg.setImageResource(guideEntities.get(i).getImgRes());
			guideTitle.setText(guideEntities.get(i).getTitle());
			guideDesc.setText(guideEntities.get(i).getDesc());
			views.add(view);
		}

		ViewPager mGuideViewPager = (ViewPager) findViewById(R.id.viewpager);
		// 初始化Adapter
		ViewPagerAdapter vpAdapter = new ViewPagerAdapter(views, this);
		mGuideViewPager.setAdapter(vpAdapter);
		// 绑定回调
		mGuideViewPager.addOnPageChangeListener(this);
	}

	private void initDots() {
		dotContainer = (LinearLayout) findViewById(R.id.dot_container);
		dots = new ImageView[guideEntities.size()];

		// 循环取得小点图片
		for (int i = 0; i < guideEntities.size(); i++) {
			dots[i] = new ImageView(this);
			dots[i].setLayoutParams(new ViewGroup.LayoutParams(22, 22));
			dots[i].setId(i);
			dots[i].setBackgroundResource(R.drawable.guide_dot_icon);
			dots[i].setPadding(0, 0, 0, 0);
			dots[i].setEnabled(false);
			//添加焦点图间的间隔
			View view = new View(this);
			view.setLayoutParams(new ViewGroup.LayoutParams(20, 20));
			dotContainer.addView(dots[i]);
			dotContainer.addView(view);
		}

		currentIndex = 0;
		dots[currentIndex].setBackgroundResource(R.drawable.guide_dot_icon_selected);
	}

	private void setCurrentDot(int position) {
		if (position < 0 || position > guideEntities.size() - 1 || currentIndex == position) {
			return;
		}

		currentIndex = position;
		// 循环取得小点图片
		for (int i = 0; i < guideEntities.size(); i++) {
			if (i == currentIndex) {
				dots[i].setBackgroundResource(R.drawable.guide_dot_icon_selected);
			} else {
				dots[i].setBackgroundResource(R.drawable.guide_dot_icon);
			}
		}
	}

	// 当滑动状态改变时调用
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	// 当当前页面被滑动时调用
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	// 当新的页面被选中时调用
	@Override
	public void onPageSelected(int arg0) {
		// 设置底部小点选中状态
		if (arg0 == dots.length - 1) {
			dotContainer.setVisibility(View.GONE);
		} else {
			dotContainer.setVisibility(View.VISIBLE);
		}
		setCurrentDot(arg0);
	}
}
