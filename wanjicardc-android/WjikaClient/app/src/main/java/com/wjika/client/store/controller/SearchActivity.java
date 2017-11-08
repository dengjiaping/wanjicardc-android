package com.wjika.client.store.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.PreferencesUtils;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.BaseActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.HotKeyEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.store.adapter.SearchHistoryAdapter;
import com.wjika.client.utils.InputMethodUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by jacktian on 15/9/6.
 * 商家搜索
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener {

	private final static String SEARCH_STORE_PRE_KEY_HISTORY = "search_pre_key_history";
	public static final String EXTRA_FROM = "extra_from";
	private static final int REQUEST_GET_HOT_KEYS_CODE = 0x1;
	private final static int SEARCH_HISTORY_COUNT = 10;

	@ViewInject(R.id.scrollview)
	private ScrollView mScrollView;
	@ViewInject(R.id.btn_back)
	private ImageView mBtnBack;
	@ViewInject(R.id.edit_search)
	private EditText mEditSearch;
	@ViewInject(R.id.search_history_list)
	private ListView mHistoryListView;
	@ViewInject(R.id.search_hot_key)
	private ViewGroup searchHotKey;
	@ViewInject(R.id.btn_clean_history)
	private LinearLayout mBtnCleanHistory;
	@ViewInject(R.id.layout_history)
	private View mLayoutHistory;
	@ViewInject(R.id.btn_start_search)
	private TextView mBtnStartSearch;
	@ViewInject(R.id.txt_search_label)
	private TextView mLabelSearch;
	@ViewInject(R.id.search_line)
	private View searchLine;

	private SearchHistoryAdapter mHistoryAdapter;
	List<String> mSearchHistory = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buy_search_act);
		ViewInjectUtils.inject(this);
		getSearchStoreHistory();
		initView();
		loadHotKeyData();
	}

	private void loadHistoryData() {
		getSearchStoreHistory();
		if (mSearchHistory != null && mSearchHistory.size() > 0) {
			mLayoutHistory.setVisibility(View.VISIBLE);
			mHistoryAdapter = new SearchHistoryAdapter(this, mSearchHistory);
			mHistoryListView.setAdapter(mHistoryAdapter);
			mBtnCleanHistory.setVisibility(View.VISIBLE);
			mLabelSearch.setVisibility(View.VISIBLE);

		} else {
			mLayoutHistory.setVisibility(View.GONE);
			mBtnCleanHistory.setVisibility(View.GONE);
			mLabelSearch.setVisibility(View.GONE);
		}
	}

	private void loadHotKeyData() {
		int mNeedHotKeyCount = 9;
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put(Constants.TOKEN, UserCenter.getToken(this));
		params.put("pageSize", mNeedHotKeyCount + "");
		requestHttpData(Constants.Urls.URL_GET_SEARCH_HOT_KEYS, REQUEST_GET_HOT_KEYS_CODE, FProtocol.HttpMethod.POST, params);
	}

	private void initView() {
		mBtnBack.setOnClickListener(this);
		mBtnCleanHistory.setOnClickListener(this);
		mBtnStartSearch.setOnClickListener(this);
		mScrollView.smoothScrollTo(0, 20);

		if (mSearchHistory != null && mSearchHistory.size() > 0) {
			mLayoutHistory.setVisibility(View.VISIBLE);
			mHistoryAdapter = new SearchHistoryAdapter(this, mSearchHistory);
			mHistoryListView.setAdapter(mHistoryAdapter);
		} else {
			mLayoutHistory.setVisibility(View.GONE);
		}
		mHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String storeName = mHistoryAdapter.getItem(position);
				setSearchStoreHistory(storeName);
				gotoSearchResult(storeName);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadHistoryData();//延迟搜索历史展现
	}

	@Override
	public void parseData(int requestCode, String data) {
		super.parseData(requestCode, data);
		switch (requestCode) {
			case REQUEST_GET_HOT_KEYS_CODE: {
				if (data != null) {
					List<HotKeyEntity> hotKeys = Parsers.getHotKeysList(data);
					if (hotKeys != null && hotKeys.size() > 0) {
						for (int i = 0; i < hotKeys.size(); i++) {
							final TextView textView = new TextView(this);
							textView.setBackgroundResource(R.drawable.search_key_word);
							textView.setText(hotKeys.get(i).getName());
							textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
							textView.setTextColor(getResources().getColor(R.color.store_list_category));
							textView.setGravity(Gravity.CENTER);
							searchHotKey.addView(textView);
							textView.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									setSearchStoreHistory(textView.getText().toString());
									gotoSearchResult(textView.getText().toString());
								}
							});
						}
						int padding = 24;
						searchHotKey.setPadding(padding, padding, padding, padding);
					}
				}
				break;
			}
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_back: {
				this.finish();
				break;
			}
			case R.id.btn_clean_history: {
				cleanHistory();
				break;
			}
			case R.id.btn_start_search: {
				String searchStr = mEditSearch.getText().toString();
				if (StringUtil.isEmpty(searchStr)) {
					ToastUtil.shortShow(this, this.getString(R.string.search_conteng));
					return;
				}
				setSearchStoreHistory(searchStr);
				InputMethodUtil.closeInputMethod(this);
				gotoSearchResult(searchStr);
				break;
			}
		}
	}

	@Override
	public void onDestroy() {
		InputMethodUtil.closeInputMethod(this);
		super.onDestroy();
	}

	private void gotoSearchResult(String name) {
		Intent intent = new Intent(this, SearchResultActivity.class);
		intent.putExtra(SearchResultActivity.EXTRA_SEARCH_RESULT_NAME, name);
		intent.putExtra(EXTRA_FROM, SearchResultActivity.EXTRA_FROM_SEARCH_CODE);
		startActivity(intent);
	}

	private void cleanHistory() {
		PreferencesUtils.putString(this, SEARCH_STORE_PRE_KEY_HISTORY, "");
		mHistoryAdapter.clear();
		mHistoryAdapter.notifyDataSetChanged();
		mLabelSearch.setVisibility(View.GONE);
		mLayoutHistory.setVisibility(View.GONE);
		searchLine.setVisibility(View.GONE);
	}

	public void setSearchStoreHistory(String history) {
		StringBuilder historyStr = new StringBuilder();
		if (mSearchHistory != null) {
			if (mSearchHistory.contains(history)) {
				mSearchHistory.remove(history);
			}
			if (mSearchHistory.size() >= SEARCH_HISTORY_COUNT) {
				mSearchHistory.remove(mSearchHistory.size() - 1);
			}
			historyStr.append(history);
			for (String str : mSearchHistory) {
				historyStr.append(",");
				historyStr.append(str);
			}
			mSearchHistory.add(0, history);
		}
		PreferencesUtils.putString(this, SEARCH_STORE_PRE_KEY_HISTORY, historyStr.toString());
	}

	public void getSearchStoreHistory() {
		String historyStr = PreferencesUtils.getString(this, SEARCH_STORE_PRE_KEY_HISTORY);
		if (mSearchHistory != null) {
			mSearchHistory.clear();
			List<String> historys = new ArrayList<>();
			if (!StringUtil.isEmpty(historyStr)) {
				searchLine.setVisibility(View.VISIBLE);
				Collections.addAll(historys, historyStr.split(","));
				for (int i = 0; i < historys.size() && i < SEARCH_HISTORY_COUNT; i++) {
					mSearchHistory.add(historys.get(i));
				}
			} else {
				searchLine.setVisibility(View.GONE);
			}
		}
	}
}
