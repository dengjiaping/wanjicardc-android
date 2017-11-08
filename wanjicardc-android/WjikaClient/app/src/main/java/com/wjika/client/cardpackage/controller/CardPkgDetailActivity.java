package com.wjika.client.cardpackage.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.AnalysisUtil;
import com.common.utils.DeviceUtil;
import com.common.viewinject.annotation.ViewInject;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.base.ui.WebViewActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.CardPkgDetailEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.pay.controller.CardRechargeActivity;
import com.wjika.client.person.controller.AuthenticationActivity;
import com.wjika.client.person.controller.ConsumptionHistoryActivity;
import com.wjika.client.store.controller.CardDetailActivity;
import com.wjika.client.store.controller.CorrelationStoreActivity;
import com.wjika.client.utils.ImageUtils;
import com.wjika.client.utils.NumberFormatUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;

/**
 * Created by Liu_Zhichao on 2016/7/10 14:58.
 * 卡包卡详情
 */
public class CardPkgDetailActivity extends ToolBarActivity implements View.OnClickListener {

	private static final int REQUEST_NET_CARD_DETAIL = 10;

	@ViewInject(R.id.card_pkg_detail_bg)
	private CardView cardPkgDetailBg;
	@ViewInject(R.id.card_pkg_detail_logo)
	private SimpleDraweeView cardPkgDetailLogo;
	@ViewInject(R.id.card_pkg_detail_name)
	private TextView cardPkgDetailName;
	@ViewInject(R.id.card_pkg_detail_balance)
	private TextView cardPkgDetailBalance;
	@ViewInject(R.id.card_pkg_detail_privilege_layout)
	private RelativeLayout cardPkgDetailPrivilegeLayout;
	@ViewInject(R.id.card_pkg_detail_privileges)
	private LinearLayout privilegeIconContainer;
	@ViewInject(R.id.card_pkg_detail_store)
	private TextView cardPkgDetailStore;
	@ViewInject(R.id.card_pkg_detail_consume)
	private TextView cardPkgDetailConsume;
	@ViewInject(R.id.card_pkg_detail_explain)
	private TextView cardPkgDetailExplain;
	@ViewInject(R.id.card_pkg_detail_desc)
	private View cardPkgDetailDesc;
	@ViewInject(R.id.card_pkg_detail_arrow)
	private ImageView cardPkgDetailArrow;
	@ViewInject(R.id.card_pkg_detail_recharge)
	private TextView cardPkgDetailRecharge;

	private String id;
	private CardPkgDetailEntity cardPkgDetail;
	private int height;
	private boolean isExtend = true;//是否展开。
	private boolean isRunAnim = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_card_pkg_detail);
		ViewInjectUtils.inject(this);
		initView();
		loadData();
	}

	private void initView() {
		setLeftTitle(getString(R.string.card_pkg_detail_title));
		id = getIntent().getStringExtra("id");
		cardPkgDetailPrivilegeLayout.setOnClickListener(this);
		cardPkgDetailStore.setOnClickListener(this);
		cardPkgDetailConsume.setOnClickListener(this);
		cardPkgDetailRecharge.setOnClickListener(this);
		cardPkgDetailDesc.setOnClickListener(this);
	}

	private void loadData() {
		IdentityHashMap<String, String> param = new IdentityHashMap<>();
		param.put("merchantCardId", id);
		param.put(Constants.TOKEN, UserCenter.getToken(this));
		requestHttpData(Constants.Urls.URL_GET_CARD_DETAILS, REQUEST_NET_CARD_DETAIL, FProtocol.HttpMethod.POST, param);
	}

	@Override
	protected void onResume() {
		super.onResume();
		AnalysisUtil.onEvent(this, "Android_vie_Mycard");
	}

	@Override
	protected void parseData(int requestCode, String data) {
		if (REQUEST_NET_CARD_DETAIL == requestCode) {
			cardPkgDetail = Parsers.getCardPkgDetail(data);
			if (cardPkgDetail != null) {
				cardPkgDetailName.setText(cardPkgDetail.getName());
				cardPkgDetailBalance.setText(getString(R.string.card_pkg_detail_balance, NumberFormatUtil.formatMoney(cardPkgDetail.getAmount())));
				ImageUtils.setSmallImg(cardPkgDetailLogo, cardPkgDetail.getImgPath());
				switch (cardPkgDetail.getImgType()) {
					case BLUE:
						cardPkgDetailBg.setCardBackgroundColor(getResources().getColor(R.color.wjika_client_card_blue));
						break;
					case RED:
						cardPkgDetailBg.setCardBackgroundColor(getResources().getColor(R.color.wjika_client_card_red));
						break;
					case ORANGE:
						cardPkgDetailBg.setCardBackgroundColor(getResources().getColor(R.color.wjika_client_card_yellow));
						break;
					case GREEN:
						cardPkgDetailBg.setCardBackgroundColor(getResources().getColor(R.color.wjika_client_card_green));
						break;
					default:
						cardPkgDetailBg.setCardBackgroundColor(getResources().getColor(R.color.wjika_client_card_blue));
						break;
				}
				cardPkgDetailExplain.setText(cardPkgDetail.getAdDesc());
				cardPkgDetailExplain.setMovementMethod(LinkMovementMethod.getInstance());
				cardPkgDetailExplain.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						cardPkgDetailExplain.getViewTreeObserver().removeGlobalOnLayoutListener(this);
						height = cardPkgDetailExplain.getHeight();
						cardPkgDetailExplain.getLayoutParams().height = height;
						cardPkgDetailExplain.requestLayout();
					}
				});
				if (cardPkgDetail.getPrivilegeEntityList().size() > 0) {
					int defaultPadding = this.getResources().getDimensionPixelSize(R.dimen.wjika_client_comment_marginright) * 2;
					int width = DeviceUtil.getWidth(this) - 44 - defaultPadding;
					int privilegeSize = this.getResources().getDimensionPixelSize(R.dimen.wjika_client_privilege_size);
					int privilegePadding = this.getResources().getDimensionPixelSize(R.dimen.wjika_client_privilege_padding);
					int columsNum = width / (privilegeSize + privilegePadding);
					if (columsNum > cardPkgDetail.getPrivilegeEntityList().size()){
						columsNum = cardPkgDetail.getPrivilegeEntityList().size();
					}

					for (int i = 0; i < columsNum; i++) {
						SimpleDraweeView privilegeIcon = new SimpleDraweeView(this);
						LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(privilegeSize, privilegeSize);
						privilegeIcon.setLayoutParams(imgParams);

						GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
						GenericDraweeHierarchy hierarchy = builder
								.setFadeDuration(300)
								.setPlaceholderImage(this.getResources().getDrawable(R.drawable.def_privilege_img))
								.build();
						privilegeIcon.setHierarchy(hierarchy);
						privilegeIcon.setId(i);
						View blankView = new View(this);
						blankView.setId(i + 1000);
						ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(privilegePadding, privilegePadding);
						blankView.setLayoutParams(layoutParams);
						String url = cardPkgDetail.getPrivilegeEntityList().get(i).getImgPath();
						if (!TextUtils.isEmpty(url) && !url.contains("?")) {
							ImageUtils.setSmallImg(privilegeIcon,url);
						}
						privilegeIconContainer.addView(privilegeIcon);
						privilegeIconContainer.addView(blankView);
					}
				} else {
					cardPkgDetailPrivilegeLayout.setVisibility(View.GONE);
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (cardPkgDetail != null) {
			switch (v.getId()) {
				case R.id.card_pkg_detail_privilege_layout:
					Intent privilegeIntent = new Intent(this, WebViewActivity.class);
					privilegeIntent.putExtra(WebViewActivity.EXTRA_FROM, WebViewActivity.FROM_CARD_PKG_DETAIL);
					privilegeIntent.putExtra(WebViewActivity.EXTRA_URL, Constants.Urls.URL_PRIVILEGE_DOMAIN + "?privilegeType=user&merchantId=" + cardPkgDetail.getMerId() + "&token=" + UserCenter.getToken(this));
					privilegeIntent.putExtra(WebViewActivity.EXTRA_TITLE, getString(R.string.card_pkg_detail_privilege));
					privilegeIntent.putExtra(WebViewActivity.EXTRA_MERID, cardPkgDetail.getMerId());
					startActivity(privilegeIntent);
					break;
				case R.id.card_pkg_detail_store:
					Intent storeIntent = new Intent(this, CorrelationStoreActivity.class);
					storeIntent.putExtra(CorrelationStoreActivity.EXTRA_MERID, cardPkgDetail.getMerId());
					startActivity(storeIntent);
					break;
				case R.id.card_pkg_detail_consume:
					AnalysisUtil.onEvent(this, "Android_act_ConsumptionRecord");
					startActivity(new Intent(this, ConsumptionHistoryActivity.class).putExtra("merId", cardPkgDetail.getMerId()));
					break;
				case R.id.card_pkg_detail_recharge:
					AnalysisUtil.onEvent(this, "Android_act_recharge");
					if (UserCenter.isAuthencaiton(this)) {
						gotoRecharge();
					} else {
						Intent intent = new Intent(this, AuthenticationActivity.class);
						intent.putExtra(CardDetailActivity.EXTRA_FROM, AuthenticationActivity.BUY_CARD_DETAIL);
						startActivityForResult(intent, CardDetailActivity.REQUEST_AUTH_CODE);
					}
					break;
				case R.id.card_pkg_detail_desc:
					if(isRunAnim) return;
					ValueAnimator valueAnimator;
					if(isExtend){
						//执行收缩动画
						valueAnimator = ValueAnimator.ofInt(height,0);
					}else {
						//执行展开动画
						valueAnimator = ValueAnimator.ofInt(0,height);
					}
					//设置动画执行的监听器
					valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator animation) {
							cardPkgDetailExplain.getLayoutParams().height = (int) animation.getAnimatedValue();
							cardPkgDetailExplain.requestLayout();
						}
					});
					valueAnimator.setDuration(350);
					valueAnimator.addListener(new Animator.AnimatorListener() {
						@Override
						public void onAnimationStart(Animator animation) {
							isRunAnim = true;
						}

						@Override
						public void onAnimationEnd(Animator animation) {
							isRunAnim = false;
						}

						@Override
						public void onAnimationCancel(Animator animation) {

						}

						@Override
						public void onAnimationRepeat(Animator animation) {

						}
					});
					valueAnimator.start();
					isExtend = !isExtend;
					ViewPropertyAnimator.animate(cardPkgDetailArrow).rotationBy(180).setDuration(350).start();
					break;
			}
		}
	}

	private void gotoRecharge() {
		Intent intent = new Intent(this, CardRechargeActivity.class);
		intent.putExtra(CardRechargeActivity.EXTRA_MER_ID, cardPkgDetail.getMerId());
		intent.putExtra(CardRechargeActivity.EXTRA_TYPE, CardRechargeActivity.TYPE_CHARGE);
		intent.putExtra(CardRechargeActivity.EXTRA_CARD_NAME, cardPkgDetail.getName());
		intent.putExtra(CardRechargeActivity.EXTRA_CARD_ID, cardPkgDetail.getId());
		intent.putExtra(CardRechargeActivity.EXTRA_CARD_PRIVILEGE, cardPkgDetail.getPrivilegeEntityList());
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode && CardDetailActivity.REQUEST_AUTH_CODE == requestCode) {
			gotoRecharge();
		}
	}
}
