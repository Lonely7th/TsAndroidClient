package com.system.ts.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.system.ts.android.R;
import com.system.ts.android.base.YBaseHolder;
import com.system.ts.android.bean.SearchBean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Time ： 2018/3/7 .
 * Author ： JN Zhang .
 * Description ： .
 * Version : 1.0.0 .
 */
public class SearchTkHolder extends YBaseHolder<SearchBean> {

    @Bind(R.id.tv_content)
    TextView tvContent;

    public SearchTkHolder(Context mContext, List<SearchBean> mLists) {
        super(mContext, mLists);
        ButterKnife.bind(this, holderView);
    }

    @Override
    public View getInflateView(Context mContext) {
        View view = View.inflate(mContext, R.layout.item_search_data, null);
        return view;
    }

    @Override
    public void bindData(int position) {
        tvContent.setText(mLists.get(position).getTkName() + "(" + mLists.get(position).getTkCode() + ")");
    }

}
