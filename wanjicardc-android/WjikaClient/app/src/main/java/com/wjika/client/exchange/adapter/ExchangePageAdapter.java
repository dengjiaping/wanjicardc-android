package com.wjika.client.exchange.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.wjika.client.exchange.controller.ExchangeCardFragment;

import java.util.List;

/**
 * Created by zhaoweiwei on 2016/11/30.
 */

public class ExchangePageAdapter extends FragmentPagerAdapter{
	private List<ExchangeCardFragment> fragments;
	public ExchangePageAdapter(FragmentManager fm, List<ExchangeCardFragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}
}
