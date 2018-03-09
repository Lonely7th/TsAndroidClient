package com.system.ts.android.adapter;

import android.content.Context;

import com.system.ts.android.base.YBaseAdapter;
import com.system.ts.android.base.YBaseHolder;
import com.system.ts.android.bean.SearchBean;

import java.util.List;

/**
 * Time ： 2018/3/7 .
 * Author ： JN Zhang .
 * Description ： .
 * Version : 1.0.0 .
 */
public class SearchTkAdapter extends YBaseAdapter<SearchBean> {

    private Context mContext;
    private List<SearchBean> list;

    public SearchTkAdapter(List<SearchBean> list, Context mContext) {
        super(list, mContext);
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public YBaseHolder initHolder() {
        return new SearchTkHolder(mContext, list);
    }


}
