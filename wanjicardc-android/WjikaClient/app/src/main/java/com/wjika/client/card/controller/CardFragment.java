package com.wjika.client.card.controller;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.viewinject.annotation.ViewInject;
import com.common.widget.RefreshRecyclerView;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.fragment.BaseFragment;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.market.adapter.ECardAdapter;
import com.wjika.client.market.controller.ECardDetailActivity;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.ECardEntity;
import com.wjika.client.network.entities.MarketMainEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.utils.CommonTools;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by Liu_Zhichao on 2016/11/2 18:15.
 * 卡商城
 */
public class CardFragment extends BaseFragment implements View.OnClickListener, RefreshRecyclerView.OnRefreshAndLoadMoreListener {

	public static final int REQUEST_NET_CARD_LIST = 0x1;
	public static final int REQUEST_NET_CARD_LIST_MORE = 0x2;

	private View view;
	@ViewInject(R.id.toolbar_title)
	private TextView toolbarTitle;
	@ViewInject(R.id.card_main_list)
	private RefreshRecyclerView cardMainList;

	private ECardAdapter adapter;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.frag_card_mian, null);
			ViewInjectUtils.inject(this, view);
			initView();
		}
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		return view;
	}

	private void initView() {
		toolbarTitle.setText(getActivity().getString(R.string.main_tab_store));
		toolbarTitle.setVisibility(View.VISIBLE);
		initLoadingView(this, view);
		setLoadingStatus(LoadingStatus.LOADING);
		cardMainList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
		cardMainList.setMode(RefreshRecyclerView.Mode.BOTH);
		cardMainList.setOnRefreshAndLoadMoreListener(this);
		cardMainList.addItemDecoration(new RecyclerView.ItemDecoration() {
			@Override
			public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
				if (parent.getLayoutManager() instanceof GridLayoutManager) {
					GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
					int spanCount = gridLayoutManager.getSpanCount();//设置的列数
					int headerSize = cardMainList.getHeaderSize();//头部数量
					int pos = parent.getChildLayoutPosition(view) - headerSize;//减去头部后的下标位置

					if (pos < 0) {//头部
						return;
					}

					//item左右偏移量(左右间距)，仅对2列有效
					if (pos % 2 == 0) {
						outRect.right = CommonTools.dp2px(getActivity(), 6);
						outRect.left = CommonTools.dp2px(getActivity(), 12);
					} else {
						outRect.left = CommonTools.dp2px(getActivity(), 6);
						outRect.right = CommonTools.dp2px(getActivity(), 12);
					}

					//item上下偏移量(上下间距)
					if (pos >= (spanCount + headerSize - 1)) {
						//第二行开始的上偏移量
						outRect.top = CommonTools.dp2px(getActivity(), 16);
					} else {
						//头部和内容第一行上偏移量
						outRect.top = CommonTools.dp2px(getActivity(), 12);
					}
				}
			}
		});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		loadData(false);
	}

	private void loadData(boolean isMore) {
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put(Constants.TOKEN, UserCenter.getToken(getActivity()));
		params.put(Constants.PAGESIZE, String.valueOf(20));
		int pageNum = 1;
		int requestCode = REQUEST_NET_CARD_LIST;
		if (isMore) {
			pageNum = adapter.getPage() + 1;
			requestCode = REQUEST_NET_CARD_LIST_MORE;
		}
		params.put(Constants.PAGENUM, String.valueOf(pageNum));
		requestHttpData(Constants.Urls.URL_POST_EMARKET_MAIN, requestCode, FProtocol.HttpMethod.POST, params);
	}

	@Override
	public void success(int requestCode, String data) {
		super.success(requestCode, data);
		if (cardMainList != null) {
			cardMainList.reSetStatus();
		}
		switch (requestCode) {
			case REQUEST_NET_CARD_LIST: {
				MarketMainEntity marketMainEntity = Parsers.getMarketMainEntity(data);
				if (marketMainEntity != null && marketMainEntity.getMarketECardEntity() != null) {
					List<ECardEntity> eCardEntities = marketMainEntity.getMarketECardEntity().geteCardList();
					if (eCardEntities != null && eCardEntities.size() > 0) {
						setLoadingStatus(LoadingStatus.GONE);
						adapter = new ECardAdapter(eCardEntities, this);
						adapter.setFlag(ECardAdapter.FLAG_CARD_STORE);
						cardMainList.setAdapter(adapter);
						if (marketMainEntity.getMarketECardEntity().getPages() <= 1) {
							cardMainList.setCanAddMore(false);
						}
					} else {
						setLoadingStatus(LoadingStatus.EMPTY);
					}
				} else {
					setLoadingStatus(LoadingStatus.EMPTY);
				}
			}
			break;
			case REQUEST_NET_CARD_LIST_MORE: {
				MarketMainEntity marketMainEntity = Parsers.getMarketMainEntity(data);
				if (marketMainEntity != null && marketMainEntity.getMarketECardEntity() != null) {
					List<ECardEntity> eCardEntityList = marketMainEntity.getMarketECardEntity().geteCardList();
					if (eCardEntityList != null && eCardEntityList.size() > 0) {
						adapter.addDatas(eCardEntityList);
						if (adapter.getPage() >= marketMainEntity.getMarketECardEntity().getPages()) {
							cardMainList.setCanAddMore(false);
						}
					}
				}
			}
			break;
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
		if (cardMainList != null) {
			cardMainList.reSetStatus();
		}
		switch (requestCode) {
			case REQUEST_NET_CARD_LIST:
				setLoadingStatus(LoadingStatus.RETRY);
				break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.loading_layout:
				setLoadingStatus(LoadingStatus.LOADING);
				loadData(false);
				break;
			case R.id.ecard_item_layout:
				ECardEntity eCardEntity = (ECardEntity) v.getTag();
				if (eCardEntity != null) {
					Intent intent = new Intent(getActivity(), ECardDetailActivity.class);
					intent.putExtra(ECardDetailActivity.EXTRA_TITLE, eCardEntity.getName());
					intent.putExtra(ECardDetailActivity.EXTRA_ECARD_ID, eCardEntity.getId());
					intent.putExtra(ECardDetailActivity.EXTRA_FLAG, ECardDetailActivity.FLAG_CARD_STORE);
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
