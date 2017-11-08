package com.wjika.client.market.controller;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.AnalysisUtil;
import com.common.viewinject.annotation.ViewInject;
import com.common.widget.RefreshRecyclerView;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.fragment.BaseFragment;
import com.wjika.client.login.controller.LoginActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.main.controller.MainActivity;
import com.wjika.client.market.adapter.MarketECardAdapter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.ECardEntity;
import com.wjika.client.network.entities.MarketMainEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.pay.controller.BaoziRechargeActivity;
import com.wjika.client.utils.CommonTools;
import com.wjika.client.utils.NumberFormatUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by Bob on 2016/8/15
 * 包子商城
 */
public class MarketFragment extends BaseFragment implements View.OnClickListener {

	private static final int REQUEST_CODE_ACTION_LIST = 0x10;
	private static final int REQUEST_CODE_ACTION_LIST_MORE = 0x20;
	private static final int BTN_TYPE_RECHARGE = 1;
	private static final int BTN_TYPE_LOGIN = 2;
	private static final int RECYCLE_VIEW_COLUMN_NUM = 2;

	private View mView;
	@ViewInject(R.id.toolbar_title)
	private TextView btnWithArrow;
	@ViewInject(R.id.baozi_recycleview)
	private RefreshRecyclerView recyclerView;//包子列表
	@ViewInject(R.id.ecard_item_layout)
	private LinearLayout linearLayout;

	private TextView tvNoLoginInfo;//未登录展示信息
	private TextView tvSteambunCount;//包子的数量
	private TextView tvBaoziUnit;//个包子
	private Button btnRecharge; //立即充值按钮
	private View vSteamBun;//包子父view，点击可跳转到包子

	private static long mLoadTime = 0;
	private MarketECardAdapter eCardAdapter;
	private MarketMainEntity marketData;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.frag_market_main, null);
			ViewInjectUtils.inject(this, mView);
			mLoadTime = 0;//只要mView销毁，则需要重新加载数据
			initView();
		}
		ViewGroup parent = (ViewGroup) mView.getParent();
		if (parent != null) {
			parent.removeView(mView);
		}
		return mView;
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		setHeadByLogin(UserCenter.isLogin(this.getActivity()));
	}

	private void initView() {
		recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), RECYCLE_VIEW_COLUMN_NUM));
		recyclerView.setMode(RefreshRecyclerView.Mode.BOTH);
		recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
			@Override
			public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
				if (parent.getLayoutManager() instanceof GridLayoutManager) {
					GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
					int spanCount = gridLayoutManager.getSpanCount();//设置的列数
					int headerSize = recyclerView.getHeaderSize();//头部数量
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
		recyclerView.setOnRefreshAndLoadMoreListener(new RefreshRecyclerView.OnRefreshAndLoadMoreListener() {
			@Override
			public void onRefresh() {
				loadData(false);
			}

			@Override
			public void onLoadMore() {
				loadData(true);
			}
		});
		View headView = LayoutInflater.from(getActivity()).inflate(R.layout.view_market_main, null);
		tvNoLoginInfo = (TextView) headView.findViewById(R.id.tv_no_login_info);
		tvSteambunCount = (TextView) headView.findViewById(R.id.tv_steam_bun_count);
		tvBaoziUnit = (TextView) headView.findViewById(R.id.tv_steam_bun_unit);
		btnRecharge = (Button) headView.findViewById(R.id.btn_recharge);
		vSteamBun = headView.findViewById(R.id.v_baozi);

		headView.findViewById(R.id.tv_lift_water).setOnClickListener(this);
		headView.findViewById(R.id.tv_lift_ele).setOnClickListener(this);
		headView.findViewById(R.id.tv_lift_fire).setOnClickListener(this);
		vSteamBun.setOnClickListener(this);
		btnRecharge.setOnClickListener(this);

		recyclerView.addHeaderView(headView);

		btnWithArrow.setText(R.string.market_baozi_title);
		btnWithArrow.setVisibility(View.VISIBLE);

		if (!UserCenter.isLogin(this.getActivity())) {//如果没有登录在create时加载数据，登录后在onresume中加载数据
			initLoadingView(this, mView);
			setLoadingStatus(LoadingStatus.LOADING);
			loadData(false);
		}
	}

	private void setHeadByLogin(boolean isLogin) {
		vSteamBun.setEnabled(isLogin);
		if (isLogin) {
			tvSteambunCount.setVisibility(View.VISIBLE);
			tvBaoziUnit.setVisibility(View.VISIBLE);
			tvNoLoginInfo.setVisibility(View.GONE);
			btnRecharge.setText(R.string.card_current_charge);
			btnRecharge.setTag(BTN_TYPE_RECHARGE);
		} else {
			tvSteambunCount.setVisibility(View.GONE);
			tvBaoziUnit.setVisibility(View.GONE);
			tvNoLoginInfo.setVisibility(View.VISIBLE);
			btnRecharge.setText(R.string.person_login_now);
			btnRecharge.setTag(BTN_TYPE_LOGIN);
		}
	}

	private void loadData(boolean isMore) {
		if (isMore) {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put(Constants.TOKEN, UserCenter.getToken(getActivity()));
			params.put("pageSize", String.valueOf(20));
			params.put("pageNum", String.valueOf(eCardAdapter.getPage() + 1));
			requestHttpData(Constants.Urls.URL_POST_EMARKET_MAIN, REQUEST_CODE_ACTION_LIST_MORE, FProtocol.HttpMethod.POST, params);
		} else {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put(Constants.TOKEN, UserCenter.getToken(getActivity()));
			params.put("pageSize", String.valueOf(20));
			params.put("pageNum", String.valueOf(1));
			requestHttpData(Constants.Urls.URL_POST_EMARKET_MAIN, REQUEST_CODE_ACTION_LIST, FProtocol.HttpMethod.POST, params);
		}
	}

	@Override
	public void success(int requestCode, String data) {
		super.success(requestCode, data);
		mLoadTime = System.currentTimeMillis();
		setLoadingStatus(LoadingStatus.GONE);
		if (recyclerView != null) {
			recyclerView.reSetStatus();
		}
		if (data != null) {
			marketData = Parsers.getMarketMainEntity(data);
		} else {
			setLoadingStatus(LoadingStatus.EMPTY);
		}

		switch (requestCode) {
			case REQUEST_CODE_ACTION_LIST:
				if (marketData != null) {
					//包子个数
					tvSteambunCount.setText(NumberFormatUtil.formatBun(Double.parseDouble(marketData.getMarketUserEntity().getBalance())));
					//电子卡列表
					if (marketData.getMarketECardEntity() != null && marketData.getMarketECardEntity().geteCardList().size() > 0) {
						List<ECardEntity> ecardList = marketData.getMarketECardEntity().geteCardList();
						eCardAdapter = new MarketECardAdapter(MarketFragment.this.getActivity(), ecardList, this);
						recyclerView.setAdapter(eCardAdapter);
						if (marketData.getMarketECardEntity().getPageNum() < marketData.getMarketECardEntity().getPages()) {
							recyclerView.setCanAddMore(true);
						} else {
							recyclerView.setCanAddMore(false);
						}
					} else {//如果电子卡列表为空时
						recyclerView.setVisibility(View.GONE);
					}
				} else {
					setLoadingStatus(LoadingStatus.EMPTY);
				}
				break;
			case REQUEST_CODE_ACTION_LIST_MORE:
				if (marketData != null) {
					//包子个数
					tvSteambunCount.setText(NumberFormatUtil.formatBun(Double.parseDouble(marketData.getMarketUserEntity().getBalance())));
					//电子卡列表
					if (marketData.getMarketECardEntity().geteCardList() != null && marketData.getMarketECardEntity().geteCardList().size() > 0) {
						List<ECardEntity> ecardList = marketData.getMarketECardEntity().geteCardList();
						eCardAdapter.addDatas(ecardList);
						if (marketData.getMarketECardEntity().getPageNum() < marketData.getMarketECardEntity().getPages()) {
							recyclerView.setCanAddMore(true);
						} else {
							recyclerView.setCanAddMore(false);
						}
					}
				}
				break;
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
		if (recyclerView != null) {
			recyclerView.reSetStatus();
		}
		if (REQUEST_CODE_ACTION_LIST == requestCode) {
			setLoadingStatus(LoadingStatus.RETRY);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		AnalysisUtil.onEvent(getActivity(), "Android_vie_Market");
		setHeadByLogin(UserCenter.isLogin(this.getActivity()));
		if (System.currentTimeMillis() - mLoadTime > 10 * 1000 && UserCenter.isLogin(this.getActivity())) {
			initLoadingView(this, mView);
			setLoadingStatus(LoadingStatus.LOADING);
			loadData(false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.loading_layout:
				setLoadingStatus(LoadingStatus.LOADING);
				loadData(false);
				break;
			case R.id.tv_lift_water:
				break;
			case R.id.tv_lift_ele:
				break;
			case R.id.tv_lift_fire:
				break;
			case R.id.v_baozi:
				AnalysisUtil.onEvent(getContext(), "Android_act_MyBun");
				Intent baoziIntent = new Intent(getActivity(), MyBaoziActivity.class);
				startActivity(baoziIntent);
				break;
			case R.id.btn_recharge:
				AnalysisUtil.onEvent(getContext(), "Android_act_instantRecharge");
				int type = (int) v.getTag();
				if (type == BTN_TYPE_RECHARGE) {
					toRecharge();
				} else if (type == BTN_TYPE_LOGIN) {
					Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
					startActivity(loginIntent);
				}
				break;
			case R.id.ecard_item_layout:
				AnalysisUtil.onEvent(getContext(), "Android_act_Ecardclick");
				ECardEntity entity = (ECardEntity) v.getTag();
				Intent intent = new Intent(MarketFragment.this.getActivity(), ECardDetailActivity.class);
				intent.putExtra(ECardDetailActivity.EXTRA_TITLE, entity.getName());
				intent.putExtra(ECardDetailActivity.EXTRA_ECARD_ID, entity.getId());
				startActivity(intent);
				break;
		}
	}

	private void toRecharge() {
		if (marketData.getMarketUserEntity().isIfRecharge()) {
			Intent actionIntent = new Intent(getActivity(), BaoziRechargeActivity.class);
			startActivity(actionIntent);
		} else {
			((MainActivity) getActivity()).showLocationDialog("", getString(R.string.person_baozipay_toomany), getString(R.string.button_ok), new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((MainActivity) getActivity()).closeLocationDialog();
				}
			});
		}
	}
}