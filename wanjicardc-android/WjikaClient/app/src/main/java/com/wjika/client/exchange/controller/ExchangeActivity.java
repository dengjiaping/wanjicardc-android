package com.wjika.client.exchange.controller;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.utils.StringUtil;
import com.common.view.GridViewForInner;
import com.common.viewinject.annotation.ViewInject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.BaseActivity;
import com.wjika.client.exchange.adapter.FaceValueAdapter;
import com.wjika.client.network.entities.ExchangeCardEntity;
import com.wjika.client.network.entities.ExchangeFacevalueEntity;
import com.wjika.client.utils.ImageUtils;
import com.wjika.client.utils.NumberFormatUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.ArrayList;

/**
 * 卡兑换面值弹窗界面
 * Created by zhaoweiwei on 2016/12/1.
 */

public class ExchangeActivity extends BaseActivity implements View.OnClickListener {

	public static final String EXTRA_LIST_ENTITY = "extra_list_entity";
	public static final String EXTRA_FACE_ENTITY = "extra_face_entity";
	public static final String EXTRA_CARD_LIST = "extra_card_list";

	@ViewInject(R.id.exchange_carditem_cardview)
	private CardView exchangeCardBg;
	@ViewInject(R.id.exchange_carditem_logo)
	private SimpleDraweeView cardLogo;
	@ViewInject(R.id.exchange_exchange_close)
	private ImageView cardClose;
	@ViewInject(R.id.exchange_exchange_name)
	private TextView cardName;
	@ViewInject(R.id.exchange_exchange_facevalue)
	private TextView cardFacevalue;
	@ViewInject(R.id.exchange_exchange_bunnum)
	private TextView cardBunnum;
	@ViewInject(R.id.exchange_exchange_grid)
	private GridViewForInner gridview;
	@ViewInject(R.id.exchange_exchange_exchange)
	private TextView cardExchange;

	private ArrayList<ExchangeFacevalueEntity> mDatas;
	private FaceValueAdapter adapter;
	private ExchangeCardEntity entity;
	private ExchangeFacevalueEntity itemEntity;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exchange_exchange_act);
		ViewInjectUtils.inject(this);
		initStyle();
		initView();
		initGridView();
		setData();
	}

	private void setData() {
		if (mDatas.size() > 0) {
			itemEntity = mDatas.get(0);
			itemEntity.setChecked(true);
			cardFacevalue.setText(getString(R.string.person_order_detail_buy_amount,NumberFormatUtil.formatBun(itemEntity.getFaceValue())));
			cardBunnum.setText(getString(R.string.exchange_can_exchange_baozi,NumberFormatUtil.formatBun(itemEntity.getBunNum())));
		}
		adapter = new FaceValueAdapter(this, mDatas);
		gridview.setAdapter(adapter);
	}


	private void initView() {
		entity = getIntent().getParcelableExtra(AllCardActivity.EXTRA_EXCHANGE_CARD);
		mDatas = getIntent().getParcelableArrayListExtra(EXTRA_CARD_LIST);
		cardName.setText(entity.getCardName());
		exchangeCardBg.setCardBackgroundColor(Color.parseColor(entity.getCardColorValue()));
		String url = entity.getLogoUrl();
		if (!StringUtil.isEmpty(url)) {
			ImageUtils.setSmallImg(cardLogo, url);
		}
		cardClose.setOnClickListener(this);
		cardExchange.setOnClickListener(this);
	}

	private void initGridView() {
		gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				itemEntity = mDatas.get(i);
				for (ExchangeFacevalueEntity entity : mDatas) {
					entity.setChecked(false);
				}
				itemEntity.setChecked(true);
				adapter.notifyDataSetChanged();
				cardFacevalue.setText(getString(R.string.person_order_detail_buy_amount,NumberFormatUtil.formatBun(itemEntity.getFaceValue())));
				cardBunnum.setText(getString(R.string.exchange_can_exchange_baozi,NumberFormatUtil.formatBun(itemEntity.getBunNum())));
			}
		});
	}



	private void initStyle() {
		Window window = getWindow();
		window.setGravity(Gravity.BOTTOM);
		overridePendingTransition(R.anim.actionsheet_dialog_in, 0);
		WindowManager.LayoutParams wl = window.getAttributes();
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		this.onWindowAttributesChanged(wl);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, R.anim.actionsheet_dialog_out);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.exchange_exchange_close:
				finish();
				break;
			case R.id.exchange_exchange_exchange:
				InputECardActivity.startInputECardActivity(this, entity, itemEntity);
				finish();
				break;
		}
	}
}
