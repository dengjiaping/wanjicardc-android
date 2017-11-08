package com.wjika.client.person.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.common.widget.FootLoadingListView;
import com.common.widget.PullToRefreshBase;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.CouponEntity;
import com.wjika.client.network.entities.CouponPageEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.person.adapter.CouponListAdapter;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by Liu_ZhiChao on 2015/9/7 18:31.
 * 我的优惠券
 */
public class CouponActivity extends ToolBarActivity implements OnClickListener, PullToRefreshBase.OnRefreshListener2<ListView> {

	private static final int REQUEST_PERSON_COUPON_CODE = 100;
	private static final int REQUEST_PERSON_COUPON_CODE_MORE = 200;
	public static final String INTETNT_COUPON_USABLENUM = "couponUsableNum";
	private static final int REQUEST_PERSON_COUPON_ADD = 300;

	@ViewInject(R.id.person_coupon_list)
	private FootLoadingListView listView;
	@ViewInject(R.id.person_coupon_explain)
	private TextView mTextExplain;
	@ViewInject(R.id.loading_img_empty)
	private ImageView loadingImgEmpty;
	@ViewInject(R.id.loading_txt_empty)
	private TextView loadingTxtEmpty;

	private CouponPageEntity couponPageEntity;
	private CouponListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_act_coupon);
		initLoadingView(this);
		setLoadingStatus(LoadingStatus.LOADING);
		ViewInjectUtils.inject(this);
		initView();
		loadData(false);
	}

	private void initView() {
		setLeftTitle(getString(R.string.person_my_coupon));
		mBtnTitleRight.setImageResource(R.drawable.coupon_add);
		mBtnTitleRight.setVisibility(View.VISIBLE);
		listView.setEnabled(false);
		mTextExplain.setOnClickListener(this);
		mBtnTitleRight.setOnClickListener(this);
		listView.setOnRefreshListener(this);
	}

	@Override
	public void setLeftTitle(String title) {
		super.setLeftTitle(title);
		mBtnTitleLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != couponPageEntity) {
					setResult(RESULT_OK,getIntent().putExtra(INTETNT_COUPON_USABLENUM,couponPageEntity.getUseableNum()));
				}
				finish();
			}
		});
	}

	private void loadData(Boolean isMore) {
		if (isMore) {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put(Constants.TOKEN, UserCenter.getToken(this));
			params.put("pageSize", Constants.PAGE_SIZE + "");
			params.put("pageNum",adapter.getPage() + 1+"");
			params.put("couponType", "");
			requestHttpData(Constants.Urls.URL_GET_COUPON_LIST, REQUEST_PERSON_COUPON_CODE_MORE, FProtocol.HttpMethod.POST, params);
		} else {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put(Constants.TOKEN, UserCenter.getToken(this));
			params.put("pageSize",Constants.PAGE_SIZE + "");
			params.put("pageNum","1");
			params.put("couponType", "");
			requestHttpData(Constants.Urls.URL_GET_COUPON_LIST, REQUEST_PERSON_COUPON_CODE, FProtocol.HttpMethod.POST, params);
		}
	}

	@Override
	protected void noLogin() {
		this.finish();
	}

	@Override
	protected void parseData(int requestCode, String data) {
		super.parseData(requestCode, data);
		listView.setOnRefreshComplete();
		couponPageEntity = Parsers.getCouponList(data);
		switch (requestCode) {
			case REQUEST_PERSON_COUPON_CODE: {
				List<CouponEntity> couponEntityList = couponPageEntity.getCouponEntities();
				if (couponEntityList != null && couponEntityList.size() > 0) {
					adapter = new CouponListAdapter(this,couponEntityList);
					listView.setAdapter(adapter);
					if (couponPageEntity.getPages() > 1) {
						listView.setCanAddMore(true);
					} else {
						listView.setCanAddMore(false);
					}
				} else {
					//空数据时不允许重复点击请求
					mLayoutLoading.setOnClickListener(null);
					loadingImgEmpty.setImageResource(R.drawable.ic_empty_coupon);
					loadingTxtEmpty.setText(getString(R.string.person_coupon_nocoupon));
					setLoadingStatus(LoadingStatus.EMPTY);
				}
				break;
			}
			case REQUEST_PERSON_COUPON_CODE_MORE:{
				List<CouponEntity> couponEntityList = couponPageEntity.getCouponEntities();
				if (couponEntityList != null && couponEntityList.size() > 0) {
					adapter.addDatas(couponEntityList);
					if (couponPageEntity.getPageNum() < couponPageEntity.getPages()) {
						listView.setCanAddMore(true);
					} else {
						listView.setCanAddMore(false);
					}
				}
				break;
			}
			case REQUEST_PERSON_COUPON_ADD:
				ToastUtil.shortShow(this,getString(R.string.person_coupon_add_sucess));
				loadData(false);
				break;
			default:
				break;
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		switch (requestCode) {
			case REQUEST_PERSON_COUPON_CODE:
				setLoadingStatus(LoadingStatus.RETRY);
				ToastUtil.shortShow(this,errorMessage);
				break;
			case REQUEST_PERSON_COUPON_ADD:
				closeProgressDialog();
				ToastUtil.shortShow(this,errorMessage);
				break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.right_button:
				showAlertDialog(getString(R.string.person_coupon_add_coupon), getString(R.string.person_coupon_input_number), getString(R.string.wjika_cancel), getString(R.string.button_ok), new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						closeDialog();
					}
				}, new AlertEtClickListenner() {
					@Override
					public void onClick(View v, EditText editText) {
						String couponNum = editText.getText().toString();
						if (!TextUtils.isEmpty(couponNum)) {
							closeDialog();
							showProgressDialog();
							IdentityHashMap<String, String> params = new IdentityHashMap<>();
							params.put(Constants.TOKEN, UserCenter.getToken(CouponActivity.this));
							params.put("code",couponNum);
							requestHttpData(Constants.Urls.URL_ADD_COUPON, REQUEST_PERSON_COUPON_ADD, FProtocol.HttpMethod.POST, params);
						} else {
							ToastUtil.shortShow(CouponActivity.this,getString(R.string.person_coupon_input_notnull));
						}
					}
				});
				break;
			case R.id.person_coupon_explain:
				startActivity(new Intent(CouponActivity.this,CouponExplainActivity.class));
				break;
			case R.id.loading_layout:
				setLoadingStatus(LoadingStatus.LOADING);
				loadData(false);
			default:
				break;
		}
	}

	@Override
	public void onBackPressed() {
		if (null != couponPageEntity) {
			setResult(RESULT_OK,getIntent().putExtra(INTETNT_COUPON_USABLENUM,couponPageEntity.getUseableNum()));
		}
		super.onBackPressed();
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadData(false);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadData(true);
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		finish();
	}
}
