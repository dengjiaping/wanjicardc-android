package com.wjika.client.store.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.wjika.cardagent.client.R;

import java.util.List;

/**
 * Created by jacktian on 15/9/8.
 * 搜索历史记录
 */
public class SearchHistoryAdapter extends BaseAdapterNew<String> {

	public SearchHistoryAdapter(Context context, List<String> mDatas) {
		super(context, mDatas);
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.buy_search_history_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		String entity = getItem(position);
		TextView historyName = ViewHolder.get(convertView, R.id.txt_history_name);
		historyName.setText(entity);
	}
}
