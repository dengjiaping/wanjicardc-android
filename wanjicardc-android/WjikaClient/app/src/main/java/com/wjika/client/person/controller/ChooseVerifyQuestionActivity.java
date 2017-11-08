package com.wjika.client.person.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.common.network.FProtocol;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.QuestionItemEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.person.adapter.SafeQuestionAdapter;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by leo_Zhang on 2015/12/7.
 * 获取系统安全问题列表
 */
public class ChooseVerifyQuestionActivity extends ToolBarActivity implements AdapterView.OnItemClickListener {

    private static final int REQUEST_CODE_QUESTION_ITEM = 100;

    @ViewInject(R.id.lv_question_list)
    private ListView lvQuestionList;

    private List<QuestionItemEntity> questionItemEntities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_act_choose_question);
        ViewInjectUtils.inject(this);
        initView();
    }

    private void initView() {
        setLeftTitle(res.getString(R.string.choose_verify_select_security_question));
        lvQuestionList.setOnItemClickListener(this);
        loadData();
    }

    private void loadData() {
        IdentityHashMap<String, String> params = new IdentityHashMap<>();
        params.put(Constants.TOKEN, UserCenter.getToken(this));
        requestHttpData(Constants.Urls.URL_GET_SECURITY_QUESTION, REQUEST_CODE_QUESTION_ITEM, FProtocol.HttpMethod.POST, params);
    }

    @Override
    protected void parseData(int requestCode, String data) {
        super.parseData(requestCode, data);
        switch (requestCode) {
            case REQUEST_CODE_QUESTION_ITEM:
                questionItemEntities = Parsers.parseQuestionItem(data);
                if (questionItemEntities != null) {
                    SafeQuestionAdapter safeQuestionAdapter = new SafeQuestionAdapter(this, questionItemEntities);
                    lvQuestionList.setAdapter(safeQuestionAdapter);
                }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        QuestionItemEntity entity = questionItemEntities.get(position);
        Intent intent = new Intent();
        String question = entity.getId();
        String detail = entity.getContext();
        intent.putExtra(AccountSafeQuestionActivity.SELECT_QUESTION_ID,question);
        intent.putExtra(AccountSafeQuestionActivity.SELECT_QUESTION, detail);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void receiverLogout(String data) {
        super.receiverLogout(data);
        finish();
    }
}
