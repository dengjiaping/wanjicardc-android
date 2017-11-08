package com.wjika.client.market.controller;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.LogUtil;
import com.common.utils.StringUtil;
import com.common.viewinject.annotation.ViewInject;
import com.common.widget.FootLoadingListView;
import com.common.widget.PullToRefreshBase;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.main.controller.MainActivity;
import com.wjika.client.market.adapter.MyElectCardExtractPwdAdapter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.MyElectCardItemEntity;
import com.wjika.client.network.entities.MyElectCardPwdEntity;
import com.wjika.client.network.entities.MyElectCardPwdItemEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.utils.ExitManager;
import com.wjika.client.utils.ImageUtils;
import com.wjika.client.utils.NumberFormatUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by ZHXIA on 2016/8/22
 * 提取电子卡详情页
 */
public class ExtractElectCardDetailActivity extends ToolBarActivity implements View.OnClickListener {

	public static final String EXTRA_FROM = "extra_from";
	private int REQUEST_GET_MY_ELECT_CARD_EXTRACT_CODE_MORE = 0x1;
	private int REQUEST_GET_MY_ELECT_CARD_EXTRACT_CODE = 0x2;
	private static final int REQUEST_PAY_PWD_SALT_CODE = 0x3;

	@ViewInject(R.id.extract_electcard_pwd_listview)
	private FootLoadingListView extractElectcardPwdListview;
	private CardView extractElectcardBg;
	private SimpleDraweeView extractElectcardLogo;
	private TextView extractElectcardFacevalue;
	private TextView extractElectcardTitle;
	private TextView extractElectcardNum;

	private MyElectCardExtractPwdAdapter myElectcardExtractpwdAdapter;
	private String cardItemId;
	private String orderNo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_extract_electcard_list);
		ViewInjectUtils.inject(this);
		initLayoutView();
		initView();
		initLoadingView(this);
		setLoadingStatus(LoadingStatus.LOADING);
		loadData(false);
	}

	private void initLayoutView() {
		View actExtractElectcardDetail = this.getLayoutInflater().inflate(R.layout.act_extract_electcard_detail, null);
		initLayoutViewId(actExtractElectcardDetail);
		extractElectcardPwdListview.getRefreshableView().addHeaderView(actExtractElectcardDetail);
		extractElectcardPwdListview.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
		extractElectcardPwdListview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData(false);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData(true);
			}
		});
	}

	private void initLayoutViewId(View v) {
		extractElectcardBg = (CardView) v.findViewById(R.id.extract_electcard_bg);
		extractElectcardLogo = (SimpleDraweeView) v.findViewById(R.id.extract_electcard_logo);
		extractElectcardFacevalue = (TextView) v.findViewById(R.id.extract_electcard_facevalue);
		extractElectcardTitle = (TextView) v.findViewById(R.id.extract_electcard_title);
		extractElectcardNum = (TextView) v.findViewById(R.id.extract_electcard_num);
	}

    private void initView() {
        setLeftTitle("提取电子卡");
        if (getIntent() != null) {
	        MyElectCardItemEntity cardOrderItemEntity = getIntent().getParcelableExtra("cardOrderItemEntity");
            String cardLogo = cardOrderItemEntity.getMyCardlogoUrl();
            String cardPrice = cardOrderItemEntity.getMyCardFacePrice();
            String cardName = cardOrderItemEntity.getMyCardName();
            int cardNum = cardOrderItemEntity.getMyCardCount();
            cardItemId = cardOrderItemEntity.getMyCardItemId();
            orderNo = cardOrderItemEntity.getOrderNo();
            if (!StringUtil.isEmpty(cardOrderItemEntity.getBgcolor())) {
	            extractElectcardBg.setCardBackgroundColor(Color.parseColor(cardOrderItemEntity.getBgcolor()));
            } else {
	            extractElectcardBg.setCardBackgroundColor(Color.parseColor("#487AE0"));
            }
            if (!TextUtils.isEmpty(cardLogo) && !cardLogo.contains("?")) {
                ImageUtils.setSmallImg(extractElectcardLogo,cardLogo);
            }
	        extractElectcardFacevalue.setText(res.getString(R.string.money, NumberFormatUtil.formatBun(cardPrice)));
            extractElectcardTitle.setText(cardName);
            extractElectcardNum.setText("数量：" + cardNum);
        }
    }

	private void loadData(boolean loadMore) {
		if (loadMore) {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put(Constants.TOKEN, UserCenter.getToken(this));
			params.put("pageSize", Constants.PAGE_SIZE + "");
			params.put("pageNum", myElectcardExtractpwdAdapter.getPage() + 1 + "");
			params.put("thirdCardId", cardItemId);
			params.put("payOrderNo", orderNo);
			LogUtil.i("extractElectParams", params + "");
			requestHttpData(Constants.Urls.URL_POST_MY_ELECT_CARD_EXTRACT_LIST,
					REQUEST_GET_MY_ELECT_CARD_EXTRACT_CODE_MORE,
					FProtocol.HttpMethod.POST,
					params);
		} else {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put(Constants.TOKEN, UserCenter.getToken(this));
			params.put("pageSize", Constants.PAGE_SIZE + "");
			params.put("pageNum", 1 + "");
			params.put("thirdCardId", cardItemId);
			params.put("payOrderNo", orderNo);
			LogUtil.i("extractElectParams", params + "");
			requestHttpData(Constants.Urls.URL_POST_MY_ELECT_CARD_EXTRACT_LIST,
					REQUEST_GET_MY_ELECT_CARD_EXTRACT_CODE,
					FProtocol.HttpMethod.POST,
					params);
		}
	}

	@Override
	public void success(int requestCode, String data) {
		super.success(requestCode, data);
		if (REQUEST_GET_MY_ELECT_CARD_EXTRACT_CODE == requestCode) {
			extractElectcardPwdListview.onRefreshComplete();
			if (data != null) {
				MyElectCardPwdEntity myElectCardPwdLists = Parsers.getMyElectCardExtractList(data);
				List<MyElectCardPwdItemEntity> myElectCardItemLists = myElectCardPwdLists.getMyElectCardPwdItemEntity();
				if (myElectCardItemLists != null && myElectCardItemLists.size() > 0) {
					setLoadingStatus(LoadingStatus.GONE);
					myElectcardExtractpwdAdapter = new MyElectCardExtractPwdAdapter(this, myElectCardItemLists);
					extractElectcardPwdListview.setAdapter(myElectcardExtractpwdAdapter);
					if (myElectCardPwdLists.getPages() > 1) {
						extractElectcardPwdListview.setCanMoreAndUnReFresh(true);
					} else {
						extractElectcardPwdListview.setCanMoreAndUnReFresh(false);
					}
				} else {
					emptyStatus();
				}
			} else {
				emptyStatus();
			}
		} else if (REQUEST_GET_MY_ELECT_CARD_EXTRACT_CODE_MORE == requestCode) {
			extractElectcardPwdListview.onRefreshComplete();
			if (data != null) {
				MyElectCardPwdEntity myElectCardPwdLists = Parsers.getMyElectCardExtractList(data);
				myElectcardExtractpwdAdapter.addDatas(myElectCardPwdLists.getMyElectCardPwdItemEntity());
				if (myElectCardPwdLists.getPages() <= myElectcardExtractpwdAdapter.getPage()) {
					extractElectcardPwdListview.setCanMoreAndUnReFresh(false);
				}
			} else {
				emptyStatus();
			}
		} else if (REQUEST_PAY_PWD_SALT_CODE == requestCode) {
			if (data != null) {
				String paypwdSalt = Parsers.getPaypwdSalt(data);
				if (!StringUtil.isEmpty(paypwdSalt)) {
					UserCenter.setUserPaypwdSalt(this, paypwdSalt);
				}
			}
		}
	}

	private void emptyStatus() {
		setLoadingStatus(LoadingStatus.EMPTY);
		myElectcardExtractpwdAdapter = new MyElectCardExtractPwdAdapter(this, new ArrayList<MyElectCardPwdItemEntity>());
		extractElectcardPwdListview.setAdapter(myElectcardExtractpwdAdapter);
		extractElectcardPwdListview.setCanAddMore(false);
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
		extractElectcardPwdListview.onRefreshComplete();
		if (REQUEST_GET_MY_ELECT_CARD_EXTRACT_CODE == requestCode) {
			setLoadingStatus(LoadingStatus.RETRY);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.loading_layout) {
			setLoadingStatus(LoadingStatus.LOADING);
			loadData(false);
		}
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		ExitManager.instance.toActivity(MainActivity.class);
	}
}
