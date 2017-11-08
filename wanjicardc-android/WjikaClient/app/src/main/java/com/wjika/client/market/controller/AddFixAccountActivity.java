package com.wjika.client.market.controller;

import android.os.Bundle;

import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;

/**
 * Created by kkkkk on 2016/10/8.
 */

public class AddFixAccountActivity extends ToolBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market_addfix_account_act);
        iniView();
    }

    private void iniView() {
        setLeftTitle("新增缴费账户");
    }
}
