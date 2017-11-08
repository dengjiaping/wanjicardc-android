package com.wjika.client.store.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.common.view.SidesLipGallery;
import com.wjika.cardagent.client.R;

/**
 * Created by Leo_Zhang on 2016/7/20.
 * 控制浮层透明度的滚动条
 */
public class AlphaTitleScrollView extends ScrollView {

	private int mSlop;
	private RelativeLayout toolbar;
	private SidesLipGallery headView;

	public AlphaTitleScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public AlphaTitleScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AlphaTitleScrollView(Context context) {
		this(context, null);
	}

	private void init() {
		mSlop = 10;
	}

	public void setTitleAndHead(RelativeLayout toolbar, SidesLipGallery headView) {
		this.toolbar = toolbar;
		this.headView = headView;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		int scrollY = getScrollY();
		if (scrollY < 10) {
			toolbar.setBackgroundResource(R.color.main_titlebar_default);
		} else {
			if (headView != null && toolbar != null) {
				float headHeight = headView.getMeasuredHeight()
						- toolbar.getMeasuredHeight();
				int alpha = (int) (((float) t / headHeight) * 255);
				if (alpha >= 255)
					alpha = 255;
				if (alpha <= mSlop)
					alpha = 0;
				toolbar.setBackgroundResource(R.color.wjika_client_title_bg);
				toolbar.getBackground().mutate().setAlpha(alpha);
			}
		}
		super.onScrollChanged(l, t, oldl, oldt);
	}
}
