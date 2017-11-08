package com.wjika.client.pay.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.utils.AnalysisUtil;
import com.common.utils.DeviceUtil;
import com.common.viewinject.annotation.ViewInject;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.cardpackage.controller.CardPackageActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.main.controller.MainActivity;
import com.wjika.client.network.entities.PrivilegeEntity;
import com.wjika.client.utils.CommonTools;
import com.wjika.client.utils.ExitManager;
import com.wjika.client.utils.ImageUtils;
import com.wjika.client.utils.NumberFormatUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.ArrayList;

import static com.wjika.client.main.controller.MainActivity.REQUEST_ACT_PAYPWD_CODE;

/**
 * Created by Liu_ZhiChao on 2015/9/15 17:49.
 * 支付结果界面
 */
public class PayResultActivity extends ToolBarActivity implements View.OnClickListener {

	public static final String EXTRA_PRIVILEGE_LIST = "extra_privilege_list";
	public static final String EXTRA_NAME = "extra_name";
	public static final String EXTRA_CHANNEL = "extra_channel";
	public static final String EXTRA_TYPE = "extra_type";
	public static final String EXTRA_REAL_PAY = "extra_real_pay";
	public static final String EXTRA_FACE_VALUE = "extra_face_value";

	@ViewInject(R.id.pay_result_title)
	private TextView payResultTitle;
	@ViewInject(R.id.tv_card_name)
	private TextView payResultName;
	@ViewInject(R.id.tv_card_amount)
	private TextView payResultAmount;
	@ViewInject(R.id.tv_face_value)
	private TextView cardValue;
	@ViewInject(R.id.pay_result_left_btn)
	private Button payResultLeftBtn;
	@ViewInject(R.id.pay_result_right_btn)
	private Button payResultRightBtn;
	@ViewInject(R.id.ll_privilege)
	private LinearLayout llPrivilegeViewGroup;
	@ViewInject(R.id.rl_privilege)
	private View rlPrivilege;
	@ViewInject(R.id.vLinePrivilege)
	private View vLinePrivilege;

	private String type;
	private ArrayList<PrivilegeEntity> privilegeEntityList;//从上个页面取

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitManager.instance.addPayActivity(this);
		setContentView(R.layout.person_act_pay_result);
		ViewInjectUtils.inject(this);
		initView();
	}

	private void initView() {
		setLeftTitle("支付成功");

		Intent extraIntent = getIntent();
		type = extraIntent.getStringExtra(EXTRA_TYPE);
		String cardName = extraIntent.getStringExtra(EXTRA_NAME);
		double realPay = extraIntent.getDoubleExtra(EXTRA_REAL_PAY, 0);
		String faceValue = extraIntent.getStringExtra(EXTRA_FACE_VALUE);
		privilegeEntityList = extraIntent.getParcelableArrayListExtra(EXTRA_PRIVILEGE_LIST);

		setPrivilegeView();
		setPayResultUIByType(type);

		payResultName.setText(cardName);
		payResultAmount.setText(getString(R.string.person_order_detail_buy_amount, NumberFormatUtil.formatMoney(realPay)));

		String sBuyCardFaceValue = String.format(res.getString(R.string.buy_card_face_value), faceValue);
		cardValue.setText(sBuyCardFaceValue);

		payResultLeftBtn.setOnClickListener(this);
		payResultRightBtn.setOnClickListener(this);
	}

	private void setPayResultUIByType(String type) {
		if (CardRechargeActivity.TYPE_BUY_CARD.equals(type)) {
			payResultTitle.setText(getResources().getString(R.string.pay_buy_card_result_title));
			payResultLeftBtn.setText(getString(R.string.card_charge_to_card));
		} else {
			payResultLeftBtn.setText(getString(R.string.goto_consume));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.pay_result_left_btn:
				if (CardRechargeActivity.TYPE_BUY_CARD.equals(type)) {
					AnalysisUtil.onEvent(this, "Android_act_PaygotoCardBag");
				} else {
					AnalysisUtil.onEvent(this, "Android_act_RechargegotoCardBag");
				}
				//购卡成功和充值成功都跳到卡包
				if (UserCenter.issetNopwd(this)) {
					startActivity(new Intent(this, CardPackageActivity.class));
					finish();
				} else {
					Intent intent = new Intent(this, PayVerifyPWDActivity.class);
					intent.putExtra(PayVerifyPWDActivity.FROM_EXTRA, PayVerifyPWDActivity.PAYCONSUMES);
					startActivityForResult(intent, REQUEST_ACT_PAYPWD_CODE);
				}
				break;
			case R.id.pay_result_right_btn:
				if (CardRechargeActivity.TYPE_BUY_CARD.equals(type)) {
					AnalysisUtil.onEvent(this, "Android_act_PaygotoHome");
				} else {
					AnalysisUtil.onEvent(this, "Android_act_RechargegotoHome");
				}
				startActivity(new Intent(this, MainActivity.class));
				break;
		}
	}

	/**
	 * 设置特权UI
	 */
	private void setPrivilegeView() {
		if (privilegeEntityList != null && privilegeEntityList.size() > 0) {
			rlPrivilege.setVisibility(View.VISIBLE);
			vLinePrivilege.setVisibility(View.VISIBLE);
			llPrivilegeViewGroup.removeAllViews();
			int defaultPadding = this.getResources().getDimensionPixelSize(R.dimen.wjika_client_comment_marginright) * 2;
			int width = DeviceUtil.getWidth(this) - CommonTools.dp2px(this, 44) - defaultPadding;
			int privilegeSize = this.getResources().getDimensionPixelSize(R.dimen.wjika_client_privilege_size);
			int privilegePadding = this.getResources().getDimensionPixelSize(R.dimen.wjika_client_privilege_padding);
			int columsNum = width / (privilegeSize + privilegePadding);
			if (columsNum > privilegeEntityList.size()) {
				columsNum = privilegeEntityList.size();
			}

			for (int i = 0; i < columsNum; i++) {
				SimpleDraweeView view = new SimpleDraweeView(this);
				LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(privilegeSize, privilegeSize);
				view.setLayoutParams(imgParams);
				GenericDraweeHierarchyBuilder builder =
						new GenericDraweeHierarchyBuilder(getResources());
				GenericDraweeHierarchy hierarchy = builder
						.setFadeDuration(300)
						.setPlaceholderImage(this.getResources().getDrawable(R.drawable.def_privilege_img))
						.build();
				view.setHierarchy(hierarchy);
				view.setId(i);
				View blankView = new View(this);
				blankView.setId(i + 1000);
				ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(privilegePadding, privilegePadding);
				blankView.setLayoutParams(layoutParams);
				String url = privilegeEntityList.get(i).getImgPath();
				if (!TextUtils.isEmpty(url) && !url.contains("?")) {
					ImageUtils.setSmallImg(view,url);
				}
				llPrivilegeViewGroup.addView(view);
				llPrivilegeViewGroup.addView(blankView);
			}
		} else {
			rlPrivilege.setVisibility(View.GONE);
			vLinePrivilege.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode && REQUEST_ACT_PAYPWD_CODE == requestCode) {
			startActivity(new Intent(this, CardPackageActivity.class));
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		ExitManager.instance.closePayActivity();
	}
}
