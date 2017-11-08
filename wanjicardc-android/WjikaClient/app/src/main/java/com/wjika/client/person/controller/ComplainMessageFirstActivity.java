package com.wjika.client.person.controller;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.utils.ExitManager;
import com.wjika.client.utils.InputUtil;
import com.wjika.client.utils.TimeUtil;
import com.wjika.client.utils.VerifyUtils;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.Calendar;

/**
 * Created by leo_Zhang on 2015/12/10.
 * 申诉
 */
public class ComplainMessageFirstActivity extends ToolBarActivity implements View.OnClickListener {

	@ViewInject(R.id.person_complain_msg_next)
	private TextView personComplainMsgNext;
	@ViewInject(R.id.person_complain_msg_time)
	private LinearLayout personComplainMsgTime;
	@ViewInject(R.id.person_complain_register_time)
	private TextView personComplainRegisterTime;
	@ViewInject(R.id.person_complain_msg_real_name)
	private EditText personComplainMsgRealName;
	@ViewInject(R.id.person_complain_msg_id_number)
	private EditText personComplainMsgIdNumber;
	@ViewInject(R.id.person_complain_msg_phone_num)
	private TextView personComplainMsgPhoneNum;

	private int[] mBirthday;
	private String realName;
	private String idNumber;
	private String phoneNumber;
	private String registarTime = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_act_complain_msg_first);
		ViewInjectUtils.inject(this);
		ExitManager.instance.addVerifyActivity(this);
		res = getResources();
		initView();
	}

	private void initView() {
		setLeftTitle(res.getString(R.string.complain_message_information_first));
		personComplainMsgNext.setOnClickListener(this);
		personComplainMsgTime.setOnClickListener(this);
		String userPhone = UserCenter.getUserPhone(this);
		InputUtil.editIsEmpty(personComplainMsgNext, personComplainMsgRealName, personComplainMsgIdNumber);
		if (!userPhone.isEmpty()) {
			personComplainMsgPhoneNum.setText(userPhone);
			personComplainMsgPhoneNum.setFocusable(false);
			personComplainMsgPhoneNum.setFocusableInTouchMode(false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.person_complain_msg_next:
				gainData();
				Intent intent = new Intent();
				intent.setClass(this, ComplainMessageSecondActivity.class);
				intent.putExtra("real_name", realName);
				intent.putExtra("id_number", idNumber);
				intent.putExtra("phone_num", phoneNumber);
				intent.putExtra("register_time", registarTime);
				if (!TextUtils.isEmpty(realName) && !TextUtils.isEmpty(idNumber) && !TextUtils.isEmpty(phoneNumber) && !registarTime.equals(getString(R.string.complain_message_select_register_time))) {
					if (VerifyUtils.verifyIdentityCard(idNumber)) {
						if (VerifyUtils.checkPhoneNumber(phoneNumber)) {
							startActivity(intent);
						} else {
							ToastUtil.shortShow(this, res.getString(R.string.complain_message_num_input_error));
						}
					} else {
						ToastUtil.shortShow(this, res.getString(R.string.complain_message_id_num_input_error));
					}
				} else {
					if (TextUtils.isEmpty(realName)) {
						ToastUtil.shortShow(this, res.getString(R.string.complain_message_realname_empty));
					} else if (TextUtils.isEmpty(idNumber)) {
						ToastUtil.shortShow(this, res.getString(R.string.complain_message_idnumber_empty));
					} else if (registarTime.equals(getString(R.string.complain_message_select_register_time))) {
						ToastUtil.shortShow(this, res.getString(R.string.complain_message_registertime_empty));
					} else {
						ToastUtil.shortShow(this, res.getString(R.string.complain_message_input_correct_information));
					}
				}
				break;
			case R.id.person_complain_msg_time:
				Calendar calendar = Calendar.getInstance();
				//不添加DateSet回调，通过自己处理确定事件获取日期，兼容4.x系统
				final DatePickerDialog datePickerDialog = new DatePickerDialog(this, null, 2010
						, 0, 1);
				datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, res.getString(R.string.person_confirm), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DatePicker datePicker = datePickerDialog.getDatePicker();
						int year = datePicker.getYear();
						int month = datePicker.getMonth();
						int day = datePicker.getDayOfMonth();
						Calendar calendarSelect = Calendar.getInstance();
						calendarSelect.set(year, month, day);
						registarTime = TimeUtil.formatTime(calendarSelect.getTimeInMillis(), "yyyyMM");
						mBirthday = new int[3];
						mBirthday[0] = year;
						mBirthday[1] = month + 1;
						personComplainRegisterTime.setText(mBirthday[0] + "年" + mBirthday[1] + "月");
						personComplainRegisterTime.setTextColor(getResources().getColor(R.color.wjika_client_dark_grey));
					}
				});
				datePickerDialog.setCancelable(true);
				datePickerDialog.setCanceledOnTouchOutside(true);
				DatePicker datePicker = datePickerDialog.getDatePicker();
				datePicker.setMaxDate(calendar.getTimeInMillis());
				datePickerDialog.show();
				break;
		}
	}

	private void gainData() {
		realName = personComplainMsgRealName.getText().toString();
		idNumber = personComplainMsgIdNumber.getText().toString();
		phoneNumber = personComplainMsgPhoneNum.getText().toString();
	}

	/**
	 * 隐藏软键盘
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (null != this.getCurrentFocus()) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			return inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		finish();
	}
}
