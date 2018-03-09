package com.system.ts.android.utils;

import com.system.ts.android.bean.SearchBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Time ： 2018/3/7 .
 * Author ： JN Zhang .
 * Description ： .
 * Version : 1.0.0 .
 */
public class TkCodeUtils {

    public static String getNamebyCode(String code){
        for(int i = 0;i < TkCodeData.codeList.length;i++){
            if(TkCodeData.codeList[i].equals(code)){
                return TkCodeData.nameList[i];
            }
        }
        return "";
    }

    public static String getNextCode(String code, int next){
        for(int i = 0;i < TkCodeData.codeList.length;i++){
            if(TkCodeData.codeList[i].equals(code)){
                if(next == -1 && i == 0){
                    break;
                }
                if(next == 1 && i == TkCodeData.codeList.length-1){
                    break;
                }
                return TkCodeData.codeList[i+next];
            }
        }
        return "";
    }

    public static List<SearchBean> getSearchResult(String key){
        List<SearchBean> result = new ArrayList<>();
        for(int i = 0;i < TkCodeData.codeList.length;i++){
            if(TkCodeData.codeList[i].contains(key) || TkCodeData.nameList[i].contains(key)){
                SearchBean bean = new SearchBean(TkCodeData.codeList[i],TkCodeData.nameList[i]);
                result.add(bean);
            }
            if(result.size() >= 6){
                break;
            }
        }
        return result;
    }

    public static List<SearchBean> getHotResult(){
        List<SearchBean> result = new ArrayList<>();
        Random rand = new Random();
        int length = TkCodeData.codeList.length;
        for(int i = 0;i < 6;i++){
            int index = rand.nextInt(length);
            SearchBean bean = new SearchBean(TkCodeData.codeList[index],TkCodeData.nameList[index]);
            result.add(bean);
        }
        return result;
    }

}
