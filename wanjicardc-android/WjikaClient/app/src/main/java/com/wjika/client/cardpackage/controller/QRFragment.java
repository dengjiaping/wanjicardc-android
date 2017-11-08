package com.wjika.client.cardpackage.controller;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.utils.AnalysisUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.google.zxing.BarcodeFormat;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.fragment.BaseFragment;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.utils.BrightnessTools;
import com.wjika.client.utils.QRCodeUtils;
import com.wjika.client.utils.ViewInjectUtils;
import com.wjika.jni.SM3Algorithm;

import java.util.Calendar;

/**
 * Created by Liu_Zhichao on 2016/6/27 14:26.
 * 二维码
 */
public class QRFragment extends BaseFragment implements View.OnClickListener {

	@ViewInject(R.id.qr_bar_code)
	private ImageView qrBarCode;
	@ViewInject(R.id.qr_bar_txt)
	private TextView qrBarTxt;
	@ViewInject(R.id.qr_qr_code)
	private ImageView qrQrCode;
	@ViewInject(R.id.qr_code_refresh)
	private TextView qrCodeRefresh;

	private String payCode = "000000000000000000";
	private String token;
	private String paypwd;
	private boolean isAutoBrightness;
	private int brightnessValue;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_qr, container, false);
		ViewInjectUtils.inject(this, view);
		qrBarCode.setOnClickListener(this);
		qrQrCode.setOnClickListener(this);
		qrCodeRefresh.setOnClickListener(this);
		setPayCode(payCode);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		token = UserCenter.getToken(this.getActivity());
		paypwd = (UserCenter.getUserPaypwd(getActivity())).toLowerCase();
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			brightnessAdjust();
			payCode = createPayCode();
			setPayCode(payCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 屏幕亮度调节
	 */
	private void brightnessAdjust() {
		isAutoBrightness = BrightnessTools.isAutoBrightness(getActivity().getContentResolver());
		brightnessValue = BrightnessTools.getScreenBrightness(getActivity());
		if (isAutoBrightness){
			BrightnessTools.stopAutoBrightness(getActivity());
		}
		BrightnessTools.setBrightness(getActivity(), 255);
	}

	private String createPayCode(){
		String seed = token + paypwd;
		while (seed.length() < 64){
			seed = seed + "0";
		}
		long serverTime = UserCenter.getUserServerTime(getActivity());
		long realTime = UserCenter.getUserInitRealTime(getActivity());
		long currentTime = Calendar.getInstance().getTimeInMillis()/1000;
		long time = (serverTime + (currentTime - realTime));

		if (seed.length() >= 50){
			seed = seed.substring(18, 50);
		}else if (seed.length() >= 32){
			seed = seed.substring(seed.length() - 32, seed.length());
		}else {
			while (seed.length() < 32){
				seed = seed + "0";
			}
		}
		int pcode = new SM3Algorithm().getSM3TOTP(time, seed);
		String phone = UserCenter.getUserPhone(getActivity());
		String code = pcode + "";
		while (code.length() < 6){
			code = "0" + code;
		}
		if (TextUtils.isEmpty(phone) || 11 != phone.length()){
			ToastUtil.shortShow(getActivity(), getActivity().getString(R.string.card_pkg_get_code_failed));
		}else {
			payCode = "86" + phone.substring(1) + code;
		}
		return payCode;
	}

	private void setPayCode(String code) {
//		WindowManager manager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
//		Display display = manager.getDefaultDisplay();
//		Point displaySize = new Point();
//		display.getSize(displaySize);
//		int width = displaySize.x;
//		int height = displaySize.y;
//		int smallerDimension = width < height ? width : height;
//		smallerDimension = smallerDimension * 7 / 8;
		int barWidth = qrBarCode.getLayoutParams().width;
		int barheight = qrBarCode.getLayoutParams().height;
		//条形码
		try{
			QRCodeUtils.setImageView(qrBarCode, code, BarcodeFormat.CODE_128, barWidth, barheight);
		}catch (OutOfMemoryError e){
			System.gc();
			QRCodeUtils.setImageView(qrBarCode, code, BarcodeFormat.CODE_128, barWidth, barheight);
		}

//		int qrHeight = smallerDimension - (smallerDimension / 5);
		int qrWidth = qrQrCode.getLayoutParams().width;
		int qrHeight = qrQrCode.getLayoutParams().height;
		//二维码
		try{
			QRCodeUtils.setQvImageView(qrQrCode, code, BarcodeFormat.QR_CODE, qrWidth, qrHeight);
		}catch (OutOfMemoryError e){
			System.gc();
			QRCodeUtils.setQvImageView(qrQrCode, code, BarcodeFormat.QR_CODE, qrWidth, qrHeight);
		}
		if (qrBarTxt != null) {
			String codeStr = code;
			if (code.length() == 18) {
				codeStr = code.substring(0, 4) + "   " + code.substring(4, 8) + "   " + code.substring(8, 12) + "   " + code.substring(12, 18);
			}
			qrBarTxt.setText(codeStr);
		}
	}

	@Override
	public void onClick(View v) {
		CardPackageActivity activity = (CardPackageActivity) getActivity();
		switch (v.getId()) {
			case R.id.qr_bar_code:
				activity.setBarCode(payCode);
				break;
			case R.id.qr_qr_code:
				activity.setQrCode(payCode);
				break;
			case R.id.qr_code_refresh:
				AnalysisUtil.onEvent(getActivity(), "Android_act_QRRefresh");
				payCode = createPayCode();
				setPayCode(payCode);
				break;
		}
	}

	@Override
	public void onStop() {
		if (isAutoBrightness){
			BrightnessTools.startAutoBrightness(getActivity());
		}else {
			BrightnessTools.setBrightness(getActivity(), brightnessValue);
		}
		super.onStop();
	}
}
