package com.wjika.client.market.controller;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.common.network.FProtocol;
import com.common.viewinject.annotation.ViewInject;
import com.common.widget.RefreshRecyclerView;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.market.adapter.ECardAdapter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.ECardEntity;
import com.wjika.client.network.entities.ECardPageEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.utils.CommonTools;
import com.wjika.client.utils.ExitManager;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by Liu_Zhichao on 2016/8/15 11:31.
 * 电子卡列表
 */
public class ECardActivity extends ToolBarActivity implements View.OnClickListener, RefreshRecyclerView.OnRefreshAndLoadMoreListener {

	private static final int REQUEST_NET_ECARD_LIST = 10;
	private static final int REQUEST_NET_ECARD_LIST_MORE = 20;
	private static final int RECYCLE_VIEW_COLUMN_NUM = 2;

	@ViewInject(R.id.ecard_list)
	private RefreshRecyclerView ecardList;

	private ECardAdapter adapter;
	private String brandId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_ecard);
		ExitManager.instance.addECardActivity(this);
		ViewInjectUtils.inject(this);
		initView();
		initLoadingView(this);
		setLoadingStatus(LoadingStatus.LOADING);
		loadData(false);
	}

	private void initView() {
		setLeftTitle(getString(R.string.person_electric_card));
		brandId = getIntent().getStringExtra("brandId");
		ecardList.setHasFixedSize(true);
		ecardList.setMode(RefreshRecyclerView.Mode.BOTH);
		ecardList.setLayoutManager(new GridLayoutManager(this, RECYCLE_VIEW_COLUMN_NUM));
		ecardList.addItemDecoration(new RecyclerView.ItemDecoration() {
			@Override
			public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
				if (parent.getLayoutManager() instanceof GridLayoutManager) {
					GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
					int spanCount = gridLayoutManager.getSpanCount();//设置的列数
					int headerSize = ecardList.getHeaderSize();//头部数量
					int pos = parent.getChildLayoutPosition(view) - headerSize;//减去头部后的下标位置

					if (pos < 0) {//头部
						return;
					}

					//item左右偏移量(左右间距)，仅对2列有效
					if (pos % 2 == 0) {
						outRect.right = CommonTools.dp2px(ECardActivity.this, 6);
					} else {
						outRect.left = CommonTools.dp2px(ECardActivity.this, 6);
					}

					//item上下偏移量(上下间距)
					if (pos >= (spanCount + headerSize - 1)) {
						//第二行开始的上偏移量
						outRect.top = CommonTools.dp2px(ECardActivity.this, 16);
					} else {
						//头部和内容第一行上偏移量
						outRect.top = CommonTools.dp2px(ECardActivity.this, 12);
					}
				}
			}
		});

		ecardList.setOnRefreshAndLoadMoreListener(this);
	}

	private void loadData(boolean isMore) {
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put(Constants.PAGESIZE, String.valueOf(Constants.PAGE_SIZE * 2));
		params.put(Constants.TOKEN, UserCenter.getToken(this));
		params.put("merchantBranchId", brandId);
		int pageNum = 1;
		int requestCode = REQUEST_NET_ECARD_LIST;
		if (isMore && adapter != null) {
			pageNum = adapter.getPage() + 1;
			requestCode = REQUEST_NET_ECARD_LIST_MORE;
		}
		params.put(Constants.PAGENUM, String.valueOf(pageNum));
		requestHttpData(Constants.Urls.URL_POST_ECARD_LIST, requestCode, FProtocol.HttpMethod.POST, params);
	}

	@Override
	public void success(int requestCode, String data) {
		ecardList.reSetStatus();
		super.success(requestCode, data);
	}

	@Override
	protected void parseData(int requestCode, String data) {
		super.parseData(requestCode, data);
		switch (requestCode) {
			case REQUEST_NET_ECARD_LIST:{
				ECardPageEntity eCardPage = Parsers.getECardPage(data);
				if (eCardPage != null) {
					List<ECardEntity> eCardEntities = eCardPage.geteCardEntities();
					if (eCardEntities.isEmpty()) {
						setLoadingStatus(LoadingStatus.EMPTY);
					}
					adapter = new ECardAdapter(eCardEntities, this);
					ecardList.setAdapter(adapter);
					if (eCardPage.getTotalPage() > 1) {
						ecardList.setCanAddMore(true);
					} else {
						ecardList.setCanAddMore(false);
					}
				}
				break;
			}
			case REQUEST_NET_ECARD_LIST_MORE:{
				ECardPageEntity eCardPage = Parsers.getECardPage(data);
				if (eCardPage != null && eCardPage.geteCardEntities().size() > 0) {
					adapter.addDatas(eCardPage.geteCardEntities());
					if (eCardPage.getTotalPage() <= adapter.getPage()) {
						ecardList.setCanAddMore(false);
					}
				}
				break;
			}
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		ecardList.reSetStatus();
		super.mistake(requestCode, status, errorMessage);
		setLoadingStatus(LoadingStatus.RETRY);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.loading_layout:
				loadData(false);
				break;
			case R.id.ecard_item_layout:
				ECardEntity eCardEntity = (ECardEntity) v.getTag();
				if (eCardEntity != null) {
					Intent intent = new Intent(this, ECardDetailActivity.class);
					intent.putExtra(ECardDetailActivity.EXTRA_TITLE, eCardEntity.getName());
					intent.putExtra(ECardDetailActivity.EXTRA_ECARD_ID, eCardEntity.getId());
					startActivity(intent);
				}
				break;
		}
	}

	@Override
	public void onRefresh() {
		loadData(false);
	}

	@Override
	public void onLoadMore() {
		loadData(true);
	}
}
