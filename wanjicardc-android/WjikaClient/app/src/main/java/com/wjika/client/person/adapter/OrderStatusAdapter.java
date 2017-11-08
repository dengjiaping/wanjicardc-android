package com.wjika.client.person.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.wjika.cardagent.client.R;
import com.wjika.client.person.entity.OrderStatusEntity;

import java.util.List;

/**
 * Created by 张家洛 on 2016/8/26.
 */
public class OrderStatusAdapter extends BaseAdapterNew<OrderStatusEntity> {


    public OrderStatusAdapter(Context context, List<OrderStatusEntity> mDatas) {
        super(context, mDatas);

    }

    @Override
    protected int getResourceId(int Position) {
        return R.layout.order_status_detail;
    }

    @Override
    protected void setViewData(View convertView, int position) {
        OrderStatusEntity item = getItem(position);
        TextView txOrderStatus = ViewHolder.get(convertView,R.id.tx_order_status);
        txOrderStatus.setText(item.getName());

        if(item.isStatus()){
            txOrderStatus.setTextColor(getContext().getResources().getColor(R.color.wjika_client_title_bg));
        }else {
            txOrderStatus.setTextColor(getContext().getResources().getColor(R.color.wjika_client_gray));
        }
    }
}
