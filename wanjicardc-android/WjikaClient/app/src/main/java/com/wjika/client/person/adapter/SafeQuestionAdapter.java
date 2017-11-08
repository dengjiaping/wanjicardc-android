package com.wjika.client.person.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.QuestionItemEntity;

import java.util.List;

/**
 * Created by leo_Zhang on 2015/12/8.
 */
public class SafeQuestionAdapter extends BaseAdapterNew<QuestionItemEntity> {

    public SafeQuestionAdapter(Context context, List<QuestionItemEntity> mDatas) {
        super(context, mDatas);
    }


    @Override
    protected int getResourceId(int Position) {
        return R.layout.person_safe_question_item;
    }

    @Override
    protected void setViewData(View convertView, int position) {
        QuestionItemEntity item = this.getItem(position);
        TextView tv_questions= ViewHolder.get(convertView,R.id.tv_questions);
        tv_questions.setText(item.getContext());

    }

//    public SafeQuestionAdapter(Context context, List<QuestionItemEntity> mDatas) {
//        super(context, mDatas);
//    }
//
//    @Override
//    protected int getResourceId(int Position) {
//        return R.home_morestore.person_safe_question_item;
//    }
//
//    @Override
//    protected void setViewData(View convertView, int position) {
//
//        QuestionItemEntity item = this.getItem(position);
//
//        TextView tv_questions= ViewHolder.get(convertView,R.id.tv_questions);
//        tv_questions.setText(item.getQuestions());
//
//    }
}
