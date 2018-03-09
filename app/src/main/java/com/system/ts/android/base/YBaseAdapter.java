package com.system.ts.android.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Time ： 2017/3/27 .
 * Author ： Tim .
 * Description ：Adapter基类  .
 * Version : 1.0.0 .
 */
public abstract class YBaseAdapter<T> extends BaseAdapter {

    protected List<T> mList;
    protected Context mContext;
    protected LayoutInflater mInflater;

    public YBaseAdapter(List<T> list, Context mContext) {
        this.mList = list;
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        YBaseHolder holder;
        if (convertView == null) {
            holder = initHolder();
            convertView = holder.holderView;
            convertView.setTag(holder);
        } else {
            holder = (YBaseHolder) convertView.getTag();
        }
        holder.bindData(position);
        return convertView;
    }

    public abstract YBaseHolder initHolder();
}
