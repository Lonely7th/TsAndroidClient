package com.system.ts.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.system.ts.android.R;
import com.system.ts.android.adapter.SearchTkAdapter;
import com.system.ts.android.adapter.SearchTkGridAdapter;
import com.system.ts.android.bean.SearchBean;
import com.system.ts.android.utils.KeyBoardUtils;
import com.system.ts.android.utils.TkCodeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Time ： 2018/3/7 .
 * Author ： JN Zhang .
 * Description ： .
 * Version : 1.0.0 .
 */
public class SearchTkActivity extends Activity {
    private static final String TAG = "SearchTkActivity";
    @Bind(R.id.back_btn)
    RelativeLayout backBtn;
    @Bind(R.id.edt_content)
    EditText edtContent;
    @Bind(R.id.right_btn)
    RelativeLayout rightBtn;
    @Bind(R.id.my_gridview)
    GridView myGridview;
    @Bind(R.id.rl_hot_search)
    RelativeLayout rlHotSearch;
    @Bind(R.id.my_listview)
    ListView myListview;

    private SearchTkAdapter searchTkAdapter;
    private List<SearchBean> listResult = new ArrayList<>();
    private SearchTkGridAdapter searchTkGridAdapter;
    private List<SearchBean> listHotSearch = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tk_search);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        //初始化listView
        KeyBoardUtils.openKeybord(edtContent,SearchTkActivity.this);
        searchTkAdapter = new SearchTkAdapter(listResult,SearchTkActivity.this);
        myListview.setAdapter(searchTkAdapter);
        myListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                KeyBoardUtils.closeKeybord(edtContent,SearchTkActivity.this);
                setResult(100, new Intent().putExtra("code",listResult.get(i).getTkCode()));
                finish();
            }
        });
        //初始化gridView
        initHotSearch();
        searchTkGridAdapter = new SearchTkGridAdapter(listHotSearch,SearchTkActivity.this);
        myGridview.setAdapter(searchTkGridAdapter);
        myGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                KeyBoardUtils.closeKeybord(edtContent,SearchTkActivity.this);
                setResult(100, new Intent().putExtra("code",listHotSearch.get(i).getTkCode()));
                finish();
            }
        });
        edtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String key = charSequence.toString();
                if(key.length() >= 2){
                    listResult.clear();
                    listResult.addAll(TkCodeUtils.getSearchResult(key.trim()));
                    searchTkAdapter.notifyDataSetChanged();
                    myListview.setVisibility(View.VISIBLE);
                    myGridview.setVisibility(View.GONE);
                }else{
                    myListview.setVisibility(View.GONE);
                    myGridview.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initHotSearch(){
        if(listHotSearch != null){
            listHotSearch.clear();
            listHotSearch.addAll(TkCodeUtils.getHotResult());
        }
    }

    @OnClick({R.id.back_btn, R.id.right_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                KeyBoardUtils.closeKeybord(edtContent,SearchTkActivity.this);
                finish();
                break;
            case R.id.right_btn:
                break;
        }
    }
}
