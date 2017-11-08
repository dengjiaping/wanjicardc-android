package com.wjika.client.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

/**
 * Created by ${zhaoweiwei} on 2016/6/28.
 * 输入框
 */
public class InputUtil {
	/**
	 *当每个EditText不为空时，按钮才可用
	 * @param button  是否被点亮可用的按钮
	 * @param editTexts EditText数组
	 */
	public static void editIsEmpty (final View button, final EditText... editTexts) {

		for (int i = 0; i<editTexts.length; i++) {

			editTexts[i].addTextChangedListener(new TextWatcher() {

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {}

				@Override
				public void afterTextChanged(Editable s) {
					for (EditText editText:editTexts){
						if (TextUtils.isEmpty(editText.getText())){
							button.setEnabled(false);
							return;
						}
					}
					button.setEnabled(true);
				}
			});
		}
	}
}
