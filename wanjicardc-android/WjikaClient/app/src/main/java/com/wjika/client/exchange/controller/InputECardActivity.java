package com.wjika.client.exchange.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.ExchangeCardEntity;
import com.wjika.client.network.entities.ExchangeFacevalueEntity;
import com.wjika.client.pay.controller.ExchangeResultActivity;
import com.wjika.client.person.controller.AuthenticationActivity;
import com.wjika.client.utils.CommonTools;
import com.wjika.client.utils.InputUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;

import static com.wjika.client.exchange.controller.ExchangeActivity.EXTRA_FACE_ENTITY;
import static com.wjika.client.exchange.controller.ExchangeActivity.EXTRA_LIST_ENTITY;

/**
 * Created by liuzhichao on 2016/11/29.
 * 卡兑换-输入卡号密码界面
 */
public class InputECardActivity extends ToolBarActivity implements View.OnClickListener {

	public static final int REQUEST_NET_SUBMIT_CARD = 1;

	@ViewInject(R.id.input_ecard_color)
	private CardView inputEcardColor;
	@ViewInject(R.id.input_ecard_logo)
	private SimpleDraweeView inputEcardLogo;
	@ViewInject(R.id.input_ecard_no)
	private EditText inputEcardNo;
	@ViewInject(R.id.input_ecard_pwd)
	private EditText inputEcardPwd;
	@ViewInject(R.id.input_ecard_confirm)
	private View inputEcardConfirm;

	private String ecardNo;
	private String ecardPWD;
	private AlertDialog alertDialog;
	private ExchangeCardEntity cardEntity;
	private ExchangeFacevalueEntity facevalueEntity;

	public static void startInputECardActivity(Context context, Parcelable listEntity, Parcelable faceEntity) {
		Intent intent = new Intent(context, InputECardActivity.class);
		intent.putExtra(ExchangeActivity.EXTRA_LIST_ENTITY, listEntity);
		intent.putExtra(ExchangeActivity.EXTRA_FACE_ENTITY, faceEntity);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_input_ecard);
		ViewInjectUtils.inject(this);
		initView();
	}

	private void initView() {
		setLeftTitle("输入卡号卡密");
		cardEntity = getIntent().getParcelableExtra(EXTRA_LIST_ENTITY);
		facevalueEntity = getIntent().getParcelableExtra(EXTRA_FACE_ENTITY);
		if (cardEntity != null) {
			inputEcardColor.setCardBackgroundColor(Color.parseColor(cardEntity.getCardColorValue()));
			if (!StringUtil.isEmpty(cardEntity.getLogoUrl()))
				inputEcardLogo.setImageURI(Uri.parse(cardEntity.getLogoUrl()));
		}
		InputUtil.editIsEmpty(inputEcardConfirm, inputEcardNo, inputEcardPwd);
		inputEcardConfirm.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.input_ecard_confirm:
				ecardNo = inputEcardNo.getText().toString().trim();
				ecardPWD = inputEcardPwd.getText().toString().trim();
				if (StringUtil.isEmpty(ecardNo)) {
					ToastUtil.shortShow(this, "请输入卡号");
					return;
				}
				if (StringUtil.isEmpty(ecardPWD)) {
					ToastUtil.shortShow(this, "请输入密码");
					return;
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				View view = View.inflate(this, R.layout.wjk_exchange_dialog, null);
				TextView dialogMsgOne = (TextView) view.findViewById(R.id.dialog_msg_one);
				TextView dialogMsgTwo = (TextView) view.findViewById(R.id.dialog_msg_two);
				dialogMsgOne.setText("卡号:" + ecardNo);
				dialogMsgTwo.setText("密码:" + ecardPWD);
				view.findViewById(R.id.alert_btn_cancel).setOnClickListener(this);
				view.findViewById(R.id.alert_btn_ok).setOnClickListener(this);
				builder.setView(view);
				alertDialog = builder.show();
				WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();
				layoutParams.width = CommonTools.dp2px(this, 270f);
				layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
				alertDialog.getWindow().setAttributes(layoutParams);
				break;
			case R.id.alert_btn_cancel:
				alertDialog.dismiss();
				break;
			case R.id.alert_btn_ok:
				alertDialog.dismiss();
				if (UserCenter.isAuthencaiton(this)) {
					if (facevalueEntity != null) {
						showProgressDialog();
						IdentityHashMap<String, String> params = new IdentityHashMap<>();
						params.put(Constants.TOKEN, UserCenter.getToken(this));
						params.put("exchangeActivityCardId", facevalueEntity.getId());
						params.put("cardNum", ecardNo);
						params.put("cardPassword", ecardPWD);
						requestHttpData(Constants.Urls.URL_POST_EXCHANGE_SUBMIT, REQUEST_NET_SUBMIT_CARD, FProtocol.HttpMethod.POST, params);
					} else {
						ToastUtil.shortShow(this, "请重新选择卡");
					}
				} else {
					startActivity(new Intent(this, AuthenticationActivity.class));
				}
				break;
		}
	}

	@Override
	protected void parseData(int requestCode, String data) {
		super.parseData(requestCode, data);
		if (REQUEST_NET_SUBMIT_CARD == requestCode) {
			Intent intent = new Intent(this, ExchangeResultActivity.class);
			intent.putExtra(EXTRA_LIST_ENTITY, cardEntity);
			intent.putExtra(EXTRA_FACE_ENTITY, facevalueEntity);
			startActivity(intent);
			finish();
		}
	}
}
