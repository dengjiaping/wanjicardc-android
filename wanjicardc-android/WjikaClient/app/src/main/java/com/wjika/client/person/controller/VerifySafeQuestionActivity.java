package com.wjika.client.person.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.Entity;
import com.wjika.client.network.entities.QuestionItemEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.utils.ExitManager;
import com.wjika.client.utils.InputUtil;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by leo_Zhang on 2015/12/7.
 * 验证安全问题
 */
public class VerifySafeQuestionActivity extends ToolBarActivity implements View.OnClickListener {

    public static final String FROM_EXTRA = "extra_from";
    public static final int VERIFY = 1;
    public static final int MODIFY = 2;
    private static final int REQUEST_QUESTION_ITEM_CODE = 100;
    public static final int REQUEST_QUESTION_CODE = 200;
    public static final int REQUEST_VERIFY_SAFE_QUESTION = 300;

    @ViewInject(R.id.person_verify_question_detail1)
    private TextView personVerifyQuestionDetail1;
    @ViewInject(R.id.person_verify_question_detail2)
    private TextView personVerifyQuestionDetail2;
    @ViewInject(R.id.person_verify_question_detail3)
    private TextView personVerifyQuestionDetail3;
    @ViewInject(R.id.verify_result1)
    private EditText verify_result1;
    @ViewInject(R.id.verify_result2)
    private EditText verify_result2;
    @ViewInject(R.id.verify_result3)
    private EditText verify_result3;
    @ViewInject(R.id.person_verify_safe_completed)
    private TextView personVerifySafeCompleted;
    @ViewInject(R.id.person_verify_safe_third)
    private LinearLayout personVerifySafeThird;

    private String answer1;//通过intent传过来的数据
    private String answer2;
    private String answer3;
    private String verifyResult1;//用户填写验证的数据
    private String verifyResult2;
    private String verifyResult3;
    private String verifyQuestion1;
    private String verifyQuestion2;
    private String verifyQuestion3;
    private String qid1;
    private String qid2;
    private String qid3;
    private int from;
    private String firstId;
	private String secondId;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_act_verify_safe_question);
        ViewInjectUtils.inject(this);
        ExitManager.instance.addVerifyActivity(this);
        from = getIntent().getIntExtra(FROM_EXTRA, 0);
        initData();
        initView();
    }

    private void initData() {
        answer1 = getIntent().getStringExtra("a1");
        answer2 = getIntent().getStringExtra("a2");
        answer3 = getIntent().getStringExtra("a3");
        verifyQuestion1 = getIntent().getStringExtra("q1");
        verifyQuestion2 = getIntent().getStringExtra("q2");
        verifyQuestion3 = getIntent().getStringExtra("q3");
        qid1 = getIntent().getStringExtra("qid1");
        qid2 = getIntent().getStringExtra("qid2");
        qid3 = getIntent().getStringExtra("qid3");
    }

    private void initView() {
        setLeftTitle(res.getString(R.string.verify_safe_authentication_security));
        if(from == VERIFY){
            personVerifyQuestionDetail1.setText(verifyQuestion1);
            personVerifyQuestionDetail2.setText(verifyQuestion2);
            personVerifyQuestionDetail3.setText(verifyQuestion3);
            InputUtil.editIsEmpty(personVerifySafeCompleted,verify_result1,verify_result2,verify_result3);
        }else {
//            setLeftTitle(res.getString(R.string.person_pay_setting_find));
            personVerifySafeCompleted.setText(res.getString(R.string.verify_safe_immediately));
            personVerifySafeThird.setVisibility(View.GONE);
            InputUtil.editIsEmpty(personVerifySafeCompleted,verify_result1,verify_result2);
            gainPwdProtect();
        }
        personVerifySafeCompleted.setOnClickListener(this);
    }

    private void gainPwdProtect() {
        showProgressDialog();
        IdentityHashMap<String, String> params = new IdentityHashMap<>();
        params.put("size","2");
        params.put("phone",UserCenter.getUserPhone(this));
        params.put(Constants.TOKEN, UserCenter.getToken(this));
        requestHttpData(Constants.Urls.URL_GET_SECURITY_UQUESTIONS, REQUEST_QUESTION_ITEM_CODE, FProtocol.HttpMethod.POST, params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.person_verify_safe_completed:
                if(from == VERIFY){
                    verifyResult1 = verify_result1.getText().toString().trim();
                    verifyResult2 = verify_result2.getText().toString().trim();
                    verifyResult3 = verify_result3.getText().toString().trim();
                    judge();
                }else {
                    verifyResult1 = verify_result1.getText().toString().trim();
                    verifyResult2 = verify_result2.getText().toString().trim();
                    if(!TextUtils.isEmpty(verifyResult1) && !TextUtils.isEmpty(verifyResult2)){
                        verifyQuestion();
                    }else {
                        ToastUtil.shortShow(this,res.getString(R.string.verify_safe_please_enter_answer));
                    }
                }
                break;
        }
    }

    private void verifyQuestion() {
	    showProgressDialog();
        IdentityHashMap<String,String> identityHashMap = new IdentityHashMap<>();
        identityHashMap.put("question1", firstId);
        identityHashMap.put("question2", secondId);
        identityHashMap.put("answer1",verifyResult1);
        identityHashMap.put("answer2",verifyResult2);
        identityHashMap.put("phone",UserCenter.getUserPhone(this));
        identityHashMap.put("token", UserCenter.getToken(this));
        requestHttpData(Constants.Urls.URL_POST_SECURITY_VERIFY, REQUEST_VERIFY_SAFE_QUESTION, FProtocol.HttpMethod.POST, identityHashMap);
    }

    private void judge() {
        if (verifyResult1.equals(answer1) && verifyResult2.equals(answer2) && verifyResult3.equals(answer3)){
            updateQuestion();
        }else {
            ToastUtil.shortShow(this, res.getString(R.string.verify_safe_answer_not_correct));
        }
    }

	@Override
	public void success(int requestCode, String data) {
		closeProgressDialog();
		Entity entity = Parsers.getResponseSatus(data);
		if (entity != null && RESPONSE_VERIFY_FAILED_CODE.equals(entity.getResultCode()) && REQUEST_VERIFY_SAFE_QUESTION == requestCode){

            showAlertDialog(res.getString(R.string.verify_safe_validation_fails),
                    res.getString(R.string.verify_safe_account_appeal_recovered),
                    res.getString(R.string.wjika_cancel),
                    res.getString(R.string.verify_safe_account_appeal_appeal),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            closeDialog();
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(VerifySafeQuestionActivity.this, ComplainMessageFirstActivity.class));
                            closeDialog();
                        }
                    });
        } else {
            super.success(requestCode, data);
		}
	}

	@Override
    protected void parseData(int requestCode, String data) {
        super.parseData(requestCode, data);
        switch (requestCode) {
            case REQUEST_QUESTION_CODE:
                if (data != null) {
                    ToastUtil.shortShow(this, res.getString(R.string.verify_safe_complete_set));
	                UserCenter.setSecurity(this, true);
                    ExitManager.instance.closeVerifyActivity();
                }else{
                    ToastUtil.shortShow(this,res.getString(R.string.verify_safe_passwords_and_answers));
                }
                break;
            case REQUEST_QUESTION_ITEM_CODE:
                List<QuestionItemEntity> questionItemEntities = Parsers.parseQuestionItem(data);
                if(questionItemEntities.size() == 2){
                    firstId = questionItemEntities.get(0).getId();
	                String firstQuestion = questionItemEntities.get(0).getContext();
                    secondId = questionItemEntities.get(1).getId();
	                String secondQuestion = questionItemEntities.get(1).getContext();
                    personVerifyQuestionDetail1.setText(firstQuestion);
                    personVerifyQuestionDetail2.setText(secondQuestion);
                }
                break;
            case REQUEST_VERIFY_SAFE_QUESTION:
                startActivity(new Intent(this, PayPasswordActivity.class).putExtra(FROM_EXTRA, PayPasswordActivity.FIND_PASSWORD_RESET));
                finish();
                break;
        }
    }

    private void updateQuestion() {
        IdentityHashMap<String, String> identityHashMap = new IdentityHashMap<>();
        identityHashMap.put("question1", qid1);
        identityHashMap.put("question2", qid2);
        identityHashMap.put("question3", qid3);
        identityHashMap.put("answer1",verifyResult1);
        identityHashMap.put("answer2",verifyResult2);
        identityHashMap.put("answer3",verifyResult3);
        identityHashMap.put("token", UserCenter.getToken(this));
        requestHttpData(Constants.Urls.URL_POST_SECURITY_NEW, REQUEST_QUESTION_CODE, FProtocol.HttpMethod.POST, identityHashMap);
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
