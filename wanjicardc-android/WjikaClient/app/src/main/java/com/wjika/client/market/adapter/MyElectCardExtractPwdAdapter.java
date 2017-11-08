package com.wjika.client.market.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.common.utils.EnCryptionUtils;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.wjika.cardagent.client.R;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.entities.MyElectCardPwdItemEntity;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by ZHXIA on 2016/8/23
 * 提取卡密列表
 */
public class MyElectCardExtractPwdAdapter extends BaseAdapterNew<MyElectCardPwdItemEntity> {

	private Context context;
	private String paypwdSalt;
	private String key;
	private String ivs;

	public MyElectCardExtractPwdAdapter(Context context, List<MyElectCardPwdItemEntity> mDatas) {
		super(context, mDatas);
		this.context = context;
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.extract_electtinfo_list;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		MyElectCardPwdItemEntity myElectCardPwdItem = getItem(position);
		if (myElectCardPwdItem != null) {
			RelativeLayout rl_extract_electinfo_num = ViewHolder.get(convertView, R.id.rl_extract_electinfo_num);
			RelativeLayout rl_extract_electinfo_pwd = ViewHolder.get(convertView, R.id.rl_extract_electinfo_pwd);
			RelativeLayout rl_extract_electinfo_pwd1 = ViewHolder.get(convertView, R.id.rl_extract_electinfo_pwd1);
			TextView extract_electcard_num = ViewHolder.get(convertView, R.id.extract_electcard_num);
			TextView extract_electcard_num_copy = ViewHolder.get(convertView, R.id.extract_electcard_num_copy);
			TextView extract_electcard_pwd = ViewHolder.get(convertView, R.id.extract_electcard_pwd);
			TextView extract_electcard_pwd1 = ViewHolder.get(convertView, R.id.extract_electcard_pwd1);
			TextView extract_electcard_pwd_copy = ViewHolder.get(convertView, R.id.extract_electcard_pwd_copy);
			TextView extract_electcard_pwd_copy1 = ViewHolder.get(convertView, R.id.extract_electcard_pwd_copy1);

			final String myElectCardNum = myElectCardPwdItem.getMyElectCardNum();
			String myElectCardPwd = myElectCardPwdItem.getMyElectCardPwd();

			if (!StringUtil.isEmpty(myElectCardPwd)) {
				if (!StringUtil.isEmpty(myElectCardNum)) {
					extract_electcard_num.setText(getContext().getString(R.string.ecard_num, myElectCardNum));
					extract_electcard_num_copy.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
							cmb.setPrimaryClip(ClipData.newPlainText("num", myElectCardNum));
							ToastUtil.shortShow(context, getContext().getString(R.string.ecard_get_num));
						}
					});
					try {
						loadPaypwdSalt();
						final String userPwdDecoderStr = EnCryptionUtils.AES2(myElectCardPwd, key, ivs);
						extract_electcard_pwd.setText(getContext().getString(R.string.ecard_pwd, userPwdDecoderStr));
						extract_electcard_pwd_copy.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
								cmb.setPrimaryClip(ClipData.newPlainText("pwd", userPwdDecoderStr));
								ToastUtil.shortShow(context, getContext().getString(R.string.ecard_get_pwd));
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					rl_extract_electinfo_num.setVisibility(View.GONE);
					rl_extract_electinfo_pwd.setVisibility(View.GONE);
					rl_extract_electinfo_pwd1.setVisibility(View.VISIBLE);
					try {
						loadPaypwdSalt();
						final String userPwdDecoderStr = EnCryptionUtils.AES2(myElectCardPwd, key, ivs);
						extract_electcard_pwd1.setText(getContext().getString(R.string.ecard_pwd, userPwdDecoderStr));
						extract_electcard_pwd_copy1.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
								cmb.setPrimaryClip(ClipData.newPlainText("pwd", userPwdDecoderStr));
								ToastUtil.shortShow(context, getContext().getString(R.string.ecard_get_pwd));
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void loadPaypwdSalt() {
		if (StringUtil.isEmpty(paypwdSalt)) {
			paypwdSalt = UserCenter.getUserPaypwdSalt(context);
			if (paypwdSalt.length() > 16) {
				paypwdSalt = pwdFilter(paypwdSalt);
				key = paypwdSalt.substring(0, 16);
				ivs = paypwdSalt.substring(paypwdSalt.length() - 16, paypwdSalt.length());
			}
		}
	}

	//过滤掉特殊字符
	private static String pwdFilter(String str) throws PatternSyntaxException {
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("");
	}
}
