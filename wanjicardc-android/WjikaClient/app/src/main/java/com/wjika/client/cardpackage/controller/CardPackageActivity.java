package com.wjika.client.cardpackage.controller;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.AnalysisUtil;
import com.common.utils.DeviceUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.common.widget.FootLoadingListView;
import com.common.widget.PullToRefreshBase;
import com.google.zxing.BarcodeFormat;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.BaseActivity;
import com.wjika.client.cardpackage.adapter.CardAdapter;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.CardEntity;
import com.wjika.client.network.entities.CardPageEntity;
import com.wjika.client.network.entities.Entity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.person.controller.AuthenticationActivity;
import com.wjika.client.utils.QRCodeUtils;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;

/**
 * Created by Liu_Zhichao on 2016/6/27 11:18.
 * 卡包
 */
public class CardPackageActivity extends BaseActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener<ListView> {

	private static final int REQUEST_NET_GET_CARD = 10;
	private static final int REQUEST_NET_CARD_MORE = 20;
	private static final int ANIMATION_DURATION = 300;//动画执行时长

	@ViewInject(R.id.card_pkg_close)
	private ImageView cardPkgClose;
	@ViewInject(R.id.card_pkg_small_scan)
	private ImageView cardPkgSmallScan;
	@ViewInject(R.id.card_pkg_small_qr)
	private ImageView cardPkgSmallQr;
	@ViewInject(R.id.card_pkg_qr)
	private TextView cardPkgQr;
	@ViewInject(R.id.card_pkg_scan)
	private TextView cardPkgScan;
	@ViewInject(R.id.card_pkg_container)
	private LinearLayout cardPkgContainer;//默认布局，一级
	@ViewInject(R.id.card_pkg_fold)
	private RelativeLayout cardPkgFold;
	@ViewInject(R.id.card_pkg_fold_bg)
	private ImageView cardPkgFoldBg;
	@ViewInject(R.id.card_pkg_card_all)
	private View cardPkgCardAll;//卡包列表总布局，一级
	@ViewInject(R.id.card_pkg_card_loading)
	private View cardPkgCardLoading;//加载中布局，二级
	@ViewInject(R.id.card_pkg_card_failed)
	private View cardPkgCardFailed;//加载失败布局，二级
	@ViewInject(R.id.card_pkg_retry)
	private View cardPkgRetry;
	@ViewInject(R.id.card_pkg_no_card)
	private View cardPkgNoCard;//无卡布局，二级
	@ViewInject(R.id.card_pkg_card_list)
	private View cardPkgCardList;//卡列表布局，二级
	@ViewInject(R.id.card_pkg_list)
	private FootLoadingListView cardPkgList;
	@ViewInject(R.id.card_big_bar)
	private View cardBigBar;
	@ViewInject(R.id.card_big_bar_ll)
	private View cardBigBarLl;
	@ViewInject(R.id.card_big_bar_code)
	private ImageView cardBigBarCode;
	@ViewInject(R.id.card_big_bar_txt)
	private TextView cardBigBarTxt;
	@ViewInject(R.id.card_big_qr)
	private View cardBigQr;
	@ViewInject(R.id.card_big_qr_code)
	private ImageView cardBigQrCode;

	private QRFragment qrFragment;
	private ScanFragment scanFragment;
	private FragmentManager fragmentManager;
	private CardAdapter cardAdapter;
//	private boolean clearTag;//清理列表view的tag

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isHasFragment = true;
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.act_card_package);
		ViewInjectUtils.inject(this);
		initView();
	}

	private void initView() {
		qrFragment = new QRFragment();
		scanFragment = new ScanFragment();
		fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.card_pkg_content, qrFragment).commit();

		if (!UserCenter.isAuthencaiton(this)) {
			showAlertDialog(null, getString(R.string.person_auth_dialog_info), getString(R.string.wjk_alert_know), getString(R.string.person_auth_auth_right_now), new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					closeDialog();
				}
			}, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					closeDialog();
					startActivity(new Intent(CardPackageActivity.this, AuthenticationActivity.class));
				}
			});
		}

		cardPkgClose.setOnClickListener(this);
		cardPkgSmallScan.setOnClickListener(this);
		cardPkgSmallQr.setOnClickListener(this);
		cardPkgQr.setOnClickListener(this);
		cardPkgScan.setOnClickListener(this);
		cardBigBar.setOnClickListener(this);
		cardBigQr.setOnClickListener(this);
		cardPkgRetry.setOnClickListener(this);
		cardPkgFold.setOnClickListener(this);
		cardPkgList.setOnRefreshListener(this);
		cardPkgList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				AnalysisUtil.onEvent(CardPackageActivity.this, "Android_act_Card");
				CardEntity cardEntity = cardAdapter.getItem(position);
				startActivity(new Intent(CardPackageActivity.this, CardPkgDetailActivity.class).putExtra("id", cardEntity.getId()));
			}
		});
		cardPkgContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (View.VISIBLE == cardPkgContainer.getVisibility() && cardPkgContainer.getAlpha() == 1) {
					cardPkgClose.setImageResource(R.drawable.ic_back_black);
					cardPkgSmallScan.setVisibility(View.INVISIBLE);
					cardPkgSmallQr.setVisibility(View.INVISIBLE);
				} else {
					cardPkgClose.setImageResource(R.drawable.black_close_icon);
					cardPkgSmallScan.setVisibility(View.VISIBLE);
					cardPkgSmallQr.setVisibility(View.VISIBLE);
				}
			}
		});
		/*cardPkgFold.setOnTouchListener(new View.OnTouchListener() {

			private int height;
			private float startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
//						startY = event.getY();//相对触摸控件
						startY = event.getRawY();//相对屏幕
						height = v.getHeight();
						cardPkgCardAll.setAlpha(0);
						cardPkgCardAll.setVisibility(View.VISIBLE);
						break;
					case MotionEvent.ACTION_MOVE:
						float moveY = event.getRawY();

						float dY = moveY - startY;
						float qrAlpha = Math.abs(dY) / height;
						float cardAlpha = 1 - (Math.abs(dY) / height);
						cardAlpha = cardAlpha < 0 ? 0 : cardAlpha;
						if (dY < 0) {
							if (View.VISIBLE != cardPkgCardAll.getVisibility()){
								cardPkgCardAll.setVisibility(View.VISIBLE);
							}
							cardPkgContainer.setAlpha(cardAlpha);
							cardPkgCardAll.setAlpha(qrAlpha);
							toggleFold(false, cardAlpha * 0.8f, 0, true);
							return true;
						} else {
							return false;
						}
					case MotionEvent.ACTION_UP:
						float endY = event.getRawY();
						float gapY = endY - startY;
						if (gapY >= -5 && gapY <= 5) {
							resetData();
							return false;
						}else if (gapY < -20) {
							//执行动画并显示listview
							clearTag = true;
							toggleFold(false, 0, 100, true);
							cardPkgContainer.setVisibility(View.GONE);
							cardPkgCardAll.setVisibility(View.VISIBLE);
						} else {
							//还原
							toggleFold(false, 1, 0, true);
							cardPkgContainer.setVisibility(View.VISIBLE);
							cardPkgCardAll.setVisibility(View.GONE);
						}
						cardPkgContainer.setAlpha(1);
						cardPkgCardAll.setAlpha(1);
						resetData();
						return true;
					case MotionEvent.ACTION_CANCEL:
						cardPkgContainer.setVisibility(View.VISIBLE);
						cardPkgCardAll.setVisibility(View.GONE);
						cardPkgContainer.setAlpha(1);
						cardPkgCardAll.setAlpha(1);
						resetData();
						break;
				}
				return false;
			}

			private void resetData() {
				height = 0;
				startY = 0;
			}
		});
		cardPkgList.getRefreshableView().setOnTouchListener(new View.OnTouchListener() {

			public float startY;
			private int height;
			private boolean canHandle;//能否进行折叠处理

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						ListView listView = cardPkgList.getRefreshableView();
						int firstVisiblePosition = listView.getFirstVisiblePosition();
						if (firstVisiblePosition == 0) {
							canHandle = true;
							height = v.getHeight();
							cardPkgContainer.setAlpha(0);
							cardPkgContainer.setVisibility(View.VISIBLE);
							startY = event.getRawY();
						}
						break;
					case MotionEvent.ACTION_MOVE:
						if (canHandle) {
							float moveY = event.getRawY();
							float dY = moveY - startY;
							float qrAlpha = Math.abs(dY) / height;//越来越大
							float cardAlpha = 1 - (Math.abs(dY) / height);//越来越小
							if (dY > 0) {
								//下拉
								cardPkgContainer.setAlpha(qrAlpha);
								cardPkgCardAll.setAlpha(cardAlpha);
								toggleFold(true, qrAlpha, 0, true);
								return true;
							} else {
								return false;
							}
						}
						break;
					case MotionEvent.ACTION_UP:
						if (canHandle) {
							float endY = event.getRawY();
							float gapY = endY - startY;
							if (gapY >= -5 && gapY <= 5){
								resetData();
								return false;
							}else if (gapY > 20) {
								clearTag = false;
								//view的显示和隐藏要在动画完成之后处理
								toggleFold(true, 1, 100, true);
							} else {
								//还原
								toggleFold(true, 0, 0, true);
								cardPkgContainer.setVisibility(View.GONE);
								cardPkgCardAll.setVisibility(View.VISIBLE);
							}
							cardPkgContainer.setAlpha(1);
							cardPkgCardAll.setAlpha(1);
							//处理完重置
							resetData();
							return true;
						}
						break;
					case MotionEvent.ACTION_CANCEL:
						cardPkgContainer.setVisibility(View.GONE);
						cardPkgCardAll.setVisibility(View.VISIBLE);
						cardPkgContainer.setAlpha(1);
						cardPkgCardAll.setAlpha(1);
						resetData();
						break;
				}
				return false;
			}

			private void resetData() {
				startY = 0;
				height = 0;
				canHandle = false;
			}
		});*/
	}

	/**
	 * @ param fold  是否折叠
	 * @ param gradient 动画执行停止时的位置
	 * @ param duration 动画持续时间
	 * @ param isTouch 是否通过触摸事件执行的
	 */
	/*private void toggleFold(boolean fold, float gradient, int duration, boolean isTouch) {
		ListView listView = cardPkgList.getRefreshableView();
		if (cardAdapter != null && cardAdapter.getCount() > 0) {
			List<View> views = new ArrayList<>();//参与执行动画的view
			if (listView.getChildCount() > 10) {
				for (int i = 1; i < 10; i++) {//从第二个开始取9个
					views.add(listView.getChildAt(i));
				}
			} else {
				//去掉头和尾
				for (int i = 1; i < listView.getChildCount() - 1; i++) {
					views.add(listView.getChildAt(i));
				}
			}
			if (fold) {//折叠
				for (int i = 0; i < views.size(); i++) {
					View view = views.get(i);
					Object tag = view.getTag();
					float oldGradient = 0;
					if (tag != null && tag instanceof Float) {
						oldGradient = (float) tag;
						if (isTouch) {//   i越小滚动距离越大gradient越小
							gradient = gradient * (1- i * 0.1f);//换成加号item之间的距离越来越大
						}
					}
					view.clearAnimation();
					TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, 0,
							Animation.RELATIVE_TO_PARENT, oldGradient, Animation.RELATIVE_TO_PARENT, gradient);
					translateAnimation.setDuration(duration);
					translateAnimation.setFillAfter(true);
					translateAnimation.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							if (!clearTag) {
								cardPkgCardAll.setVisibility(View.GONE);
								cardPkgContainer.setVisibility(View.VISIBLE);
							}
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}
					});
					view.startAnimation(translateAnimation);
					view.setTag(gradient);
					if (!clearTag) {
						view.setTag(null);
					}
				}
			} else {//展开
				for (int i = 0; i < views.size(); i++) {
					View view = views.get(i);
					Object tag = view.getTag();
					float oldGradient = 0.8f;//叠加卡的初始位置位于父布局最下且可见
					if (tag != null && tag instanceof Float) {
						oldGradient = (float) tag;
						if (isTouch) {//   i越小滚动距离越大gradient越小
							gradient = gradient * (1- i * 0.1f);//换成加号item之间的距离越来越大
						}
					} else {
						oldGradient -= i * 0.1f;
					}

					TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, 0,
							Animation.RELATIVE_TO_PARENT, oldGradient, Animation.RELATIVE_TO_PARENT, gradient);
					translateAnimation.setDuration(duration);
					translateAnimation.setFillAfter(true);
					view.startAnimation(translateAnimation);
					view.setTag(gradient);
					if (clearTag) {
						view.setTag(null);
					}
				}
			}
		}
	}*/

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		AnalysisUtil.onEvent(this, "Android_vie_CardBag");
		cardPkgList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
		loadData(false);
	}

	private void loadData(boolean isMore) {
		if (isMore) {
			IdentityHashMap<String, String> param = new IdentityHashMap<>();
			param.put(Constants.PAGENUM, (cardAdapter.getPage() + 1) + "");
			param.put(Constants.PAGESIZE, Constants.PAGE_SIZE + "");
			param.put(Constants.TOKEN, UserCenter.getToken(this));
			requestHttpData(Constants.Urls.URL_GET_CARD_LIST, REQUEST_NET_CARD_MORE, FProtocol.HttpMethod.POST, param);
		} else {
			setCardStatus(LoadingStatus.LOADING);
			IdentityHashMap<String, String> param = new IdentityHashMap<>();
			param.put(Constants.PAGENUM, "1");
			param.put(Constants.PAGESIZE, Constants.PAGE_SIZE + "");
			param.put(Constants.TOKEN, UserCenter.getToken(this));
			requestHttpData(Constants.Urls.URL_GET_CARD_LIST, REQUEST_NET_GET_CARD, FProtocol.HttpMethod.POST, param);
		}
	}

	/**
	 * 设置卡列表的状态
	 * 只控制内容，不控制view的显示隐藏！！！
	 */
	private void setCardStatus(LoadingStatus status) {
		cardPkgCardLoading.setVisibility(View.GONE);
		cardPkgCardFailed.setVisibility(View.GONE);
		cardPkgNoCard.setVisibility(View.GONE);
		cardPkgCardList.setVisibility(View.GONE);
		switch (status) {
			case LOADING:
				cardPkgCardLoading.setVisibility(View.VISIBLE);
				break;
			case EMPTY:
				cardPkgNoCard.setVisibility(View.VISIBLE);
				break;
			case RETRY:
				cardPkgCardFailed.setVisibility(View.VISIBLE);
				break;
			case GONE:
				cardPkgCardList.setVisibility(View.VISIBLE);
				break;
		}
	}

	@Override
	public void success(int requestCode, String data) {
		cardPkgList.onRefreshComplete();
		Entity entity = Parsers.getResponseSatus(data);
		if (RESPONSE_NO_LOGIN_CODE.equals(entity.getResultCode())) {
			UserCenter.cleanLoginInfo(this);
			ToastUtil.shortShow(this, getResources().getString(R.string.str_token_invalid));
			noLogin();
			finish();
			return;
		}
		super.success(requestCode, data);
	}

	@Override
	protected void parseData(int requestCode, String data) {
		switch (requestCode) {
			case REQUEST_NET_GET_CARD: {
				CardPageEntity cardPage = Parsers.getCardPage(data);
				if (cardPage != null) {
					if (cardPage.getCardEntities().size() > 0) {
						setCardStatus(LoadingStatus.GONE);
						cardPkgFoldBg.setBackgroundResource(R.drawable.card_superposition_normal_bg);
						cardAdapter = new CardAdapter(this, cardPage.getCardEntities());
						cardPkgList.setAdapter(cardAdapter);
					} else {
						setCardStatus(LoadingStatus.EMPTY);
					}
					if (cardPage.getTotalPage() <= 1) {
						cardPkgList.setCanAddMore(false);
					}
				} else {
					ToastUtil.shortShow(this, getString(R.string.card_pkg_get_failed));
				}
				break;
			}
			case REQUEST_NET_CARD_MORE:{
				CardPageEntity cardPage = Parsers.getCardPage(data);
				if (cardPage != null) {
					cardAdapter.addDatas(cardPage.getCardEntities());
					if (cardPage.getTotalPage() <= cardAdapter.getPage()) {
						cardPkgList.setCanAddMore(false);
					}
				} else {
					ToastUtil.shortShow(this, "获取卡失败");
				}
				break;
			}
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		cardPkgList.onRefreshComplete();
		setCardStatus(LoadingStatus.RETRY);
		super.mistake(requestCode, status, errorMessage);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.card_pkg_close:
				if (View.VISIBLE == cardPkgContainer.getVisibility()) {
					finish();
				} else {
					shrinkCardList();
				}
				break;
			case R.id.card_pkg_small_qr:
				shrinkCardList();
				selectQrFrag(true);
				break;
			case R.id.card_pkg_small_scan:
				shrinkCardList();
				selectQrFrag(false);
				break;
			case R.id.card_pkg_qr:
				selectQrFrag(true);
				break;
			case R.id.card_pkg_scan:
				selectQrFrag(false);
				break;
			case R.id.card_big_qr:
				AlphaAnimation mHiddenAction = new AlphaAnimation(1, 0);
				cardBigQr.startAnimation(mHiddenAction);
				cardBigQr.setVisibility(View.GONE);
				break;
			case R.id.card_big_bar:
				AlphaAnimation hiddenAction = new AlphaAnimation(1, 0);
				cardBigBar.startAnimation(hiddenAction);
				cardBigBar.setVisibility(View.GONE);
				break;
			case R.id.card_pkg_fold:
				AnalysisUtil.onEvent(this, "Android_act_MycardBag");
				/*cardPkgCardAll.setAlpha(1);//触摸按下事件会设置为0
				clearTag = true;
				toggleFold(false, 0, ANIMATION_DURATION, false);

				AlphaAnimation hideAnimation = new AlphaAnimation(1, 0);
				hideAnimation.setDuration(ANIMATION_DURATION);
				cardPkgContainer.startAnimation(hideAnimation);

				AlphaAnimation showAnimation = new AlphaAnimation(0, 1);
				showAnimation.setDuration(ANIMATION_DURATION);
				showAnimation.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						cardPkgContainer.setVisibility(View.GONE);
						cardPkgCardAll.setVisibility(View.VISIBLE);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}
				});*/
				//使用属性动画来处理？
				cardPkgFold.setClickable(false);
				AlphaAnimation hideAnimation = new AlphaAnimation(1, 0);
				hideAnimation.setDuration(ANIMATION_DURATION);
				hideAnimation.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						cardPkgContainer.setVisibility(View.GONE);
						cardPkgFold.setClickable(true);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}
				});
				cardPkgContainer.startAnimation(hideAnimation);

				AlphaAnimation showAnimation = new AlphaAnimation(0, 1);
				showAnimation.setDuration(ANIMATION_DURATION);
				cardPkgCardAll.setVisibility(View.VISIBLE);
				cardPkgCardAll.startAnimation(showAnimation);

				if (cardAdapter != null && cardAdapter.getCount() > 0) {
					TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
					translateAnimation.setDuration(ANIMATION_DURATION);
					cardPkgList.startAnimation(translateAnimation);
				}
				break;
			case R.id.card_pkg_retry:
				loadData(false);
				break;
		}
	}

	/**
	 * 收缩卡列表
	 */
	private void shrinkCardList() {
		/*clearTag = false;
		cardPkgContainer.setAlpha(1);
		toggleFold(true, 1, ANIMATION_DURATION, false);

		AlphaAnimation showAnimation = new AlphaAnimation(0, 1);
		showAnimation.setDuration(ANIMATION_DURATION);
		cardPkgContainer.startAnimation(showAnimation);

		AlphaAnimation hideAnimation = new AlphaAnimation(1, 0);
		hideAnimation.setDuration(ANIMATION_DURATION);
		hideAnimation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				cardPkgContainer.setVisibility(View.VISIBLE);
				cardPkgCardAll.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
		cardPkgCardAll.startAnimation(hideAnimation);*/
		AlphaAnimation showAnimation = new AlphaAnimation(0, 1);
		showAnimation.setDuration(ANIMATION_DURATION);
		cardPkgContainer.setVisibility(View.VISIBLE);
		cardPkgContainer.startAnimation(showAnimation);

		AlphaAnimation hideAnimation = new AlphaAnimation(1, 0);
		hideAnimation.setDuration(ANIMATION_DURATION);
		hideAnimation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				cardPkgCardAll.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
		cardPkgCardAll.startAnimation(hideAnimation);

		if (cardAdapter != null && cardAdapter.getCount() > 0) {
			TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
			translateAnimation.setDuration(ANIMATION_DURATION);
			cardPkgList.startAnimation(translateAnimation);
		}
	}

	public void selectQrFrag(boolean isSelected) {
		if (isSelected) {
			cardPkgQr.setTextColor(getResources().getColor(R.color.wjika_client_title_bg));
			cardPkgQr.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.qr_big_selected_icon, 0, 0);
			cardPkgScan.setTextColor(getResources().getColor(R.color.wjika_client_introduce_words));
			cardPkgScan.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.scan_big_unselect_icon, 0, 0);
			fragmentManager.beginTransaction().replace(R.id.card_pkg_content, qrFragment).commit();
		} else {
			cardPkgQr.setTextColor(getResources().getColor(R.color.wjika_client_introduce_words));
			cardPkgQr.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.qr_big_unselect_icon, 0, 0);
			cardPkgScan.setTextColor(getResources().getColor(R.color.wjika_client_title_bg));
			cardPkgScan.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.scan_big_selected_icon, 0, 0);
			fragmentManager.beginTransaction().replace(R.id.card_pkg_content, scanFragment).commit();
		}
	}

	public void setBarCode(String code) {
		//如果ImageView宽度超出显示范围，那么动画将只会处理能显示的部分，所以不能设置太宽
		// 因为ImageView是match_parent，获取不到实际高度，所以这里通过获取屏幕宽度作为ImageView的宽度
//		int barWidth = cardBigBarCode.getLayoutParams().width;
		int barWidth = DeviceUtil.getWidth(this);
		int barheight = cardBigBarCode.getLayoutParams().height;
		//条形码
		try{
			QRCodeUtils.setImageView(cardBigBarCode, code, BarcodeFormat.CODE_128, barWidth, barheight);
		}catch (OutOfMemoryError e){
			System.gc();
			QRCodeUtils.setImageView(cardBigBarCode, code, BarcodeFormat.CODE_128, barWidth, barheight);
		}

		if (cardBigBarTxt != null) {
			String codeStr = code;
			if (code.length() == 18) {
				codeStr = code.substring(0, 4) + "    " + code.substring(4, 8) + "    " + code.substring(8, 12) + "    " + code.substring(12, 18);
			}
			cardBigBarTxt.setText(codeStr);
		}

		RotateAnimation rotateAnimation = new RotateAnimation(0, 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(rotateAnimation);
		animationSet.addAnimation(alphaAnimation);
		animationSet.setFillAfter(true);
		cardBigBarLl.startAnimation(animationSet);

//		cardBigBarLl.setVisibility(View.VISIBLE);
		AlphaAnimation mShowAction = new AlphaAnimation(0, 1);
		cardBigBar.startAnimation(mShowAction);
		cardBigBar.setVisibility(View.VISIBLE);
	}

	public void setQrCode(String code) {
		int qrWidth = cardBigQrCode.getLayoutParams().width;
		int qrHeight = cardBigQrCode.getLayoutParams().height;
		//二维码
		try{
			QRCodeUtils.setQvImageView(cardBigQrCode, code, BarcodeFormat.QR_CODE, qrWidth, qrHeight);
		}catch (OutOfMemoryError e){
			System.gc();
			QRCodeUtils.setQvImageView(cardBigQrCode, code, BarcodeFormat.QR_CODE, qrWidth, qrHeight);
		}
		AlphaAnimation mShowAction = new AlphaAnimation(0, 1);
		cardBigQr.startAnimation(mShowAction);
		cardBigQr.setVisibility(View.VISIBLE);
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		if (refreshView.isRefreshing()) {
			loadData(true);
		} else {
			refreshView.onRefreshComplete();
		}
	}

	@Override
	public void onBackPressed() {
		if (View.VISIBLE == cardPkgContainer.getVisibility()) {
			if (View.VISIBLE == cardBigQr.getVisibility()) {
				AlphaAnimation mHiddenAction = new AlphaAnimation(1, 0);
				cardBigQr.startAnimation(mHiddenAction);
				cardBigQr.setVisibility(View.GONE);
			} else if (View.VISIBLE == cardBigBar.getVisibility()){
				AlphaAnimation mHiddenAction = new AlphaAnimation(1, 0);
				cardBigBar.startAnimation(mHiddenAction);
				cardBigBar.setVisibility(View.GONE);
			} else {
				super.onBackPressed();
			}
		} else {
			shrinkCardList();
		}
	}
}
