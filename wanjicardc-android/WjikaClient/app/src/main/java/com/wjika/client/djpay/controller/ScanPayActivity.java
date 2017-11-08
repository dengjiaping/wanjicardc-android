package com.wjika.client.djpay.controller;

import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import com.common.utils.StringUtil;
import com.common.viewinject.annotation.ViewInject;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.wjika.cardagent.client.R;
import com.wjika.client.djpay.base.BaseDJActivity;
import com.wjika.client.djpay.utils.DJUserCenter;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.DJPayCodeEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.utils.CommonTools;
import com.wjika.client.utils.NumberFormatUtil;
import com.wjika.client.utils.ViewInjectUtils;

import javax.annotation.Nullable;

/**
 * Created by liuzhichao on 2016/11/23.
 * 斗金-付款码界面
 */
public class ScanPayActivity extends BaseDJActivity implements View.OnClickListener{

	private static final int REQUEST_NET_QR_CODE = 1;

	@ViewInject(R.id.scan_pay_desc)
	private TextView scanPayDesc;
	@ViewInject(R.id.scan_pay_amount)
	private TextView scanPayAmount;
	@ViewInject(R.id.scan_pay_qr)
	private SimpleDraweeView scanPayQr;
	@ViewInject(R.id.scan_pay_hint)
	private TextView scanPayHint;
	@ViewInject(R.id.scan_pay_save)
	private View scanPaySave;

	private String id;
	private double amount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_scan_pay);
		ViewInjectUtils.inject(this);
		initView();
		loadData();
	}

	private void initView() {
		setLeftTitle("扫码支付");
		showBtn(false);
		id = getIntent().getStringExtra(SelectChannelActivity.EXTRA_ID);
		amount = getIntent().getDoubleExtra(SelectChannelActivity.EXTRA_AMOUNT, 0);
		scanPayAmount.setText(NumberFormatUtil.formatMoney(amount));

	}

	private void loadData() {
		showProgressDialog();
		requestHttpData(DJUserCenter.getDJUrl(this) + Constants.Urls.URL_POST_DJ_QR + "?dj=&" + Constants.TOKEN + "=" + DJUserCenter.getToken(this)
				+ "&payTypeId=" + id + "&payMoney=" + amount, REQUEST_NET_QR_CODE);
	}

	@Override
	public void success(int requestCode, String data) {
		closeProgressDialog();
		if (REQUEST_NET_QR_CODE == requestCode) {
			DJPayCodeEntity djPayCode = Parsers.getDjPayCode(data);
			if (djPayCode != null) {
				if (!StringUtil.isEmpty(djPayCode.getQrImg())) {
					scanPayQr.setController(Fresco.newDraweeControllerBuilder().setControllerListener(controllerListener).setUri(djPayCode.getQrImg()).build());
				}
				scanPayDesc.setText(djPayCode.getDesc());
				scanPayHint.setText(djPayCode.getHint());
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.scan_pay_save:
				MediaStore.Images.Media.insertImage(getContentResolver(), CommonTools.convertViewToBitmap(scanPayQr), "付款码", "");
				showAlertDialog("", "付款码已保存", "确定", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						closeDialog();
					}
				});
				break;
		}
	}

	ControllerListener<ImageInfo> controllerListener = new BaseControllerListener<ImageInfo>() {

		@Override
		public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable animatable) {
			scanPaySave.setOnClickListener(ScanPayActivity.this);
		}
	};
}
