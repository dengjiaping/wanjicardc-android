package com.wjika.client.person.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.UserQuestionEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.utils.ExitManager;
import com.wjika.client.utils.InputUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by leo_Zhang on 2015/12/7.
 * 账户安全问题
 */
public class AccountSafeQuestionActivity extends ToolBarActivity implements View.OnClickListener {

    public static final String SELECT_QUESTION_ID = "select_question_id";
    public static final String SELECT_QUESTION = "select_question";
    private static final int GET_USER_QUESTION = 100;
    public static final int QUESTION_ITEM_FIRST = 200;
    public static final int QUESTION_ITEM_SECOND = 300;
    public static final int QUESTION_ITEM_THIRD = 400;

    @ViewInject(R.id.person_account_question_next)
    private TextView personAccountQuestionNext;
    @ViewInject(R.id.person_account_setting_question1)
    private RelativeLayout personAccountSettingQuestion1;
    @ViewInject(R.id.person_account_setting_question2)
    private RelativeLayout personAccountSettingQuestion2;
    @ViewInject(R.id.person_account_setting_question3)
    private RelativeLayout personAccountSettingQuestion3;
    @ViewInject(R.id.person_account_detail_question1)
    private TextView personAccountDetailQuestion1;
    @ViewInject(R.id.person_account_detail_question2)
    private TextView personAccountDetailQuestion2;
    @ViewInject(R.id.person_account_detail_question3)
    private TextView personAccountDetailQuestion3;
    @ViewInject(R.id.person_account_question_result1)
    private EditText personAccountQuestionResult1;
    @ViewInject(R.id.person_account_question_result2)
    private EditText personAccountQuestionResult2;
    @ViewInject(R.id.person_account_question_result3)
    private EditText personAccountQuestionResult3;
    @ViewInject(R.id.person_account_question_info)
    private TextView personAccountQuestionInfo;
    @ViewInject(R.id.person_account_detail_more1)
    private ImageButton personQuestionMore1;
    @ViewInject(R.id.person_account_detail_more2)
    private ImageButton personQuestionMore2;
    @ViewInject(R.id.person_account_detail_more3)
    private ImageButton personQuestionMore3;
    @ViewInject(R.id.person_account_question_answer1)
    private TextView personQuestionAnswer1;
    @ViewInject(R.id.person_account_question_answer2)
    private TextView personQuestionAnswer2;
    @ViewInject(R.id.person_account_question_answer3)
    private TextView personQuestionAnswer3;

    private String selectQuestion1;
    private String selectQuestion2;
    private String selectQuestion3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_act_account_question);
        ViewInjectUtils.inject(this);
        ExitManager.instance.addVerifyActivity(this);
        initView();
    }

    private void initView() {
        setLeftTitle(res.getString(R.string.account_safe_security));
        Boolean isSetQuestion = getIntent().getBooleanExtra("isSetQuestion",false);
        if (isSetQuestion) {
            loadData();
            personAccountQuestionInfo.setText(res.getString(R.string.verify_safe_have_set_security));
            personAccountQuestionNext.setVisibility(View.GONE);

            personAccountDetailQuestion1.setTextColor(getResources().getColor(R.color.wjika_client_hint_words));
            personAccountDetailQuestion2.setTextColor(getResources().getColor(R.color.wjika_client_hint_words));
            personAccountDetailQuestion3.setTextColor(getResources().getColor(R.color.wjika_client_hint_words));

            personQuestionAnswer1.setTextColor(getResources().getColor(R.color.wjika_client_hint_words));
            personQuestionAnswer2.setTextColor(getResources().getColor(R.color.wjika_client_hint_words));
            personQuestionAnswer3.setTextColor(getResources().getColor(R.color.wjika_client_hint_words));

            personQuestionMore1.setVisibility(View.GONE);
            personQuestionMore2.setVisibility(View.GONE);
            personQuestionMore3.setVisibility(View.GONE);

            personAccountQuestionResult1.setEnabled(false);
            personAccountQuestionResult1.setFocusable(false);
            personAccountQuestionResult1.setText("******");
            personAccountQuestionResult2.setEnabled(false);
            personAccountQuestionResult2.setFocusable(false);
            personAccountQuestionResult2.setText("******");
            personAccountQuestionResult3.setEnabled(false);
            personAccountQuestionResult3.setFocusable(false);
            personAccountQuestionResult3.setText("******");
        } else {
            personAccountQuestionInfo.setText(res.getString(R.string.verify_safe_set_info));
            InputUtil.editIsEmpty(personAccountQuestionNext,personAccountQuestionResult1,personAccountQuestionResult2,personAccountQuestionResult3);
            personAccountQuestionNext.setOnClickListener(this);
            personAccountSettingQuestion1.setOnClickListener(this);
            personAccountSettingQuestion2.setOnClickListener(this);
            personAccountSettingQuestion3.setOnClickListener(this);
        }
    }

    private void loadData() {
        showProgressDialog();
        IdentityHashMap<String, String> params = new IdentityHashMap<>();
        params.put(Constants.TOKEN, UserCenter.getToken(this));
        requestHttpData(Constants.Urls.URL_GET_USER_SECURITY_QUESTIONS, GET_USER_QUESTION, FProtocol.HttpMethod.POST, params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person_account_setting_question1:
                startActivityForResult(new Intent(this, ChooseVerifyQuestionActivity.class), QUESTION_ITEM_FIRST);
                break;
            case R.id.person_account_setting_question2:
                startActivityForResult(new Intent(this, ChooseVerifyQuestionActivity.class), QUESTION_ITEM_SECOND);
                break;
            case R.id.person_account_setting_question3:
                startActivityForResult(new Intent(this, ChooseVerifyQuestionActivity.class), QUESTION_ITEM_THIRD);
                break;
            case R.id.person_account_question_next:
                String result1 = personAccountQuestionResult1.getText().toString().trim();
                String result2 = personAccountQuestionResult2.getText().toString().trim();
                String result3 = personAccountQuestionResult3.getText().toString().trim();
                Intent intent = new Intent();
                intent.setClass(this, VerifySafeQuestionActivity.class);
                intent.putExtra("q1", personAccountDetailQuestion1.getText().toString().trim());
                intent.putExtra("q2", personAccountDetailQuestion2.getText().toString().trim());
                intent.putExtra("q3", personAccountDetailQuestion3.getText().toString().trim());
                intent.putExtra("a1", result1);
                intent.putExtra("a2", result2);
                intent.putExtra("a3", result3);
                intent.putExtra("qid1",selectQuestion1);
                intent.putExtra("qid2",selectQuestion2);
                intent.putExtra("qid3",selectQuestion3);
                intent.putExtra(VerifySafeQuestionActivity.FROM_EXTRA, VerifySafeQuestionActivity.VERIFY);

                if (!TextUtils.isEmpty(selectQuestion1) && !TextUtils.isEmpty(selectQuestion2) && !TextUtils.isEmpty(selectQuestion3)) {
                    if (!TextUtils.isEmpty(result1) && !TextUtils.isEmpty(result2) && !TextUtils.isEmpty(result3)) {
                        startActivity(intent);
                    } else {
                        ToastUtil.longShow(this, res.getString(R.string.verify_safe_please_enter_answer));
                    }
                } else {
                    ToastUtil.longShow(this, res.getString(R.string.account_safe_select_question));
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && RESULT_OK == resultCode) {
            switch (requestCode) {
                case QUESTION_ITEM_FIRST:
                    selectQuestion1 = data.getStringExtra(SELECT_QUESTION_ID);
                    if (selectQuestion1 != null && (selectQuestion1.equals(selectQuestion2) || selectQuestion1.equals(selectQuestion3))) {
                        ToastUtil.shortShow(this, res.getString(R.string.account_safe_choose_repeated_problems));
                        return;
                    }
                    personAccountDetailQuestion1.setText(res.getString(R.string.account_safe_question_first) + data.getStringExtra(SELECT_QUESTION));
                    break;
                case QUESTION_ITEM_SECOND:
                    selectQuestion2 = data.getStringExtra(SELECT_QUESTION_ID);
                    if (selectQuestion2 != null && (selectQuestion2.equals(selectQuestion1) || selectQuestion2.equals(selectQuestion3))) {
                        ToastUtil.shortShow(this,  res.getString(R.string.account_safe_choose_repeated_problems));
                        return;
                    }
                    personAccountDetailQuestion2.setText(res.getString(R.string.account_safe_question_second)  + data.getStringExtra(SELECT_QUESTION));
                    break;
                case QUESTION_ITEM_THIRD:
                    selectQuestion3 = data.getStringExtra(SELECT_QUESTION_ID);
                    if (selectQuestion3 != null && (selectQuestion3.equals(selectQuestion2) || selectQuestion3.equals(selectQuestion1))) {
                        ToastUtil.shortShow(this,  res.getString(R.string.account_safe_choose_repeated_problems));
                        return;
                    }
                    personAccountDetailQuestion3.setText(res.getString(R.string.account_safe_question_third)   + data.getStringExtra(SELECT_QUESTION));
                    break;
            }
        }
    }

    @Override
    public void success(int requestCode, String data) {
        super.success(requestCode, data);
        closeProgressDialog();
        List<UserQuestionEntity> entities = Parsers.getQuestionResult(data);
        personAccountDetailQuestion1.setText(res.getString(R.string.account_safe_question_first) + entities.get(0).getQuestionName());
        personAccountDetailQuestion1.setTextColor(getResources().getColor(R.color.wjika_client_hint_words));
        personAccountDetailQuestion2.setText(res.getString(R.string.account_safe_question_second)  + entities.get(1).getQuestionName());
        personAccountDetailQuestion2.setTextColor(getResources().getColor(R.color.wjika_client_hint_words));
        personAccountDetailQuestion3.setText(res.getString(R.string.account_safe_question_third)   + entities.get(2).getQuestionName());
        personAccountDetailQuestion3.setTextColor(getResources().getColor(R.color.wjika_client_hint_words));
    }

    @Override
    public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
        super.mistake(requestCode, status, errorMessage);
        ToastUtil.shortShow(getApplicationContext(),errorMessage);
        closeProgressDialog();
    }

    /**
     * 隐藏软键盘
     * @param event
     * @return
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
